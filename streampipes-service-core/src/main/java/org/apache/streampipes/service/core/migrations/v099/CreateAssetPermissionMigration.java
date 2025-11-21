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

import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.util.List;

public class CreateAssetPermissionMigration implements Migration {

  private final CRUDStorage<SpAssetModel> assetStorage;
  private final IPermissionStorage permissionStorage;
  private final PermissionResourceManager permissionResourceManager;


  public CreateAssetPermissionMigration() {
    this.assetStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAssetStorage();
    this.permissionStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage();
    this.permissionResourceManager = new PermissionResourceManager();
  }

  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() throws IOException {
    assetStorage.findAll().forEach(assetModel -> {
      var existingPermission = permissionStorage.getObjectPermissions(List.of(assetModel.getElementId()));
      if (existingPermission.isEmpty()) {
        permissionResourceManager.createDefault(
            assetModel.getElementId(),
            SpAssetModel.class,
            null,
            true
        );
      }
    });
  }

  @Override
  public String getDescription() {
    return "Create default permissions for assets";
  }
}
