/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.connect.protocol.stream;

import org.apache.commons.io.IOUtils;
import org.apache.streampipes.connect.SendToPipeline;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.generic.Format;
import org.apache.streampipes.connect.adapter.model.generic.Parser;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MqttProtocol extends BrokerProtocol {

  public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/mqtt";

  private static final String ACCESS_MODE = "access_mode";
  private static final String ANONYMOUS_ACCESS = "anonymous-alternative";
  private static final String USERNAME_ACCESS = "username-alternative";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";

  private Thread thread;
  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;

  public MqttProtocol() {
  }

  public MqttProtocol(Parser parser, Format format, MqttConfig mqttConfig) {
    super(parser, format, mqttConfig.getUrl(), mqttConfig.getTopic());
    this.mqttConfig = mqttConfig;
  }

  @Override
  public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
    MqttConfig mqttConfig;
    StaticPropertyExtractor extractor =
            StaticPropertyExtractor.from(protocolDescription.getConfig(), new ArrayList<>());

    String brokerUrl = extractor.singleValueParameter("broker_url", String.class);
    String topic = extractor.singleValueParameter("topic", String.class);
    String selectedAlternative = extractor.selectedAlternativeInternalId("access_mode");

    if (selectedAlternative.equals(ANONYMOUS_ACCESS)) {
      mqttConfig = new MqttConfig(brokerUrl, topic);
    } else {
      String username = extractor.singleValueParameter(USERNAME, String.class);
      String password = extractor.secretValue(PASSWORD);
      mqttConfig = new MqttConfig(brokerUrl, topic, username, password);
    }

    return new MqttProtocol(parser, format, mqttConfig);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID, "MQTT", "Consumes messages from a broker using " +
            "the MQTT protocol")
            .iconUrl("mqtt.png")
            .category(AdapterType.Generic, AdapterType.Manufacturing)
            .sourceType(AdapterSourceType.STREAM)
            .requiredTextParameter(Labels.from("broker_url", "Broker URL",
                    "Example: tcp://test-server.com:1883 (Protocol required. Port required)"))
            .requiredAlternatives(Labels.from(ACCESS_MODE, "Access Mode", ""),
                    Alternatives.from(Labels.from(ANONYMOUS_ACCESS, "Unauthenticated", "")),
                    Alternatives.from(Labels.from(USERNAME_ACCESS, "Username/Password", ""),
                            StaticProperties.group(Labels.withId("username-group"),
                                    StaticProperties.stringFreeTextProperty(Labels.from(USERNAME,
                                            "Username", "")),
                                    StaticProperties.secretValue(Labels.from(PASSWORD,
                                            "Password", "")))))
            .requiredTextParameter(Labels.from("topic", "Topic","Example: test/topic"))
            .build();
  }

  @Override
  protected List<byte[]> getNByteElements(int n) throws ParseException {
    List<byte[]> elements = new ArrayList<>();
    InternalEventProcessor<byte[]> eventProcessor = elements::add;

    MqttConsumer consumer = new MqttConsumer(this.mqttConfig, eventProcessor);

    Thread thread = new Thread(consumer);
    thread.start();

    while (consumer.getMessageCount() < n) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return elements;
  }

  @Override
  public void run(AdapterPipeline adapterPipeline) {
    SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
    this.mqttConsumer = new MqttConsumer(this.mqttConfig, new MqttProtocol.EventProcessor(stk));

    thread = new Thread(this.mqttConsumer);
    thread.start();
  }

  @Override
  public void stop() {
    this.mqttConsumer.close();
  }

  @Override
  public String getId() {
    return ID;
  }

  private class EventProcessor implements InternalEventProcessor<byte[]> {
    private SendToPipeline stk;

    public EventProcessor(SendToPipeline stk) {
      this.stk = stk;
    }

    @Override
    public void onEvent(byte[] payload) {
      try {
        parser.parse(IOUtils.toInputStream(new String(payload), "UTF-8"), stk);
      } catch (IOException | ParseException e) {
        e.printStackTrace();
        //logger.error("Adapter " + ID + " could not read value!",e);
      }
    }
  }
}
