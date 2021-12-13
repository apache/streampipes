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

import org.apache.streampipes.logging.evaluation.EvaluationLogger;
import org.apache.streampipes.manager.execution.pipeline.executor.*;
import org.apache.streampipes.manager.execution.pipeline.executor.operations.types.MigrationOperation;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.*;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.List;
import java.util.Set;

public class StartGraphsAndAssociatedRelaysOperation extends PipelineExecutionOperation implements MigrationOperation {

    public StartGraphsAndAssociatedRelaysOperation(PipelineExecutor pipelineExecutor){
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        long nanoTimeBeforeOperation = System.nanoTime();
        List<InvocableStreamPipesEntity> graphs = pipelineExecutor.getGraphs().getEntitiesToStart();
        List<SpDataStreamRelayContainer> relays = PipelineElementUtils.extractRelaysFromDataProcessor(graphs);

        RelayUtils.updateRelays(relays, pipelineExecutor.getRelays().getEntitiesToStore());

        //State needs to be deleted to not store it in database
        pipelineExecutor.getGraphs().getEntitiesToStart().forEach(entity -> entity.setState(null));

        PipelineOperationStatus status = CommunicationUtils.
                startPipelineElementsAndRelays(graphs, relays, pipelineExecutor.getPipeline());

        long duration = System.nanoTime() - nanoTimeBeforeOperation;
        EvaluationLogger.getInstance().logMQTT("Migration", "start target element", "", duration, duration/1000000000.0);

        return status;
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        List<InvocableStreamPipesEntity> graphsToRollBack = pipelineExecutor.getGraphs().getEntitiesToStart();
        List<SpDataStreamRelayContainer> relaysToRollBack = PipelineElementUtils.extractRelaysFromDataProcessor(graphsToRollBack);

        return CommunicationUtils.stopPipelineElementsAndRelays(graphsToRollBack,
                relaysToRollBack, pipelineExecutor.getPipeline());
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        Set<String> idsToRollback = StatusUtils.extractUniqueSuccessfulIds(this.getStatus());

        List<InvocableStreamPipesEntity> graphs = pipelineExecutor.getGraphs().getEntitiesToStart();
        List<SpDataStreamRelayContainer> relays = PipelineElementUtils.extractRelaysFromDataProcessor(graphs);

        List<InvocableStreamPipesEntity> graphsToRollBack =
                PipelineElementUtils.filterPipelineElementsById(graphs, idsToRollback);

        List<SpDataStreamRelayContainer> relaysToRollBack =
                RelayUtils.filterRelaysById(relays, idsToRollback);

        return CommunicationUtils.stopPipelineElementsAndRelays(graphsToRollBack,
                relaysToRollBack, pipelineExecutor.getPipeline());
    }
}
