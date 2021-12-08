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
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementMigrationEntity;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PipelineExecutor {

    private Pipeline pipeline;
    private boolean visualize;
    private boolean storeStatus;
    private boolean monitor;

    /**
     * Old pipeline before migration
     */
    private Pipeline secondaryPipeline;

    /**
     * Pipeline element to be migrated. Contains pair of source and target element description
     */
    private PipelineElementMigrationEntity migrationEntity;

    /**
     * Predecessors of the pipeline element to be migrated in the migration pipeline
     */
    private List<NamedStreamPipesEntity> predecessorsAfterMigration;

    /**
     * Predecessors of the pipeline element to be migrated in the old pipeline before migration
     */
    private List<NamedStreamPipesEntity> predecessorsBeforeMigration;

    /**
     * Collection of relays that were created in the migration process needing to be stored
     */
    private List<SpDataStreamRelayContainer> relaysToBePersisted;

    /**
     * Collection of relays that were removed in the migration process needing to be deleted from persistent storage.
     */
    private List<SpDataStreamRelayContainer> relaysToBeDeleted;

    private PipelineOperationStatus status;

    private PipelineElementReconfigurationEntity reconfigurationEntity;

    private final LinkedList<PipelineExecutionOperation> operations = new LinkedList<>();

    private List<SpDataSet> dataSets;

    private List<SpDataStreamRelayContainer> relays;

    private List<InvocableStreamPipesEntity> graphs;

    private final LifecycleEntity<InvocableStreamPipesEntity> grafs;

    private final LifecycleEntity<SpDataStreamRelayContainer> relais;

    private final LifecycleEntity<SpDataSet> dataZeds;

    public PipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor){
        this.pipeline = pipeline;
        this.visualize = visualize;
        this.storeStatus = storeStatus;
        this.monitor = monitor;
        this.predecessorsAfterMigration = new ArrayList<>();
        this.predecessorsBeforeMigration = new ArrayList<>();
        this.relaysToBePersisted = new ArrayList<>();
        this.relaysToBeDeleted = new ArrayList<>();
        this.status = StatusUtils.initPipelineOperationStatus(pipeline);

        this.grafs = new LifecycleEntity<>();
        this.relais = new LifecycleEntity<>();
        this.dataZeds = new LifecycleEntity<>();
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

    //Getter and Setter

    public void addOperation(PipelineExecutionOperation operation){
        this.operations.add(operation);
    }

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

    public List<NamedStreamPipesEntity> getPredecessorsAfterMigration() {
        return predecessorsAfterMigration;
    }

    public void setPredecessorsAfterMigration(List<NamedStreamPipesEntity> predecessorsAfterMigration) {
        this.predecessorsAfterMigration = predecessorsAfterMigration;
    }

    public List<NamedStreamPipesEntity> getPredecessorsBeforeMigration() {
        return predecessorsBeforeMigration;
    }

    public void setPredecessorsBeforeMigration(List<NamedStreamPipesEntity> predecessorsBeforeMigration) {
        this.predecessorsBeforeMigration = predecessorsBeforeMigration;
    }

    public List<SpDataStreamRelayContainer> getRelaysToBePersisted() {
        return relaysToBePersisted;
    }

    public void setRelaysToBePersisted(List<SpDataStreamRelayContainer> relaysToBePersisted) {
        this.relaysToBePersisted = relaysToBePersisted;
    }

    public List<SpDataStreamRelayContainer> getRelaysToBeDeleted() {
        return relaysToBeDeleted;
    }

    public void setRelaysToBeDeleted(List<SpDataStreamRelayContainer> relaysToBeDeleted) {
        this.relaysToBeDeleted = relaysToBeDeleted;
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

    public List<SpDataSet> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<SpDataSet> dataSets) {
        this.dataSets = dataSets;
    }

    public List<SpDataStreamRelayContainer> getRelays() {
        return relays;
    }

    public void setRelays(List<SpDataStreamRelayContainer> relays) {
        this.relays = relays;
    }

    public List<InvocableStreamPipesEntity> getGraphs() {
        return graphs;
    }

    public void setGraphs(List<InvocableStreamPipesEntity> graphs) {
        this.graphs = graphs;
    }


    public LifecycleEntity<InvocableStreamPipesEntity> getGrafs() {
        return grafs;
    }

    public LifecycleEntity<SpDataStreamRelayContainer> getRelais() {
        return relais;
    }

    public LifecycleEntity<SpDataSet> getDataZeds() {
        return dataZeds;
    }

    public boolean containsReconfigurationOperation(){
        return this.operations.stream().anyMatch(operation -> operation instanceof ReconfigurationOperation);
    }

    public boolean containsMigrationOperation(){
        return this.operations.stream().anyMatch(operation -> operation instanceof MigrationOperation);
    }
}
