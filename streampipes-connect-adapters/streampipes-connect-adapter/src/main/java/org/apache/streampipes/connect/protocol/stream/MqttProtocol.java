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
import org.apache.streampipes.connect.utils.MqttConnectUtils;
import org.apache.streampipes.connect.SendToPipeline;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.generic.Format;
import org.apache.streampipes.connect.adapter.model.generic.Parser;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MqttProtocol extends BrokerProtocol {

  public static final String ID = "org.apache.streampipes.connect.protocol.stream.mqtt";

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

    mqttConfig = MqttConnectUtils.getMqttConfig(extractor);

    return new MqttProtocol(parser, format, mqttConfig);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID)
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .category(AdapterType.Generic, AdapterType.Manufacturing)
            .sourceType(AdapterSourceType.STREAM)
            .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
            .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(), MqttConnectUtils.getAlternativesTwo())
            .requiredTextParameter(MqttConnectUtils.getTopicLabel())
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
