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
package org.apache.streampipes.connect.protocol.stream.pulsar;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.connect.SendToPipeline;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.generic.Format;
import org.apache.streampipes.connect.adapter.model.generic.Parser;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.connect.protocol.stream.BrokerProtocol;
import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.ArrayList;
import java.util.List;

public class PulsarProtocol extends BrokerProtocol implements ResolvesContainerProvidedOptions {

  private static final Logger LOG = LoggerFactory.getLogger(PulsarProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.protocol.stream.pulsar";

  private static final String PULSAR_BROKER_HOST = "pulsar-broker-host";
  private static final String PULSAR_BROKER_PORT = "pulsar-broker-port";
  private static final String PULSAR_TOPIC = "pulsar-topic";

  private Thread thread;
  private PulsarConsumer pulsarConsumer;

  public PulsarProtocol() {

  }

  public PulsarProtocol(Parser parser, Format format, String brokerUrl, String topic) {
    super(parser, format, brokerUrl, topic);
  }

  @Override
  protected List<byte[]> getNByteElements(int n) throws ParseException {
    List<byte[]> elements = new ArrayList<>();
    InternalEventProcessor<byte[]> eventProcessor = elements::add;
    PulsarConsumer consumer = new PulsarConsumer(this.brokerUrl, this.topic, eventProcessor, n);

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
  public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
    ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
    String brokerHost = extractor.singleValue(PULSAR_BROKER_HOST, String.class);
    Integer brokerPort = extractor.singleValue(PULSAR_BROKER_PORT, Integer.class);
    String brokerUrl = brokerHost + ":" + brokerPort;
    String topic = extractor.singleValue(PULSAR_TOPIC, String.class);

    return new PulsarProtocol(parser, format, brokerUrl, topic);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .category(AdapterType.Generic)
            .sourceType(AdapterSourceType.STREAM)
            .requiredTextParameter(Labels.withId(PULSAR_BROKER_HOST))
            .requiredIntegerParameter(Labels.withId(PULSAR_BROKER_PORT), 6650)
            .requiredTextParameter(Labels.withId(PULSAR_TOPIC))
//            .requiredSingleValueSelectionFromContainer(Labels.from(PULSAR_TOPIC, "Topic",
//                    "Example: topic"), Arrays.asList(PULSAR_BROKER_HOST, PULSAR_BROKER_PORT))
            .build();
  }

  @Override
  public void run(AdapterPipeline adapterPipeline) {
    SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
    this.pulsarConsumer = new PulsarConsumer(this.brokerUrl,
            this.topic, stk::emit);

    thread = new Thread(this.pulsarConsumer);
    thread.start();
  }

  @Override
  public void stop() {
    try {
      this.pulsarConsumer.stop();
    } catch (PulsarClientException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public List<Option> resolveOptions(String requestId, StaticPropertyExtractor extractor) {
    String brokerHost = extractor.singleValueParameter(PULSAR_BROKER_HOST, String.class);
    Integer brokerPort = extractor.singleValueParameter(PULSAR_BROKER_PORT, Integer.class);

    try {
      PulsarClient client = PulsarUtils.makePulsarClient(brokerHost + ":" + brokerPort);
      return new ArrayList<>();
    } catch (PulsarClientException e) {
      return new ArrayList<>();
    }
  }
}
