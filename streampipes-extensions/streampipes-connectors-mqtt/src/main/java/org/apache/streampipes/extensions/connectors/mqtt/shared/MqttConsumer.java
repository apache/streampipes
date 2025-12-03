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

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class MqttConsumer extends MqttBase implements Runnable {

    private final InternalEventProcessor<byte[]> consumer;
    private boolean running;
    private int maxElementsToReceive = -1;
    private int messageCount = 0;

    private Mqtt3AsyncClient client;

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
            this.client = super.setupMqttClient();
            client.connectWith()
                    .keepAlive(30)
                    .send()
                    .whenComplete((cAck, throwable) -> {
                        if (throwable != null) {
                            LOG.error("MQTT connection failed", throwable);
                        } else {
                            LOG.info("MQTT connection established");
                        }
                    })
                    .get();

            subscribe(client);

        } catch (Exception e) {
            LOG.error("Error in MQTT consumer: ", e);
            throw new RuntimeException("Error when receiving data from MQTT", e);
        }
    }

    private void subscribe(Mqtt3AsyncClient client) throws Exception {

        CountDownLatch subscribed = new CountDownLatch(1);

        client.subscribeWith()
                .topicFilter(super.mqttConfig.getTopic())
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(this::handleMessage)
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        LOG.error("MQTT subscribe failed", throwable);
                    } else {
                        LOG.info("Successfully subscribed to topic {}", super.mqttConfig.getTopic());
                    }
                    subscribed.countDown();
                });

        subscribed.await();
    }

    private void handleMessage(Mqtt3Publish publish) {
        if (!this.running) {
            return;
        }

        try {
            byte[] payload = publish.getPayloadAsBytes();
            consumer.onEvent(payload);
            messageCount++;

            if (maxElementsToReceive != -1 && messageCount >= maxElementsToReceive) {
                LOG.info("Max elements ({}) received. Stopping consumer.", maxElementsToReceive);
                this.running = false;
            }

        } catch (Exception e) {
            LOG.error("Error processing MQTT message", e);
        }
    }

    public void close() {
        this.running = false;
        try {

            this.client.disconnect().get();

        } catch (InterruptedException e) {
            LOG.error("Error disconnecting from MQTT due to thread interruption", e);
        } catch (ExecutionException e) {
            LOG.error("Error disconnecting from MQTT", e.getCause());
        }

    }

    public Integer getMessageCount() {
        return messageCount;
    }
}