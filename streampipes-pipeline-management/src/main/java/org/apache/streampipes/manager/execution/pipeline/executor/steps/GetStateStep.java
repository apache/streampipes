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
import org.apache.streampipes.manager.execution.pipeline.executor.utils.CommunicationUtils;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GetStateStep extends PipelineExecutionStep {

    public GetStateStep(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        long nanoTimeBeforeOperation = System.nanoTime();
        PipelineElementStatus statusGettingState = CommunicationUtils.getState(
                pipelineExecutor.getMigrationEntity().getSourceElement(),
                pipelineExecutor.getPipeline());
        if(statusGettingState.isSuccess()) {
            pipelineExecutor.getMigrationEntity().getTargetElement()
                    .setState(statusGettingState.getOptionalMessage());
            statusGettingState.setOptionalMessage("Successfully retrieved state");
        }
        PipelineOperationStatus getStateStatus = StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
        getStateStatus.addPipelineElementStatus(statusGettingState);
        StatusUtils.checkSuccess(getStateStatus);
        long duration = System.nanoTime() - nanoTimeBeforeOperation;
        EvaluationLogger.getInstance().logMQTT("Migration", "get state", "", duration, duration/1000000000.0);
        try {
            int stateSize = getSizeInBytes(pipelineExecutor.getMigrationEntity().getTargetElement().getState());
            EvaluationLogger.getInstance().logMQTT("Migration", "state size", stateSize/1024.0, stateSize/1048576.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getStateStatus;
    }

    private int getSizeInBytes(Object map) throws IOException {
        // Measuring the size by serializing it and then measuring the bytes
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteStream);

        out.writeObject(map);
        out.close();

        return byteStream.toByteArray().length;
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        pipelineExecutor.getMigrationEntity().getTargetElement().setState(null);
        return StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        pipelineExecutor.getMigrationEntity().getTargetElement().setState(null);
        return StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());
    }
}
