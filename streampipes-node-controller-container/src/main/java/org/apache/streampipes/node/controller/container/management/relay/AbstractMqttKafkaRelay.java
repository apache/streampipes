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

import org.apache.kafka.common.KafkaException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;

public class AbstractMqttKafkaRelay extends AbstractMqttKafkaBridge {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractMqttKafkaRelay.class);

    private final String kafkaUrl;

    public AbstractMqttKafkaRelay(String topic, String mqttUrl, String kafkaUrl) {
        super(topic, mqttUrl, kafkaUrl);
        this.kafkaUrl = kafkaUrl;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        LOG.info("Connection lost because: {}", throwable.toString());
        System.exit(1);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        byte[] event = mqttMessage.getPayload();

        // Check if Kafka is reachable
        // If Kafka is not reachable buffer events in temporary record buffer
        // TODO: isServiceReachable should run independant of callback
        if(isKafkaAlive()) {
            if (eventBuffer.isEmpty()) {
                // send events to upstream
                try{
                    producer.publish(event);
                    numRelayedEvents++;
                }  catch( KafkaException e ) {
                    LOG.error("Kafka exception: {}", e);
                }
                //this.totalRelayedEventsSize = this.totalRelayedEventsSize + event.getBytes(StandardCharsets.UTF_8).length;
            }
            else {
                // TODO: send buffered event should run independant of callback
                // send buffered events & clear buffer
                LOG.info("Connection re-established. Send events from temporary buffer for opic={} (send={}, dropped={})", getTopic(), eventBuffer.size(), numDroppedEvents);

                // add current event from callback
                eventBuffer.add(event);
                eventBuffer.forEach(e -> {
                    try{
                        producer.publish(e);
                        numRelayedEvents++;
                    }  catch( KafkaException ke ) {
                        LOG.error("Kafka exception: {}", ke);
                    }
                });
                eventBuffer.clear();
                numDroppedEvents = 0;
                buffering = false;
            }
        }
        else {
            // add event to buffer
            if(!buffering) {
                LOG.info("Connection issue. Temporarily store event in buffer for topic={}", getTopic());
                buffering = true;
            }

            if (eventBuffer.size() != EVENT_BUFFER_SIZE) {
                eventBuffer.add(event);
            }
            else {
                // evict oldest event
                eventBuffer.remove(0);
                numDroppedEvents++;
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public boolean isKafkaAlive() {
        String ip = kafkaUrl.split(":")[0];
        int port = Integer.parseInt(kafkaUrl.split(":")[1]);
        boolean connectivity = true;
        try{
            InetSocketAddress sa = new InetSocketAddress(ip, port);
            Socket ss = new Socket();
            ss.connect(sa, 50);
            ss.close();
        }catch(Exception e) {
            connectivity = false;
        }
        return connectivity;
    }

}
