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
package org.apache.streampipes.manager.execution.pipeline;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.execution.http.GraphSubmitter;
import org.apache.streampipes.manager.node.StreamPipesClusterManager;
import org.apache.streampipes.manager.util.TemporaryGraphStorage;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelay;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.encryption.CredentialsManager;

import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class AbstractPipelineExecutor {

    protected Pipeline pipeline;
    protected boolean visualize;
    protected boolean storeStatus;
    protected boolean monitor;

    public AbstractPipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor) {
        this.pipeline = pipeline;
        this.visualize = visualize;
        this.storeStatus = storeStatus;
        this.monitor = monitor;
    }

    // standard methods
    protected void setPipelineStarted(Pipeline pipeline) {
        pipeline.setRunning(true);
        pipeline.setStartedAt(new Date().getTime());
        getPipelineStorageApi().updatePipeline(pipeline);
    }

    protected void setPipelineStopped(Pipeline pipeline) {
        pipeline.setRunning(false);
        getPipelineStorageApi().updatePipeline(pipeline);
    }

    protected void deleteVisualization(String pipelineId) {
        StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getVisualizationStorageApi()
                .deleteVisualization(pipelineId);
    }

    protected void storeInvocationGraphs(String pipelineId, List<InvocableStreamPipesEntity> graphs,
                                       List<SpDataSet> dataSets) {
        TemporaryGraphStorage.graphStorage.put(pipelineId, graphs);
        TemporaryGraphStorage.datasetStorage.put(pipelineId, dataSets);
    }

    protected void storeDataStreamRelayContainer(List<SpDataStreamRelayContainer> relays) {
        relays.forEach(StreamPipesClusterManager::persistDataStreamRelay);
    }

    protected void deleteDataStreamRelayContainer(List<SpDataStreamRelayContainer> relays) {
        relays.forEach(StreamPipesClusterManager::deleteDataStreamRelay);
    }

    protected void updateDataStreamRelayContainer(List<SpDataStreamRelayContainer> relays) {
        relays.forEach(StreamPipesClusterManager::updateDataStreamRelay);
    }


    protected PipelineOperationStatus startPipelineElementsAndRelays(List<InvocableStreamPipesEntity> graphs,
                                                                     List<SpDataStreamRelayContainer> relays){
        if (graphs.isEmpty()) {
            return initPipelineOperationStatus();
        }
        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),
                graphs, new ArrayList<>(), relays).invokePipelineElementsAndRelays();
    }

    protected PipelineOperationStatus stopPipelineElementsAndRelays(List<InvocableStreamPipesEntity> graphs,
                                                                    List<SpDataStreamRelayContainer> relays){
        if (graphs.isEmpty()) {
            return initPipelineOperationStatus();
        }
        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),
                graphs, new ArrayList<>(),relays).detachPipelineElementsAndRelays();
    }

    protected PipelineOperationStatus startRelays(List<SpDataStreamRelayContainer> relays){
        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), new ArrayList<>(), new ArrayList<>(),
                relays).invokeRelaysOnMigration();
    }

    protected PipelineOperationStatus stopRelays(List<SpDataStreamRelayContainer> relays){
        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),new ArrayList<>(), new ArrayList<>(),
                relays).detachRelaysOnMigration();
    }

    protected List<SpDataStreamRelayContainer> findRelays(List<NamedStreamPipesEntity> predecessors,
                                                          InvocableStreamPipesEntity target){

        List<SpDataStreamRelayContainer> relays = new ArrayList<>();

        predecessors.forEach(pred -> {
            List<SpDataStreamRelay> dataStreamRelays = new ArrayList<>();
            SpDataStreamRelayContainer relayContainer = new SpDataStreamRelayContainer();

            if (pred instanceof DataProcessorInvocation){
                //Data Processor
                DataProcessorInvocation graph = (DataProcessorInvocation) pred;
                if (differentDeploymentTargets(pred, target)) {
                    dataStreamRelays.addAll(findRelaysWithMatchingTopic(graph, target));

                    //dsRelayContainer.setRunningStreamRelayInstanceId(pipeline.getPipelineId());
                    relayContainer = new SpDataStreamRelayContainer(graph);
                    relayContainer.setOutputStreamRelays(dataStreamRelays);

                    relays.add(relayContainer);
                }
            } else if (pred instanceof SpDataStream){
                //DataStream
                SpDataStream stream = (SpDataStream) pred;
                if (differentDeploymentTargets(stream, target)){
                    //There is a relay that needs to be removed
                    dataStreamRelays.add(new SpDataStreamRelay(new EventGrounding(target.getInputStreams()
                            .get(getIndex(pred.getDOM(), target))
                            .getEventGrounding())));

                    String id = extractUniqueAdpaterId(stream.getElementId());
                    String relayStrategy = pipeline.getEventRelayStrategy();

                    relays.add(new SpDataStreamRelayContainer(id, relayStrategy, stream, dataStreamRelays));
                }
            }
        });
        return relays;
    }

    protected List<NamedStreamPipesEntity> getPredecessors(NamedStreamPipesEntity source,
                                                           InvocableStreamPipesEntity target,
                                                           PipelineGraph pipelineGraph,
                                                           List<NamedStreamPipesEntity> foundPredecessors){

        Set<InvocableStreamPipesEntity> targets = getTargetsAsSet(source, pipelineGraph,
                InvocableStreamPipesEntity.class);

        //TODO: Check if this works for all graph topologies
        if (targets.contains(target)){
            foundPredecessors.add(source);
        } else {
            List<NamedStreamPipesEntity> successors = getTargetsAsList(source, pipelineGraph,
                    NamedStreamPipesEntity.class);

            if (successors.isEmpty()) return foundPredecessors;
            successors.forEach(successor -> getPredecessors(successor, target, pipelineGraph, foundPredecessors));
        }
        return foundPredecessors;
    }

    protected NamedStreamPipesEntity findMatching(NamedStreamPipesEntity entity, PipelineGraph pipelineGraph){
        AtomicReference<NamedStreamPipesEntity> match = new AtomicReference<>();
        List<SpDataStream> dataStreams = PipelineGraphHelpers.findStreams(pipelineGraph);

        for (SpDataStream stream : dataStreams) {
            NamedStreamPipesEntity foundEntity = compareGraphs(stream, entity, pipelineGraph, new ArrayList<>());
            if (foundEntity != null) {
                match.set(foundEntity);
            }
        }
        return match.get();
    }

    private NamedStreamPipesEntity compareGraphs(NamedStreamPipesEntity source,
                                                   NamedStreamPipesEntity searchedEntity,
                                                   PipelineGraph pipelineGraph,
                                                   List<NamedStreamPipesEntity> successors){
        if(matchingDOM(source, searchedEntity)) {
            return source;
        } else if (successors.isEmpty()) {
            successors = getTargetsAsList(source, pipelineGraph, NamedStreamPipesEntity.class);
            Optional<NamedStreamPipesEntity> successor = successors.stream().findFirst();
            if (successor.isPresent()) {
                successors.remove(successor.get());
                return compareGraphs(successor.get(), searchedEntity, pipelineGraph, successors);
            }
        }
        return null;
    }

    protected List<SpDataStreamRelayContainer> generateRelays(List<InvocableStreamPipesEntity> graphs) {
        return generateDataStreamRelays(graphs).stream()
                .filter(r -> r.getOutputStreamRelays().size() > 0)
                .collect(Collectors.toList());
    }

    private List<SpDataStreamRelayContainer> generateDataStreamRelays(List<InvocableStreamPipesEntity> graphs) {
        List<SpDataStreamRelayContainer> relays = new ArrayList<>();

        for (InvocableStreamPipesEntity graph : graphs) {
            for (SpDataStream stream: pipeline.getStreams()) {
                if (differentDeploymentTargets(stream, graph) && connected(stream, graph)) {

                    List<SpDataStreamRelay> dataStreamRelays = new ArrayList<>();
                    dataStreamRelays.add(new SpDataStreamRelay(new EventGrounding(graph.getInputStreams()
                            .get(getIndex(stream.getDOM(), graph))
                            .getEventGrounding())));

                    String id = extractUniqueAdpaterId(stream.getElementId());
                    String relayStrategy = pipeline.getEventRelayStrategy();

                    relays.add(new SpDataStreamRelayContainer(id, relayStrategy, stream, dataStreamRelays));
                }
            }
            for (DataProcessorInvocation processor : pipeline.getSepas()) {
                if (differentDeploymentTargets(processor, graph) && connected(processor, graph)) {
                    SpDataStreamRelayContainer processorRelay = new SpDataStreamRelayContainer(processor);
                    relays.add(processorRelay);
                }
            }
        }
        return relays;
    }


    // Helpers

    /**
     * Updates group.id for data processor/sink. Note: KafkaTransportProtocol only!!
     *
     * @param entity    data processor/sink
     */
    protected void updateKafkaGroupIds(InvocableStreamPipesEntity entity) {
        entity.getInputStreams()
                .stream()
                .filter(is -> is.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol)
                .map(is -> is.getEventGrounding().getTransportProtocol())
                .map(KafkaTransportProtocol.class::cast)
                .forEach(tp -> tp.setGroupId(UUID.randomUUID().toString()));
    }

    /**
     * Decrypt potential secrets contained in static properties, e.g., passwords
     *
     * @param graphs    List of graphs
     * @return  List of decrypted graphs
     */
    protected List<InvocableStreamPipesEntity> decryptSecrets(List<InvocableStreamPipesEntity> graphs) {
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

    /**
     * Get pipeline storage dispatcher API
     *
     * @return IPipelineStorage NoSQL storage interface for pipelines
     */
    private IPipelineStorage getPipelineStorageApi() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
    }

    /**
     * Extract topic name
     *
     * @param entity
     * @return
     */
    private String extractActualTopic(NamedStreamPipesEntity entity) {
        if (entity instanceof SpDataStream) {
            return ((SpDataStream) entity)
                    .getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
        } else if (entity instanceof SpDataStreamRelay) {
            return ((SpDataStreamRelay) entity)
                    .getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
        }
        throw new SpRuntimeException("Could not extract actual topic name from entity");
    }

    // Edge / Migration Helpers

    /**
     * Compare deployment targets of two pipeline elements, namely data stream/processor (source) and data
     * processor/sink (target)
     *
     * @param source    data stream/processor
     * @param target    data processor/sink
     * @return boolean value that returns true if source and target share the same deployment target, else false
     */
    private boolean differentDeploymentTargets(NamedStreamPipesEntity source, InvocableStreamPipesEntity target) {
        if (source instanceof SpDataStream) {
            return !((SpDataStream) source).getDeploymentTargetNodeId().equals(target.getDeploymentTargetNodeId());
        } else if (source instanceof DataProcessorInvocation) {
            return !((DataProcessorInvocation) source).getDeploymentTargetNodeId().equals(target.getDeploymentTargetNodeId());
        }
        throw new SpRuntimeException("Matching deployment targets check failed");
    }

    /**
     * Find relays with matching topics
     *
     * @param graph     data processor
     * @param target    data processor/sink
     * @return collection of data stream relays
     */
    private Collection<? extends SpDataStreamRelay> findRelaysWithMatchingTopic(DataProcessorInvocation graph,
                                                                                InvocableStreamPipesEntity target) {
        return graph.getOutputStreamRelays().stream().
                filter(relay ->
                        target.getInputStreams().stream()
                                .map(this::extractActualTopic)
                                .collect(Collectors.toSet())
                                .contains(extractActualTopic(relay)))
                .collect(Collectors.toList());
    }


    private <T> Set<T> getTargetsAsSet(NamedStreamPipesEntity source, PipelineGraph pipelineGraph,
                                       Class<T> clazz){
        return pipelineGraph.outgoingEdgesOf(source)
                .stream()
                .map(pipelineGraph::getEdgeTarget)
                .map(clazz::cast)
                .collect(Collectors.toSet());
    }

    private <T> List<T> getTargetsAsList(NamedStreamPipesEntity source, PipelineGraph pipelineGraph,
                                         Class<T> clazz){
        return new ArrayList<>(getTargetsAsSet(source, pipelineGraph, clazz));
    }

    /**
     * Compare connection of two pipeline elements, namely data stream/processor (source) and data processor/sink
     * (target) by DOM identifier.
     *
     * @param source    data stream or data processor
     * @param target    data processor/sink
     * @return boolean value that returns true if source and target are connected, else false
     */
    private boolean connected(NamedStreamPipesEntity source, InvocableStreamPipesEntity target) {
        int index = getIndex(source.getDOM(), target);
        if (index != -1) {
            return target.getConnectedTo().get(index).equals(source.getDOM());
        }
        return false;
    }

    /**
     * Get index of data processor/sink connection based on source DOM identifier
     *
     * @param sourceDomId   source DOM identifier
     * @param target        data processor/sink
     * @return Integer with index of connection, if invalid returns -1.
     */
    private Integer getIndex(String sourceDomId, InvocableStreamPipesEntity target) {
        return target.getConnectedTo().indexOf(sourceDomId);
    }

    /**
     * Checks if DOM are equal
     *
     * @param source pipeline element
     * @param target pipeline element
     * @return true if DOM is the same, else false
     */
    private boolean matchingDOM(NamedStreamPipesEntity source, NamedStreamPipesEntity target) {
        return source.getDOM().equals(target.getDOM());
    }

    /**
     * Get List of InvocableStreamPipes entities, i.e., data processors/sinks from list of NameStreamPipesEntity
     *
     * @param graphs    List<NamedStreamPipesEntity> graphs
     * @return
     */
    private List<InvocableStreamPipesEntity> getListOfInvocableStreamPipesEntity(List<NamedStreamPipesEntity> graphs) {
        List<InvocableStreamPipesEntity> invocableEntities = new ArrayList<>();
        graphs.stream()
                .filter(i -> i instanceof InvocableStreamPipesEntity)
                .forEach(i -> invocableEntities.add((InvocableStreamPipesEntity) i));
        return invocableEntities;
    }

    /**
     * Create pipeline operation status with pipeline id and name and set success to true
     *
     * @return PipelineOperationStatus
     */
    protected PipelineOperationStatus initPipelineOperationStatus() {
        PipelineOperationStatus status = new PipelineOperationStatus();
        status.setPipelineId(pipeline.getPipelineId());
        status.setPipelineName(pipeline.getName());
        status.setSuccess(true);
        return status;
    }

    private <T> List<T> filter(List<InvocableStreamPipesEntity> graphs, Class<T> clazz) {
        return graphs
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    private String extractUniqueAdpaterId(String s) {
        return s.substring(s.lastIndexOf("/") + 1);
    }

}
