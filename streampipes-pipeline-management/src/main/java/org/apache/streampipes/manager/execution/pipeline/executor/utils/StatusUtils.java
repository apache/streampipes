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
package org.apache.streampipes.manager.execution.pipeline.executor.utils;

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementStatus;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class StatusUtils {

    /**
     * Create pipeline operation status with pipeline id and name and set success to true
     *
     * @return PipelineOperationStatus
     */
    public static PipelineOperationStatus initPipelineOperationStatus(Pipeline pipeline) {
        PipelineOperationStatus status = new PipelineOperationStatus();
        status.setPipelineId(pipeline.getPipelineId());
        status.setPipelineName(pipeline.getName());
        status.setSuccess(true);
        return status;
    }

    public static void updateStatus(PipelineOperationStatus partialStatus, PipelineOperationStatus status) {
        // Add status to global migration status
        partialStatus.getElementStatus().forEach(status::addPipelineElementStatus);
        String title = partialStatus.getTitle();
        if(title != null)
            status.setTitle(partialStatus.getTitle());
    }

    public static Set<String> extractUniqueSuccessfulIds(PipelineOperationStatus status) {
        return status.getElementStatus().stream()
                .filter(PipelineElementStatus::isSuccess)
                .map(PipelineElementStatus::getRunningInstanceId)
                .collect(Collectors.toSet());
    }

    public static void checkSuccess(PipelineOperationStatus status){
        if(status.isSuccess())
            status.setSuccess(status.getElementStatus().stream().allMatch(PipelineElementStatus::isSuccess));
    }
}
