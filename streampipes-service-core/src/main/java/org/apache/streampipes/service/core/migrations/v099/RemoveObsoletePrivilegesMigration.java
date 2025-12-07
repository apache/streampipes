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

import org.apache.streampipes.model.client.user.Privilege;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.util.List;

public class RemoveObsoletePrivilegesMigration implements Migration {

  public CRUDStorage<Privilege> privilegeStorage;

  private static final List<String> privilegesToRemove = List.of(
      "PRIVILEGE_READ_DASHBOARD_WIDGET",
      "PRIVILEGE_WRITE_DASHBOARD_WIDGET",
      "PRIVILEGE_READ_DATA_EXPLORER_WIDGET",
      "PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET"
  );

  public RemoveObsoletePrivilegesMigration() {
    this.privilegeStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getPrivilegeStorage();
  }

  @Override
  public boolean shouldExecute() {
    return privilegeStorage.findAll().stream().anyMatch(p -> privilegesToRemove.contains(p.getElementId()));
  }

  @Override
  public void executeMigration() throws IOException {
    privilegesToRemove.forEach(p -> {
      var privilege = privilegeStorage.getElementById(p);
      if (privilege != null) {
        privilegeStorage.deleteElement(privilege);
      }
    });
  }

  @Override
  public String getDescription() {
    return "Remove obsolete dashboard and data explorer privileges";
  }
}
