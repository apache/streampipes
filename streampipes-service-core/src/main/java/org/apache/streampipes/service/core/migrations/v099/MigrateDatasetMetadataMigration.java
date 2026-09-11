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
import org.apache.streampipes.storage.api.explorer.IDatasetMetadataStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import java.io.IOException;

/**
 * Rewrites persisted dataset metadata using the current model. This removes fields that are no
 * longer part of the model and updates the serialized class name after the package move.
 */
public class MigrateDatasetMetadataMigration implements Migration {

  private static final String LEGACY_MODEL_PACKAGE = "org.apache.streampipes.model.datalake.";
  private static final String DATASET_MODEL_PACKAGE = "org.apache.streampipes.model.dataset.";

  private final IDatasetMetadataStorage datasetMetadataStorage;
  private final IPermissionStorage permissionStorage;

  public MigrateDatasetMetadataMigration(IDatasetMetadataStorage datasetMetadataStorage,
                                         IPermissionStorage permissionStorage) {
    this.datasetMetadataStorage = datasetMetadataStorage;
    this.permissionStorage = permissionStorage;
  }

  @Override
  public boolean shouldExecute() {
    return !datasetMetadataStorage.findAll().isEmpty()
        || permissionStorage.findAll().stream()
        .map(Permission::getObjectClassName)
        .anyMatch(className -> className != null && className.startsWith(LEGACY_MODEL_PACKAGE));
  }

  @Override
  public void executeMigration() throws IOException {
    datasetMetadataStorage.findAll().forEach(datasetMetadata -> {
      datasetMetadataStorage.updateElement(datasetMetadata);
    });

    permissionStorage.findAll().forEach(permission -> {
      var className = permission.getObjectClassName();
      if (className != null && className.startsWith(LEGACY_MODEL_PACKAGE)) {
        permission.setObjectClassName(className.replace(LEGACY_MODEL_PACKAGE, DATASET_MODEL_PACKAGE));
        permissionStorage.updateElement(permission);
      }
    });
  }

  @Override
  public String getDescription() {
    return "Migrate persisted dataset metadata";
  }
}
