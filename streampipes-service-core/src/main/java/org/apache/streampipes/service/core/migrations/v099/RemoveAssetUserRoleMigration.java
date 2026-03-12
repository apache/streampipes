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

import org.apache.streampipes.model.client.user.Group;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;

public class RemoveAssetUserRoleMigration implements Migration {

  private static final String ROLE_ASSET_USER = "ROLE_ASSET_USER";

  private final IRoleStorage roleStorage;
  private final IUserStorage userStorage;
  private final IUserGroupStorage userGroupStorage;

  public RemoveAssetUserRoleMigration() {
    this.roleStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getRoleStorage();
    this.userStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
    this.userGroupStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserGroupStorage();
  }

  @Override
  public boolean shouldExecute() {
    return roleStorage.getElementById(ROLE_ASSET_USER) != null
        || userStorage.getAllUsers().stream().anyMatch(this::hasAssetUserRole)
        || userGroupStorage.findAll().stream().anyMatch(this::hasAssetUserRole);
  }

  @Override
  public void executeMigration() throws IOException {
    userStorage.getAllUsers().stream()
        .filter(this::hasAssetUserRole)
        .forEach(this::removeAssetUserRole);

    userGroupStorage.findAll().stream()
        .filter(this::hasAssetUserRole)
        .forEach(this::removeAssetUserRole);

    var assetUserRole = roleStorage.getElementById(ROLE_ASSET_USER);
    if (assetUserRole != null) {
      roleStorage.deleteElement(assetUserRole);
    }
  }

  @Override
  public String getDescription() {
    return "Remove obsolete ROLE_ASSET_USER role and role assignments";
  }

  private boolean hasAssetUserRole(Principal principal) {
    return principal.getRoles() != null && principal.getRoles().contains(ROLE_ASSET_USER);
  }

  private void removeAssetUserRole(Principal principal) {
    principal.getRoles().remove(ROLE_ASSET_USER);
    userStorage.updateUser(principal);
  }

  private boolean hasAssetUserRole(Group group) {
    return group.getRoles() != null && group.getRoles().contains(ROLE_ASSET_USER);
  }

  private void removeAssetUserRole(Group group) {
    group.getRoles().remove(ROLE_ASSET_USER);
    userGroupStorage.updateElement(group);
  }
}
