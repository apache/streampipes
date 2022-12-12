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
package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.couchdb.dao.CrudDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionStorageImpl extends CrudDao implements IPermissionStorage {

  private String viewName = "users/permissions";

  public PermissionStorageImpl() {
    super(Utils::getCouchDbUserClient);
  }

  @Override
  public List<Permission> getAllPermissions() {
    return findAll(viewName, Permission.class);
  }

  @Override
  public Permission getPermissionById(String permissionId) {
    return findWithNullIfEmpty(permissionId, Permission.class);
  }

  @Override
  public void addPermission(Permission permission) {
    persist(permission, Permission.class);
  }

  @Override
  public void updatePermission(Permission permission) {
    update(permission, Permission.class);
  }

  @Override
  public void deletePermission(String permissionId) {
    delete(permissionId, Permission.class);
  }

  public Set<String> getObjectPermissions(List<String> principalSids) {
    List<Permission> objectInstanceSids = couchDbClientSupplier
        .get()
        .view("users/userpermissions")
        .keys(principalSids)
        .includeDocs(true)
        .query(Permission.class);

    return toPermissionSet(objectInstanceSids);
  }

  public List<Permission> getUserPermissionsForObject(String objectInstanceId) {

    return couchDbClientSupplier
        .get()
        .view("users/objectpermissions")
        .key(objectInstanceId)
        .includeDocs(true)
        .query(Permission.class);
  }

  private Set<String> toPermissionSet(List<Permission> permissions) {
    return permissions.stream().map(Permission::getObjectInstanceId).collect(Collectors.toSet());
  }
}
