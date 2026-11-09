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
import com.hivemq.client.mqtt.mqtt3.message.connect.Mqtt3Connect;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MqttConsumer extends MqttBase {

    private static final Logger LOG = LoggerFactory.getLogger(MqttConsumer.class);
    private static final int KEEP_ALIVE_SECONDS = 30;

    private final InternalEventProcessor<byte[]> eventProcessor;
    private final AtomicBoolean running;
    private Mqtt3AsyncClient client;
    private AtomicBoolean reconnectAllowed = new AtomicBoolean(false);
    private final Duration startupTimeout;

    public MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer) {
        this(mqttConfig, consumer, Duration.ofSeconds(30));
    }

    MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer, Duration startupTimeout) {
        super(mqttConfig);
        this.startupTimeout = startupTimeout;
        this.eventProcessor = consumer;
        this.running = new AtomicBoolean(false);
    }

    /**
     * Starts the MQTT consumer and subscribes to the configured topic.
     *
     * @throws AdapterException when the connection or subscription fails
     */
    public synchronized void start() throws AdapterException {
        if (!this.running.compareAndSet(false, true)) {
            return;
        }
        try {
            var allowReconnect = new AtomicBoolean(false);
            this.reconnectAllowed = allowReconnect;
            this.client = setupMqttClient(allowReconnect::get);
            await(this.client.connect(Mqtt3Connect.builder().keepAlive(KEEP_ALIVE_SECONDS).build()), "connect");
            subscribeToTopic(this.client);
            allowReconnect.set(true);
        } catch (InterruptedException e) {
            stop();
            Thread.currentThread().interrupt();
            throw new AdapterException("Interrupted while starting MQTT consumer", e);
        } catch (AdapterException e) {
            stop();
            throw e;
        } catch (Exception e) {
            stop();
            throw new AdapterException("Failed to start MQTT consumer", e);
        }
    }

    /**
     * Stops the MQTT consumer and disconnects from the broker.
     */
    public synchronized void stop() {
        this.running.set(false);
        this.reconnectAllowed.set(false);
        if (this.client == null) {
            return;
        }
        disconnectSafely();
        this.client = null;
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


    private <T> T await(CompletableFuture<T> operation, String phase)
            throws AdapterException, InterruptedException {
        try {
            return operation.get(startupTimeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new AdapterException("Timed out waiting for MQTT " + phase, e);
        } catch (ExecutionException e) {
            throw new AdapterException("MQTT " + phase + " failed", e.getCause());
        } finally {
            if (!operation.isDone()) {
                operation.cancel(true);
            }
        }
    }

    private void subscribeToTopic(Mqtt3AsyncClient client) throws AdapterException, InterruptedException {
        var subscription = Mqtt3Subscribe.builder()
                .topicFilter(super.mqttConfig.getTopic())
                .qos(MqttQos.AT_LEAST_ONCE)
                .build();
        await(client.subscribe(subscription, this::handleMessage), "subscription");
    }

    private void processPayload(byte[] payload) throws InterruptedException{
        eventProcessor.onEvent(payload);
    }

    private void disconnectSafely() {
        try {
            this.client.disconnect().orTimeout(5, TimeUnit.SECONDS).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    LOG.error("Error disconnecting from MQTT", throwable);
                }
            });
        } catch (RuntimeException e) {
            LOG.error("Error disconnecting from MQTT", e);
        }
    }
}
