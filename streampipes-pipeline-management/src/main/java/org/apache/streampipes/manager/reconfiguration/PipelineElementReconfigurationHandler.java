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
package org.apache.streampipes.manager.reconfiguration;

import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutorFactory;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.*;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineElementReconfigurationHandler {

    private final PipelineOperationStatus pipelineReconfigurationStatus;
    private final Pipeline reconfiguredPipeline;
    private final Pipeline storedPipeline;

    public PipelineElementReconfigurationHandler(Pipeline reconfiguredPipeline) {
        this.reconfiguredPipeline = reconfiguredPipeline;
        this.storedPipeline = getPipelineById(reconfiguredPipeline.getPipelineId());
        this.pipelineReconfigurationStatus = initPipelineOperationStatus();
    }

    public PipelineOperationStatus handleReconfiguration() {
        reconfigurePipelineElementOrRollback();
        return verifyPipelineReconfigurationStatus(pipelineReconfigurationStatus,
                "Successfully reconfigured Pipeline Elements in Pipeline " + reconfiguredPipeline.getName(),
                "Could not reconfigure all Pipeline Elements in Pipeline " + reconfiguredPipeline.getName());
    }

    private void reconfigurePipelineElementOrRollback() {
        List<PipelineElementReconfigurationEntity> reconfiguredEntityList = comparePipelinesAndGetReconfiguration();

        reconfiguredEntityList.forEach(entity -> {
            PipelineOperationStatus entityStatus = reconfigurePipelineElement(entity);

            entityStatus.getElementStatus().forEach(pipelineReconfigurationStatus::addPipelineElementStatus);

            if (entityStatus.isSuccess()) {
                Operations.overwritePipeline(reconfiguredPipeline);
            }
        });
    }

    private PipelineOperationStatus reconfigurePipelineElement(PipelineElementReconfigurationEntity entity) {
        return PipelineExecutorFactory
                .createReconfigurationExecutor(this.storedPipeline, false, false, false, this.reconfiguredPipeline, entity)
                .execute();
    }

    private List<PipelineElementReconfigurationEntity> comparePipelinesAndGetReconfiguration() {
        List<PipelineElementReconfigurationEntity> delta = new ArrayList<>();

        reconfiguredPipeline.getSepas().forEach(reconfiguredProcessor -> storedPipeline.getSepas().forEach(storedProcessor -> {
            if (matchingElementIds(reconfiguredProcessor, storedProcessor)) {
                List<StaticProperty> list = new ArrayList<>();
                getReconfigurableStaticProperties(reconfiguredProcessor).forEach(reconfiguredStaticProperty ->
                        getReconfigurableStaticProperties(storedProcessor).forEach(storedStaticProperty -> {
                            if (matchAndCompare(reconfiguredStaticProperty, storedStaticProperty)) {
                                list.add(reconfiguredStaticProperty);
                            }
                        }));
                PipelineElementReconfigurationEntity entity = reconfigurationEntity(reconfiguredProcessor, list);
                if (list.size() > 0 && !exists(delta, entity)) {
                    delta.add(entity);
                }
            }
        }));

        return delta;
    }

    private boolean exists(List<PipelineElementReconfigurationEntity> delta,
                           PipelineElementReconfigurationEntity entity) {
        return delta.stream()
                .anyMatch(e -> e.getDeploymentRunningInstanceId().equals(entity.getDeploymentRunningInstanceId()));
    }

    private PipelineElementReconfigurationEntity reconfigurationEntity(DataProcessorInvocation graph,
                                                                       List<StaticProperty> adaptedStaticProperty) {
        PipelineElementReconfigurationEntity entity = new PipelineElementReconfigurationEntity(graph);
        entity.setReconfiguredStaticProperties(adaptedStaticProperty);
        return entity;
    }

    private boolean compareForChanges(FreeTextStaticProperty one, FreeTextStaticProperty two) {
        return one.getInternalName().equals(two.getInternalName()) && !one.getValue().equals(two.getValue());
    }

    private boolean matchAndCompare(StaticProperty one, StaticProperty two) {

        if (one instanceof FreeTextStaticProperty && two instanceof FreeTextStaticProperty) {
            return one.getInternalName().equals(two.getInternalName()) &&
                    !((FreeTextStaticProperty) one).getValue().equals(((FreeTextStaticProperty) two).getValue());
        } else if (one instanceof CodeInputStaticProperty && two instanceof CodeInputStaticProperty) {
            return one.getInternalName().equals(two.getInternalName()) &&
                    !((CodeInputStaticProperty) one).getValue().equals(((CodeInputStaticProperty) two).getValue());
        } else {
            return false;
        }
    }

    private List<FreeTextStaticProperty> getReconfigurableFsp(DataProcessorInvocation graph) {
        return graph.getStaticProperties().stream()
                .filter(FreeTextStaticProperty.class::isInstance)
                .map(FreeTextStaticProperty.class::cast)
                .filter(FreeTextStaticProperty::isReconfigurable)
                .collect(Collectors.toList());
    }

    private List<StaticProperty> getReconfigurableStaticProperties(DataProcessorInvocation graph) {
        return graph.getStaticProperties().stream()
                .filter(sp -> {
                    if (sp instanceof FreeTextStaticProperty) {
                        return ((FreeTextStaticProperty) sp).isReconfigurable();
                    } else if (sp instanceof CodeInputStaticProperty) {
                        return ((CodeInputStaticProperty) sp).isReconfigurable();
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    private boolean matchingElementIds(DataProcessorInvocation one, DataProcessorInvocation two) {
        return one.getElementId().equals(two.getElementId());
    }

    // Helpers

    private PipelineOperationStatus verifyPipelineReconfigurationStatus(PipelineOperationStatus status,
                                                                        String successMessage,
                                                                        String errorMessage) {
        status.setSuccess(status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess));
        if (status.isSuccess()) {
            status.setTitle(successMessage);
        } else {
            status.setTitle(errorMessage);
        }
        return status;
    }

    private Pipeline getPipelineById(String pipelineId) {
        return getPipelineStorageApi().getPipeline(pipelineId);
    }

    private IPipelineStorage getPipelineStorageApi() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
    }

    private PipelineOperationStatus initPipelineOperationStatus() {
        PipelineOperationStatus status = new PipelineOperationStatus();
        status.setPipelineId(reconfiguredPipeline.getPipelineId());
        status.setPipelineName(reconfiguredPipeline.getName());
        return status;
    }

}
