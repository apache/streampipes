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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.messaging.InternalEventProcessor;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class MqttConsumer extends MqttBase {

    private static final Logger LOG = LoggerFactory.getLogger(MqttConsumer.class);
    private static final int KEEP_ALIVE_SECONDS = 30;

    private final InternalEventProcessor<byte[]> eventProcessor;
    private final AtomicBoolean running;
    private Mqtt3AsyncClient client;

    public MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer) {
        super(mqttConfig);
        this.eventProcessor = consumer;
        this.running = new AtomicBoolean(false);
    }

    /**
     * Starts the MQTT consumer and subscribes to the configured topic.
     *
     * @throws AdapterException when the connection or subscription fails
     */
    public void start() throws AdapterException {
        if (!this.running.compareAndSet(false, true)) {
            return;
        }
        try {
            this.client = connectClient();
            subscribeToTopic(this.client);
        } catch (InterruptedException e) {
            this.running.set(false);
            Thread.currentThread().interrupt();
            throw new AdapterException("Interrupted while starting MQTT consumer", e);
        } catch (AdapterException e) {
            this.running.set(false);
            throw e;
        }
    }

    /**
     * Stops the MQTT consumer and disconnects from the broker.
     */
    public void stop() {
        this.running.set(false);
        if (this.client == null) {
            return;
        }
        disconnectSafely();
    }

    private void handleMessage(Mqtt3Publish publish) {
        if (!this.running.get()) {
            return;
        }
        try {
            processPayload(publish.getPayloadAsBytes());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Interrupted while processing MQTT message", e);
        } catch (RuntimeException e) {
            LOG.error("Error processing MQTT message", e);
        }
    }


    private Mqtt3AsyncClient connectClient() throws AdapterException, InterruptedException {
        try {
            var mqttClient = super.setupMqttClient();
            mqttClient.connectWith()
                      .keepAlive(KEEP_ALIVE_SECONDS)
                      .send()
                      .whenComplete(this::logConnect)
                      .get();
            return mqttClient;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new AdapterException("Error while connecting to MQTT broker", e);
        }
    }

    private void logConnect(Mqtt3ConnAck cAck, Throwable throwable) {
        if (throwable != null) {
            LOG.error("MQTT connection failed", throwable);
        } else {
            LOG.info("MQTT connection established");
        }
    }

    private void subscribeToTopic(Mqtt3AsyncClient client) throws AdapterException, InterruptedException {
        CountDownLatch subscribed = new CountDownLatch(1);
        client.subscribeWith()
              .topicFilter(super.mqttConfig.getTopic())
              .qos(MqttQos.AT_LEAST_ONCE)
              .callback(this::handleMessage)
              .send()
              .whenComplete((subAck, throwable) -> {
                  logSubscribe(throwable);
                  subscribed.countDown();
              });
        try {
            subscribed.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private void logSubscribe(Throwable throwable) {
        if (throwable != null) {
            LOG.error("MQTT subscribe failed", throwable);
        } else {
            LOG.info("Successfully subscribed to topic {}", super.mqttConfig.getTopic());
        }
    }

    private void processPayload(byte[] payload) throws InterruptedException{
        eventProcessor.onEvent(payload);
    }

    private void disconnectSafely() {
        try {
            this.client.disconnect().whenComplete((result, throwable) -> {
                if (throwable != null) {
                    LOG.error("Error disconnecting from MQTT", throwable);
                }
            });
        } catch (RuntimeException e) {
            LOG.error("Error disconnecting from MQTT", e);
        }
    }
}
