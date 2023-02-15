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

package org.apache.streampipes.manager.execution.task;

import org.apache.streampipes.manager.execution.PipelineExecutionInfo;
import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.manager.storage.RunningPipelineElementStorage;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.message.PipelineStatusMessage;
import org.apache.streampipes.model.message.PipelineStatusMessageType;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.List;

public class AfterInvocationTask implements PipelineExecutionTask {

  private final PipelineStatusMessageType statusMessageType;

  public AfterInvocationTask(PipelineStatusMessageType statusMessageType) {
    this.statusMessageType = statusMessageType;
  }

  @Override
  public boolean shouldExecute(PipelineExecutionInfo executionInfo) {
    return executionInfo.isOperationSuccessful();
  }

  @Override
  public void executeTask(Pipeline pipeline,
                          PipelineExecutionInfo executionInfo) {
    var graphs = executionInfo.getProcessorsAndSinks();
    storeInvocationGraphs(pipeline.getPipelineId(), graphs);
    addPipelineStatus(pipeline);
  }

  private void storeInvocationGraphs(String pipelineId,
                                     List<InvocableStreamPipesEntity> graphs) {
    RunningPipelineElementStorage.runningProcessorsAndSinks.put(pipelineId, graphs);
  }

  private void addPipelineStatus(Pipeline pipeline) {
    PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(),
        new PipelineStatusMessage(pipeline.getPipelineId(), System.currentTimeMillis(), statusMessageType));
  }
}
