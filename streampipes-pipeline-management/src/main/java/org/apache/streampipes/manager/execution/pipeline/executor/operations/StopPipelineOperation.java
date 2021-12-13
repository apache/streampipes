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

import org.apache.streampipes.manager.execution.http.GraphSubmitter;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.*;
import org.apache.streampipes.manager.execution.status.PipelineStatusManager;
import org.apache.streampipes.manager.util.TemporaryGraphStorage;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.message.PipelineStatusMessage;
import org.apache.streampipes.model.message.PipelineStatusMessageType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.List;
import java.util.Set;

public class StopPipelineOperation extends PipelineExecutionOperation {

    public StopPipelineOperation(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        Pipeline pipeline = pipelineExecutor.getPipeline();
        List<InvocableStreamPipesEntity> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
        List<SpDataSet> dataSets = TemporaryGraphStorage.datasetStorage.get(pipeline.getPipelineId());
        List<SpDataStreamRelayContainer> relays = PipelineElementUtils.generateRelays(graphs, pipeline);

        PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs,
                dataSets, relays).detachPipelineElementsAndRelays();

        if (status.isSuccess()) {
            if (pipelineExecutor.isMonitor()) StorageUtils.deleteVisualization(pipeline.getPipelineId());
            if (pipelineExecutor.isStoreStatus()) StorageUtils.setPipelineStopped(pipeline);

            StorageUtils.deleteDataStreamRelayContainer(relays);

            PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(),
                    new PipelineStatusMessage(pipeline.getPipelineId(),
                            System.currentTimeMillis(),
                            PipelineStatusMessageType.PIPELINE_STOPPED.title(),
                            PipelineStatusMessageType.PIPELINE_STOPPED.description()));
        }
        return status;
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        Pipeline pipeline = pipelineExecutor.getPipeline();

        Set<String> idsToRollback = StatusUtils.extractUniqueSuccessfulIds(this.getStatus());

        List<InvocableStreamPipesEntity> graphsToRollBack =
                PipelineElementUtils.filterPipelineElementsById(
                        pipelineExecutor.getGraphs().getEntitiesToStop(),
                        idsToRollback);
        List<SpDataSet> dataSetsToRollBack =
                DataSetUtils.filterDataSetsById(
                        pipelineExecutor.getDataSets().getEntitiesToStart(),
                        idsToRollback);
        List<SpDataStreamRelayContainer> relaysToRollBack =
                RelayUtils.filterRelaysById(
                        pipelineExecutor.getRelays().getEntitiesToStart(),
                        idsToRollback);

        return new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(),
                graphsToRollBack,
                dataSetsToRollBack,
                relaysToRollBack).invokePipelineElementsAndRelays();
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        //TODO: Implement sth?
        return StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
    }
}
