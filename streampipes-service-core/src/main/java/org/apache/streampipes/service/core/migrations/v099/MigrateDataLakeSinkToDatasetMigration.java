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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MigrateDataLakeSinkToDatasetMigration implements Migration {

  static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  static final String DATASET_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.dataset";

  private final IPipelineStorage pipelineStorage;
  private final IDataSinkStorage dataSinkStorage;

  public MigrateDataLakeSinkToDatasetMigration(IPipelineStorage pipelineStorage,
                                                IDataSinkStorage dataSinkStorage) {
    this.pipelineStorage = pipelineStorage;
    this.dataSinkStorage = dataSinkStorage;
  }

  @Override
  public boolean shouldExecute() {
    return containsDataLakeSinkInPipelines()
        || !dataSinkStorage.getDataSinksByAppId(DATA_LAKE_SINK_APP_ID).isEmpty();
  }

  @Override
  public void executeMigration() throws IOException {
    migratePipelineInvocations();
    removeDataLakeSinkDescriptions();
  }

  @Override
  public String getDescription() {
    return "Migrate legacy internal Data Lake sink references to the Dataset sink";
  }

  private boolean containsDataLakeSinkInPipelines() {
    return pipelineStorage.findAll()
        .stream()
        .map(Pipeline::getActions)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .map(DataSinkInvocation::getAppId)
        .anyMatch(DATA_LAKE_SINK_APP_ID::equals);
  }

  private void migratePipelineInvocations() {
    pipelineStorage.findAll().forEach(pipeline -> {
      var actions = pipeline.getActions();
      if (actions == null || actions.isEmpty()) {
        return;
      }

      var containsDataLakeSink = actions.stream()
          .anyMatch(action -> DATA_LAKE_SINK_APP_ID.equals(action.getAppId()));
      if (containsDataLakeSink) {
        actions.stream()
            .filter(action -> DATA_LAKE_SINK_APP_ID.equals(action.getAppId()))
            .forEach(action -> action.setAppId(DATASET_SINK_APP_ID));
        pipelineStorage.updateElement(pipeline);
      }
    });
  }

  private void removeDataLakeSinkDescriptions() {
    dataSinkStorage.getDataSinksByAppId(DATA_LAKE_SINK_APP_ID)
        .forEach(dataSinkStorage::deleteElement);
  }
}
