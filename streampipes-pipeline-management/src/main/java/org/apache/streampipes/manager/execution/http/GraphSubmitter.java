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

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.client.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.client.pipeline.PipelineOperationStatus;

import java.util.List;
import java.util.Optional;

public class GraphSubmitter {

  private List<InvocableStreamPipesEntity> graphs;
  private List<SpDataSet> dataSets;

  private String pipelineId;
  private String pipelineName;

  private final static Logger LOG = LoggerFactory.getLogger(GraphSubmitter.class);

  private final String startSuccess;
  private final String startFailure;
  private final String stopSuccess;
  private final String stopFailure;

  public GraphSubmitter(String pipelineId, String pipelineName, List<InvocableStreamPipesEntity> graphs,
                        List<SpDataSet> dataSets) {
    this.graphs = graphs;
    this.pipelineId = pipelineId;
    this.pipelineName = pipelineName;
    this.dataSets = dataSets;
    this.startSuccess = "Pipeline " + pipelineName + " successfully started";
    this.startFailure = "Could not start pipeline" + pipelineName + ".";
    this.stopSuccess = "Pipeline " + pipelineName + " successfully stopped";
    this.stopFailure = "Could not stop all pipeline elements of pipeline " + pipelineName + ".";
  }

  public PipelineOperationStatus invokeGraphs() {
    PipelineOperationStatus status = initializeStatus();

    graphs.forEach(g -> invokeGraph(new InvocableEntityUrlGenerator(g), g, status));

    if (status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess)) {
      dataSets.forEach(dataSet -> invokeGraph(new DataSetEntityUrlGenerator(dataSet), dataSet, status));
    }

    processPipelineInvocationStatus(status, startSuccess, startFailure, true);

    return status;
  }

  public PipelineOperationStatus detachGraphs() {
    PipelineOperationStatus status = initializeStatus();

    graphs.forEach(g -> detachGraph(new InvocableEntityUrlGenerator(g), g, status));
    dataSets.forEach(dataSet -> detachGraph(new DataSetEntityUrlGenerator(dataSet), dataSet, status));

    processPipelineInvocationStatus(status, stopSuccess, stopFailure, false);

    return status;
  }

  private void invokeGraph(EndpointUrlGenerator<?> urlGenerator,
                           NamedStreamPipesEntity graph, PipelineOperationStatus status) {
    String endpointUrl = urlGenerator.generateStartPipelineEndpointUrl();
    PipelineElementStatus invocationStatus = new HttpRequestBuilder(graph, endpointUrl).invoke();
    status.addPipelineElementStatus(invocationStatus);
  }

  private void detachGraph(EndpointUrlGenerator<?> urlGenerator,
                           NamedStreamPipesEntity graph, PipelineOperationStatus status) {
    String endpointUrl = urlGenerator.generateStopPipelineEndpointUrl();
    PipelineElementStatus detachStatus = new HttpRequestBuilder(graph, endpointUrl).detach();
    status.addPipelineElementStatus(detachStatus);
  }

  private void rollbackInvokedPipelineElements(PipelineOperationStatus status) {
    for (PipelineElementStatus s : status.getElementStatus()) {
      if (s.isSuccess()) {
        Optional<InvocableStreamPipesEntity> graph = findGraph(s.getElementId());
        graph.ifPresent(g -> {
          LOG.info("Rolling back element " + g.getElementId());
          new HttpRequestBuilder(g, new InvocableEntityUrlGenerator(g).generateStopPipelineEndpointUrl()).detach();
        });
      }
    }
  }

  private Optional<InvocableStreamPipesEntity> findGraph(String elementId) {
    return graphs.stream().filter(g -> g.getBelongsTo().equals(elementId)).findFirst();
  }

  private void processPipelineInvocationStatus(PipelineOperationStatus status, String successMessage,
                                               String errorMessage, boolean rollback) {
    status.setSuccess(status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess));

    if (status.isSuccess()) {
      status.setTitle(successMessage);
    } else {
      if (rollback) {
        LOG.info("Could not start pipeline, initializing rollback...");
        rollbackInvokedPipelineElements(status);
      }
      status.setTitle(errorMessage);
    }
  }

  private PipelineOperationStatus initializeStatus() {
    PipelineOperationStatus status = new PipelineOperationStatus();
    status.setPipelineId(pipelineId);
    status.setPipelineName(pipelineName);

    return status;
  }
}
