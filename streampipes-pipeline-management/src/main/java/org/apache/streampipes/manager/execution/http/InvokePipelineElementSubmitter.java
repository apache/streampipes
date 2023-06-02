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

import org.apache.streampipes.model.api.EndpointSelectable;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class InvokePipelineElementSubmitter extends PipelineElementSubmitter {

  private static final Logger LOG = LoggerFactory.getLogger(InvokePipelineElementSubmitter.class);

  public InvokePipelineElementSubmitter(Pipeline pipeline) {
    super(pipeline);
  }

  @Override
  protected PipelineElementStatus submitElement(EndpointSelectable pipelineElement) {
    String endpointUrl = pipelineElement.getSelectedEndpointUrl();
    return new InvokeHttpRequest().execute(pipelineElement, endpointUrl, this.pipelineId);
  }

  @Override
  protected void onSuccess() {
    status.setTitle("Pipeline " + pipelineName + " successfully started");
  }

  @Override
  protected void onFailure(List<InvocableStreamPipesEntity> processorsAndSinks) {
    LOG.info("Could not start pipeline, initializing rollback...");
    rollbackInvokedPipelineElements(status, processorsAndSinks);
    status.setTitle("Could not start pipeline " + pipelineName + ".");
  }

  private void rollbackInvokedPipelineElements(PipelineOperationStatus status,
                                               List<InvocableStreamPipesEntity> pe) {
    for (PipelineElementStatus s : status.getElementStatus()) {
      if (s.isSuccess()) {
        Optional<InvocableStreamPipesEntity> graph = findPipelineElements(s.getElementId(), pe);
        graph.ifPresent(this::performDetach);
      }
    }
  }

  private Optional<InvocableStreamPipesEntity> findPipelineElements(String elementId,
                                                                    List<InvocableStreamPipesEntity> pe) {
    return pe
        .stream()
        .filter(g -> g.getBelongsTo().equals(elementId))
        .findFirst();
  }
}
