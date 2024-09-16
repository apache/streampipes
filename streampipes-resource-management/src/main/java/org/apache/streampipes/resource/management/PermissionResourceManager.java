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

import java.util.List;

public class PermissionResourceManager extends AbstractResourceManager<IPermissionStorage> {

  public PermissionResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage());
  }

  public List<Permission> findAll() {
    return db.findAll();
  }

  public Permission find(String elementId) {
    return db.getElementById(elementId);
  }

  public List<Permission> findForObjectId(String objectInstanceId) {
    return db.getUserPermissionsForObject(objectInstanceId);
  }

  public void create(Permission permission) {
    db.persist(permission);
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
    db.updateElement(permission);
  }

  public void delete(Permission permission) {
    db.deleteElementById(permission.getPermissionId());
  }
}
