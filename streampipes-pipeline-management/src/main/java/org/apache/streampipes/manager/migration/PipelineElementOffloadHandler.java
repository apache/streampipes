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

import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

public class PipelineElementOffloadHandler {

    private final InvocableStreamPipesEntity graph;

    public PipelineElementOffloadHandler(InvocableStreamPipesEntity elementToOffload) {
        this.graph = elementToOffload;
    }

    public Message handleOffloading() {
        Pipeline currentPipeline = getPipelineById(graph.getCorrespondingPipeline());
        Pipeline offloadPipeline = new MigrationPipelineGenerator(graph, currentPipeline).generateMigrationPipeline();
          if(offloadPipeline == null)
            return Notifications.error(NotificationType.NO_NODE_FOUND);

          try {
            PipelineOperationStatus status = Operations.handlePipelineElementMigration(offloadPipeline,
                    true, true, true);
            if (status.isSuccess()) {
              return Notifications.success(NotificationType.OFFLOADING_SUCCESS);
            } else {
              return Notifications.success(NotificationType.OFFLOADING_ERROR);
            }

          } catch (Exception e) {
            e.printStackTrace();
            return Notifications.error(NotificationType.UNKNOWN_ERROR);
          }
    }

    // Helpers

    private Pipeline getPipelineById(String pipelineId) {
        return getPipelineStorageApi().getPipeline(pipelineId);
    }

    private IPipelineStorage getPipelineStorageApi() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
    }
}
