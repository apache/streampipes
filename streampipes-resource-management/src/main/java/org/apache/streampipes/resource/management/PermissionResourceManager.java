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
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionResourceManager extends AbstractResourceManager<IPermissionStorage> {

  public PermissionResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage());
  }

  public List<Permission> findAll() {
//    return db.getAllPermissions();
    throw new NotImplementedException("Not implemented yet");
  }

  public List<Permission> findForObjectId(String objectInstanceId) {
    List<List<String>> permissionList = RBACManager.INSTANCE.getPermissionForObject(objectInstanceId);
    List<Permission> result = new ArrayList<>();
    for (List<String> l : permissionList) {
      Permission permission = PermissionBuilder.create(objectInstanceId, Permission.class, l.get(0))
          .publicElement(RBACManager.INSTANCE.hasPermissionForUser(RBACManager.PUBLIC_USER, objectInstanceId,
              RBACManager.ALL_PERMISSION))
          .build();
      permission.setPermissionId(objectInstanceId + "-" + l.get(0));
      result.add(permission);
    }
    if (result.isEmpty()) {
      Permission permission = PermissionBuilder.create(objectInstanceId, Permission.class, "public")
          .publicElement(false)
          .build();
      permission.setPermissionId(objectInstanceId + "-" + "public");
      return Collections.singletonList(permission);
    }
//    return db.getUserPermissionsForObject(objectInstanceId);
    return result;
  }

  public void create(Permission permission) {
//    db.addPermission(permission);
    if (permission.isPublicElement()) {
      RBACManager.INSTANCE.addPermissionForUser(RBACManager.PUBLIC_USER, permission.getObjectInstanceId(),
          RBACManager.ALL_PERMISSION);
    }
    RBACManager.INSTANCE.addPermissionForUser(permission.getOwnerSid(), permission.getObjectInstanceId(),
        RBACManager.ALL_PERMISSION);
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
//    db.updatePermission(permission);
    delete(permission);
    create(permission);
  }

  public void delete(Permission permission) {
//    db.deletePermission(permission.getPermissionId());
    RBACManager.INSTANCE.deletePermission(RBACManager.PUBLIC_USER, permission.getObjectInstanceId(),
        RBACManager.ALL_PERMISSION);
    RBACManager.INSTANCE.deletePermission(permission.getOwnerSid(), permission.getObjectInstanceId(),
        RBACManager.ALL_PERMISSION);
  }
}
