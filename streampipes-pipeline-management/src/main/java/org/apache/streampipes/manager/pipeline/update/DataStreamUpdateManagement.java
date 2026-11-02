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

package org.apache.streampipes.manager.pipeline.update;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.PipelineUpdateInfo;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class DataStreamUpdateManagement {

  private final DataStreamResourceManager dataStreamResourceManager;
  private final PipelineUpdateCoordinator pipelineUpdateCoordinator;
  private final ApplicationEventPublisher eventPublisher;

  public DataStreamUpdateManagement(ExtensionServiceRequestManager requestManager,
                                    SpResourceManager resourceManager,
                                    ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = Objects.requireNonNull(eventPublisher);
    this.pipelineUpdateCoordinator = new PipelineUpdateCoordinator(requestManager, resourceManager);
    this.dataStreamResourceManager = resourceManager.manageDataStreams();
  }

  public void updateDataStream(SpDataStream dataStream) {
    var previous = dataStreamResourceManager.find(dataStream.getElementId());
    boolean schemaChanged = EventSchemaChangeDetector.hasChanged(
        previous == null ? null : previous.getEventSchema(), dataStream.getEventSchema());
    dataStreamResourceManager.update(dataStream);
    pipelineUpdateCoordinator.updatePipelines(dataStream);
    if (schemaChanged) {
      eventPublisher.publishEvent(new DataStreamSchemaUpdatedEvent(dataStream.getElementId(), dataStream.getName()));
    }
  }

  public List<PipelineUpdateInfo> checkPipelineMigrations(SpDataStream dataStream) {
    return pipelineUpdateCoordinator.checkPipelineMigrations(dataStream);
  }
}
