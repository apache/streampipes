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
package org.apache.streampipes.extensions.connectors.mqtt.shared;

import org.apache.streampipes.messaging.InternalEventProcessor;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttConsumer extends MqttBase implements Runnable {

    private final InternalEventProcessor<byte[]> consumer;
    private boolean running;
    private int maxElementsToReceive = -1;
    private int messageCount = 0;

    private static final Logger LOG = LoggerFactory.getLogger(MqttConsumer.class);

    public MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer) {
        super(mqttConfig);
        this.consumer = consumer;
    }

    public MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer, int maxElementsToReceive) {
        this(mqttConfig, consumer);
        this.maxElementsToReceive = maxElementsToReceive;
    }

    @Override
    public void run() {
        this.running = true;
        try {
            MQTT mqtt = super.setupMqttClient();
            BlockingConnection connection = mqtt.blockingConnection();
            connection.connect();
            subscribeToTopic(connection);
            processMessages(connection);
            connection.disconnect();
        } catch (Exception e) {
            LOG.error("Error in MQTT consumer: ", e);
            throw new RuntimeException("Error when receiving data from MQTT", e);
        }
    }

    private void processMessages(BlockingConnection connection) throws Exception {
        while (running && (maxElementsToReceive == -1 || messageCount < maxElementsToReceive)) {
            Message message = connection.receive();
            byte[] payload = message.getPayload();
            consumer.onEvent(payload);
            message.ack();
            messageCount++;
        }
    }

    private void subscribeToTopic(BlockingConnection connection) throws Exception {
        Topic[] topics = { new Topic(super.mqttConfig.getTopic(), QoS.AT_LEAST_ONCE) };
        connection.subscribe(topics);
    }

    public void close() {
        this.running = false;
    }

    public Integer getMessageCount() {
        return messageCount;
    }
}