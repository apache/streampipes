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
import org.apache.streampipes.manager.execution.http.GraphSubmitter;
import org.apache.streampipes.manager.execution.pipeline.executor.*;
import org.apache.streampipes.manager.execution.pipeline.executor.operations.types.MigrationOperation;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.CommunicationUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.PipelineElementUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.RelayUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartTargetPipelineElementsOperation extends PipelineExecutionOperation implements MigrationOperation {

    public StartTargetPipelineElementsOperation(PipelineExecutor pipelineExecutor){
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        long nanoTimeBeforeOperation = System.nanoTime();
        List<InvocableStreamPipesEntity> decryptedGraphs =
                PipelineElementUtils.decryptSecrets(
                        Collections.singletonList(associatedPipelineExecutor.getMigrationEntity().getTargetElement()),
                        associatedPipelineExecutor.getPipeline());
        List<SpDataStreamRelayContainer> relays = PipelineElementUtils.extractRelaysFromDataProcessor(decryptedGraphs);

        RelayUtils.updateRelays(relays, associatedPipelineExecutor.getRelaysToBePersisted());

        //State needs to be deleted to not store it
        associatedPipelineExecutor.getMigrationEntity().getTargetElement().setState(null);

        PipelineOperationStatus status = CommunicationUtils.
                startPipelineElementsAndRelays(decryptedGraphs, relays, associatedPipelineExecutor.getPipeline());

        long duration = System.nanoTime() - nanoTimeBeforeOperation;
        EvaluationLogger.getInstance().logMQTT("Migration", "start target element", "", duration, duration/1000000000.0);

        return status;
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        List<InvocableStreamPipesEntity> graphs =
                Collections.singletonList(associatedPipelineExecutor.getMigrationEntity().getTargetElement());
        List<SpDataStreamRelayContainer> relays = PipelineElementUtils.extractRelaysFromDataProcessor(graphs);

        return new GraphSubmitter(associatedPipelineExecutor.getPipeline().getPipelineId(),
                associatedPipelineExecutor.getPipeline().getName(), graphs, new ArrayList<>(), relays)
                .detachPipelineElementsAndRelays();
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        return StatusUtils.initPipelineOperationStatus(associatedPipelineExecutor.getPipeline());
    }
}
