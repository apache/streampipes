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
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.iiot.protocol.stream.BrokerEventProcessor;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PulsarProtocol implements StreamPipesAdapter, SupportsRuntimeConfig {

  private static final Logger LOG = LoggerFactory.getLogger(PulsarProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.pulsar";

  public static final String PULSAR_BROKER_HOST = "pulsar-broker-host";
  public static final String PULSAR_BROKER_PORT = "pulsar-broker-port";
  public static final String PULSAR_TOPIC = "pulsar-topic";
  public static final String PULSAR_SUBSCRIPTION_NAME = "pulsar-subscription-name";

  private PulsarConfig config;
  private Consumer<byte[]> consumer;

  public PulsarProtocol() {

  }

  public void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.config = PulsarConfig.from(extractor);
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName, IStaticPropertyExtractor extractor)
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

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, PulsarProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic)
        .requiredTextParameter(Labels.withId(PULSAR_BROKER_HOST))
        .requiredIntegerParameter(Labels.withId(PULSAR_BROKER_PORT), 6650)
        .requiredTextParameter(Labels.withId(PULSAR_TOPIC))
        .requiredTextParameter(Labels.withId(PULSAR_SUBSCRIPTION_NAME))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    applyConfiguration(extractor.getStaticPropertyExtractor());
    var processor = new BrokerEventProcessor(extractor.selectedParser(), collector);
    try {
      if (consumer != null) {
        consumer.close();
      }
      PulsarClient client = PulsarUtils.makePulsarClient(config.getBrokerUrl());
      consumer = client.newConsumer()
          .topic(config.getTopic())
          .subscriptionName(config.getSubscriptionName())
          .messageListener((MessageListener<byte[]>) (consumer, msg) -> {
            try {
              processor.onEvent(msg.getValue());
            } catch (ParseException e) {
              LOG.error("Failed to parse message.", e);
            }
          }).subscribe();
    } catch (PulsarClientException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
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
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    List<byte[]> elements = new ArrayList<>();
    applyConfiguration(extractor.getStaticPropertyExtractor());
    try (PulsarClient pulsarClient = PulsarUtils.makePulsarClient(config.getBrokerUrl());
         Reader<byte[]> reader = pulsarClient.newReader()
             .topic(config.getTopic())
             .startMessageId(MessageId.earliest)
             .create()) {
      int readCount = 0;
      while (readCount < 1) {
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
    return extractor.selectedParser().getGuessSchema(new ByteArrayInputStream(elements.get(0)));
  }
}
