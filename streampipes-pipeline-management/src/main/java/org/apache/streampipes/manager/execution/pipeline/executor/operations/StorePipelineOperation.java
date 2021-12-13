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
package org.apache.streampipes.manager.execution.pipeline.executor.operations;

import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StorageUtils;
import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.model.message.PipelineStatusMessage;
import org.apache.streampipes.model.message.PipelineStatusMessageType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

public class StorePipelineOperation extends PipelineExecutionOperation{

    public StorePipelineOperation(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        PipelineOperationStatus status = StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
        Pipeline pipeline = pipelineExecutor.getPipeline();
        try{
            StorageUtils.storeInvocationGraphs(pipeline.getPipelineId(), pipelineExecutor.getGraphs().getEntitiesToStore(),
                    pipelineExecutor.getDataSets().getEntitiesToStore());
            StorageUtils.storeDataStreamRelayContainer(pipelineExecutor.getRelays().getEntitiesToStore());

            PipelineStatusManager.addPipelineStatus(
                    pipeline.getPipelineId(),
                    new PipelineStatusMessage(pipeline.getPipelineId(),
                            System.currentTimeMillis(),
                            PipelineStatusMessageType.PIPELINE_STARTED.title(),
                            PipelineStatusMessageType.PIPELINE_STARTED.description()));

            if (pipelineExecutor.isStoreStatus()) StorageUtils.setPipelineStarted(pipeline);
        }catch (Exception e){
            status.setSuccess(false);
            status.setTitle(e.getMessage());
        }

        return status;
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        return StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        return StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
    }
}
