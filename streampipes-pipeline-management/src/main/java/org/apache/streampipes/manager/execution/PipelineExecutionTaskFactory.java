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

import org.apache.streampipes.manager.execution.http.DetachPipelineElementSubmitter;
import org.apache.streampipes.manager.execution.http.InvokePipelineElementSubmitter;
import org.apache.streampipes.manager.execution.provider.CurrentPipelineElementProvider;
import org.apache.streampipes.manager.execution.provider.StoredPipelineElementProvider;
import org.apache.streampipes.manager.execution.task.AfterInvocationTask;
import org.apache.streampipes.manager.execution.task.DiscoverEndpointsTask;
import org.apache.streampipes.manager.execution.task.PipelineExecutionTask;
import org.apache.streampipes.manager.execution.task.SecretEncryptionTask;
import org.apache.streampipes.manager.execution.task.StorePipelineStatusTask;
import org.apache.streampipes.manager.execution.task.SubmitRequestTask;
import org.apache.streampipes.manager.execution.task.UpdateGroupIdTask;
import org.apache.streampipes.model.message.PipelineStatusMessageType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.secret.SecretProvider;

import java.util.List;

public class PipelineExecutionTaskFactory {

  public static List<PipelineExecutionTask> makeStartPipelineTasks(Pipeline pipeline) {
    return List.of(
        new UpdateGroupIdTask(),
        new SecretEncryptionTask(SecretProvider.getDecryptionService()),
        new DiscoverEndpointsTask(),
        new SubmitRequestTask(new InvokePipelineElementSubmitter(pipeline), new CurrentPipelineElementProvider()),
        new SecretEncryptionTask(SecretProvider.getEncryptionService()),
        new AfterInvocationTask(PipelineStatusMessageType.PIPELINE_STARTED),
        new StorePipelineStatusTask(true, false)
    );
  }

  public static List<PipelineExecutionTask> makeStopPipelineTasks(Pipeline pipeline,
                                                                  boolean forceStop) {
    return List.of(
        new SubmitRequestTask(new DetachPipelineElementSubmitter(pipeline), new StoredPipelineElementProvider()),
        new AfterInvocationTask(PipelineStatusMessageType.PIPELINE_STOPPED),
        new StorePipelineStatusTask(false, forceStop)
    );
  }
}
