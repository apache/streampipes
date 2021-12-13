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

import org.apache.streampipes.manager.execution.pipeline.executor.operations.LifecycleEntity;
import org.apache.streampipes.manager.execution.pipeline.executor.operations.PipelineExecutionOperation;
import org.apache.streampipes.manager.execution.pipeline.executor.operations.types.MigrationOperation;
import org.apache.streampipes.manager.execution.pipeline.executor.operations.types.ReconfigurationOperation;
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

    private final LifecycleEntity<InvocableStreamPipesEntity> graphs;

    private final LifecycleEntity<SpDataStreamRelayContainer> relays;

    private final LifecycleEntity<SpDataSet> dataSets;


    private PipelineOperationStatus status;

    private final LinkedList<PipelineExecutionOperation> operations = new LinkedList<>();

    public PipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor){
        this.pipeline = pipeline;
        this.visualize = visualize;
        this.storeStatus = storeStatus;
        this.monitor = monitor;
        this.status = StatusUtils.initPipelineOperationStatus(pipeline);

        this.graphs = new LifecycleEntity<>();
        this.relays = new LifecycleEntity<>();
        this.dataSets = new LifecycleEntity<>();
    }

    public PipelineOperationStatus execute(){
        for(PipelineExecutionOperation pipelineExecutionOperation: this.operations){
            PipelineOperationStatus operationStatus = pipelineExecutionOperation.executeOperation();
            StatusUtils.checkSuccess(operationStatus);
            pipelineExecutionOperation.setStatus(operationStatus);
            StatusUtils.updateStatus(operationStatus, this.status);
            if(!operationStatus.isSuccess()){
                rollback(pipelineExecutionOperation);
                break;
            }
        }
        StatusUtils.checkSuccess(this.status);
        return this.status;
    }

    private void rollback(PipelineExecutionOperation failedOperation){
        PipelineOperationStatus rollbackStatus = StatusUtils.initPipelineOperationStatus(pipeline);
        for(int currentOperationIndex = this.operations.indexOf(failedOperation);
            currentOperationIndex<=0; currentOperationIndex--){
            PipelineExecutionOperation currentOperation = this.operations.get(currentOperationIndex);
            PipelineOperationStatus rollbackOperationStatus = currentOperation.rollbackOperation();
            StatusUtils.checkSuccess(rollbackOperationStatus);
            StatusUtils.updateStatus(rollbackOperationStatus, rollbackStatus);
        }
        StatusUtils.checkSuccess(rollbackStatus);
        StatusUtils.updateStatus(rollbackStatus, this.status);
    }

    public void addOperation(PipelineExecutionOperation operation){
        this.operations.add(operation);
    }

    public boolean containsReconfigurationOperation(){
        return this.operations.stream().anyMatch(operation -> operation instanceof ReconfigurationOperation);
    }

    public boolean containsMigrationOperation(){
        return this.operations.stream().anyMatch(operation -> operation instanceof MigrationOperation);
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

    public LifecycleEntity<InvocableStreamPipesEntity> getGraphs() {
        return graphs;
    }

    public LifecycleEntity<SpDataStreamRelayContainer> getRelays() {
        return relays;
    }

    public LifecycleEntity<SpDataSet> getDataSets() {
        return dataSets;
    }
}
