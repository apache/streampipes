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
import org.apache.streampipes.model.util.ElementIdGenerator;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MigrateDataLakeSinkToDatasetMigration implements Migration {

  static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  static final String DATASET_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.dataset";
  static final String DATA_LAKE_SINK_NAME = "Data Lake";
  static final String DATASET_SINK_NAME = "Dataset";
  static final String DATASET_SINK_DESCRIPTION = "Stores events in the internal dataset.";
  static final String DATASET_SINK_ELEMENT_ID = ElementIdGenerator.makeElementIdFromAppId(DATASET_SINK_APP_ID);

  private final IPipelineStorage pipelineStorage;
  private final IDataSinkStorage dataSinkStorage;
  private final IPermissionStorage permissionStorage;

  public MigrateDataLakeSinkToDatasetMigration(IPipelineStorage pipelineStorage,
                                                IDataSinkStorage dataSinkStorage,
                                                IPermissionStorage permissionStorage) {
    this.pipelineStorage = pipelineStorage;
    this.dataSinkStorage = dataSinkStorage;
    this.permissionStorage = permissionStorage;
  }

  @Override
  public boolean shouldExecute() {
    return containsDataLakeSinkInPipelines()
        || !dataSinkStorage.getDataSinksByAppId(DATA_LAKE_SINK_APP_ID).isEmpty()
        || containsDatasetSinkWithLegacyElementId();
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
    action.setBelongsTo(DATASET_SINK_ELEMENT_ID);
    action.setName(DATASET_SINK_NAME);
    action.setDescription(DATASET_SINK_DESCRIPTION);
  }

  private void migrateDataLakeSinkDescriptions() {
    var legacySinkDescriptions = dataSinkStorage.getDataSinksByAppId(DATA_LAKE_SINK_APP_ID);
    var datasetSinkDescriptions = dataSinkStorage.getDataSinksByAppId(DATASET_SINK_APP_ID);
    var descriptionsToMigrate = new ArrayList<DataSinkDescription>();
    descriptionsToMigrate.addAll(legacySinkDescriptions);
    datasetSinkDescriptions.stream()
        .filter(description -> !DATASET_SINK_ELEMENT_ID.equals(description.getElementId()))
        .forEach(descriptionsToMigrate::add);

    var datasetSinkInstalled = datasetSinkDescriptions.stream()
        .anyMatch(description -> DATASET_SINK_ELEMENT_ID.equals(description.getElementId()));
    for (var sinkDescription : descriptionsToMigrate) {
      datasetSinkInstalled = migrateDataLakeSinkDescription(sinkDescription, datasetSinkInstalled);
    }
  }

  private boolean migrateDataLakeSinkDescription(DataSinkDescription sinkDescription,
                                                 boolean datasetSinkInstalled) {
    if (!DATASET_SINK_ELEMENT_ID.equals(sinkDescription.getElementId())) {
      if (!datasetSinkInstalled) {
        var migratedDescription = new DataSinkDescription(sinkDescription);
        migratedDescription.setElementId(DATASET_SINK_ELEMENT_ID);
        migratedDescription.setRev(null);
        migratedDescription.setAppId(DATASET_SINK_APP_ID);
        migratedDescription.setName(DATASET_SINK_NAME);
        migratedDescription.setDescription(DATASET_SINK_DESCRIPTION);
        dataSinkStorage.persist(migratedDescription);
        datasetSinkInstalled = true;
      }
      migratePermissions(sinkDescription.getElementId());
      dataSinkStorage.deleteElement(sinkDescription);
    }
    return datasetSinkInstalled;
  }

  private boolean containsDatasetSinkWithLegacyElementId() {
    return dataSinkStorage.getDataSinksByAppId(DATASET_SINK_APP_ID)
        .stream()
        .anyMatch(description -> !DATASET_SINK_ELEMENT_ID.equals(description.getElementId()));
  }

  private void migratePermissions(String previousElementId) {
    permissionStorage.getUserPermissionsForObject(previousElementId)
        .forEach(permission -> {
          permission.setObjectInstanceId(DATASET_SINK_ELEMENT_ID);
          permissionStorage.updateElement(permission);
        });
  }
}
