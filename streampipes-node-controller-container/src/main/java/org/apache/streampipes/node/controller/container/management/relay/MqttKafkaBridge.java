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
package org.apache.streampipes.node.controller.container.management.relay;

import org.apache.kafka.common.KafkaException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventRelay;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MqttKafkaBridge extends AbstractMqttKafkaConnector implements EventRelay<MqttTransportProtocol, KafkaTransportProtocol> {
    private final static Logger LOG = LoggerFactory.getLogger(MqttKafkaBridge.class);

    private final int QOS_LEVEL= 1;
    private final int EVENT_BUFFER_SIZE = NodeControllerConfig.INSTANCE.getEventBufferSize();

    private final ArrayList<byte[]> eventBuffer = new ArrayList<>();

    public boolean isBuffering = false;

    public MqttKafkaBridge() {
    }

    @Override
    public void subscribe(MqttTransportProtocol sourceProtocol, KafkaTransportProtocol targetProtocol) throws SpRuntimeException {
        try {
            this.createBrokerConnection(sourceProtocol, targetProtocol);
            consumer.subscribe(extractTopic(sourceProtocol), QOS_LEVEL, (topic, msg) -> publish(msg.getPayload()));
        } catch (Exception e) {
            throw new SpRuntimeException(e);
        }
    }

    @Override
    public void publish(byte[] event) {
        // check if Kafka host is alive
        if(isKafkaAlive()) {
            if (eventBuffer.isEmpty()) {
                // send events to upstream
                try{
                    producer.publish(event);
                    metrics.increaseNumRelayedEvents();
                }  catch( KafkaException e ) {
                    LOG.error(e.toString());
                }
            }
            else {
                // TODO: send buffered event should run independent of callback
                // send buffered events & clear buffer
                LOG.info("Connection re-established. Send events from temporary buffer for " +
                        "topic={} (send={}, dropped={})", relay.getTopic(), eventBuffer.size(), metrics.getNumDroppedEvents());

                // add current event from callback
                eventBuffer.add(event);
                eventBuffer.forEach(e -> {
                    try{
                        producer.publish(e);
                        metrics.increaseNumRelayedEvents();
                    }  catch( KafkaException ke ) {
                        LOG.error(ke.toString());
                    }
                });
                eventBuffer.clear();
                metrics.clearNumDroppedEvents();
                isBuffering = false;
            }
        }
        else {
            // add event to buffer
            if(!isBuffering) {
                LOG.info("Connection issue. Temporarily store event in buffer for topic={}", relay.getTopic());
                isBuffering = true;
            }

            if (eventBuffer.size() != EVENT_BUFFER_SIZE) {
                eventBuffer.add(event);
            }
            else {
                // evict oldest event
                eventBuffer.remove(0);
                metrics.increaseNumDroppedEvents();
            }
        }
    }

    @Override
    public void disconnect() throws SpRuntimeException {
        try {
            this.consumer.disconnect();
            this.producer.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        } finally {
            this.isMqttConnected = false;
            this.isKafkaConnected = false;
        }
    }

    @Override
    public Boolean isSourceConnected() {
        return this.isMqttConnected;
    }

    @Override
    public Boolean isTargetConnected() {
        return this.isKafkaConnected;
    }
}
