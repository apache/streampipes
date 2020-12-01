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
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.connect.SendToPipeline;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.generic.Format;
import org.apache.streampipes.connect.adapter.model.generic.Parser;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.connect.utils.KafkaConnectUtils;
import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class KafkaProtocol extends BrokerProtocol implements ResolvesContainerProvidedOptions {

    Logger logger = LoggerFactory.getLogger(KafkaProtocol.class);
    KafkaConfig config;

    public static final String ID = "org.apache.streampipes.connect.protocol.stream.kafka";

    private Thread thread;
    private SpKafkaConsumer kafkaConsumer;

    public KafkaProtocol() {
    }

    public KafkaProtocol(Parser parser, Format format, KafkaConfig config) {
        super(parser, format, config.getKafkaHost() + ":" + config.getKafkaPort(), config.getTopic());
        this.config = config;
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        StaticPropertyExtractor extractor = StaticPropertyExtractor
                .from(protocolDescription.getConfig(), new ArrayList<>());
        this.config = KafkaConnectUtils.getConfig(extractor);

        return new KafkaProtocol(parser, format, config);
    }

    @Override
    public ProtocolDescription declareModel() {
        return ProtocolDescriptionBuilder.create(ID)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .category(AdapterType.Generic, AdapterType.Manufacturing)
                .sourceType(AdapterSourceType.STREAM)
                .requiredAlternatives(KafkaConnectUtils.getAccessModeLabel(),
                        KafkaConnectUtils.getAlternativesOne(), KafkaConnectUtils.getAlternativesTwo())
                .requiredTextParameter(KafkaConnectUtils.getHostLabel())
                .requiredIntegerParameter(KafkaConnectUtils.getPortLabel())
                .requiredSingleValueSelectionFromContainer(KafkaConnectUtils.getTopicLabel(), Arrays.asList(
                        KafkaConnectUtils.getHostKey(),
                        KafkaConnectUtils.getPortKey()))
                .build();
    }

    @Override
    protected List<byte[]> getNByteElements(int n) throws ParseException {
        final Consumer<Long, String> consumer;
        if (authenticationRequired()) {
            consumer = createConsumer(this.brokerUrl, this.topic, config.getUsername(), config.getPassword());
        }
        else {
            consumer = createConsumer(this.brokerUrl, this.topic);
        }

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
        List<byte[]> resultEventsByte = new ArrayList<>();


        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

            consumerRecords.forEach(record -> {
                try {
                    InputStream inputStream = IOUtils.toInputStream(record.value(), "UTF-8");

                    nEventsByte.addAll(parser.parseNEvents(inputStream, n));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private static Consumer<Long, String> createConsumer(String broker, String topic) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                broker);

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
        consumer.subscribe(Collections.singletonList(topic));

        return consumer;
    }

    private static Consumer<Long, String> createConsumer(String broker, String topic, String username, String password) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                broker);

        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer" + System.currentTimeMillis());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username + "\" password=\"" + password + "\";");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.toString());

        // Create the consumer using props.
        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(topic));

        return consumer;
    }

    @Override
    public void run(AdapterPipeline adapterPipeline) {
        SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
        KafkaTransportProtocol protocol = new KafkaTransportProtocol();
        protocol.setKafkaPort(config.getKafkaPort());
        protocol.setBrokerHostname(config.getKafkaHost());
        protocol.setTopicDefinition(new SimpleTopicDefinition(topic));
        if (authenticationRequired()) {
            this.kafkaConsumer = new SpKafkaConsumer(protocol, config.getTopic(), new EventProcessor(stk),
                    config.getUsername(), config.getPassword());
        }
        else {
            this.kafkaConsumer = new SpKafkaConsumer(protocol, config.getTopic(), new EventProcessor(stk));
        }

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
        String kafkaHost = extractor.singleValueParameter(KafkaConnectUtils.getHostKey(), String.class);
        Integer kafkaPort = extractor.singleValueParameter(KafkaConnectUtils.getPortKey(), Integer.class);
        String authenticationMode = extractor.selectedAlternativeInternalId(KafkaConnectUtils.getAccessModeKey());
        boolean useAuthentication = authenticationMode.equals(KafkaConnectUtils.getSaslAccessKey());

        String kafkaAddress = kafkaHost + ":" + kafkaPort;

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaAddress);
        props.put("group.id", "test-consumer-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        if (useAuthentication) {
            String username = extractor.singleValueParameter(KafkaConnectUtils.getUsernameKey(), String.class);
            String password = extractor.secretValue(KafkaConnectUtils.getPasswordKey());
            props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username + "\" password=\"" + password + "\";");
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.toString());
        }

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        Set<String> topics = consumer.listTopics().keySet();
        consumer.close();
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
            } catch (IOException e) {
                logger.error("Adapter " + ID + " could not read value!",e);
            } catch (ParseException e) {
                logger.error("Error while parsing: " + e.getMessage());
        }
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    private boolean authenticationRequired() {
        return config.getAuthentication().equals(KafkaConnectUtils.getSaslAccessKey());
    }
}
