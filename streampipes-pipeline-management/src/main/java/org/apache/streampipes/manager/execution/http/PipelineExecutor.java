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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamRelay;
import org.apache.streampipes.model.SpDataStreamRelayContainer;
import org.apache.streampipes.model.grounding.EventGrounding;
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
