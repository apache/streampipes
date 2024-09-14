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

package org.apache.streampipes.user.management.authorization;

import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoleManager {

  private final CRUDStorage<Role> storage;

  public RoleManager() {
    this.storage = StorageDispatcher.INSTANCE.getNoSqlStore().getRoleStorage();
  }

  public List<Role> makeDefaultRoles() {
    return List.of(
        Role.createDefaultRole(DefaultRole.Constants.ROLE_ADMIN_VALUE, "Admin", List.of()),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_SERVICE_ADMIN_VALUE, "Service Admin", List.of()),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_DASHBOARD_ADMIN_VALUE, "Dashboard Admin", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_READ_DASHBOARD_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_DASHBOARD_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_VALUE
        )),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_DASHBOARD_USER_VALUE, "Dashboard User", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_READ_DASHBOARD_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_DASHBOARD_WIDGET_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_VALUE
        )),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_PIPELINE_ADMIN_VALUE, "Pipeline Admin", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_PIPELINE_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_ELEMENT_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_FILES_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_FILES_VALUE
        )),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_PIPELINE_USER_VALUE, "Pipeline User", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_ELEMENT_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_FILES_VALUE
        )),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_ASSET_ADMIN_VALUE, "Asset Admin", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_READ_ASSETS_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_ASSETS_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_LABELS_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_LABELS_VALUE
        )),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_ASSET_USER_VALUE, "Asset User", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_READ_GENERIC_STORAGE_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_ASSETS_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_LABELS_VALUE
        )),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_DATA_EXPLORER_ADMIN_VALUE, "Data Explorer Admin", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_READ_DATA_EXPLORER_VIEW_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_DATA_EXPLORER_WIDGET_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_VALUE
        )),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_DATA_EXPLORER_USER_VALUE, "Data Explorer User", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_READ_DATA_EXPLORER_VIEW_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_DATA_EXPLORER_WIDGET_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_VALUE
        )),
        Role.createDefaultRole(DefaultRole.Constants.ROLE_CONNECT_ADMIN_VALUE, "Connect Admin", List.of(
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_ADAPTER_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_ADAPTER_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_READ_FILES_VALUE,
            DefaultPrivilege.Constants.PRIVILEGE_WRITE_FILES_VALUE
        ))
    );
  }

  public List<String> getPrivileges(String roleId) {
    var role = storage.getElementById(roleId);
    if (role == null) {
      return List.of();
    } else {
      return Stream.concat(
              role.getPrivilegeIds().stream(),
              Stream.of(roleId))
          .collect(Collectors.toList());
    }
  }
}
