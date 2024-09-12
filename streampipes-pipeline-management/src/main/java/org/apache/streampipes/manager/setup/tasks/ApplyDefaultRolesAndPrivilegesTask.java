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

package org.apache.streampipes.manager.setup.tasks;

import org.apache.streampipes.model.client.user.Privilege;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.shared.api.Storable;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.authorization.PrivilegeManager;
import org.apache.streampipes.user.management.authorization.RoleManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ApplyDefaultRolesAndPrivilegesTask implements InstallationTask {

  private static final Logger LOG = LoggerFactory.getLogger(ApplyDefaultRolesAndPrivilegesTask.class);

  private final CRUDStorage<Role> roleStorage;
  private final CRUDStorage<Privilege> privilegeStorage;

  public ApplyDefaultRolesAndPrivilegesTask() {
    this.roleStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getRoleStorage();
    this.privilegeStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getPrivilegeStorage();
  }

  @Override
  public void execute() {
    LOG.info("Creating or updating default roles and privileges");
    var defaultRoles = new RoleManager().makeDefaultRoles();
    var defaultPrivileges = new PrivilegeManager().makeDefaultPrivileges();
    updateDocs(roleStorage, defaultRoles);
    updateDocs(privilegeStorage, defaultPrivileges);
  }

  private <T extends Storable> void updateDocs(CRUDStorage<T> storage,
                                               List<T> defaultDocs) {
    defaultDocs.forEach(role -> {
      var doc = storage.getElementById(role.getElementId());
      if (doc != null) {
        role.setRev(doc.getRev());
        storage.updateElement(role);
      } else {
        storage.persist(role);
      }
    });
  }
}
