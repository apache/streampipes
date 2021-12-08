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

import org.apache.streampipes.manager.execution.http.ReconfigurationSubmitter;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.operations.types.ReconfigurationOperation;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

public class ReconfigureElementOperation extends PipelineExecutionOperation implements ReconfigurationOperation {

    public ReconfigureElementOperation(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        Pipeline reconfiguredPipeline = associatedPipelineExecutor.getSecondaryPipeline();
        return new ReconfigurationSubmitter(reconfiguredPipeline.getPipelineId(), reconfiguredPipeline.getName(),
                associatedPipelineExecutor.getReconfigurationEntity()).reconfigure();
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        return StatusUtils.initPipelineOperationStatus(associatedPipelineExecutor.getPipeline());
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        return StatusUtils.initPipelineOperationStatus(associatedPipelineExecutor.getPipeline());
    }
}
