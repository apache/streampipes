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

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import java.io.IOException;
import java.util.Map;

public class RenameDataLakeMetadataToDatasetMigration implements Migration {

  static final String DATA_LAKE_MEASURE_CLASS =
      "org.apache.streampipes.model.datalake.DataLakeMeasure";
  static final String DATASET_MEASURE_CLASS =
      "org.apache.streampipes.model.dataset.DatasetMeasure";
  static final String DATA_LAKE_WIDGET_CLASS =
      "org.apache.streampipes.model.datalake.DataExplorerWidgetModel";
  static final String DATASET_WIDGET_CLASS =
      "org.apache.streampipes.model.dataset.DataExplorerWidgetModel";

  private static final Map<String, String> CLASS_NAME_RENAMES = Map.of(
      DATA_LAKE_MEASURE_CLASS, DATASET_MEASURE_CLASS,
      DATA_LAKE_WIDGET_CLASS, DATASET_WIDGET_CLASS
  );

  private final IPermissionStorage permissionStorage;

  public RenameDataLakeMetadataToDatasetMigration(IPermissionStorage permissionStorage) {
    this.permissionStorage = permissionStorage;
  }

  @Override
  public boolean shouldExecute() {
    return permissionStorage.findAll().stream()
        .map(Permission::getObjectClassName)
        .anyMatch(CLASS_NAME_RENAMES::containsKey);
  }

  @Override
  public void executeMigration() throws IOException {
    permissionStorage.findAll().forEach(permission -> {
      var renamedClass = CLASS_NAME_RENAMES.get(permission.getObjectClassName());
      if (renamedClass != null) {
        permission.setObjectClassName(renamedClass);
        permissionStorage.updateElement(permission);
      }
    });
  }

  @Override
  public String getDescription() {
    return "Rename stored data lake metadata to dataset naming";
  }
}
