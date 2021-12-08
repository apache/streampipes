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
package org.apache.streampipes.manager.execution.pipeline.executor.utils;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelay;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.user.management.encryption.CredentialsManager;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PipelineElementUtils {

    public static List<SpDataStreamRelayContainer> extractRelaysFromDataProcessor(List<InvocableStreamPipesEntity> graphs) {
        return graphs.stream()
                .map(DataProcessorInvocation.class::cast)
                .map(SpDataStreamRelayContainer::new)
                .collect(Collectors.toList());
    }

    /**
     * Compare deployment targets of two pipeline elements, namely data stream/processor (source) and data
     * processor/sink (target)
     *
     * @param e1
     * @param e2
     * @return boolean value that returns true if source and target share the same deployment target, else false
     */
    public static boolean differentDeploymentTargets(NamedStreamPipesEntity e1, InvocableStreamPipesEntity e2) {
        if (e1 instanceof SpDataStream) {
            return !((SpDataStream) e1).getDeploymentTargetNodeId().equals(e2.getDeploymentTargetNodeId());
        } else if (e1 instanceof DataProcessorInvocation) {
            return !((DataProcessorInvocation) e1).getDeploymentTargetNodeId().equals(e2.getDeploymentTargetNodeId());
        } else if (e1 instanceof DataSinkInvocation) {
            return !((DataSinkInvocation) e1).getDeploymentTargetNodeId().equals(e2.getDeploymentTargetNodeId());
        }
        throw new SpRuntimeException("Matching deployment targets check failed");
    }

    /**
     * Extract topic name
     *
     * @param entity
     * @return
     */
    public static String extractActualTopic(NamedStreamPipesEntity entity) {
        if (entity instanceof SpDataStream) {
            return ((SpDataStream) entity)
                    .getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
        } else if (entity instanceof SpDataStreamRelay) {
            return ((SpDataStreamRelay) entity)
                    .getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
        }
        throw new SpRuntimeException("Could not extract actual topic name from entity");
    }

    /**
     * Get index of data processor/sink connection based on source DOM identifier
     *
     * @param sourceDomId   source DOM identifier
     * @param target        data processor/sink
     * @return Integer with index of connection, if invalid returns -1.
     */
    public static Integer getIndex(String sourceDomId, InvocableStreamPipesEntity target) {
        return target.getConnectedTo().indexOf(sourceDomId);
    }

    public static List<SpDataStreamRelayContainer> findRelaysAndFilterById(Set<String> relayIdsToRollback,
                                                                     List<NamedStreamPipesEntity> predecessor,
                                                                     InvocableStreamPipesEntity target, Pipeline pipeline) {
        return RelayUtils.findRelays(predecessor, target, pipeline).stream()
                .filter(relay -> relayIdsToRollback.contains(relay.getRunningStreamRelayInstanceId()))
                .collect(Collectors.toList());
    }

    // TODO: when using kafka as edge protocol it generates duplicate event relays -> check with mqtt as edge
    //  protocol and fix
    public static List<SpDataStreamRelayContainer> generateDataStreamRelays(List<InvocableStreamPipesEntity> graphs,
                                                                            Pipeline pipeline) {
        List<SpDataStreamRelayContainer> relays = new ArrayList<>();

        for (InvocableStreamPipesEntity graph : graphs) {
            for (SpDataStream stream: pipeline.getStreams()) {
                if (differentDeploymentTargets(stream, graph) && connected(stream, graph)) {

                    List<SpDataStreamRelay> dataStreamRelays = new ArrayList<>();
                    dataStreamRelays.add(new SpDataStreamRelay(new EventGrounding(graph.getInputStreams()
                            .get(getIndex(stream.getDOM(), graph))
                            .getEventGrounding())));

                    String id = RelayUtils.extractUniqueAdpaterId(stream.getElementId());
                    String relayStrategy = pipeline.getEventRelayStrategy();

                    if (!RelayUtils.relayExists(relays, id)) {
                        relays.add(new SpDataStreamRelayContainer(id, relayStrategy, stream, dataStreamRelays));
                    }
                }
            }
            for (DataProcessorInvocation processor : pipeline.getSepas()) {
                if (differentDeploymentTargets(processor, graph) && connected(processor, graph)) {
                    if (!RelayUtils.relayExists(relays, processor.getDeploymentRunningInstanceId())) {
//                        String previousId = processor.getDeploymentRunningInstanceId();
//                        String modifiedId = previousId + "-" + processor.getDeploymentTargetNodeId();
//                        processor.setDeploymentRunningInstanceId(modifiedId);
                        SpDataStreamRelayContainer processorRelay = new SpDataStreamRelayContainer(processor);
                        relays.add(processorRelay);
                    }
                }
            }
        }
        return relays;
    }

    /**
     * Compare connection of two pipeline elements, namely data stream/processor (source) and data processor/sink
     * (target) by DOM identifier.
     *
     * @param source    data stream or data processor
     * @param target    data processor/sink
     * @return boolean value that returns true if source and target are connected, else false
     */
    private static boolean connected(NamedStreamPipesEntity source, InvocableStreamPipesEntity target) {
        int index = getIndex(source.getDOM(), target);
        if (index != -1) {
            return target.getConnectedTo().get(index).equals(source.getDOM());
        }
        return false;
    }

    /**
     * Decrypt potential secrets contained in static properties, e.g., passwords
     *
     * @param graphs    List of graphs
     * @return  List of decrypted graphs
     */
    public static List<InvocableStreamPipesEntity> decryptSecrets(List<InvocableStreamPipesEntity> graphs, Pipeline pipeline) {
        List<InvocableStreamPipesEntity> decryptedGraphs = new ArrayList<>();
        graphs.stream().map(g -> {
            if (g instanceof DataProcessorInvocation) {
                return new DataProcessorInvocation((DataProcessorInvocation) g);
            } else {
                return new DataSinkInvocation((DataSinkInvocation) g);
            }
        }).forEach(g -> {
            g.getStaticProperties()
                    .stream()
                    .filter(SecretStaticProperty.class::isInstance)
                    .forEach(sp -> {
                        try {
                            String decrypted = CredentialsManager.decrypt(pipeline.getCreatedByUser(),
                                    ((SecretStaticProperty) sp).getValue());
                            ((SecretStaticProperty) sp).setValue(decrypted);
                            ((SecretStaticProperty) sp).setEncrypted(false);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    });
            decryptedGraphs.add(g);
        });
        return decryptedGraphs;
    }

    public static List<SpDataStreamRelayContainer> generateRelays(List<InvocableStreamPipesEntity> graphs,
                                                                  Pipeline pipeline) {
        return generateDataStreamRelays(graphs, pipeline).stream()
                .filter(r -> r.getOutputStreamRelays().size() > 0)
                .collect(Collectors.toList());
    }
}
