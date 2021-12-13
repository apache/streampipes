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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelay;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.*;
import java.util.stream.Collectors;

public class RelayUtils {

    public static void updateRelays(List<SpDataStreamRelayContainer> relays,
                                    List<SpDataStreamRelayContainer> relaysToBeUpdated) {
        relays.stream()
                .filter(r -> r.getOutputStreamRelays().size() > 0)
                .forEach(relaysToBeUpdated::add);
    }

    public static List<SpDataStreamRelayContainer> findRelays(List<NamedStreamPipesEntity> predecessors,
                                                          InvocableStreamPipesEntity target, Pipeline pipeline){

        List<SpDataStreamRelayContainer> relays = new ArrayList<>();

        predecessors.forEach(pred -> {
            List<SpDataStreamRelay> dataStreamRelays = new ArrayList<>();

            if (pred instanceof DataProcessorInvocation){
                //Data Processor
                DataProcessorInvocation graph = (DataProcessorInvocation) pred;
                if (PipelineElementUtils.differentDeploymentTargets(pred, target)) {

                    String runningRelayId = ((DataProcessorInvocation) pred).getDeploymentRunningInstanceId();
                    Optional<SpDataStreamRelayContainer> existingRelay = getRelayContainerById(runningRelayId);

                    // only add relay if not existing - prevent from duplicate relays with same topic to same target
                    Collection<? extends SpDataStreamRelay> foundRelays = findRelaysWithMatchingTopic(graph, target);

                    if (!existingRelay.isPresent() || missingRelayToTarget(existingRelay.get(), foundRelays)) {
                        dataStreamRelays.addAll(findRelaysWithMatchingTopic(graph, target));

                        //dsRelayContainer.setRunningStreamRelayInstanceId(pipeline.getPipelineId());
                        SpDataStreamRelayContainer relayContainer = new SpDataStreamRelayContainer(graph);
                        relayContainer.setOutputStreamRelays(dataStreamRelays);

                        relays.add(relayContainer);
                    }

                }
            } else if (pred instanceof SpDataStream){
                //DataStream
                SpDataStream stream = (SpDataStream) pred;
                if (PipelineElementUtils.differentDeploymentTargets(stream, target)){

                    String id = extractUniqueAdpaterId(stream.getElementId());
                    Optional<SpDataStreamRelayContainer> existingRelay = getRelayContainerById(id);

                    // only add relay if not existing - prevent from duplicate relays with same topic
                    if(!existingRelay.isPresent()) {
                        //There is a relay that needs to be removed
                        dataStreamRelays.add(new SpDataStreamRelay(new EventGrounding(target.getInputStreams()
                                .get(PipelineElementUtils.getIndex(pred.getDOM(), target))
                                .getEventGrounding())));
                        String relayStrategy = pipeline.getEventRelayStrategy();
                        relays.add(new SpDataStreamRelayContainer(id, relayStrategy, stream, dataStreamRelays));
                    } else {
                        // generate relays for adapter streams to remote processors
                        List<SpDataStreamRelayContainer> generatedRelays =
                                PipelineElementUtils.generateDataStreamRelays(Collections.singletonList(target), pipeline);

                        relays.addAll(generatedRelays);
                    }
                }
            }
        });
        return relays;
    }

    public static List<SpDataStreamRelayContainer> findRelaysWhenStopping(List<NamedStreamPipesEntity> predecessors,
                                                                      InvocableStreamPipesEntity target, Pipeline pipeline){

        List<SpDataStreamRelayContainer> relays = new ArrayList<>();

        predecessors.forEach(pred -> {
            List<SpDataStreamRelay> dataStreamRelays = new ArrayList<>();

            if (pred instanceof DataProcessorInvocation){
                //Data Processor
                DataProcessorInvocation graph = (DataProcessorInvocation) pred;
                if (PipelineElementUtils.differentDeploymentTargets(pred, target)) {

                    // TODO only add if no other processor or sink depends on relay
                    String predDOMId = pred.getDOM();
                    String targetRunningInstanceId = target.getDeploymentRunningInstanceId();
                    Optional<DataProcessorInvocation> foundProcessor = pipeline.getSepas().stream()
                            .filter(processor -> processor.getConnectedTo().contains(predDOMId))
                            .filter(processor -> !processor.getDeploymentRunningInstanceId().equals(targetRunningInstanceId))
                            .findAny();

                    Optional<DataSinkInvocation> foundSink = pipeline.getActions().stream()
                            .filter(action -> action.getConnectedTo().contains(predDOMId))
                            .findAny();

                    boolean foundDependencyOnDifferentTarget = false;
                    if (foundProcessor.isPresent()) {
                        foundDependencyOnDifferentTarget =  PipelineElementUtils.differentDeploymentTargets(foundProcessor.get(), target);
                    }

                    if (foundSink.isPresent()) {
                        foundDependencyOnDifferentTarget =  PipelineElementUtils.differentDeploymentTargets(foundSink.get(), target);
                    }

                    if (foundDependencyOnDifferentTarget) {
                        dataStreamRelays.addAll(findRelaysWithMatchingTopic(graph, target));

                        SpDataStreamRelayContainer relayContainer = new SpDataStreamRelayContainer(graph);
                        relayContainer.setOutputStreamRelays(dataStreamRelays);

                        relays.add(relayContainer);
                    }

                }
            } else if (pred instanceof SpDataStream){
                //DataStream
                SpDataStream stream = (SpDataStream) pred;
                if (PipelineElementUtils.differentDeploymentTargets(stream, target)){

                    String id = extractUniqueAdpaterId(stream.getElementId());
                    //There is a relay that needs to be removed
                    dataStreamRelays.add(new SpDataStreamRelay(new EventGrounding(target.getInputStreams()
                            .get(PipelineElementUtils.getIndex(pred.getDOM(), target))
                            .getEventGrounding())));
                    String relayStrategy = pipeline.getEventRelayStrategy();
                    relays.add(new SpDataStreamRelayContainer(id, relayStrategy, stream, dataStreamRelays));
                }
            }
        });
        return relays;
    }

    /**
     * Find relays with matching topics
     *
     * @param graph     data processor
     * @param target    data processor/sink
     * @return collection of data stream relays
     */
    public static Collection<? extends SpDataStreamRelay> findRelaysWithMatchingTopic(DataProcessorInvocation graph,
                                                                                InvocableStreamPipesEntity target) {
        return graph.getOutputStreamRelays().stream().
                filter(relay ->
                        target.getInputStreams().stream()
                                .map(PipelineElementUtils::extractActualTopic)
                                .collect(Collectors.toSet())
                                .contains(PipelineElementUtils.extractActualTopic(relay)))
                .collect(Collectors.toList());
    }

    public static String extractUniqueAdpaterId(String s) {
        return s.substring(s.lastIndexOf("/") + 1);
    }

    /**
     *
     * @param id
     * @return
     */
    private static Optional<SpDataStreamRelayContainer> getRelayContainerById(String id) {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeDataStreamRelayStorage().getRelayContainerById(id);
    }

    private static boolean missingRelayToTarget(SpDataStreamRelayContainer existingRelayContainer,
                                         Collection<? extends SpDataStreamRelay> foundRelays) {

        List<TransportProtocol> set = foundRelays.stream()
                .map(SpDataStreamRelay::getEventGrounding)
                .map(EventGrounding::getTransportProtocol)
                .collect(Collectors.toList());

        List<TransportProtocol> relay = existingRelayContainer.getOutputStreamRelays().stream()
                .map(SpDataStreamRelay::getEventGrounding)
                .map(EventGrounding::getTransportProtocol)
                .collect(Collectors.toList());

        for (TransportProtocol tp: set) {
            for (TransportProtocol r: relay) {
                String targetTopic = tp.getTopicDefinition().getActualTopicName();
                String rTopic = r.getTopicDefinition().getActualTopicName();
                String targetHost = tp.getBrokerHostname();
                String rHost = r.getBrokerHostname();

                if (targetHost.equals(rHost) && targetTopic.equals(rTopic)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Check if relay with deploymentRunningInstanceId of predecessor already exists
     *
     * @param relays                        List of existing relays
     * @param deploymentRunningInstanceId   Id to check
     * @return boolean
     */
    public static boolean relayExists(List<SpDataStreamRelayContainer> relays,
                                String deploymentRunningInstanceId) {
        return relays.stream().anyMatch(r -> r.getRunningStreamRelayInstanceId().equals(deploymentRunningInstanceId));
    }

    public static List<SpDataStreamRelayContainer> filterRelaysById(List<SpDataStreamRelayContainer> relays,
                                                                    Set<String> relayIds) {
        return relays.stream().
                filter(relay -> relayIds.contains(relay.getRunningStreamRelayInstanceId()))
                .collect(Collectors.toList());
    }
}
