/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.protocol.stream;

import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.SendToPipeline;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.generic.Format;
import org.streampipes.connect.adapter.model.generic.Parser;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.kafka.SpKafkaConsumer;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.streampipes.sdk.helpers.AdapterSourceType;
import org.streampipes.sdk.helpers.Labels;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class KafkaProtocol extends BrokerProtocol implements ResolvesContainerProvidedOptions {

    Logger logger = LoggerFactory.getLogger(KafkaProtocol.class);

    public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/kafka";

    private Thread thread;
    private SpKafkaConsumer kafkaConsumer;

    public KafkaProtocol() {
    }

    public KafkaProtocol(Parser parser, Format format, String brokerUrl, String topic) {
        super(parser, format, brokerUrl, topic);
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
        String brokerHost = extractor.singleValue("broker_url", String.class);
        Integer brokerPort = extractor.singleValue("broker_port", Integer.class);
        String brokerUrl = brokerHost + ":" + brokerPort;
        String topic = extractor.selectedSingleValueOption("topic");

        return new KafkaProtocol(parser, format, brokerUrl, topic);
    }

    @Override
    public ProtocolDescription declareModel() {
        return ProtocolDescriptionBuilder.create(ID,"Apache Kafka","Consumes messages from an " +
                "Apache Kafka broker")
                .iconUrl("kafka.jpg")
                .category(AdapterType.Generic, AdapterType.Manufacturing)
                .sourceType(AdapterSourceType.STREAM)
                .requiredTextParameter(Labels.from("broker_url", "Broker Hostname",
                        "Example: test.server.com (No protocol)"))
                .requiredIntegerParameter(Labels.from("broker_port", "Broker Port", "Example: " +
                        "9092"))
                .requiredSingleValueSelectionFromContainer(Labels.from("topic", "Topic",
                        "Example: test.topic"), Arrays.asList("broker_url", "broker_port"))
                .build();
    }

    @Override
    protected List<byte[]> getNByteElements(int n) throws ParseException {
        final Consumer<Long, String> consumer = createConsumer(this.brokerUrl, this.topic);

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


    @Override
    public void run(AdapterPipeline adapterPipeline) {
        SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
        this.kafkaConsumer = new SpKafkaConsumer(this.brokerUrl, this.topic, new EventProcessor(stk));

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
        String kafkaHost = extractor.singleValueParameter("broker_url", String.class);
        Integer kafkaPort = extractor.singleValueParameter("broker_port", Integer.class);

        String kafkaAddress = kafkaHost + ":" + kafkaPort;

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaAddress);
        props.put("group.id", "test-consumer-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

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

}
