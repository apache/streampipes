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
import org.apache.streampipes.manager.execution.http.PipelineElementSubmitter;
import org.apache.streampipes.manager.execution.provider.PipelineElementProvider;
import org.apache.streampipes.model.pipeline.Pipeline;

public class SubmitRequestTask implements PipelineExecutionTask {

  private final PipelineElementProvider elementProvider;
  private final PipelineElementSubmitter submitter;

  public SubmitRequestTask(PipelineElementSubmitter submitter,
                           PipelineElementProvider elementProvider) {
    this.elementProvider = elementProvider;
    this.submitter = submitter;
  }

  @Override
  public boolean shouldExecute(PipelineExecutionInfo executionInfo) {
    return executionInfo.getFailedServices().size() == 0;
  }

  @Override
  public void executeTask(Pipeline pipeline, PipelineExecutionInfo executionInfo) {
    var processorsAndSinks = elementProvider.getProcessorsAndSinks(executionInfo);

    var status = submitter.submit(processorsAndSinks);

    executionInfo.applyPipelineOperationStatus(status);
  }
}
