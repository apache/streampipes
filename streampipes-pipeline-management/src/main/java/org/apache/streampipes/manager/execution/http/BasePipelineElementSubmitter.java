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

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.resource.management.SpResourceManager;

import java.util.List;

public abstract class BasePipelineElementSubmitter {

  protected final String pipelineId;
  protected final String pipelineName;

  protected final PipelineOperationStatus status;
  protected final ExtensionServiceRequestManager requestManager;
  protected final SpResourceManager resourceManager;

  public BasePipelineElementSubmitter(Pipeline pipeline,
                                      ExtensionServiceRequestManager requestManager,
                                      SpResourceManager resourceManager) {
    this.pipelineId = pipeline.getPipelineId();
    this.pipelineName = pipeline.getName();
    this.status = new PipelineOperationStatus(pipelineId, pipelineName);
    this.requestManager = requestManager;
    this.resourceManager = resourceManager;
  }

  public PipelineOperationStatus submit(List<InvocableStreamPipesEntity> processorsAndSinks) {
    // First, try handling all data processors and sinks
    processorsAndSinks.forEach(g -> {
      var response = submitElement(g);
      status.addPipelineElementStatus(response);
    });

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

  protected PipelineElementStatus performDetach(InvocableStreamPipesEntity pipelineElement) {
    return new DetachExtensionRequest(requestManager, resourceManager).execute(pipelineElement, this.pipelineId);
  }

  protected abstract PipelineElementStatus submitElement(InvocableStreamPipesEntity pipelineElement);

  protected abstract void onSuccess();

  protected abstract void onFailure(List<InvocableStreamPipesEntity> processorsAndSinks);
}
