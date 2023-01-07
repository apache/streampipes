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

import org.apache.streampipes.commons.MD5;
import org.apache.streampipes.commons.Utils;
import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.manager.util.TemporaryGraphStorage;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.message.PipelineStatusMessage;
import org.apache.streampipes.model.message.PipelineStatusMessageType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.lightcouch.DocumentConflictException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineExecutor {

  private final Pipeline pipeline;
  private final boolean storeStatus;
  private final boolean forceStop;

  public PipelineExecutor(Pipeline pipeline,
                          boolean storeStatus,
                          boolean forceStop) {
    this.pipeline = pipeline;
    this.storeStatus = storeStatus;
    this.forceStop = forceStop;
  }

  public PipelineOperationStatus startPipeline() {

    pipeline.getSepas().forEach(this::updateGroupIds);
    pipeline.getActions().forEach(this::updateGroupIds);

    List<DataProcessorInvocation> sepas = pipeline.getSepas();
    List<DataSinkInvocation> secs = pipeline.getActions();

    List<SpDataSet> dataSets = pipeline
        .getStreams()
        .stream()
        .filter(s -> s instanceof SpDataSet)
        .map(s -> new SpDataSet((SpDataSet) s))
        .collect(Collectors.toList());

    List<NamedStreamPipesEntity> failedServices = new ArrayList<>();

    dataSets.forEach(ds -> {
      ds.setCorrespondingPipeline(pipeline.getPipelineId());
      try {
        ds.setSelectedEndpointUrl(findSelectedEndpoint(ds));
      } catch (NoServiceEndpointsAvailableException e) {
        failedServices.add(ds);
      }
    });

    List<InvocableStreamPipesEntity> graphs = new ArrayList<>();
    graphs.addAll(sepas);
    graphs.addAll(secs);

    decryptSecrets(graphs);

    graphs.forEach(g -> {
      try {
        g.setSelectedEndpointUrl(findSelectedEndpoint(g));
        g.setCorrespondingPipeline(pipeline.getPipelineId());
      } catch (NoServiceEndpointsAvailableException e) {
        failedServices.add(g);
      }
    });

    PipelineOperationStatus status;
    if (failedServices.size() == 0) {

      status = new GraphSubmitter(pipeline.getPipelineId(),
          pipeline.getName(), graphs, dataSets)
          .invokeGraphs();

      encryptSecrets(graphs);

      if (status.isSuccess()) {
        storeInvocationGraphs(pipeline.getPipelineId(), graphs, dataSets);

        PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(),
            new PipelineStatusMessage(pipeline.getPipelineId(), System.currentTimeMillis(),
                PipelineStatusMessageType.PIPELINE_STARTED.title(),
                PipelineStatusMessageType.PIPELINE_STARTED.description()));

        if (storeStatus) {
          pipeline.setHealthStatus(PipelineHealthStatus.OK);
          setPipelineStarted(pipeline);
        }
      }
    } else {
      List<PipelineElementStatus> pe = failedServices.stream().map(fs ->
          new PipelineElementStatus(fs.getElementId(),
              fs.getName(),
              false,
              "No active supporting service found")).collect(Collectors.toList());
      status = new PipelineOperationStatus(pipeline.getPipelineId(),
          pipeline.getName(),
          "Could not start pipeline " + pipeline.getName() + ".",
          pe);
    }
    return status;
  }

  private String findSelectedEndpoint(InvocableStreamPipesEntity g) throws NoServiceEndpointsAvailableException {
    return new ExtensionsServiceEndpointGenerator(
        g.getAppId(),
        ExtensionsServiceEndpointUtils.getPipelineElementType(g))
        .getEndpointResourceUrl();
  }

  private String findSelectedEndpoint(SpDataSet ds) throws NoServiceEndpointsAvailableException {
    String appId = ds.getAppId() != null ? ds.getAppId() : ds.getCorrespondingAdapterId();
    if (ds.isInternallyManaged()) {
      return getConnectMasterSourcesUrl();
    } else {
      return new ExtensionsServiceEndpointGenerator(appId, SpServiceUrlProvider.DATA_SET)
          .getEndpointResourceUrl();
    }
  }

  private String getConnectMasterSourcesUrl() throws NoServiceEndpointsAvailableException {
    List<String> connectMasterEndpoints = SpServiceDiscovery.getServiceDiscovery()
        .getServiceEndpoints(DefaultSpServiceGroups.CORE, true,
            Collections.singletonList(DefaultSpServiceTags.CONNECT_MASTER.asString()));
    if (connectMasterEndpoints.size() > 0) {
      return connectMasterEndpoints.get(0) + GlobalStreamPipesConstants.CONNECT_MASTER_SOURCES_ENDPOINT;
    } else {
      throw new NoServiceEndpointsAvailableException("Could not find any available connect master service endpoint");
    }
  }

  private void updateGroupIds(InvocableStreamPipesEntity entity) {
    entity.getInputStreams()
        .stream()
        .filter(is -> is.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol)
        .map(is -> is.getEventGrounding().getTransportProtocol())
        .map(KafkaTransportProtocol.class::cast)
        .forEach(tp -> tp.setGroupId(Utils.filterSpecialChar(pipeline.getName()) + MD5.crypt(tp.getElementId())));
  }

  private void decryptSecrets(List<InvocableStreamPipesEntity> graphs) {
    SecretProvider.getDecryptionService().apply(graphs);
  }

  private void encryptSecrets(List<InvocableStreamPipesEntity> graphs) {
    SecretProvider.getEncryptionService().apply(graphs);
  }

  public PipelineOperationStatus stopPipeline() {
    List<InvocableStreamPipesEntity> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
    List<SpDataSet> dataSets = TemporaryGraphStorage.datasetStorage.get(pipeline.getPipelineId());

    PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(),
        pipeline.getName(), graphs, dataSets)
        .detachGraphs();

    if (status.isSuccess()) {
      PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(),
          new PipelineStatusMessage(pipeline.getPipelineId(),
              System.currentTimeMillis(),
              PipelineStatusMessageType.PIPELINE_STOPPED.title(),
              PipelineStatusMessageType.PIPELINE_STOPPED.description()));

    }

    if (status.isSuccess() || forceStop) {
      if (storeStatus) {
        setPipelineStopped(pipeline);
      }
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
