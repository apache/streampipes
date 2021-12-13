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
package org.apache.streampipes.manager.migration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutorFactory;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementMigrationEntity;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PipelineElementMigrationHandler {

    private final PipelineOperationStatus pipelineMigrationStatus;
    private final Pipeline desiredPipeline;
    private final Pipeline migrationPipeline;
    private Pipeline currentPipeline;
    private final boolean visualize;
    private final boolean storeStatus;
    private final boolean monitor;

    public PipelineElementMigrationHandler(Pipeline desiredPipeline, boolean visualize, boolean storeStatus,
                                           boolean monitor) {
        this.pipelineMigrationStatus = new PipelineOperationStatus();
        this.desiredPipeline = desiredPipeline;
        this.currentPipeline = getPipelineById(desiredPipeline.getPipelineId());
        this.migrationPipeline = getPipelineById(desiredPipeline.getPipelineId());
        this.visualize = visualize;
        this.storeStatus = storeStatus;
        this.monitor = monitor;
    }

    public PipelineOperationStatus handlePipelineMigration() {
        migratePipelineElementOrRollback();
        return verifyPipelineMigrationStatus(pipelineMigrationStatus,
                "Successfully migrated Pipeline Elements in Pipeline " + desiredPipeline.getName(),
                "Could not migrate all Pipeline Elements in Pipeline " + desiredPipeline.getName());
    }

    private void migratePipelineElementOrRollback() {
        List<PipelineElementMigrationEntity> migrationEntityList = getPipelineDelta();

        migrationEntityList.forEach(entity -> {
            swapPipelineElement(migrationPipeline, entity);

            PipelineOperationStatus entityStatus = migratePipelineElement(entity);

            entityStatus.getElementStatus()
                    .forEach(this.pipelineMigrationStatus::addPipelineElementStatus);

            if (entityStatus.isSuccess()) {
                try {
                    currentPipeline = deepCopyPipeline(migrationPipeline);
                } catch (JsonProcessingException e) {
                    throw new SpRuntimeException("Could not deep copy pipeline for migration: " + e.getMessage(), e);
                }
            } else {
                PipelineElementMigrationEntity failedEntity =
                        new PipelineElementMigrationEntity(entity.getTargetElement(), entity.getSourceElement());

                swapPipelineElement(migrationPipeline, failedEntity);
            }
        });
        Operations.overwritePipeline(migrationPipeline);
    }

    private PipelineOperationStatus migratePipelineElement(PipelineElementMigrationEntity migrationEntity) {
        PipelineExecutor migrationExecutor = PipelineExecutorFactory.
                createMigrationExecutor(migrationPipeline, visualize, storeStatus, monitor, currentPipeline, migrationEntity);
        return migrationExecutor.execute();
    }


    // Helpers

    private Pipeline getPipelineById(String pipelineId) {
        return getPipelineStorageApi().getPipeline(pipelineId);
    }

    private IPipelineStorage getPipelineStorageApi() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
    }

    private List<PipelineElementMigrationEntity> getPipelineDelta(){
        List<PipelineElementMigrationEntity> delta = new ArrayList<>();
        desiredPipeline.getSepas().forEach(iX -> {
            if (migrationPipeline.getSepas().stream().filter(iY -> iY.getElementId().equals(iX.getElementId()))
                    .noneMatch(iY -> iY.getDeploymentTargetNodeId().equals(iX.getDeploymentTargetNodeId()))){
                Optional<DataProcessorInvocation> invocationY = migrationPipeline.getSepas().stream().
                        filter(iY -> iY.getDeploymentRunningInstanceId().equals(iX.getDeploymentRunningInstanceId())).findFirst();
                invocationY.ifPresent(dataProcessorInvocation -> delta.add(new PipelineElementMigrationEntity(dataProcessorInvocation, iX)));
            }
        });
        return delta;
    }

    private PipelineOperationStatus verifyPipelineMigrationStatus(PipelineOperationStatus status, String successMessage,
                                                                  String errorMessage) {
        //Duplicate from method in GraphSubmitter
        status.setSuccess(status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess));
        if (status.isSuccess()) {
            status.setTitle(successMessage);
        } else {
            status.setTitle(errorMessage);
        }
        return status;
    }

    private void swapPipelineElement(Pipeline exchangePipeline,
                                           PipelineElementMigrationEntity migrationEntity){
        if (migrationEntity.getTargetElement() instanceof DataProcessorInvocation){
            int index = exchangePipeline.getSepas().indexOf(migrationEntity.getSourceElement());
            exchangePipeline.getSepas().remove(index);
            exchangePipeline.getSepas().add(index, (DataProcessorInvocation) migrationEntity.getTargetElement());
        }
    }

    public static Pipeline deepCopyPipeline(Pipeline object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(objectMapper.writeValueAsString(object), Pipeline.class);
    }
}
