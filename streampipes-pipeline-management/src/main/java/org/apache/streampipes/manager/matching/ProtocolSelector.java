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

package org.apache.streampipes.manager.matching;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.SpEdgeNodeProtocol;
import org.apache.streampipes.config.backend.SpProtocol;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.manager.util.TopicGenerator;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.List;
import java.util.Set;

public class ProtocolSelector extends GroundingSelector {

    private final String outputTopic;
    private final List<SpProtocol> prioritizedProtocols;
    private final SpEdgeNodeProtocol edgeNodeProtocol;

    public ProtocolSelector(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
        super(source, targets);
        if ((source instanceof DataProcessorInvocation
                && ((DataProcessorInvocation)source).getOutputStream() != null
                && ((DataProcessorInvocation)source).getOutputStream().getEventGrounding().getTransportProtocol()
                .getTopicDefinition().getActualTopicName() != null)){
            this.outputTopic = ((DataProcessorInvocation)source).getOutputStream().getEventGrounding().getTransportProtocol()
                    .getTopicDefinition().getActualTopicName();
        }else if ((source instanceof SpDataStream
                && ((SpDataStream)source).getEventGrounding() != null
                && ((SpDataStream)source).getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName() != null)){
            this.outputTopic = ((SpDataStream)source).getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
        }else{
            this.outputTopic = TopicGenerator.generateRandomTopic();
        }
        //this.outputTopic = TopicGenerator.generateRandomTopic();
        this.prioritizedProtocols = BackendConfig.INSTANCE
                .getMessagingSettings()
                .getPrioritizedProtocols();
        this.edgeNodeProtocol = BackendConfig.INSTANCE
                .getMessagingSettings()
                .getEdgeNodeProtocol();
    }

    public TransportProtocol getPreferredProtocol() {
        if (source instanceof SpDataStream) {
            return ((SpDataStream) source)
                    .getEventGrounding()
                    .getTransportProtocol();
        } else {
            if(sourceInvocableOnEdgeNode()) {
                // use edge node protocol
                if (matches(edgeNodeProtocol, MqttTransportProtocol.class) && supportsProtocol(MqttTransportProtocol.class)) {
                    if(matchesDeploymentTargets()) {
                       return new MqttTransportProtocol(getTargetNodeBrokerHost(), getTargetNodeBrokerPort(),
                               outputTopic);
                    } else {
                        MqttTransportProtocol tp = (MqttTransportProtocol)
                                ((InvocableStreamPipesEntity) source).getInputStreams().get(0).getEventGrounding().getTransportProtocol();
                        return new MqttTransportProtocol(tp.getBrokerHostname(), tp.getPort(), outputTopic);
                    }
                }
            } else {
                for(SpProtocol p: prioritizedProtocols) {
                    if (matches(p, KafkaTransportProtocol.class) && supportsProtocol(KafkaTransportProtocol.class)) {
                        return kafkaTransportProtocol();
                    } else if (matches(p, JmsTransportProtocol.class) && supportsProtocol(JmsTransportProtocol.class)) {
                        return jmsTransportProtocol();
                    } else if (matches(p, MqttTransportProtocol.class) && supportsProtocol(MqttTransportProtocol.class)) {
                        return mqttTransportProtocol();
                    } else {
                        throw new IllegalArgumentException("Transport protocol not found: " + p.getProtocolClass());
                    }
                }
            }
            throw new IllegalArgumentException("Could not get preferred transport protocol");
        }
    }

    private boolean sourceInvocableOnEdgeNode() {
        return ((InvocableStreamPipesEntity) source).getDeploymentTargetNodeId() != null &&
                !((InvocableStreamPipesEntity) source).getDeploymentTargetNodeId().equals("default");
    }

    private TransportProtocol mqttTransportProtocol() {
        if (matchesDeploymentTargets()) {
            MqttTransportProtocol tp = (MqttTransportProtocol)
                    ((InvocableStreamPipesEntity) source).getInputStreams().get(0).getEventGrounding().getTransportProtocol();
            return new MqttTransportProtocol(
                    tp.getBrokerHostname(),
                    tp.getPort(),
                    outputTopic);
        } else {
            return new MqttTransportProtocol(
                    BackendConfig.INSTANCE.getMqttHost(),
                    BackendConfig.INSTANCE.getMqttPort(),
                    outputTopic);
        }
    }

    private TransportProtocol jmsTransportProtocol() {
        return new JmsTransportProtocol(BackendConfig.INSTANCE.getJmsHost(),
                BackendConfig.INSTANCE.getJmsPort(),
                outputTopic);
    }

    private TransportProtocol kafkaTransportProtocol() {
        return new KafkaTransportProtocol(BackendConfig.INSTANCE.getKafkaHost(),
                BackendConfig.INSTANCE.getKafkaPort(),
                outputTopic,
                BackendConfig.INSTANCE.getZookeeperHost(),
                BackendConfig.INSTANCE.getZookeeperPort());
    }

    private <T extends TransportProtocol> boolean matches(SpProtocol p, Class<T> clazz) {
        return p.getProtocolClass().equals(clazz.getCanonicalName());
    }

    private <T extends TransportProtocol> boolean matches(SpEdgeNodeProtocol p, Class<T> clazz) {
        return p.getProtocolClass().equals(clazz.getCanonicalName());
    }

    private <T extends TransportProtocol> boolean supportsProtocol(Class<T> protocol) {
        List<InvocableStreamPipesEntity> elements = buildInvocables();
        return elements
                .stream()
                .allMatch(e -> e
                        .getSupportedGrounding()
                        .getTransportProtocols()
                        .stream()
                        .anyMatch(protocol::isInstance));
    }

    private boolean matchesDeploymentTargets() {
        if(((InvocableStreamPipesEntity) source).getDeploymentTargetNodeId() != null) {
            return targets
                    .stream()
                    .filter(t -> t.getDeploymentTargetNodeId() != null)
                    .allMatch(t -> t
                            .getDeploymentTargetNodeId().equals(
                                    ((InvocableStreamPipesEntity) source).getDeploymentTargetNodeId()));
        }
        return false;
    }

    private String getTargetNodeBrokerHost() {
        // TODO: no hardcoded route - only for testing

        return ConsulUtil.getValueForRoute(
                "sp/v1/node/org.apache.streampipes.node.controller/"
                        + ((InvocableStreamPipesEntity) source).getDeploymentTargetNodeHostname()
                        + "/config/SP_NODE_BROKER_CONTAINER_HOST", String.class);
    }

    private int getTargetNodeBrokerPort() {
        // TODO: no hardcoded route - only for testing
        return ConsulUtil.getValueForRoute(
                "sp/v1/node/org.apache.streampipes.node.controller/"
                        + ((InvocableStreamPipesEntity) source).getDeploymentTargetNodeHostname()
                        + "/config/SP_NODE_BROKER_CONTAINER_PORT", Integer.class);
    }
}
