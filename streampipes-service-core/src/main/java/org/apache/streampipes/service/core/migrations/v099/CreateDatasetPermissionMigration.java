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
import org.apache.streampipes.storage.api.core.CRUDStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
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
      var existingPermission = permissionStorage.getUserPermissionsForObject(measure.getElementId());

      if (existingPermission.isEmpty()) {

        permissionResourceManager.createDefault(
            measure.getElementId(),
            DataLakeMeasure.class,
            findAssociatedPipelineOwner(measure),
            true
        );
      }
    });
  }

  private String findAssociatedPipelineOwner(DataLakeMeasure measure) {
    String measureName = measure.getMeasureName();

    return pipelineStorage.findAll().stream()
        .filter(pipeline -> pipeline.getActions() != null)
        .filter(pipeline ->
            pipeline.getActions().stream()
                .filter(Objects::nonNull)
                .filter(action -> DATALAKE_APP_ID.equals(action.getAppId()))
                .map(this::extractMeasurement)
                .flatMap(Optional::stream)
                .anyMatch(measureName::equals)
        )
        .map(pipeline -> getPipelineOwner(pipeline.getPipelineId()))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private String getPipelineOwner(String pipelineId) {
    var permission = permissionStorage.getUserPermissionsForObject(pipelineId);
    if (!permission.isEmpty()) {
      return permission.get(0).getOwnerSid();
    }
    return null;
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
