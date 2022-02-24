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

import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.connect.SendToPipeline;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.api.IAdapterPipeline;
import org.apache.streampipes.connect.api.IFormat;
import org.apache.streampipes.connect.api.IParser;
import org.apache.streampipes.connect.api.exception.ParseException;
import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.messaging.kafka.security.*;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.pe.shared.config.kafka.KafkaConfig;
import org.apache.streampipes.pe.shared.config.kafka.KafkaConnectUtils;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class KafkaProtocol extends BrokerProtocol implements ResolvesContainerProvidedOptions {

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
        final Consumer<Long, String> consumer;

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
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

            consumerRecords.forEach(record -> {
                InputStream inputStream = IOUtils.toInputStream(record.value(), "UTF-8");

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

    private static Consumer<Long, String> createConsumer(KafkaConfig kafkaConfig) {
        final Properties props = new Properties();

        kafkaConfig.getSecurityConfig().appendConfig(props);

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfig.getKafkaHost() + ":" + kafkaConfig.getKafkaPort());

        // TODO make serializer configurable
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer" + System.currentTimeMillis());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        // Create the consumer using props.
        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(kafkaConfig.getTopic()));

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
    public List<Option> resolveOptions(String requestId, StaticPropertyExtractor extractor) {
        KafkaConfig kafkaConfig = KafkaConnectUtils.getConfig(extractor, false);
        boolean hideInternalTopics = extractor.slideToggleValue(KafkaConnectUtils.getHideInternalTopicsKey());

        String kafkaAddress = kafkaConfig.getKafkaHost() + ":" + kafkaConfig.getKafkaPort();
        Properties props = new Properties();

        // add security properties to kafka configuration
        kafkaConfig.getSecurityConfig().appendConfig(props);

        props.put("bootstrap.servers", kafkaAddress);
        props.put("group.id", "test-consumer-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        Set<String> topics = consumer.listTopics().keySet();
        consumer.close();

        if (hideInternalTopics) {
            topics = topics
                    .stream()
                    .filter(t -> !t.startsWith(GlobalStreamPipesConstants.INTERNAL_TOPIC_PREFIX))
                    .collect(Collectors.toSet());
        }
        return topics.stream().map(Option::new).collect(Collectors.toList());
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
