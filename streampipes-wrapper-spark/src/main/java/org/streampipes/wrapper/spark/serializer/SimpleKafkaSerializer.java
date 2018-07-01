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

package org.streampipes.wrapper.spark.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by Jochen Lutz on 2017-12-21.
 */
public class SimpleKafkaSerializer implements VoidFunction<org.apache.spark.api.java.JavaRDD<java.util.Map<java.lang.String,java.lang.Object>>> {
    private static SimpleKafkaSerializer instance;

    private final Map kafkaParams;
    private final String topic;

    private SimpleKafkaSerializer(Map kafkaParams, String topicName) {
        this.topic = topicName;
        //System.out.println("Sending output to Kafka topic '" + topicName + "'");
        this.kafkaParams = kafkaParams;
    }

    @Override
    public void call(JavaRDD<Map<String, Object>> javaRDD) throws Exception {
        //System.out.println("Sending Kafka output");

        javaRDD.foreach(new VoidFunction<Map<String, Object>>() {
            private static final long serialVersionUID = 1L;

            private final ObjectMapper objectMapper = new ObjectMapper();

            @Override
            public void call(Map<String, Object> map) throws Exception {
                KafkaProducer<String, String> producer = new KafkaProducer<String, String>(kafkaParams);

                producer.send(new ProducerRecord<>(topic, objectMapper.writeValueAsString(map)));
            }
        });
    }

    public static synchronized SimpleKafkaSerializer getInstance(Map kafkaParams, String topicName) {
        if (SimpleKafkaSerializer.instance == null) {
            SimpleKafkaSerializer.instance = new SimpleKafkaSerializer(kafkaParams, topicName);
        }

        return SimpleKafkaSerializer.instance;
    }
}
