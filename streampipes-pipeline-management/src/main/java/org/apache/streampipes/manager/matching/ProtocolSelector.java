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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.SpEdgeNodeProtocol;
import org.apache.streampipes.config.backend.SpProtocol;
import org.apache.streampipes.manager.util.TopicGenerator;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.management.NodeManagement;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProtocolSelector extends GroundingSelector {

    private final String outputTopic;
    private final List<SpProtocol> prioritizedProtocols;
    private final List<SpEdgeNodeProtocol> prioritizedEdgeProtocols;

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
        this.prioritizedEdgeProtocols = BackendConfig.INSTANCE
                .getMessagingSettings()
                .getPrioritizedEdgeProtocols();
    }

    public TransportProtocol getPreferredProtocol() {
        if (source instanceof SpDataStream) {
            return ((SpDataStream) source)
                    .getEventGrounding()
                    .getTransportProtocol();
        } else {
            if(sourceInvocableManagedByNodeController()) {
                InvocableStreamPipesEntity invocableStreamPipesEntity = (InvocableStreamPipesEntity ) source;

                boolean isEdgeOrFog = NodeManagement.getInstance().getAllNodes().stream()
                        .filter(n -> n.getNodeControllerId()
                                .equals(invocableStreamPipesEntity.getDeploymentTargetNodeId()))
                        .anyMatch(n -> n.getStaticNodeMetadata().getType().equals("edge") ||
                                n.getStaticNodeMetadata().getType().equals("fog"));

                if (isEdgeOrFog) {
                    for (SpEdgeNodeProtocol p: prioritizedEdgeProtocols) {
                        // use edge node protocol
                        if (matches(p, MqttTransportProtocol.class) && supportsProtocol(MqttTransportProtocol.class)) {
                            return mqttTransportProtocolForEdge();
                        } else if (matches(p, KafkaTransportProtocol.class) && supportsProtocol(KafkaTransportProtocol.class)) {
                            return kafkaTransportProtocolForEdge();
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

    private TransportProtocol mqttTransportProtocolForEdge() {
        if(matchesDeploymentTargets()) {
            return mqttTransportProtocolForEdge(true);
        } else {
            return mqttTransportProtocolForEdge(false);
        }
    }

    private TransportProtocol mqttTransportProtocolForEdge(boolean matchingDeploymentTarget) {
        if (matchingDeploymentTarget) {
            // get node transport protocol
            String nodeControllerId = ((InvocableStreamPipesEntity) source).getDeploymentTargetNodeId();
            MqttTransportProtocol nodeTransportProtocol = (MqttTransportProtocol)
                    getNodeBrokerTransportProtocol(nodeControllerId);
            nodeTransportProtocol.setTopicDefinition(new SimpleTopicDefinition(outputTopic));

            return nodeTransportProtocol;
        } else {
            MqttTransportProtocol tp = (MqttTransportProtocol)
                    ((InvocableStreamPipesEntity) source).getInputStreams().stream()
                            .filter(s -> (s.getEventGrounding().getTransportProtocol() instanceof MqttTransportProtocol))
                            .findFirst().get().getEventGrounding().getTransportProtocol();
            return new MqttTransportProtocol(tp.getBrokerHostname(), tp.getPort(), outputTopic);
        }
    }

    private TransportProtocol kafkaTransportProtocolForEdge() {
        if(matchesDeploymentTargets()) {
            return kafkaTransportProtocolForEdge(true);
        } else {
            return kafkaTransportProtocolForEdge(false);
        }
    }

    private TransportProtocol kafkaTransportProtocolForEdge(boolean matchingDeploymentTarget) {
        if (matchingDeploymentTarget) {
            // get node transport protocol
            String nodeControllerId = ((InvocableStreamPipesEntity) source).getDeploymentTargetNodeId();
            KafkaTransportProtocol nodeTransportProtocol = (KafkaTransportProtocol)
                    getNodeBrokerTransportProtocol(nodeControllerId);
            nodeTransportProtocol.setTopicDefinition(new SimpleTopicDefinition(outputTopic));

            return nodeTransportProtocol;
        } else {
            KafkaTransportProtocol tp = (KafkaTransportProtocol)
                    ((InvocableStreamPipesEntity) source).getInputStreams().stream()
                            .filter(s -> (s.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol))
                            .findFirst().get().getEventGrounding().getTransportProtocol();
            return new KafkaTransportProtocol(tp.getBrokerHostname(), tp.getKafkaPort(), outputTopic);
        }
    }

    private boolean sourceInvocableManagedByNodeController() {
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

    private static TransportProtocol getNodeBrokerTransportProtocol(String id) {
        Optional<NodeInfoDescription> nodeInfoDescription = getNodeInfoDescriptionForId(id);
        if (nodeInfoDescription.isPresent()) {
            return nodeInfoDescription.get().getNodeBroker().getNodeTransportProtocol();
        }
        throw new SpRuntimeException("Could not find node description for id: " + id);
    }

    private static Optional<NodeInfoDescription> getNodeInfoDescriptionForId(String id){
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().getNode(id);
    }
}
