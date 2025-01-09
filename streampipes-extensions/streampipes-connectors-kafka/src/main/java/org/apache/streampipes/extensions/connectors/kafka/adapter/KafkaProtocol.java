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

package org.apache.streampipes.extensions.connectors.kafka.adapter;


import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaBaseConfig;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigExtractor;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider;
import org.apache.streampipes.extensions.management.connect.adapter.BrokerEventProcessor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class KafkaProtocol implements StreamPipesAdapter, SupportsRuntimeConfig {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaProtocol.class);
  private KafkaBaseConfig config;

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.kafka";

  private Thread thread;
  private SpKafkaConsumer kafkaConsumer;

  public KafkaProtocol() {
  }

  private void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.config = new KafkaConfigExtractor().extractAdapterConfig(extractor, true);
  }

  private Consumer<byte[], byte[]> createConsumer(KafkaBaseConfig kafkaConfig) throws KafkaException {
    final Properties props = new Properties();

    kafkaConfig.getConfigAppenders().forEach(c -> c.appendConfig(props));

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaConfig.getKafkaHost() + ":" + kafkaConfig.getKafkaPort());

    props.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 6000);

    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        ByteArrayDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        ByteArrayDeserializer.class.getName());

    return new KafkaConsumer<>(props);
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             IStaticPropertyExtractor extractor)
      throws SpConfigurationException {
    RuntimeResolvableOneOfStaticProperty config = extractor
        .getStaticPropertyByName(KafkaConfigProvider.TOPIC_KEY, RuntimeResolvableOneOfStaticProperty.class);
    var kafkaConfig = new KafkaConfigExtractor().extractAdapterConfig(extractor, false);
    boolean hideInternalTopics = extractor.slideToggleValue(KafkaConfigProvider.getHideInternalTopicsKey());

    try {
      var consumer = createConsumer(kafkaConfig);
      List<String> topics = new ArrayList<>(consumer.listTopics().keySet()).stream().sorted().toList();
      consumer.close();

      if (hideInternalTopics) {
        topics = topics
            .stream()
            .filter(t -> (!t.startsWith(GlobalStreamPipesConstants.INTERNAL_TOPIC_PREFIX)
                && !t.startsWith(GlobalStreamPipesConstants.CONNECT_TOPIC_PREFIX)))
            .toList();
      }

      config.setOptions(topics.stream().map(Option::new).collect(Collectors.toList()));

      return config;
    } catch (Exception e) {
      var message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
      throw new SpConfigurationException(message, e.getCause());
    }
  }

  @Override
  public IAdapterConfiguration declareConfig() {

    StaticPropertyAlternative latestAlternative = KafkaConfigProvider.getAlternativesLatest();
    latestAlternative.setSelected(true);

    return AdapterConfigurationBuilder
        .create(ID, 2, KafkaProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic, AdapterType.Manufacturing)

        .requiredAlternatives(KafkaConfigProvider.getAccessModeLabel(),
            KafkaConfigProvider.getAlternativeUnauthenticatedPlain(),
            KafkaConfigProvider.getAlternativeUnauthenticatedSSL(),
            KafkaConfigProvider.getAlternativesSaslPlain(),
            KafkaConfigProvider.getAlternativesSaslSSL())

        .requiredTextParameter(KafkaConfigProvider.getHostLabel())
        .requiredIntegerParameter(KafkaConfigProvider.getPortLabel())

        .requiredAlternatives(KafkaConfigProvider.getConsumerGroupLabel(),
            KafkaConfigProvider.getAlternativesRandomGroupId(),
            KafkaConfigProvider.getAlternativesGroupId())

        .requiredSlideToggle(KafkaConfigProvider.getHideInternalTopicsLabel(), true)

        .requiredSingleValueSelectionFromContainer(KafkaConfigProvider.getTopicLabel(), Arrays.asList(
            KafkaConfigProvider.HOST_KEY,
            KafkaConfigProvider.PORT_KEY))
        .requiredAlternatives(KafkaConfigProvider.getAutoOffsetResetConfigLabel(),
            KafkaConfigProvider.getAlternativesEarliest(),
            latestAlternative,
            KafkaConfigProvider.getAlternativesNone())
        .requiredCodeblock(
            Labels.withId(KafkaConfigProvider.ADDITIONAL_PROPERTIES),
            "# key=value, comments are ignored"
        )
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    KafkaTransportProtocol protocol = new KafkaTransportProtocol();
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    protocol.setKafkaPort(config.getKafkaPort());
    protocol.setBrokerHostname(config.getKafkaHost());
    protocol.setTopicDefinition(new SimpleTopicDefinition(config.getTopic()));

    this.kafkaConsumer = new SpKafkaConsumer(protocol,
        config.getTopic(),
        new BrokerEventProcessor(extractor.selectedParser(), collector),
        config.getConfigAppenders()
    );

    thread = new Thread(this.kafkaConsumer);
    thread.start();
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    try {
      kafkaConsumer.disconnect();
    } catch (SpRuntimeException e) {
      LOG.warn("Runtime exception when disconnecting from Kafka", e);
    }

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      LOG.warn("Interrupted exception when stopping thread", e);
    }

    LOG.info("Kafka Adapter was sucessfully stopped");
    thread.interrupt();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {

    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    final Consumer<byte[], byte[]> consumer;

    consumer = createConsumer(this.config);
    consumer.subscribe(Collections.singletonList(config.getTopic()), new ConsumerRebalanceListener() {
      @Override
      public void onPartitionsRevoked(Collection<TopicPartition> collection) {

      }

      @Override
      public void onPartitionsAssigned(Collection<TopicPartition> collection) {
        consumer.seekToBeginning(collection);
      }
    });

    List<byte[]> nEventsByte = new ArrayList<>();
    List<byte[]> resultEventsByte;
    int n = 1;


    while (true) {
      final ConsumerRecords<byte[], byte[]> consumerRecords =
          consumer.poll(Duration.ofMillis(1000));

      consumerRecords.forEach(record -> nEventsByte.add(record.value()));

      if (nEventsByte.size() > n) {
        resultEventsByte = nEventsByte.subList(0, n);
        break;
      } else if (nEventsByte.size() == n) {
        resultEventsByte = nEventsByte;
        break;
      }

      consumer.commitAsync();
    }

    consumer.close();

    return extractor.selectedParser().getGuessSchema(new ByteArrayInputStream(resultEventsByte.get(0)));
  }
}
