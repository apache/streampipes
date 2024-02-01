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
package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.PermissionBuilder;
import org.apache.streampipes.model.client.user.PermissionEntry;
import org.apache.streampipes.model.client.user.PrincipalType;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PermissionResourceManager extends AbstractResourceManager<IPermissionStorage> {

  public PermissionResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage());
  }

  public List<Permission> findAll() {
    throw new NotImplementedException("Not implemented yet");
  }

  public List<Permission> findForObjectId(String objectInstanceId) {
    List<List<String>> permissionList = RBACManager.INSTANCE.getPermissionForObject(objectInstanceId);
    String ownerId = RBACManager.INSTANCE.getOwner(objectInstanceId);
    Permission permission = PermissionBuilder.create(objectInstanceId, Permission.class, ownerId)
        .publicElement(RBACManager.INSTANCE.hasPermissionForUser(RBACManager.PUBLIC_USER, objectInstanceId,
            RBACManager.ALL_PERMISSION))
        .build();
    permission.setPermissionId(objectInstanceId);
    for (List<String> l : permissionList) {
      String uid = l.get(0);
      if (Objects.equals(uid, ownerId)) {
        continue;
      }
      PermissionEntry entry = new PermissionEntry();
      entry.setSid(l.get(0));
      entry.setPrincipalType(PrincipalType.USER_ACCOUNT);
      permission.addPermissionEntry(entry);
    }
    return Collections.singletonList(permission);
  }

  public void create(Permission permission) {
    if (permission.isPublicElement()) {
      RBACManager.INSTANCE.addPermissionForUser(RBACManager.PUBLIC_USER, permission.getObjectInstanceId(),
          RBACManager.ALL_PERMISSION);
    }
    if (permission.getOwnerSid() != null) {
      RBACManager.INSTANCE.setOwner(permission.getOwnerSid(), permission.getObjectInstanceId());
    }
    for (PermissionEntry e : permission.getGrantedAuthorities()) {
      RBACManager.INSTANCE.addPermissionForUser(e.getSid(), permission.getObjectInstanceId(),
          RBACManager.ALL_PERMISSION);
    }
  }

  public void createDefault(String objectInstanceId,
                            Class<?> objectInstanceClass,
                            String ownerSid,
                            boolean publicElement) {
    Permission permission = PermissionBuilder
        .create(objectInstanceId, objectInstanceClass, ownerSid)
        .publicElement(publicElement)
        .build();

    create(permission);
  }

  public void update(Permission permission) {
    delete(permission);
    create(permission);
  }

  public void delete(Permission permission) {
    RBACManager.INSTANCE.deleteObject(permission.getObjectInstanceId());
  }
}
