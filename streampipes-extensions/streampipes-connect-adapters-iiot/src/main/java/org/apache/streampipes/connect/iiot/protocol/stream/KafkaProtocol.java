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
import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.connect.SendToPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.pe.shared.config.kafka.KafkaConfig;
import org.apache.streampipes.pe.shared.config.kafka.KafkaConnectUtils;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.commons.io.IOUtils;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class KafkaProtocol extends BrokerProtocol implements SupportsRuntimeConfig {

  Logger logger = LoggerFactory.getLogger(KafkaProtocol.class);
  KafkaConfig config;

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.kafka";

  private Thread thread;
  private SpKafkaConsumer kafkaConsumer;

  public KafkaProtocol() {
  }

  public KafkaProtocol(IParser parser, IFormat format, KafkaConfig config) {
    super(parser, format, config.getKafkaHost() + ":" + config.getKafkaPort(), config.getTopic());
    this.config = config;
  }

  @Override
  public Protocol getInstance(ProtocolDescription protocolDescription, IParser parser, IFormat format) {
    StaticPropertyExtractor extractor = StaticPropertyExtractor
        .from(protocolDescription.getConfig(), new ArrayList<>());
    this.config = KafkaConnectUtils.getConfig(extractor, true);

    return new KafkaProtocol(parser, format, config);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(AdapterType.Generic, AdapterType.Manufacturing)
        .sourceType(AdapterSourceType.STREAM)

        .requiredAlternatives(Labels.withId(KafkaConnectUtils.ACCESS_MODE),
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
        .build();
  }

  @Override
  protected List<byte[]> getNByteElements(int n) throws ParseException {
    final Consumer<byte[], byte[]> consumer;

    consumer = createConsumer(this.config);
    consumer.subscribe(Arrays.asList(this.topic), new ConsumerRebalanceListener() {
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


    while (true) {
      final ConsumerRecords<byte[], byte[]> consumerRecords =
          consumer.poll(1000);

      consumerRecords.forEach(record -> {
        InputStream inputStream = new ByteArrayInputStream(record.value());

        nEventsByte.addAll(parser.parseNEvents(inputStream, n));
      });

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

    return resultEventsByte;
  }

  private Consumer<byte[], byte[]> createConsumer(KafkaConfig kafkaConfig) throws KafkaException {
    final Properties props = new Properties();

    kafkaConfig.getSecurityConfig().appendConfig(props);

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaConfig.getKafkaHost() + ":" + kafkaConfig.getKafkaPort());

    props.put(ConsumerConfig.GROUP_ID_CONFIG,
        "KafkaExampleConsumer" + System.currentTimeMillis());

    props.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 6000);

    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        ByteArrayDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        ByteArrayDeserializer.class.getName());

    // Create the consumer using props.
    final Consumer<byte[], byte[]> consumer =
        new KafkaConsumer<>(props);

    return consumer;
  }


  @Override
  public void run(IAdapterPipeline adapterPipeline) {
    SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
    KafkaTransportProtocol protocol = new KafkaTransportProtocol();
    protocol.setKafkaPort(config.getKafkaPort());
    protocol.setBrokerHostname(config.getKafkaHost());
    protocol.setTopicDefinition(new SimpleTopicDefinition(topic));

    this.kafkaConsumer = new SpKafkaConsumer(protocol,
        config.getTopic(),
        new EventProcessor(stk),
        Arrays.asList(this.config.getSecurityConfig()));

    thread = new Thread(this.kafkaConsumer);
    thread.start();
  }

  @Override
  public void stop() {
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
  public StaticProperty resolveConfiguration(String staticPropertyInternalName, StaticPropertyExtractor extractor)
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


  private class EventProcessor implements InternalEventProcessor<byte[]> {
    private SendToPipeline stk;

    public EventProcessor(SendToPipeline stk) {
      this.stk = stk;
    }

    @Override
    public void onEvent(byte[] payload) {
      try {
        parser.parse(IOUtils.toInputStream(new String(payload), "UTF-8"), stk);
      } catch (ParseException e) {
        logger.error("Error while parsing: " + e.getMessage());
      }
    }
  }

  @Override
  public String getId() {
    return ID;
  }
}
