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

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CreateDatasetPermissionMigration implements Migration {

  private final CRUDStorage<DataLakeMeasure> dataLakeStorage;
  private final CRUDStorage<Pipeline> pipelineStorage;
  private final IPermissionStorage permissionStorage;
  private final PermissionResourceManager permissionResourceManager;

  private static final String DATALAKE_APP_ID =
        "org.apache.streampipes.sinks.internal.jvm.datalake";

  private static final String DB_MEASUREMENT = "db_measurement";



  public CreateDatasetPermissionMigration() {
    this.dataLakeStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getDataLakeStorage();
    this.pipelineStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
    this.permissionStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage();
    this.permissionResourceManager = new PermissionResourceManager();
  }

  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() throws IOException {
    dataLakeStorage.findAll().forEach(measure -> {
      var existingPermission = permissionStorage.getObjectPermissions(List.of(measure.getMeasureName()));

      if (existingPermission.isEmpty()) {

        permissionResourceManager.createDefault(
            measure.getMeasureName(),
            DataLakeMeasure.class,
            findAssociatedPipelineOwner(measure),
            true
        );
      }
    });
  }

  private String findAssociatedPipelineOwner(DataLakeMeasure measure) {
    return pipelineStorage.findAll().stream()
        .filter(pipeline -> pipeline.getActions().stream()
            .anyMatch(action -> action instanceof DataSinkInvocation 
              && DATALAKE_APP_ID.equals(((DataSinkInvocation) action).getAppId())))
        .map(pipeline -> {
            return pipeline.getActions().stream()
                .filter(action -> action instanceof DataSinkInvocation)
                .map(action -> (DataSinkInvocation) action)
                .filter(ds -> DATALAKE_APP_ID.equals(ds.getAppId()))
                .map(this::extractMeasurement)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(measurement -> pipeline.getCreatedByUser())
                .orElse(null);
        })
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
}

private Optional<String> extractMeasurement(DataSinkInvocation datasink) {
    return datasink.getStaticProperties().stream()
        .filter(sp -> DB_MEASUREMENT.equals(sp.getInternalName()))
        .filter(FreeTextStaticProperty.class::isInstance)
        .map(FreeTextStaticProperty.class::cast)
        .map(FreeTextStaticProperty::getValue)
        .filter(value -> !value.isBlank())
        .findFirst();
}

  @Override
  public String getDescription() {
    return "Create default permissions for datasets";
  }
}
