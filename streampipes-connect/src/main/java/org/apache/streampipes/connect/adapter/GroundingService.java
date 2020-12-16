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

package org.apache.streampipes.connect.adapter;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.SpEdgeNodeProtocol;
import org.apache.streampipes.config.backend.SpProtocol;
import org.apache.streampipes.connect.adapter.util.TransportFormatGenerator;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.*;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.Collections;
import java.util.UUID;

public class GroundingService {

    private static final String ENV_NODE_CONTROLLER_ID_KEY = "SP_NODE_CONTROLLER_ID";
    private static final String TOPIC_PREFIX = "org.apache.streampipes.connect.";

    public static String extractBroker(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding = getEventGrounding(adapterDescription);

        SpProtocol prioritizedProtocol =
                BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedProtocols().get(0);

        String host = eventGrounding.getTransportProtocol().getBrokerHostname();
        int port = 0;

        if (isPrioritized(prioritizedProtocol, JmsTransportProtocol.class)) {
            port = ((JmsTransportProtocol) eventGrounding.getTransportProtocol()).getPort();
        } else if (isPrioritized(prioritizedProtocol, KafkaTransportProtocol.class)){
            port = ((KafkaTransportProtocol) eventGrounding.getTransportProtocol()).getKafkaPort();
        } else if (isPrioritized(prioritizedProtocol, MqttTransportProtocol.class)) {
            port = ((MqttTransportProtocol) eventGrounding.getTransportProtocol()).getPort();
        }

        return host + ":" + port;
    }

    public static String extractTopic(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding = getEventGrounding(adapterDescription);
        return eventGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();
    }

    private static EventGrounding getEventGrounding(AdapterDescription adapterDescription) {
        EventGrounding eventGrounding = null;

        if (adapterDescription instanceof SpecificAdapterSetDescription) {
            eventGrounding = ((SpecificAdapterSetDescription) adapterDescription).getDataSet().getEventGrounding();
        } else if (adapterDescription instanceof GenericAdapterSetDescription) {
            eventGrounding = ((GenericAdapterSetDescription) adapterDescription).getDataSet().getEventGrounding();
        } else {
            eventGrounding = adapterDescription.getEventGrounding();
        }

        return eventGrounding;
    }

    public static EventGrounding createEventGrounding(AdapterDescription adapterDescription) {

        EventGrounding eventGrounding = new EventGrounding();

        String topic = TOPIC_PREFIX + UUID.randomUUID().toString();
        TopicDefinition topicDefinition = new SimpleTopicDefinition(topic);

        if (adapterDescription.getDeploymentTargetNodeId() != null) {
            SpEdgeNodeProtocol edgeNodeProtocol = BackendConfig.INSTANCE.getMessagingSettings().getEdgeNodeProtocol();

            if (isEdgeProtocol(edgeNodeProtocol, MqttTransportProtocol.class)) {

                if (adapterDescription instanceof GenericAdapterDescription) {
                    String deploymentTargetNodeHostname = ((GenericAdapterDescription) adapterDescription)
                            .getProtocolDescription()
                            .getDeploymentTargetNodeHostname();

                    eventGrounding.setTransportProtocol(
                            makeMqttTransportProtocol(
                                    getTargetNodeBrokerHost(deploymentTargetNodeHostname),
                                    getTargetNodeBrokerPort(deploymentTargetNodeHostname),
                                    topicDefinition));
                } else {
                    String deploymentTargetNodeHostname = adapterDescription.getDeploymentTargetNodeHostname();

                    eventGrounding.setTransportProtocol(
                            makeMqttTransportProtocol(
                                    getTargetNodeBrokerHost(deploymentTargetNodeHostname),
                                    getTargetNodeBrokerPort(deploymentTargetNodeHostname),
                                    topicDefinition));
                }
            }

        } else {
            SpProtocol prioritizedProtocol =
                BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedProtocols().get(0);

            if (isPrioritized(prioritizedProtocol, JmsTransportProtocol.class)) {
                eventGrounding.setTransportProtocol(
                        makeJmsTransportProtocol(
                                BackendConfig.INSTANCE.getJmsHost(),
                                BackendConfig.INSTANCE.getJmsPort(),
                                topicDefinition));
            } else if (isPrioritized(prioritizedProtocol, KafkaTransportProtocol.class)){
                eventGrounding.setTransportProtocol(
                        makeKafkaTransportProtocol(
                                BackendConfig.INSTANCE.getKafkaHost(),
                                BackendConfig.INSTANCE.getKafkaPort(),
                                topicDefinition));
            } else if (isPrioritized(prioritizedProtocol, MqttTransportProtocol.class)) {
                eventGrounding.setTransportProtocol(
                        makeMqttTransportProtocol(
                                BackendConfig.INSTANCE.getMqttHost(),
                                BackendConfig.INSTANCE.getMqttPort(),
                                topicDefinition));
            }
        }

        eventGrounding.setTransportFormats(Collections
                .singletonList(TransportFormatGenerator.getTransportFormat()));

        return eventGrounding;
    }

    public static Boolean isPrioritized(SpProtocol prioritizedProtocol,
                                         Class<?> protocolClass) {
        return prioritizedProtocol.getProtocolClass().equals(protocolClass.getCanonicalName());
    }

    public static Boolean isEdgeProtocol(SpEdgeNodeProtocol edgeNodeProtocol,
                                        Class<?> protocolClass) {
        return edgeNodeProtocol.getProtocolClass().equals(protocolClass.getCanonicalName());
    }

    private static JmsTransportProtocol makeJmsTransportProtocol(String hostname, Integer port,
                                                          TopicDefinition topicDefinition) {
        JmsTransportProtocol transportProtocol = new JmsTransportProtocol();
        transportProtocol.setPort(port);
        fillTransportProtocol(transportProtocol, hostname, topicDefinition);

        return transportProtocol;
    }

    private static MqttTransportProtocol makeMqttTransportProtocol(String hostname, Integer port,
                                                                 TopicDefinition topicDefinition) {
        MqttTransportProtocol transportProtocol = new MqttTransportProtocol();
        transportProtocol.setPort(port);
        fillTransportProtocol(transportProtocol, hostname, topicDefinition);

        return transportProtocol;
    }

    private static KafkaTransportProtocol makeKafkaTransportProtocol(String hostname, Integer port,
                                                              TopicDefinition topicDefinition) {
        KafkaTransportProtocol transportProtocol = new KafkaTransportProtocol();
        transportProtocol.setKafkaPort(port);
        fillTransportProtocol(transportProtocol, hostname, topicDefinition);

        return transportProtocol;
    }

    private static void fillTransportProtocol(TransportProtocol protocol, String hostname,
                                       TopicDefinition topicDefinition) {
        protocol.setBrokerHostname(hostname);
        protocol.setTopicDefinition(topicDefinition);
    }

    private static String getTargetNodeBrokerHost(String deploymentTargetNodeHostname) {
        // TODO: no hardcoded route - only for testing
        return ConsulUtil.getValueForRoute(
                "sp/v1/node/org.apache.streampipes.node.controller/"
                        + deploymentTargetNodeHostname
                        + "/config/SP_NODE_BROKER_CONTAINER_HOST", String.class);
    }

    private static int getTargetNodeBrokerPort(String deploymentTargetNodeHostname) {
        // TODO: no hardcoded route - only for testing
        return ConsulUtil.getValueForRoute(
                "sp/v1/node/org.apache.streampipes.node.controller/"
                        + deploymentTargetNodeHostname
                        + "/config/SP_NODE_BROKER_CONTAINER_PORT", Integer.class);
    }
}
