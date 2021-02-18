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

package org.apache.streampipes.manager.execution.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MigrationHelpers {

    static public List<Tuple2<DataProcessorInvocation, DataProcessorInvocation>> getDelta(Pipeline pipelineX, Pipeline pipelineY){
        List<Tuple2<DataProcessorInvocation, DataProcessorInvocation>> delta = new ArrayList<>();
        pipelineX.getSepas().forEach(iX -> {
            if (pipelineY.getSepas().stream().filter(iY -> iY.getElementId().equals(iX.getElementId()))
                    .noneMatch(iY -> iY.getDeploymentTargetNodeId().equals(iX.getDeploymentTargetNodeId()))){
                Optional<DataProcessorInvocation> invocationY = pipelineY.getSepas().stream().
                        filter(iY -> iY.getDeploymentRunningInstanceId().equals(iX.getDeploymentRunningInstanceId())).findFirst();
                invocationY.ifPresent(dataProcessorInvocation -> delta.add(new Tuple2<>(iX, dataProcessorInvocation)));
            }
        });
        return delta;
    }

    static public PipelineOperationStatus verifyPipelineOperationStatus(PipelineOperationStatus status, String successMessage,
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

    public static void exchangePipelineElement(Pipeline exchangePipeline, Tuple2<? extends NamedStreamPipesEntity, ? extends NamedStreamPipesEntity> entity){

        if (entity.a instanceof DataProcessorInvocation){
            int index = exchangePipeline.getSepas().indexOf(entity.b);
            exchangePipeline.getSepas().remove(index);
            exchangePipeline.getSepas().add(index, (DataProcessorInvocation) entity.a);
        }
    }

    public static Pipeline deepCopyPipeline(Pipeline object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(objectMapper.writeValueAsString(object), Pipeline.class);
    }

}
