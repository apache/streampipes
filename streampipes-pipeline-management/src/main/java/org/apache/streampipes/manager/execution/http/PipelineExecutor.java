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

import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.manager.util.TemporaryGraphStorage;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.message.PipelineStatusMessage;
import org.apache.streampipes.model.message.PipelineStatusMessageType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.encryption.CredentialsManager;
import org.lightcouch.DocumentConflictException;

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

    pipeline.getSepas().forEach(this::updateGroupIds);
    pipeline.getActions().forEach(this::updateGroupIds);

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

    graphs.forEach(g -> g.setStreamRequirements(Collections.emptyList()));

    PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(),
            pipeline.getName(), decryptedGraphs, dataSets)
            .invokeGraphs();

    if (status.isSuccess()) {
      storeInvocationGraphs(pipeline.getPipelineId(), graphs, dataSets);

      PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(),
              new PipelineStatusMessage(pipeline.getPipelineId(), System.currentTimeMillis(), PipelineStatusMessageType.PIPELINE_STARTED.title(), PipelineStatusMessageType.PIPELINE_STARTED.description()));

      if (storeStatus) {
        setPipelineStarted(pipeline);
      }
    }
    return status;
  }

  private void updateGroupIds(InvocableStreamPipesEntity entity) {
    entity.getInputStreams()
            .stream()
            .filter(is -> is.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol)
            .map(is -> is.getEventGrounding().getTransportProtocol())
            .map(KafkaTransportProtocol.class::cast)
            .forEach(tp -> tp.setGroupId(UUID.randomUUID().toString()));
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

  public PipelineOperationStatus stopPipeline() {
    List<InvocableStreamPipesEntity> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
    List<SpDataSet> dataSets = TemporaryGraphStorage.datasetStorage.get(pipeline.getPipelineId());

    PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(),
            pipeline.getName(),  graphs, dataSets)
            .detachGraphs();

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

    }
    return status;
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

}
