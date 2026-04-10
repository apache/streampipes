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
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.resource.management.DataStreamResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;

import java.util.List;

public class DataStreamUpdateManagement {

  private static final PipelineUpdateStrategy<SpDataStream> PIPELINE_UPDATE_STRATEGY =
      new PipelineUpdateStrategy<>() {
        @Override
        public String affectedElementId(SpDataStream updateElement) {
          return updateElement.getElementId();
        }

        @Override
        public String updatedStreamName(SpDataStream updateElement) {
          return updateElement.getName();
        }

        @Override
        public EventSchema updatedEventSchema(SpDataStream updateElement) {
          return updateElement.getEventSchema();
        }

        @Override
        public String notificationType() {
          return "Data stream";
        }
      };

  private final DataStreamResourceManager dataStreamResourceManager;
  private final PipelineUpdateCoordinator pipelineUpdateCoordinator;

  public DataStreamUpdateManagement(ExtensionServiceRequestManager requestManager) {
    this.dataStreamResourceManager = new SpResourceManager().manageDataStreams();
    this.pipelineUpdateCoordinator = new PipelineUpdateCoordinator(requestManager);
  }

  public void updateDataStream(SpDataStream dataStream) {
    dataStreamResourceManager.update(dataStream);
    pipelineUpdateCoordinator.updatePipelines(dataStream, PIPELINE_UPDATE_STRATEGY);
  }

  public List<PipelineUpdateInfo> checkPipelineMigrations(SpDataStream dataStream) {
    return pipelineUpdateCoordinator.checkPipelineMigrations(dataStream, PIPELINE_UPDATE_STRATEGY);
  }
}
