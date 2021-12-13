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
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

public abstract class PipelineExecutionOperation {

    protected final PipelineExecutor pipelineExecutor;

    private PipelineOperationStatus status;

    public PipelineExecutionOperation(PipelineExecutor pipelineExecutor){
        this.pipelineExecutor = pipelineExecutor;
        this.status = StatusUtils.initPipelineOperationStatus(this.pipelineExecutor.getPipeline());
    }

    public abstract PipelineOperationStatus executeOperation();

    public abstract PipelineOperationStatus rollbackOperationPartially();

    public abstract PipelineOperationStatus rollbackOperationFully();

    public PipelineOperationStatus rollbackOperation(){
        if(status.isSuccess()){
            return rollbackOperationFully();
        }
        return rollbackOperationPartially();
    }

    public PipelineOperationStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineOperationStatus status){
        this.status = status;
    }
}
