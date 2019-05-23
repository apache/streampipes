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

package org.streampipes.connect.adapter.generic.protocol.stream;

import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.SendToPipeline;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.format.json.object.JsonObjectFormat;
import org.streampipes.connect.adapter.generic.format.json.object.JsonObjectParser;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.kafka.SpKafkaConsumer;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.streampipes.sdk.helpers.AdapterSourceType;
import org.streampipes.sdk.helpers.Labels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaProtocol extends BrokerProtocol {

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
        String brokerUrl = extractor.singleValue("broker_url");
        String topic = extractor.singleValue("topic");

        return new KafkaProtocol(parser, format, brokerUrl, topic);
    }

    @Override
    public ProtocolDescription declareModel() {
        return ProtocolDescriptionBuilder.create(ID,"Apache Kafka","Consumes messages from an " +
                "Apache Kafka broker")
                .iconUrl("kafka.jpg")
                .category(AdapterType.Generic, AdapterType.Manufacturing)
                .sourceType(AdapterSourceType.STREAM)
                .requiredTextParameter(Labels.from("broker_url", "Broker URL",
                        "This property defines the URL of the Kafka broker."))
                .requiredTextParameter(Labels.from("topic", "Topic",
                        "Topic in the broker"))
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

    public static void main(String... args) throws ParseException {
        Parser parser = new JsonObjectParser();
        Format format = new JsonObjectFormat();
        KafkaProtocol kp = new KafkaProtocol(parser, format, "localhost:9092", "org.streampipes.examples.flowrate-1");
        GuessSchema gs = kp.getGuessSchema();

        System.out.println(gs);

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
