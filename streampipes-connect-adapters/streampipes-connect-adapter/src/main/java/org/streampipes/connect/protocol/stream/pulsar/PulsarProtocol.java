/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.connect.protocol.stream.pulsar;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.SendToPipeline;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.generic.Format;
import org.streampipes.connect.adapter.model.generic.Parser;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.connect.protocol.stream.BrokerProtocol;
import org.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.streampipes.sdk.helpers.AdapterSourceType;
import org.streampipes.sdk.helpers.Labels;

import java.util.ArrayList;
import java.util.List;

public class PulsarProtocol extends BrokerProtocol implements ResolvesContainerProvidedOptions {

  private static final Logger LOG = LoggerFactory.getLogger(PulsarProtocol.class);

  public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/pulsar";

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
    return ProtocolDescriptionBuilder.create(ID,"Apache Pulsar","Consumes messages from an " +
            "Apache Pulsar broker")
            .iconUrl("pulsar.png")
            .category(AdapterType.Generic)
            .sourceType(AdapterSourceType.STREAM)
            .requiredTextParameter(Labels.from(PULSAR_BROKER_HOST, "Broker Hostname",
                    "Example: test.server.com"))
            .requiredIntegerParameter(Labels.from(PULSAR_BROKER_PORT, "Broker Port", "Example: " +
                    "6650"))
            .requiredTextParameter(Labels.from(PULSAR_TOPIC, "Topic", ""))
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
