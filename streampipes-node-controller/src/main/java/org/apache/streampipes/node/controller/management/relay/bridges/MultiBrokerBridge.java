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
package org.apache.streampipes.node.controller.management.relay.bridges;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.EventRelay;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.eventrelay.metrics.RelayMetrics;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.sdk.helpers.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class MultiBrokerBridge<T1 extends TransportProtocol, T2 extends TransportProtocol> implements EventRelay {
    private final static Logger LOG = LoggerFactory.getLogger(MultiBrokerBridge.class);

    protected T1 sourceProtocol;
    protected T2 targetProtocol;
    protected RelayMetrics metrics;

    private final EventConsumer<T1> consumer;
    private final EventProducer<T2> producer;
    private final EventRelayStrategy eventRelayStrategy;
    private final ArrayList<byte[]> eventBuffer = new ArrayList<>();

    private final int EVENT_BUFFER_SIZE = NodeConfiguration.getRelayEventBufferSize();
    private final Tuple3<String, Integer, String> relayInfo;
    private boolean isBuffering = false;

    public MultiBrokerBridge(TransportProtocol sourceProtocol, TransportProtocol targetProtocol,
                             String eventRelayStrategy, Supplier<EventConsumer<T1>> consumerSupplier,
                             Supplier<EventProducer<T2>> producerSupplier, Class<T1> sourceProtocolClass,
                             Class<T2> targetProtocolClass) {

        this.sourceProtocol = sourceProtocolClass.cast(sourceProtocol);
        this.targetProtocol = targetProtocolClass.cast(targetProtocol);
        this.eventRelayStrategy = EventRelayStrategy.valueOf(eventRelayStrategy.toUpperCase());
        this.consumer = consumerSupplier.get();
        this.producer = producerSupplier.get();

        this.relayInfo = relayInfo();
        this.metrics = new RelayMetrics(relayInfo.c, sourceProtocol, targetProtocol, eventRelayStrategy);

        if ("true".equals(System.getenv("SP_DEBUG"))) {
            modifyProtocolForDebugging();
        }
    }

    protected abstract void modifyProtocolForDebugging();

    @Override
    public void start() throws SpRuntimeException {
        LOG.info("Start event relay to broker={}:{}, topic={}", relayInfo.a, relayInfo.b, relayInfo.c);
        this.producer.connect(this.targetProtocol);
        this.consumer.connect(this.sourceProtocol, this::publish);
    }

    @Override
    public void publish(byte[] event) {
        // check if target broker can be reached
        if (isTargetBrokerAlive()) {

            if (eventRelayStrategy == EventRelayStrategy.BUFFER) {
                if (eventBuffer.isEmpty()) {
                    // send events to upstream
                    producer.publish(event);
                    metrics.increaseNumRelayedEvents();
                } else {
                    // TODO: send buffered event should run independent of callback
                    // send buffered events & clear buffer
                    LOG.info("Re-established connection to broker={}:{}. Resent buffered events for topic={} " +
                            "(buffer_size={}, num_dropped_events={})", relayInfo.a, relayInfo.b,
                            relayInfo.c, eventBuffer.size(), metrics.getNumDroppedEvents());

                    // add current event from callback
                    eventBuffer.add(event);
                    eventBuffer.forEach(e -> {
                        try{
                            producer.publish(e);
                            metrics.increaseNumRelayedEvents();
                        }  catch (Exception ex) {
                            LOG.error(ex.toString());
                        }
                    });
                    eventBuffer.clear();
                    metrics.clearNumDroppedEvents();
                    isBuffering = false;
                }
            } else if (eventRelayStrategy == EventRelayStrategy.PURGE) {
                // send events to upstream
                producer.publish(event);
                metrics.increaseNumRelayedEvents();
            }
        } else {
            //
            if (eventRelayStrategy == EventRelayStrategy.BUFFER) {
                // add event to buffer
                if(!isBuffering) {
                    LOG.info("Connection issue to broker={}:{}. Buffer events for topic={}", relayInfo.a, relayInfo.b
                            , relayInfo.c);
                    isBuffering = true;
                }

                if (eventBuffer.size() != EVENT_BUFFER_SIZE) {
                    eventBuffer.add(event);
                } else {
                    // evict oldest event
                    eventBuffer.remove(0);
                    metrics.increaseNumDroppedEvents();
                }
            } else if (eventRelayStrategy == EventRelayStrategy.PURGE) {
                LOG.info("Connection issue to broker={}:{}. Purge events for topic={}", relayInfo.a, relayInfo.b,
                        relayInfo.c);
            }
        }
    }

    @Override
    public void stop() throws SpRuntimeException {
        LOG.info("Stop event relay to broker={}:{}, topic={}", relayInfo.a, relayInfo.b, relayInfo.c);
        this.consumer.disconnect();
        this.producer.disconnect();
    }

    @Override
    public Boolean sourceConnected() {
        return consumer.isConnected();
    }

    @Override
    public Boolean targetConnected() {
        return producer.isConnected();
    }

    public RelayMetrics getRelayMerics() {
        return metrics;
    }

    protected Tuple3<String, Integer, String> relayInfo() {
        String host = this.targetProtocol.getBrokerHostname();
        String topic = this.targetProtocol.getTopicDefinition().getActualTopicName();
        int port = getTransportProtocolPort(this.targetProtocol);
        return new Tuple3<>( host, port, topic);
    }

    protected boolean isTargetBrokerAlive() {
        boolean isAlive = true;
        String host = this.targetProtocol.getBrokerHostname();
        int port = getTransportProtocolPort(this.targetProtocol);
        try {
            InetSocketAddress sa = new InetSocketAddress(host, port);
            Socket ss = new Socket();
            ss.connect(sa, 500);
            ss.close();
        } catch(Exception e) {
            isAlive = false;
        }
        return isAlive;
    }

    protected int getTransportProtocolPort(TransportProtocol tp) {
        if (tp instanceof MqttTransportProtocol) {
            return ((MqttTransportProtocol) tp).getPort();
        } else if (tp instanceof JmsTransportProtocol) {
            return ((JmsTransportProtocol) tp).getPort();
        } else if (tp instanceof KafkaTransportProtocol) {
            return ((KafkaTransportProtocol) tp).getKafkaPort();
        }
        throw new SpRuntimeException("Transport protocol not valid");
    }
}
