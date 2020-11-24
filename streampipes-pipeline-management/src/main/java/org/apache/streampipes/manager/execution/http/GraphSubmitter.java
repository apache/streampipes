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
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.List;
import java.util.Optional;

public class GraphSubmitter {

  private final static Logger LOG = LoggerFactory.getLogger(GraphSubmitter.class);

  private final List<InvocableStreamPipesEntity> graphs;
  private final List<SpDataSet> dataSets;
  private final String pipelineId;
  private final String pipelineName;

  public GraphSubmitter(String pipelineId, String pipelineName, List<InvocableStreamPipesEntity> graphs,
                        List<SpDataSet> dataSets) {
    this.graphs = graphs;
    this.pipelineId = pipelineId;
    this.pipelineName = pipelineName;
    this.dataSets = dataSets;
  }

  public PipelineOperationStatus invokeGraphs() {
    PipelineOperationStatus status = initPipelineOperationStatus();

    graphs.forEach(graph -> invoke(new InvocableEntityUrlGenerator(graph), graph, status));
    // only invoke datasets when following pipeline elements are started
    if (allInvocableEntitiesRunning(status)) {
        dataSets.forEach(dataset -> invoke(new DataSetEntityUrlGenerator(dataset), dataset, status));
    }

    return verifyPipelineOperationStatus(
            status,
            "Successfully started pipeline " + pipelineName,
            "Could not start pipeline" + pipelineName,
            true);
  }

  public PipelineOperationStatus detachGraphs() {
    PipelineOperationStatus status = initPipelineOperationStatus();

    graphs.forEach(graph -> detach(new InvocableEntityUrlGenerator(graph), graph, status));
    dataSets.forEach(dataset -> detach(new DataSetEntityUrlGenerator(dataset), dataset, status));

    return verifyPipelineOperationStatus(
            status,
            "Successfully stopped pipeline " + pipelineName,
            "Could not stop all pipeline elements of pipeline " + pipelineName,
            false);
  }

  private PipelineOperationStatus verifyPipelineOperationStatus(PipelineOperationStatus status, String successMessage,
                                             String errorMessage, boolean rollbackIfFailed) {
    status.setSuccess(status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess));

    if (status.isSuccess()) {
      status.setTitle(successMessage);
    } else {
      if (rollbackIfFailed) {
        LOG.info("Could not start pipeline, initializing rollback...");
        rollbackInvokedEntities(status);
      }
      status.setTitle(errorMessage);
    }
    return status;
  }

  private void rollbackInvokedEntities(PipelineOperationStatus status) {
    for (PipelineElementStatus s : status.getElementStatus()) {
      if (s.isSuccess()) {
        Optional<InvocableStreamPipesEntity> graphs = findGraphs(s.getElementId());
        graphs.ifPresent(graph -> {
          LOG.info("Rolling back element " + graph.getElementId());
          makeHttpRequest(new InvocableEntityUrlGenerator(graph), graph, "detach");
        });
      }
    }
  }

  private void invoke(EndpointUrlGenerator<?> urlGenerator,
                      NamedStreamPipesEntity namedEntity, PipelineOperationStatus status) {
    status.addPipelineElementStatus(makeHttpRequest(urlGenerator, namedEntity, "invoke"));
  }

  private void detach(EndpointUrlGenerator<?> urlGenerator,
                      NamedStreamPipesEntity namedEntity, PipelineOperationStatus status) {
    status.addPipelineElementStatus(makeHttpRequest(urlGenerator, namedEntity, "detach"));
  }

  // Helper methods

  private PipelineOperationStatus initPipelineOperationStatus() {
    PipelineOperationStatus status = new PipelineOperationStatus();
    status.setPipelineId(pipelineId);
    status.setPipelineName(pipelineName);
    return status;
  }

  private PipelineElementStatus makeHttpRequest(EndpointUrlGenerator<?> urlGenerator,
                                                NamedStreamPipesEntity namedEntity, String type) {
    switch (type) {
      case "invoke":
        return new HttpRequestBuilder(namedEntity, urlGenerator.generateInvokeEndpoint()).invoke();
      case "detach":
        return new HttpRequestBuilder(namedEntity, urlGenerator.generateDetachEndpoint()).detach();
      default:
        throw new IllegalArgumentException("Type not known: " + type);
    }
  }

  private Optional<InvocableStreamPipesEntity> findGraphs(String elementId) {
    return graphs.stream().filter(i -> i.getBelongsTo().equals(elementId)).findFirst();
  }

  private boolean allInvocableEntitiesRunning(PipelineOperationStatus status) {
    return status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess);
  }
}
