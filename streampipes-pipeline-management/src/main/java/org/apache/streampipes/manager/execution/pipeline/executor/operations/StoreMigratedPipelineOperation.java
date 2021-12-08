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
import org.apache.streampipes.manager.execution.pipeline.executor.utils.PipelineUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StorageUtils;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.ArrayList;
import java.util.List;

public class StoreMigratedPipelineOperation extends PipelineExecutionOperation{

    public StoreMigratedPipelineOperation(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        List<InvocableStreamPipesEntity> graphs = new ArrayList<>();
        graphs.addAll(associatedPipelineExecutor.getPipeline().getActions());
        graphs.addAll(associatedPipelineExecutor.getPipeline().getSepas());

        List<SpDataSet> dataSets = PipelineUtils.findDataSets(associatedPipelineExecutor.getPipeline());

        // store new pipeline and relays
        StorageUtils.storeInvocationGraphs(associatedPipelineExecutor.getPipeline().getPipelineId(), graphs, dataSets);
        StorageUtils.deleteDataStreamRelayContainer(associatedPipelineExecutor.getRelaysToBeDeleted());
        StorageUtils.storeDataStreamRelayContainer(associatedPipelineExecutor.getRelaysToBePersisted());
        return StatusUtils.initPipelineOperationStatus(associatedPipelineExecutor.getPipeline());
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        return null;
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        return null;
    }
}
