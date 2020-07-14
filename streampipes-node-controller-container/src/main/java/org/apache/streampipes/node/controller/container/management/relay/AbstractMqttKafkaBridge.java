package org.apache.streampipes.node.controller.container.management.relay;/*
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

import com.google.gson.Gson;
import org.apache.streampipes.messaging.kafka.SpKafkaProducer;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public abstract class AbstractMqttKafkaBridge implements MqttCallback {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractMqttKafkaBridge.class);

    private final String topic;

    public final int qos = 1;
    public final int EVENT_BUFFER_SIZE = NodeControllerConfig.INSTANCE.getEventBufferSize();
    public final ArrayList<byte[]> eventBuffer = new ArrayList<>();
    public int numDroppedEvents = 0;
    public int numRelayedEvents = 0;
    public boolean buffering = false;
    public SpKafkaProducer producer;
    public MqttClient mqttClient;
    private long timeRelayStarted;

    public AbstractMqttKafkaBridge(String topic, String mqttUrl, String kafkaUrl) {
        this.topic = topic;
        this.producer = new SpKafkaProducer(kafkaUrl, topic);
        try {
            this.mqttClient = new MqttClient(mqttUrl,"relay-" + UUID.randomUUID(), new MemoryPersistence());
            this.mqttClient.setCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void start() throws MqttException {
        timeRelayStarted = System.currentTimeMillis();
        mqttClient.connect();
        mqttClient.subscribe(topic, qos);
    }

    public void close() throws MqttException {
        eventBuffer.clear();
        mqttClient.disconnect();
        producer.disconnect();
    }

    public String getTopic() {
        return topic;
    }

    public String getStats() {
        Gson gson = new Gson();
        Map<String, Object> m = new HashMap<>();
        m.put("id", topic);
        m.put("startedAt", timeRelayStarted);
        m.put("numRelayedEvents", numRelayedEvents);
        return gson.toJson(m);
    }


//    private KafkaProducer<Long, String> createKafkaProducer() {
//        Properties props = new Properties();
//        props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaUri);
//        // exactly-once
////        props.put("enable.idempotence", "true");
////        props.put("transactional.id", "prod-" + UUID.randomUUID());
//        props.put(ACKS_CONFIG, "all");
//        //props.put(RETRIES_CONFIG, 3);
//        props.put(REQUEST_TIMEOUT_MS_CONFIG, 2000);
//        props.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
//        // TODO: tune batch size and linger ms to suit use case of potential far away broker
//        // higher batch size/linger results in higher throughput
//        props.put(BATCH_SIZE_CONFIG, 16384);
//        props.put(LINGER_MS_CONFIG, 20);
//        props.put(BUFFER_MEMORY_CONFIG, 33554432);
//        props.put(KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
//        props.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//
//        return new KafkaProducer<Long, String>(props);
//    }

}
