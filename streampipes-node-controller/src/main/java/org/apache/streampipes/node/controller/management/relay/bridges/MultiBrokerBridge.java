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
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

public abstract class MultiBrokerBridge<T1 extends TransportProtocol, T2 extends TransportProtocol> implements EventRelay {
    private final static Logger LOG = LoggerFactory.getLogger(MultiBrokerBridge.class);

    protected T1 sourceProtocol;
    protected T2 targetProtocol;
    protected RelayMetrics metrics;

    private final EventConsumer<T1> consumer;
    private final EventProducer<T2> producer;
    private final EventRelayStrategy eventRelayStrategy;
    private final Queue<byte[]> eventBuffer = new ArrayBlockingQueue<>(NodeConfiguration.getEventRelayBufferSize());
    //private CircularFifoBuffer buffer = new CircularFifoBuffer(NodeConfiguration.getEventRelayBufferSize());

    private final int EVENT_BUFFER_SIZE = NodeConfiguration.getEventRelayBufferSize();
    private final Tuple3<String, Integer, String> relayInfo;
    private boolean isBuffering = false;
    private boolean targetAlive = true;

    public MultiBrokerBridge(TransportProtocol sourceProtocol, TransportProtocol targetProtocol,
                             String eventRelayStrategy, Supplier<EventConsumer<T1>> consumerSupplier,
                             Supplier<EventProducer<T2>> producerSupplier, Class<T1> sourceProtocolClass,
                             Class<T2> targetProtocolClass) {

        this.sourceProtocol = sourceProtocolClass.cast(sourceProtocol);
        this.targetProtocol = targetProtocolClass.cast(targetProtocol);
        this.eventRelayStrategy = EventRelayStrategy.valueOf(eventRelayStrategy.toUpperCase());
        this.consumer = consumerSupplier.get();
        this.producer = producerSupplier.get();

        if ("true".equals(System.getenv("SP_DEBUG"))) {
            modifyProtocolForDebugging();
        }

        this.relayInfo = relayInfo();
        this.metrics = new RelayMetrics(relayInfo.c, sourceProtocol, targetProtocol, eventRelayStrategy);
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
        if (NodeConfiguration.isEventRelayTargetBrokerCheckEnabled()) {
            publishWithCheck(event);
        } else {
            publishWithoutCheck(event);
        }
    }

    private void publishWithCheck(byte[] event) {

        if (targetAlive && !isTargetBrokerAlive()){
            targetAlive = false;
            startAliveThread();
        }
        // check if target broker can be reached
        if (targetAlive) {

            if(!eventBuffer.isEmpty()){
                publishBufferedEvents(eventBuffer);
                publishEvent(event);
            }else{
                publishEvent(event);
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
                bufferEvent(event);
            } else if (eventRelayStrategy == EventRelayStrategy.PURGE) {
                purgeEvent(event);
            }
        }
    }

    //TODO: Delete after testing
    private void publishWithoutCheck(byte[] event) {
        producer.publish(event);
        metrics.increaseNumRelayedEvents();
    }

    private void publishEvent(byte[] event){
        producer.publish(event);
        metrics.increaseNumRelayedEvents();
    }

    private synchronized void bufferEvent(byte[] event){
        if (!eventBuffer.offer(event)){
            eventBuffer.poll();
            eventBuffer.offer(event);
            metrics.increaseNumDroppedEvents();
        }
    }

    private void purgeEvent(byte[] event){
        LOG.info("Connection issue to broker={}:{}. Purge events for topic={}", relayInfo.a, relayInfo.b,
                relayInfo.c);
    }

    private synchronized void publishBufferedEvents(Queue<byte[]> eventBuffer){
        LOG.info("Re-established connection to broker={}:{}. Resent buffered events for topic={} " +
                        "(buffer_size={}, num_dropped_events={})", relayInfo.a, relayInfo.b,
                relayInfo.c, eventBuffer.size(), metrics.getNumDroppedEvents());

        // add current event from callback
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

    public RelayMetrics getRelayMetrics() {
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

    private void startAliveThread(){
        LOG.info("Alive Thread started.");
        Thread th = new Thread(){
            public synchronized void run(){
                boolean isAlive = false;
                while (!isAlive){
                    if(isTargetBrokerAlive())
                        isAlive = true;
                }
                publishBufferedEvents(eventBuffer);
                targetAlive = true;
            }
        };
        th.start();
    }
}
