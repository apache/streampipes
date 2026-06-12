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
package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineSummaryDto;
import org.apache.streampipes.model.resource.ResourceSummaryDto;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.security.core.Authentication;

public class PipelineResourceManager extends CrudResourceManager<Pipeline> {

  public PipelineResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI(), Pipeline.class);
  }

  public ResourceSummaryDto<PipelineSummaryDto> getSummary(Authentication auth) {
    var pipelines = findAll()
        .stream()
        .filter(pipeline -> permissionEvaluator.hasPermission(auth, pipeline.getElementId(), "READ"))
        .map(pipeline -> new PipelineSummaryDto(
            pipeline.getElementId(),
            pipeline.getName(),
            pipeline.getDescription(),
            pipeline.getCreatedAt(),
            pipeline.isRunning(),
            pipeline.getHealthStatus(),
            pipeline.getPipelineNotifications(),
            pipeline.isValid()))
        .toList();

    return new ResourceSummaryDto<>(pipelines, pipelines.size());
  }
}
