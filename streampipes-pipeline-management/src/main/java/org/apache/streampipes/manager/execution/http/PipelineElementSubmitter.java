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

import java.util.List;

public abstract class PipelineElementSubmitter {

  protected final String pipelineId;
  protected final String pipelineName;

  protected final PipelineOperationStatus status;

  public PipelineElementSubmitter(Pipeline pipeline) {
    this.pipelineId = pipeline.getPipelineId();
    this.pipelineName = pipeline.getName();
    this.status = new PipelineOperationStatus(pipelineId, pipelineName);
  }

  public PipelineOperationStatus submit(List<InvocableStreamPipesEntity> processorsAndSinks) {
    // First, try handling all data processors and sinks
    processorsAndSinks.forEach(g -> status.addPipelineElementStatus(submitElement(g)));

    applySuccess(processorsAndSinks);
    return status;
  }

  protected boolean isSuccess() {
    return status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess);
  }

  protected void applySuccess(List<InvocableStreamPipesEntity> processorsAndSinks) {
    status.setSuccess(isSuccess());
    if (status.isSuccess()) {
      this.onSuccess();
    } else {
      this.onFailure(processorsAndSinks);
    }
  }

  protected PipelineElementStatus performDetach(EndpointSelectable pipelineElement) {
    String endpointUrl = pipelineElement.getSelectedEndpointUrl() + pipelineElement.getDetachPath();
    return new DetachHttpRequest().execute(pipelineElement, endpointUrl, this.pipelineId);
  }

  protected abstract PipelineElementStatus submitElement(EndpointSelectable pipelineElement);

  protected abstract void onSuccess();

  protected abstract void onFailure(List<InvocableStreamPipesEntity> processorsAndSinks);
}
