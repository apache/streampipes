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

package org.apache.streampipes.manager.execution;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PipelineExecutionInfo {

  private final List<NamedStreamPipesEntity> failedServices;
  private final List<InvocableStreamPipesEntity> processorsAndSinks;
  private PipelineOperationStatus pipelineOperationStatus;

  private final String pipelineId;

  public static PipelineExecutionInfo create(Pipeline pipeline) {
    return new PipelineExecutionInfo(pipeline);
  }

  private PipelineExecutionInfo(Pipeline pipeline) {
    this.failedServices = new ArrayList<>();
    this.processorsAndSinks = findProcessorsAndSinks(pipeline);
    this.pipelineOperationStatus = new PipelineOperationStatus();
    this.pipelineId = pipeline.getPipelineId();
  }

  private List<InvocableStreamPipesEntity> findProcessorsAndSinks(Pipeline pipeline) {
    return Stream
        .concat(
            pipeline.getSepas().stream(),
            pipeline.getActions().stream()
        ).collect(Collectors.toList());
  }

  public void addFailedPipelineElement(NamedStreamPipesEntity failedElement) {
    this.failedServices.add(failedElement);
  }

  public List<NamedStreamPipesEntity> getFailedServices() {
    return failedServices;
  }

  public List<InvocableStreamPipesEntity> getProcessorsAndSinks() {
    return processorsAndSinks;
  }

  public void applyPipelineOperationStatus(PipelineOperationStatus status) {
    this.pipelineOperationStatus = status;
  }

  public PipelineOperationStatus getPipelineOperationStatus() {
    return this.pipelineOperationStatus;
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public boolean isOperationSuccessful() {
    return failedServices.size() == 0 && pipelineOperationStatus.isSuccess();
  }
}
