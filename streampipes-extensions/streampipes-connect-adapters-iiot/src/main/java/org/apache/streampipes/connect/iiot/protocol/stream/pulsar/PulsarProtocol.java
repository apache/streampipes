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
package org.apache.streampipes.connect.iiot.protocol.stream.pulsar;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.connect.iiot.protocol.stream.BrokerProtocol;
import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.connect.SendToPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PulsarProtocol extends BrokerProtocol implements SupportsRuntimeConfig {

  private static final Logger LOG = LoggerFactory.getLogger(PulsarProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.pulsar";

  public static final String PULSAR_BROKER_HOST = "pulsar-broker-host";
  public static final String PULSAR_BROKER_PORT = "pulsar-broker-port";
  public static final String PULSAR_TOPIC = "pulsar-topic";
  public static final String PULSAR_SUBSCRIPTION_NAME = "pulsar-subscription-name";

  private String subscriptionName;
  private Consumer<byte[]> consumer;

  public PulsarProtocol() {

  }

  public PulsarProtocol(IParser parser, IFormat format, String brokerUrl, String topic, String subscriptionName) {
    super(parser, format, brokerUrl, topic);
    this.subscriptionName = subscriptionName;
  }

  @Override
  protected List<byte[]> getNByteElements(int n) throws ParseException {
    List<byte[]> elements = new ArrayList<>();
    try (PulsarClient pulsarClient = PulsarUtils.makePulsarClient(brokerUrl);
         Reader<byte[]> reader = pulsarClient.newReader()
             .topic(topic)
             .startMessageId(MessageId.earliest)
             .create()) {
      int readCount = 0;
      while (readCount < n) {
        Message<byte[]> message = reader.readNext(1, TimeUnit.SECONDS);
        if (message == null) {
          continue;
        }
        elements.add(message.getValue());
        readCount++;
      }
    } catch (IOException e) {
      throw new ParseException("Failed to fetch messages.", e);
    }
    return elements;
  }

  @Override
  public Protocol getInstance(ProtocolDescription protocolDescription, IParser parser, IFormat format) {
    ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
    String brokerHost = extractor.singleValue(PULSAR_BROKER_HOST, String.class);
    Integer brokerPort = extractor.singleValue(PULSAR_BROKER_PORT, Integer.class);
    String brokerUrl = brokerHost + ":" + brokerPort;
    String topic = extractor.singleValue(PULSAR_TOPIC, String.class);
    String subscriptionName = extractor.singleValue(PULSAR_SUBSCRIPTION_NAME, String.class);

    return new PulsarProtocol(parser, format, brokerUrl, topic, subscriptionName);
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
        .requiredTextParameter(Labels.withId(PULSAR_SUBSCRIPTION_NAME))
        .build();
  }

  @Override
  public void run(IAdapterPipeline adapterPipeline) {
    SendToPipeline stk = new SendToPipeline(format, adapterPipeline);

    try {
      if (consumer != null) {
        consumer.close();
      }
      PulsarClient client = PulsarUtils.makePulsarClient(brokerUrl);
      consumer = client.newConsumer()
          .topic(topic)
          .subscriptionName(subscriptionName)
          .messageListener((MessageListener<byte[]>) (consumer, msg) -> {
            try {
              stk.emit(msg.getValue());
            } catch (ParseException e) {
              LOG.error("Failed to parse message.", e);
            }
          }).subscribe();
    } catch (PulsarClientException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void stop() {
    if (consumer != null) {
      try {
        consumer.close();
      } catch (PulsarClientException e) {
        throw new RuntimeException(e);
      }
    }
    consumer = null;
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName, StaticPropertyExtractor extractor)
      throws
      SpConfigurationException {
    String brokerHost = extractor.singleValueParameter(PULSAR_BROKER_HOST, String.class);
    Integer brokerPort = extractor.singleValueParameter(PULSAR_BROKER_PORT, Integer.class);

    try {
      PulsarClient client = PulsarUtils.makePulsarClient(brokerHost + ":" + brokerPort);
      return null;
    } catch (PulsarClientException e) {
      throw new SpConfigurationException(e);
    }
  }
}
