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
package org.apache.streampipes.manager.execution.pipeline.executor;

import org.apache.streampipes.manager.execution.pipeline.executor.steps.EntitiesLifecycleObject;
import org.apache.streampipes.manager.execution.pipeline.executor.steps.PipelineExecutionStep;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementMigrationEntity;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.LinkedList;

public class PipelineExecutor {

    private Pipeline pipeline;
    private boolean visualize;
    private boolean storeStatus;
    private boolean monitor;

    private Pipeline secondaryPipeline;

    /**
     * Pipeline element to be migrated. Contains pair of source and target element description
     */
    private PipelineElementMigrationEntity migrationEntity;

    private PipelineElementReconfigurationEntity reconfigurationEntity;

    private final EntitiesLifecycleObject<InvocableStreamPipesEntity> graphs;

    private final EntitiesLifecycleObject<SpDataStreamRelayContainer> relays;

    private final EntitiesLifecycleObject<SpDataSet> dataSets;


    private PipelineOperationStatus status;

    private final LinkedList<PipelineExecutionStep> operations = new LinkedList<>();

    public PipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor){
        this.pipeline = pipeline;
        this.visualize = visualize;
        this.storeStatus = storeStatus;
        this.monitor = monitor;
        this.status = StatusUtils.initPipelineOperationStatus(pipeline);

        this.graphs = new EntitiesLifecycleObject<>();
        this.relays = new EntitiesLifecycleObject<>();
        this.dataSets = new EntitiesLifecycleObject<>();
    }

    public PipelineOperationStatus execute(){
        for(PipelineExecutionStep pipelineExecutionStep : this.operations){
            PipelineOperationStatus operationStatus = pipelineExecutionStep.executeOperation();
            StatusUtils.checkSuccess(operationStatus);
            pipelineExecutionStep.setStatus(operationStatus);
            StatusUtils.updateStatus(operationStatus, this.status);
            if(!operationStatus.isSuccess()){
                rollback(pipelineExecutionStep);
                break;
            }
        }
        StatusUtils.checkSuccess(this.status);
        return this.status;
    }

    private void rollback(PipelineExecutionStep failedOperation){
        PipelineOperationStatus rollbackStatus = StatusUtils.initPipelineOperationStatus(pipeline);
        for(int currentOperationIndex = this.operations.indexOf(failedOperation);
            currentOperationIndex>=0; currentOperationIndex--){
            PipelineExecutionStep currentOperation = this.operations.get(currentOperationIndex);
            PipelineOperationStatus rollbackOperationStatus = currentOperation.rollbackOperation();
            StatusUtils.checkSuccess(rollbackOperationStatus);
            StatusUtils.updateStatus(rollbackOperationStatus, rollbackStatus);
        }
        StatusUtils.checkSuccess(rollbackStatus);
        StatusUtils.updateStatus(rollbackStatus, this.status);
    }

    public void addStep(PipelineExecutionStep operation){
        this.operations.add(operation);
    }


    //Getter and Setter

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public boolean isVisualize() {
        return visualize;
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

    public boolean isStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(boolean storeStatus) {
        this.storeStatus = storeStatus;
    }

    public boolean isMonitor() {
        return monitor;
    }

    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }

    public Pipeline getSecondaryPipeline() {
        return secondaryPipeline;
    }

    public void setSecondaryPipeline(Pipeline secondaryPipeline) {
        this.secondaryPipeline = secondaryPipeline;
    }

    public PipelineElementMigrationEntity getMigrationEntity() {
        return migrationEntity;
    }

    public void setMigrationEntity(PipelineElementMigrationEntity migrationEntity) {
        this.migrationEntity = migrationEntity;
    }

    public PipelineOperationStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineOperationStatus status) {
        this.status = status;
    }

    public PipelineElementReconfigurationEntity getReconfigurationEntity() {
        return reconfigurationEntity;
    }

    public void setReconfigurationEntity(PipelineElementReconfigurationEntity reconfigurationEntity) {
        this.reconfigurationEntity = reconfigurationEntity;
    }

    public EntitiesLifecycleObject<InvocableStreamPipesEntity> getGraphs() {
        return graphs;
    }

    public EntitiesLifecycleObject<SpDataStreamRelayContainer> getRelays() {
        return relays;
    }

    public EntitiesLifecycleObject<SpDataSet> getDataSets() {
        return dataSets;
    }
}
