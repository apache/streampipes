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
package org.apache.streampipes.manager.execution.pipeline.executor.steps;

import org.apache.streampipes.logging.evaluation.EvaluationLogger;
import org.apache.streampipes.manager.execution.pipeline.executor.*;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.CommunicationUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.RelayUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.List;
import java.util.Set;

public class StopRelaysStep extends PipelineExecutionStep {

    public StopRelaysStep(PipelineExecutor pipelineExecutor){
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        long nanoTimeBeforeOperation = System.nanoTime();
        List<SpDataStreamRelayContainer> relays = pipelineExecutor.getRelays().getEntitiesToStop();

        PipelineOperationStatus statusStopRelays =
                CommunicationUtils.stopRelays(relays, pipelineExecutor.getPipeline());

        long duration = System.nanoTime() - nanoTimeBeforeOperation;
        EvaluationLogger.getInstance().logMQTT("Migration", "stop relay from origin", "", duration, duration/1000000000.0);

        return statusStopRelays;
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        Set<String> relayIdsToRollback = StatusUtils.extractUniqueSuccessfulIds(this.getStatus());
        List<SpDataStreamRelayContainer> rollbackRelays =
                RelayUtils.filterRelaysById(pipelineExecutor.getRelays().getEntitiesToStop(),
                        relayIdsToRollback);

        return CommunicationUtils.startRelays(rollbackRelays, pipelineExecutor.getPipeline());
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        List<SpDataStreamRelayContainer> rollbackRelays = pipelineExecutor.getRelays().getEntitiesToStop();
        return CommunicationUtils.startRelays(rollbackRelays, pipelineExecutor.getPipeline());
    }
}
