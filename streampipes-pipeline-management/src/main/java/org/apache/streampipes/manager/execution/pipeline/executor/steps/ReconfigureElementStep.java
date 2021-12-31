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

import org.apache.streampipes.manager.execution.http.ReconfigurationSubmitter;
import org.apache.streampipes.manager.execution.pipeline.executor.PipelineExecutor;
import org.apache.streampipes.manager.execution.pipeline.executor.utils.StatusUtils;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReconfigureElementStep extends PipelineExecutionStep {

    public ReconfigureElementStep(PipelineExecutor pipelineExecutor) {
        super(pipelineExecutor);
    }

    @Override
    public PipelineOperationStatus executeOperation() {
        Pipeline reconfiguredPipeline = pipelineExecutor.getSecondaryPipeline();
        return new ReconfigurationSubmitter(reconfiguredPipeline.getPipelineId(), reconfiguredPipeline.getName(),
                pipelineExecutor.getReconfigurationEntity()).reconfigure();
    }

    @Override
    public PipelineOperationStatus rollbackOperationPartially() {
        return rollbackOperationFully();
    }

    @Override
    public PipelineOperationStatus rollbackOperationFully() {
        Pipeline originalPipeline = pipelineExecutor.getPipeline();
        PipelineElementReconfigurationEntity originalReconfigurationEntity = pipelineExecutor.getReconfigurationEntity();

        Optional<DataProcessorInvocation> reconfiguredEntity =
                findReconfiguredEntity(originalPipeline, originalReconfigurationEntity);

        if(!reconfiguredEntity.isPresent())
            return StatusUtils.initPipelineOperationStatus(pipelineExecutor.getPipeline());

        PipelineElementReconfigurationEntity rollbackReconfigurationEntity =
                new PipelineElementReconfigurationEntity(reconfiguredEntity.get());

        rollbackReconfigurationEntity.setReconfiguredStaticProperties(
                findMatchingStaticProperty(reconfiguredEntity.get().getStaticProperties(),
                        originalReconfigurationEntity.getReconfiguredStaticProperties()));

        return new ReconfigurationSubmitter(originalPipeline.getPipelineId(), originalPipeline.getName(),
                rollbackReconfigurationEntity).reconfigure();
    }

    private Optional<DataProcessorInvocation> findReconfiguredEntity(Pipeline originalPipeline,
                                                                     PipelineElementReconfigurationEntity reconfigurationEntity){
        return originalPipeline.getSepas().stream()
                .filter(invocation -> invocation.getDeploymentRunningInstanceId()
                        .equals(reconfigurationEntity.getDeploymentRunningInstanceId())).findFirst();
    }

    private List<StaticProperty> findMatchingStaticProperty(List<StaticProperty> listToSearchIn,
                                                            List<StaticProperty> listToCompareTo){
        return listToSearchIn.stream().filter(searchProperty ->
            listToCompareTo.stream().anyMatch(compareProperty ->
                    compareProperty.getInternalName().equals(searchProperty.getInternalName()))).collect(Collectors.toList());
    }
}
