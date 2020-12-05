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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamContainer;
import org.apache.streampipes.model.SpDataStreamRelay;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EventRelayManager {
    private final static Logger LOG = LoggerFactory.getLogger(EventRelayManager.class);

    private final String MQTT_PROTOCOL = "tcp://";
    private final String MQTT_HOST = "localhost";
    private final int MQTT_PORT = 1883;
    private final String COLON = ":";

    private final MqttKafkaBridge mqttKafkaBridge;
    private final String relayedTopic;

    private final MqttTransportProtocol sourceProtocol = new MqttTransportProtocol();
    private final KafkaTransportProtocol targetProtocol = new KafkaTransportProtocol();

    public EventRelayManager(InvocableStreamPipesEntity graph){
        this.relayedTopic = extractTopic(graph);
        this.mqttKafkaBridge = new MqttKafkaBridge();

        // TODO: only for debugging
        createDebugTransportProtocol();
    }

    public EventRelayManager(SpDataStreamRelay relay){
        this.relayedTopic = relay.getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
        this.mqttKafkaBridge = new MqttKafkaBridge();
        createTransportProtocol();
    }

    public EventRelayManager() {
        this.relayedTopic = "org.apache.streampipes.flowrate01";
        this.mqttKafkaBridge = new MqttKafkaBridge();
        createDebugTransportProtocol();
    }

    private String getMqttUrl() {
        return MQTT_PROTOCOL + MQTT_HOST + COLON + MQTT_PORT;
    }

    public void start() throws SpRuntimeException{
        LOG.info("Start event relay for topic: {}", relayedTopic);
        mqttKafkaBridge.subscribe(sourceProtocol, targetProtocol);
    }

    // TODO: only for debugging
    private void createDebugTransportProtocol() {
        sourceProtocol.setBrokerHostname(MQTT_HOST);
        sourceProtocol.setPort(MQTT_PORT);
        sourceProtocol.setTopicDefinition(new TopicDefinition(relayedTopic) {
            @Override
            public void setActualTopicName(String actualTopicName) {
                super.setActualTopicName(actualTopicName);
            }
        });
        targetProtocol.setBrokerHostname("localhost");
        targetProtocol.setKafkaPort(9094);
        targetProtocol.setTopicDefinition(new TopicDefinition(relayedTopic) {
            @Override
            public void setActualTopicName(String actualTopicName) {
                super.setActualTopicName(actualTopicName);
            }
        });
    }

    private void createTransportProtocol() {
        createDebugTransportProtocol();
        //TODO: Implement - currently only for debugging
    }


    public void stop() throws SpRuntimeException{
        LOG.info("Stop event relay for topic: {}", relayedTopic);
        mqttKafkaBridge.disconnect();
    }

    public String metrics() {
        LOG.info("Get current stats of event relay for topic: {}", relayedTopic);
        return mqttKafkaBridge.getMetrics();
    }

    public String getRelayedTopic() {
        return relayedTopic;
    }

    private String extractKafkaBroker(InvocableStreamPipesEntity graph) {
        EventGrounding eventGrounding = getEventGrounding(graph);

        String host = eventGrounding.getTransportProtocol().getBrokerHostname();
        int port = ((KafkaTransportProtocol) eventGrounding.getTransportProtocol()).getKafkaPort();

        return host + ":" + port;
    }

    private EventGrounding getEventGrounding(InvocableStreamPipesEntity graph) {
        return graph.getSupportedGrounding();
    }

    private static String extractTopic(InvocableStreamPipesEntity graph) {
        return graph.getSupportedGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
    }

    private List<SpDataStreamRelay> extractRelays(InvocableStreamPipesEntity graph){
        return ((DataProcessorInvocation) graph).getOutputStreamRelays();
    }
}
