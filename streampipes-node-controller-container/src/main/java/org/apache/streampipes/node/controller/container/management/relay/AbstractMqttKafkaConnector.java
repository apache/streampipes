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
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.node.controller.container.management.relay.model.Metrics;
import org.apache.streampipes.node.controller.container.management.relay.model.Relay;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

public abstract class AbstractMqttKafkaConnector {

    private final String MQTT_PROTOCOL = "tcp://";
    private final String COLON = ":";
    private final String MQTT_CLIENT_ID = String.valueOf(UUID.randomUUID());
    private final int SOCKET_TIMEOUT = 500;

    protected SpKafkaProducer producer;
    protected IMqttClient consumer;
    protected Boolean isMqttConnected = false;
    protected Boolean isKafkaConnected = false;

    protected Metrics metrics;
    protected Relay relay;

    protected void createBrokerConnection(MqttTransportProtocol sourceProtocol, KafkaTransportProtocol targetProtocol) {
        String sourceUrl = makeMqttBrokerUrl(sourceProtocol);
        String targetUrl = makeKafkaBrokerUrl(targetProtocol);
        String topic = extractTopic(sourceProtocol);

        this.metrics = new Metrics(topic);
        this.relay = new Relay(sourceProtocol.getBrokerHostname(), sourceProtocol.getPort(), targetProtocol.getBrokerHostname(), targetProtocol.getKafkaPort(), topic);
        try {
            this.producer = new SpKafkaProducer(targetUrl, topic);
            this.producer.connect(targetProtocol);
            this.consumer = new MqttClient(sourceUrl, MQTT_CLIENT_ID, new MemoryPersistence());
            this.consumer.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        this.isKafkaConnected = true;
        this.isMqttConnected = true;
    }

    public String extractTopic(MqttTransportProtocol mqttProtocol) {
        return mqttProtocol.getTopicDefinition().getActualTopicName();
    }

    private String makeMqttBrokerUrl(MqttTransportProtocol mqttProtocol) {
        return MQTT_PROTOCOL + mqttProtocol.getBrokerHostname() + COLON + mqttProtocol.getPort();
    }

    private String makeKafkaBrokerUrl(KafkaTransportProtocol kafkaProtocol) {
        return kafkaProtocol.getBrokerHostname() + COLON + kafkaProtocol.getKafkaPort();
    }

    public String getMetrics() {
        return new Gson().toJson(metrics);
    }

    public boolean isKafkaAlive() {
        boolean isAlive = true;
        try{
            InetSocketAddress sa = new InetSocketAddress(relay.getTargetHost(), relay.getTargetPort());
            Socket ss = new Socket();
            ss.connect(sa, SOCKET_TIMEOUT);
            ss.close();
        }catch(Exception e) {
            isAlive = false;
        }
        return isAlive;
    }
}
