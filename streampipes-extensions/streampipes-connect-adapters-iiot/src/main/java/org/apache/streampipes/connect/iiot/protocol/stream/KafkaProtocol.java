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

package org.apache.streampipes.connect.iiot.protocol.stream;


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
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.messaging.kafka.config.KafkaConfigAppender;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.pe.shared.config.kafka.kafka.KafkaConfig;
import org.apache.streampipes.pe.shared.config.kafka.kafka.KafkaConnectUtils;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class KafkaProtocol implements StreamPipesAdapter, SupportsRuntimeConfig {

  private static final Logger logger = LoggerFactory.getLogger(KafkaProtocol.class);
  KafkaConfig config;

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.kafka";

  private Thread thread;
  private SpKafkaConsumer kafkaConsumer;

  public KafkaProtocol() {
  }

  private void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.config = KafkaConnectUtils.getConfig(extractor, true);
  }

  private Consumer<byte[], byte[]> createConsumer(KafkaConfig kafkaConfig) throws KafkaException {
    final Properties props = new Properties();

    kafkaConfig.getSecurityConfig().appendConfig(props);
    kafkaConfig.getAutoOffsetResetConfig().appendConfig(props);

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaConfig.getKafkaHost() + ":" + kafkaConfig.getKafkaPort());

    props.put(ConsumerConfig.GROUP_ID_CONFIG,
        "KafkaExampleConsumer" + System.currentTimeMillis());

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
        .getStaticPropertyByName(KafkaConnectUtils.TOPIC_KEY, RuntimeResolvableOneOfStaticProperty.class);
    KafkaConfig kafkaConfig = KafkaConnectUtils.getConfig(extractor, false);
    boolean hideInternalTopics = extractor.slideToggleValue(KafkaConnectUtils.getHideInternalTopicsKey());

    try {
      Consumer<byte[], byte[]> consumer = createConsumer(kafkaConfig);
      Set<String> topics = consumer.listTopics().keySet();
      consumer.close();

      if (hideInternalTopics) {
        topics = topics
            .stream()
            .filter(t -> !t.startsWith(GlobalStreamPipesConstants.INTERNAL_TOPIC_PREFIX))
            .collect(Collectors.toSet());
      }

      config.setOptions(topics.stream().map(Option::new).collect(Collectors.toList()));

      return config;
    } catch (KafkaException e) {
      throw new SpConfigurationException(e.getMessage(), e);
    }
  }

  @Override
  public IAdapterConfiguration declareConfig() {

    StaticPropertyAlternative latestAlternative = KafkaConnectUtils.getAlternativesLatest();
    latestAlternative.setSelected(true);

    return AdapterConfigurationBuilder
        .create(ID, KafkaProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic, AdapterType.Manufacturing)

        .requiredAlternatives(KafkaConnectUtils.getAccessModeLabel(),
            KafkaConnectUtils.getAlternativeUnauthenticatedPlain(),
            KafkaConnectUtils.getAlternativeUnauthenticatedSSL(),
            KafkaConnectUtils.getAlternativesSaslPlain(),
            KafkaConnectUtils.getAlternativesSaslSSL())

        .requiredTextParameter(KafkaConnectUtils.getHostLabel())
        .requiredIntegerParameter(KafkaConnectUtils.getPortLabel())

        .requiredSlideToggle(KafkaConnectUtils.getHideInternalTopicsLabel(), true)

        .requiredSingleValueSelectionFromContainer(KafkaConnectUtils.getTopicLabel(), Arrays.asList(
            KafkaConnectUtils.HOST_KEY,
            KafkaConnectUtils.PORT_KEY))
        .requiredAlternatives(KafkaConnectUtils.getAutoOffsetResetConfigLabel(),
                KafkaConnectUtils.getAlternativesEarliest(),
                latestAlternative,
                KafkaConnectUtils.getAlternativesNone())
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

    List<KafkaConfigAppender> kafkaConfigAppenderList = new ArrayList<>(2);
    kafkaConfigAppenderList.add(this.config.getSecurityConfig());
    kafkaConfigAppenderList.add(this.config.getAutoOffsetResetConfig());

    this.kafkaConsumer = new SpKafkaConsumer(protocol,
        config.getTopic(),
        new BrokerEventProcessor(extractor.selectedParser(), (event) -> {
          collector.collect(event);
        }),
        kafkaConfigAppenderList
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
      e.printStackTrace();
    }

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    logger.info("Kafka Adapter was sucessfully stopped");
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
          consumer.poll(1000);

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
