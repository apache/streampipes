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

package org.apache.streampipes.manager.execution.http;

import org.apache.streampipes.manager.data.PipelineGraph;
import org.apache.streampipes.manager.data.PipelineGraphBuilder;
import org.apache.streampipes.manager.data.PipelineGraphHelpers;
import org.apache.streampipes.manager.matching.InvocationGraphBuilder;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamRelay;
import org.apache.streampipes.model.SpDataStreamRelayContainer;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.lightcouch.DocumentConflictException;
import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.manager.execution.status.SepMonitoringManager;
import org.apache.streampipes.manager.util.TemporaryGraphStorage;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.message.PipelineStatusMessage;
import org.apache.streampipes.model.message.PipelineStatusMessageType;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.encryption.CredentialsManager;

import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PipelineExecutor {

  private Pipeline pipeline;
  private boolean visualize;
  private boolean storeStatus;
  private boolean monitor;

  public PipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus,
                          boolean monitor) {
    this.pipeline = pipeline;
    this.visualize = visualize;
    this.storeStatus = storeStatus;
    this.monitor = monitor;
  }

  public PipelineOperationStatus startPipeline() {

    List<DataProcessorInvocation> sepas = pipeline.getSepas();
    List<DataSinkInvocation> secs = pipeline.getActions();
    List<SpDataSet> dataSets = pipeline.getStreams().stream().filter(s -> s instanceof SpDataSet).map(s -> new
            SpDataSet((SpDataSet) s)).collect(Collectors.toList());

    for (SpDataSet ds : dataSets) {
      ds.setCorrespondingPipeline(pipeline.getPipelineId());
    }

    List<InvocableStreamPipesEntity> graphs = new ArrayList<>();
    graphs.addAll(sepas);
    graphs.addAll(secs);

    List<InvocableStreamPipesEntity> decryptedGraphs = decryptSecrets(graphs);

    graphs.forEach(g -> g.setStreamRequirements(Arrays.asList()));

    List<SpDataStreamRelayContainer> dataStreamRelayContainers = generateDataStreamRelays(decryptedGraphs);

    PipelineOperationStatus status = new GraphSubmitter(
            pipeline.getPipelineId(),
            pipeline.getName(),
            decryptedGraphs,
            dataSets,
            dataStreamRelayContainers).invokeGraphs();

    if (status.isSuccess()) {
      storeInvocationGraphs(pipeline.getPipelineId(), graphs, dataSets);

      PipelineStatusManager.addPipelineStatus(
              pipeline.getPipelineId(),
              new PipelineStatusMessage(pipeline.getPipelineId(),
                      System.currentTimeMillis(),
                      PipelineStatusMessageType.PIPELINE_STARTED.title(),
                      PipelineStatusMessageType.PIPELINE_STARTED.description()));

      if (monitor) {
        SepMonitoringManager.addObserver(pipeline.getPipelineId());
      }

      if (storeStatus) {
        setPipelineStarted(pipeline);
      }
    }
    return status;
  }

  public PipelineOperationStatus stopPipeline() {
    List<InvocableStreamPipesEntity> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
    List<SpDataSet> dataSets = TemporaryGraphStorage.datasetStorage.get(pipeline.getPipelineId());

    List<SpDataStreamRelayContainer> dataStreamRelayContainers = generateDataStreamRelays(graphs);

    PipelineOperationStatus status = new GraphSubmitter(
            pipeline.getPipelineId(),
            pipeline.getName(),
            graphs,
            dataSets,
            dataStreamRelayContainers).detachGraphs();

    if (status.isSuccess()) {
      if (visualize) {
        StorageDispatcher
                .INSTANCE
                .getNoSqlStore()
                .getVisualizationStorageApi()
                .deleteVisualization(pipeline.getPipelineId());
      }
      if (storeStatus) {
        setPipelineStopped(pipeline);
      }

      PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(),
              new PipelineStatusMessage(pipeline.getPipelineId(),
                      System.currentTimeMillis(),
                      PipelineStatusMessageType.PIPELINE_STOPPED.title(),
                      PipelineStatusMessageType.PIPELINE_STOPPED.description()));

      if (monitor) {
        SepMonitoringManager.removeObserver(pipeline.getPipelineId());
      }

    }
    return status;
  }

  public PipelineOperationStatus updatePipelineDeploymentPartial(Pipeline pipelineOld){
    PipelineOperationStatus status = initPipelineOperationStatus(pipeline.getPipelineId(), pipeline.getName());
    //Adjust Relays in Description using existing configure Method in InvocationGraphBuilder
    pipeline.getSepas().forEach(s -> s.setOutputStreamRelays(new ArrayList<>()));

    PipelineGraph oldPipelineGraph = new PipelineGraphBuilder(pipelineOld).buildGraph();
    PipelineGraph newPipelineGraph = new PipelineGraphBuilder(pipeline).buildGraph();
    new InvocationGraphBuilder(newPipelineGraph, pipeline.getPipelineId()).buildGraphs();

    getDelta(this.pipeline, pipelineOld).forEach(t -> {
      List<NamedStreamPipesEntity> predecessors = new ArrayList<>();

      PipelineGraphHelpers
              .findStreams(newPipelineGraph)
              .forEach(source -> predecessors.addAll(getPredecessors(source, t.a, newPipelineGraph, new ArrayList<>())));
      List<Tuple2<NamedStreamPipesEntity, NamedStreamPipesEntity>> matchingPredecessors = new ArrayList<>();
      for(NamedStreamPipesEntity e : predecessors){
        matchingPredecessors.add(new Tuple2<>(e, findMatching(e, oldPipelineGraph)));
      }
      List<NamedStreamPipesEntity> predecessorsOld = new ArrayList<>();
      for(Tuple2<NamedStreamPipesEntity, NamedStreamPipesEntity> pred : matchingPredecessors){
        predecessorsOld.add(pred.b);
      }

      PipelineOperationStatus statusStartTarget = startPipelineElement(Collections.singletonList(t.a));
      statusStartTarget.getElementStatus().forEach(status::addPipelineElementStatus);

      //Stop relay to origin
      PipelineOperationStatus statusStopRelays = stopRelays(predecessorsOld, t.b);
      statusStopRelays.getElementStatus().forEach(status::addPipelineElementStatus);
      //start Relay to target
      PipelineOperationStatus statusStartRelays = startRelays(predecessors, t.a);
      statusStartRelays.getElementStatus().forEach(status::addPipelineElementStatus);
      //Stop origin and associated relay
      PipelineOperationStatus stopOrigin = stopPipelineElements(Collections.singletonList(t.b));
      stopOrigin.getElementStatus().forEach(status::addPipelineElementStatus);

      //Remove if unnecessary
      List<InvocableStreamPipesEntity> graphs = new ArrayList<>();
      graphs.addAll(pipeline.getActions());
      graphs.addAll(pipeline.getSepas());
      List<SpDataSet> dataSets = pipeline.getStreams().stream().filter(s -> s instanceof SpDataSet).map(s -> new
              SpDataSet((SpDataSet) s)).collect(Collectors.toList());
      storeInvocationGraphs(pipeline.getPipelineId(), graphs, dataSets);

    });
    return verifyPipelineOperationStatus(status,
            "Successfully migrated Pipeline Elements in Pipeline " + pipeline.getName(),
            "Could not migrate all Pipeline Elements in Pipeline " + pipeline.getName());
  }

  private PipelineOperationStatus stopPipelineElements(List<NamedStreamPipesEntity> graphs){
    List<InvocableStreamPipesEntity> invocations = new ArrayList<>();
    graphs.stream().filter(i -> i instanceof InvocableStreamPipesEntity).forEach(i -> invocations.add((InvocableStreamPipesEntity) i));

    if (invocations.isEmpty()) {
      PipelineOperationStatus ret = initPipelineOperationStatus(pipeline.getPipelineId(), pipeline.getName());
      ret.setSuccess(true);
      return ret;
    }
    List<SpDataStreamRelayContainer> dataStreamRelayContainers = generateDataStreamRelays(invocations);
    return new GraphSubmitter(pipeline.getPipelineId(),
            pipeline.getName(),  invocations, new ArrayList<>(), dataStreamRelayContainers)
            .detachGraphs();
  }

  private PipelineOperationStatus startPipelineElement(List<NamedStreamPipesEntity> graphs){
    List<InvocableStreamPipesEntity> invocations = new ArrayList<>();
    graphs.stream().filter(i -> i instanceof InvocableStreamPipesEntity).forEach(i -> invocations.add((InvocableStreamPipesEntity) i));

    if (invocations.isEmpty()) {
      PipelineOperationStatus ret = initPipelineOperationStatus(pipeline.getPipelineId(), pipeline.getName());
      ret.setSuccess(true);
      return ret;
    }
    List<InvocableStreamPipesEntity> decryptedGraphs = decryptSecrets(invocations);
    List<SpDataStreamRelayContainer> dataStreamRelayContainers = generateDataStreamRelays(invocations);
    return new GraphSubmitter(pipeline.getPipelineId(),
            pipeline.getName(), decryptedGraphs, new ArrayList<>(), dataStreamRelayContainers)
            .invokeGraphs();
  }

  private PipelineOperationStatus stopRelays(List<NamedStreamPipesEntity> predecessors, InvocableStreamPipesEntity target){
    Map<NamedStreamPipesEntity, SpDataStreamRelayContainer> relays = new HashMap<>();

    findRelays(relays, predecessors, target);

    if (relays.isEmpty()) {
      PipelineOperationStatus ret = initPipelineOperationStatus(pipeline.getPipelineId(), pipeline.getName());
      ret.setSuccess(true);
      return ret;
    }
    return new GraphSubmitter(pipeline.getPipelineId(),
            pipeline.getName(),  new ArrayList<>(), new ArrayList<>(), new ArrayList<>()).detachRelays(relays);
  }

  private PipelineOperationStatus startRelays(List<NamedStreamPipesEntity> predecessors, InvocableStreamPipesEntity target){
    Map<NamedStreamPipesEntity, SpDataStreamRelayContainer> relays = new HashMap<>();
    findRelays(relays, predecessors, target);

    if (relays.isEmpty()) {
      PipelineOperationStatus ret = initPipelineOperationStatus(pipeline.getPipelineId(), pipeline.getName());
      ret.setSuccess(true);
      return ret;
    }
    return new GraphSubmitter(pipeline.getPipelineId(),
            pipeline.getName(),  new ArrayList<>(), new ArrayList<>(), new ArrayList<>()).invokeRelays(relays);
  }

  private void findRelays(Map<NamedStreamPipesEntity, SpDataStreamRelayContainer> relays,
                          List<NamedStreamPipesEntity> predecessors, InvocableStreamPipesEntity target){

    predecessors.forEach(pred -> {
      List<SpDataStreamRelay> dataStreamRelays = new ArrayList<>();
      SpDataStreamRelayContainer dsRelayContainer = new SpDataStreamRelayContainer();

      if (pred instanceof DataProcessorInvocation){
        //Data Processor
        DataProcessorInvocation processorInvocation = (DataProcessorInvocation) pred;
        dataStreamRelays.addAll(processorInvocation.getOutputStreamRelays().stream().
                filter(r ->
                        target.getInputStreams().stream().map(s ->
                                s.getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName())
                                .collect(Collectors.toSet()).contains(r.getEventGrounding().getTransportProtocol()
                                .getTopicDefinition().getActualTopicName()))
                .collect(Collectors.toList()));
        dsRelayContainer.setRunningStreamRelayInstanceId(pipeline.getPipelineId());
        dsRelayContainer.setEventRelayStrategy(pipeline.getEventRelayStrategy());
        dsRelayContainer.setName(processorInvocation.getName() + " (Stream Relay)");
        dsRelayContainer.setInputGrounding(new EventGrounding(processorInvocation.getOutputStream().getEventGrounding()));
        dsRelayContainer.setDeploymentTargetNodeId(processorInvocation.getDeploymentTargetNodeId());
        dsRelayContainer.setDeploymentTargetNodeHostname(processorInvocation.getDeploymentTargetNodeHostname());
        dsRelayContainer.setDeploymentTargetNodePort(processorInvocation.getDeploymentTargetNodePort());
        dsRelayContainer.setOutputStreamRelays(dataStreamRelays);
        relays.put(pred, dsRelayContainer);
      } else if (pred instanceof SpDataStream){
        //DataStream
        SpDataStream dataStream = (SpDataStream) pred;
        if (!matchingDeploymentTargets(dataStream, target)){
          //There is a relay that needs to be removed
          dataStreamRelays.add(new SpDataStreamRelay(new EventGrounding(target.getInputStreams()
                  .get(getIndex(pred.getDOM(), target))
                  .getEventGrounding())));

          dsRelayContainer.setRunningStreamRelayInstanceId(pipeline.getPipelineId());
          dsRelayContainer.setEventRelayStrategy(pipeline.getEventRelayStrategy());
          dsRelayContainer.setName(dataStream.getName() + " (Stream Relay)");
          dsRelayContainer.setInputGrounding(new EventGrounding(dataStream.getEventGrounding()));
          dsRelayContainer.setDeploymentTargetNodeId(dataStream.getDeploymentTargetNodeId());
          dsRelayContainer.setDeploymentTargetNodeHostname(dataStream.getDeploymentTargetNodeHostname());
          dsRelayContainer.setDeploymentTargetNodePort(dataStream.getDeploymentTargetNodePort());
          dsRelayContainer.setOutputStreamRelays(dataStreamRelays);
          relays.put(pred, dsRelayContainer);
        }
      }
    });

  }

  private List<Tuple2<DataProcessorInvocation, DataProcessorInvocation>> getDelta(Pipeline pipelineX, Pipeline pipelineY){
    List<Tuple2<DataProcessorInvocation, DataProcessorInvocation>> delta = new ArrayList<>();
    pipelineX.getSepas().forEach(iX -> {
      if (pipelineY.getSepas().stream().filter(iY -> iY.getElementId().equals(iX.getElementId())).
              filter(iY -> iY.getDeploymentTargetNodeId().equals(iX.getDeploymentTargetNodeId())).count() == 0){
        Optional<DataProcessorInvocation> invocationY = pipelineY.getSepas().stream().
                filter(iY -> iY.getDeploymentRunningInstanceId().equals(iX.getDeploymentRunningInstanceId())).findFirst();
        invocationY.ifPresent(dataProcessorInvocation -> delta.add(new Tuple2<>(iX, dataProcessorInvocation)));
      }
    });
    return delta;
  }

  private List<NamedStreamPipesEntity> getPredecessors(NamedStreamPipesEntity source,
                                                           InvocableStreamPipesEntity target, PipelineGraph pipelineGraph,
                                                           List<NamedStreamPipesEntity> foundPredecessors){
    //TODO: Check if this works for all graph topologies
    if (pipelineGraph.outgoingEdgesOf(source)
            .stream()
            .map(pipelineGraph::getEdgeTarget)
            .map(g -> (InvocableStreamPipesEntity) g)
            .collect(Collectors.toSet()).contains(target)){
      foundPredecessors.add(source);
    } else{
      List<NamedStreamPipesEntity> successors = pipelineGraph.outgoingEdgesOf(source)
              .stream()
              .map(pipelineGraph::getEdgeTarget)
              .map(g -> (InvocableStreamPipesEntity) g)
              .collect(Collectors.toList());
      if (successors.isEmpty()) return foundPredecessors;
      successors.forEach(successor -> getPredecessors(successor, target, pipelineGraph, foundPredecessors));
    }
    return foundPredecessors;
  }

  private NamedStreamPipesEntity findMatching(NamedStreamPipesEntity entity, PipelineGraph pipelineGraph){
    AtomicReference<NamedStreamPipesEntity> match = new AtomicReference<>();
    PipelineGraphHelpers.findStreams(pipelineGraph).forEach(ds -> {
    NamedStreamPipesEntity foundEntity = compareGraphs(ds, entity, pipelineGraph, new ArrayList<>());
    if (foundEntity != null) match.set(foundEntity);
    });
    return match.get();
  }

  private NamedStreamPipesEntity compareGraphs(NamedStreamPipesEntity source, NamedStreamPipesEntity searchedEntity, PipelineGraph pipelineGraph, List<NamedStreamPipesEntity> successors){
    if(source.getDOM().equals(searchedEntity.getDOM())) return source;
    else if (successors.isEmpty())
      successors = pipelineGraph.outgoingEdgesOf(source)
            .stream()
            .map(pipelineGraph::getEdgeTarget)
            .map(g -> (InvocableStreamPipesEntity) g)
            .collect(Collectors.toList());
    Optional<NamedStreamPipesEntity> successor= successors.stream().findFirst();
    if (successor.isPresent()) {
      successors.remove(successor.get());
      return compareGraphs(successor.get(), searchedEntity, pipelineGraph, successors);
    }
    return null;
  }

  private PipelineOperationStatus initPipelineOperationStatus(String pipelineId, String pipelineName) {
    //Duplicate from method in GraphSubmitter
    PipelineOperationStatus status = new PipelineOperationStatus();
    status.setPipelineId(pipelineId);
    status.setPipelineName(pipelineName);
    return status;
  }

  private PipelineOperationStatus verifyPipelineOperationStatus(PipelineOperationStatus status, String successMessage,
                                                                String errorMessage) {
    //Duplicate from method in GraphSubmitter
    status.setSuccess(status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess));
    if (status.isSuccess()) {
      status.setTitle(successMessage);
    } else {
      status.setTitle(errorMessage);
    }
    return status;
  }

  private String extractTopic(EventGrounding eg) {
    return eg.getTransportProtocol().getTopicDefinition().getActualTopicName();
  }

  private List<SpDataStreamRelayContainer> generateDataStreamRelays(List<InvocableStreamPipesEntity> graphs) {
    Set<String> topicSet = new HashSet<>();
    List<SpDataStreamRelayContainer> dsRelayContainers = new ArrayList<>();
    SpDataStreamRelayContainer dsRelayContainer = new SpDataStreamRelayContainer();
    List<SpDataStreamRelay> dataStreamRelays = new ArrayList<>();

    graphs.stream()
            .filter(g -> pipeline.getStreams().stream()
                    .filter(ds -> topicSet.add(extractTopic(ds.getEventGrounding())))
                    .anyMatch(ds -> (!matchingDeploymentTargets(ds, g) && connected(ds, g))))
            .forEach(g -> pipeline.getStreams()
                    .forEach(ds -> {
                      dsRelayContainer.setRunningStreamRelayInstanceId(pipeline.getPipelineId());
                      dsRelayContainer.setEventRelayStrategy(pipeline.getEventRelayStrategy());
                      dsRelayContainer.setName(ds.getName() + " (Stream Relay)");
                      dsRelayContainer.setInputGrounding(new EventGrounding(ds.getEventGrounding()));
                      dsRelayContainer.setDeploymentTargetNodeId(ds.getDeploymentTargetNodeId());
                      dsRelayContainer.setDeploymentTargetNodeHostname(ds.getDeploymentTargetNodeHostname());
                      dsRelayContainer.setDeploymentTargetNodePort(ds.getDeploymentTargetNodePort());

                      dataStreamRelays.add(new SpDataStreamRelay(new EventGrounding(g.getInputStreams()
                              .get(getIndex(ds.getDOM(), g))
                              .getEventGrounding())));
                    })
            );

    dsRelayContainer.setOutputStreamRelays(dataStreamRelays);
    dsRelayContainers.add(dsRelayContainer);

    return dsRelayContainers;
  }

  private boolean matchingDeploymentTargets(SpDataStream source, InvocableStreamPipesEntity target) {
    return source.getDeploymentTargetNodeId().equals(target.getDeploymentTargetNodeId());
  }

  private boolean connected(SpDataStream source, InvocableStreamPipesEntity target) {
    int index = getIndex(source.getDOM(), target);
    if (index != -1) {
      return target.getConnectedTo().get(index).equals(source.getDOM());
    }
    return false;
  }

  private List<InvocableStreamPipesEntity> decryptSecrets(List<InvocableStreamPipesEntity> graphs) {
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

  private void setPipelineStarted(Pipeline pipeline) {
    pipeline.setRunning(true);
    pipeline.setStartedAt(new Date().getTime());
    try {
      getPipelineStorageApi().updatePipeline(pipeline);
    } catch (DocumentConflictException dce) {
      //dce.printStackTrace();
    }
  }

  private void setPipelineStopped(Pipeline pipeline) {
    pipeline.setRunning(false);
    getPipelineStorageApi().updatePipeline(pipeline);
  }

  private void storeInvocationGraphs(String pipelineId, List<InvocableStreamPipesEntity> graphs,
                                     List<SpDataSet> dataSets) {
    TemporaryGraphStorage.graphStorage.put(pipelineId, graphs);
    TemporaryGraphStorage.datasetStorage.put(pipelineId, dataSets);
  }

  private IPipelineStorage getPipelineStorageApi() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
  }

  private Integer getIndex(String sourceDomId, InvocableStreamPipesEntity targetElement) {
    return targetElement.getConnectedTo().indexOf(sourceDomId);
  }

}
