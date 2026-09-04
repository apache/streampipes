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

import org.apache.streampipes.model.graph.DataSinkDescription;
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
  static final String DATA_LAKE_SINK_NAME = "Data Lake";
  static final String DATASET_SINK_NAME = "Dataset";
  static final String DATASET_SINK_DESCRIPTION = "Stores events in the internal dataset.";

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
    migrateDataLakeSinkDescriptions();
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
        .anyMatch(this::isLegacyDataLakeSink);
  }

  private void migratePipelineInvocations() {
    pipelineStorage.findAll().forEach(pipeline -> {
      var actions = pipeline.getActions();
      if (actions == null || actions.isEmpty()) {
        return;
      }

      var containsDataLakeSink = actions.stream().anyMatch(this::isLegacyDataLakeSink);
      if (containsDataLakeSink) {
        actions.stream()
            .filter(this::isLegacyDataLakeSink)
            .forEach(this::migrateDataLakeSink);
        pipelineStorage.updateElement(pipeline);
      }
    });
  }

  private boolean isLegacyDataLakeSink(DataSinkInvocation action) {
    return DATA_LAKE_SINK_APP_ID.equals(action.getAppId())
        || (DATASET_SINK_APP_ID.equals(action.getAppId()) && DATA_LAKE_SINK_NAME.equals(action.getName()));
  }

  private void migrateDataLakeSink(DataSinkInvocation action) {
    action.setAppId(DATASET_SINK_APP_ID);
    action.setName(DATASET_SINK_NAME);
    action.setDescription(DATASET_SINK_DESCRIPTION);
  }

  private void migrateDataLakeSinkDescriptions() {
    var legacySinkDescriptions = dataSinkStorage.getDataSinksByAppId(DATA_LAKE_SINK_APP_ID);
    if (legacySinkDescriptions.isEmpty()) {
      return;
    }

    if (dataSinkStorage.getDataSinksByAppId(DATASET_SINK_APP_ID).isEmpty()) {
      legacySinkDescriptions.forEach(this::migrateDataLakeSinkDescription);
    } else {
      legacySinkDescriptions.forEach(dataSinkStorage::deleteElement);
    }
  }

  private void migrateDataLakeSinkDescription(DataSinkDescription sinkDescription) {
    sinkDescription.setAppId(DATASET_SINK_APP_ID);
    sinkDescription.setName(DATASET_SINK_NAME);
    sinkDescription.setDescription(DATASET_SINK_DESCRIPTION);
    dataSinkStorage.updateElement(sinkDescription);
  }
}
