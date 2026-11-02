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
package org.apache.streampipes.storage.couchdb.impl.user;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStores;
import org.apache.streampipes.storage.couchdb.impl.core.DefaultViewCrudStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionStorageImpl extends DefaultViewCrudStorage<Permission> implements IPermissionStorage {

  public PermissionStorageImpl(CouchDbStores stores, String viewName) {
    super(stores.principal(CouchDbStores.USER_DB_NAME), Permission.class, viewName);
  }

  public Set<String> getObjectPermissions(List<String> principalSids) {
    List<Permission> objectInstanceSids = view("users/userpermissions", Permission.class)
        .keys(principalSids)
        .docs();

    return toPermissionSet(objectInstanceSids);
  }

  public List<Permission> getUserPermissionsForObject(String objectInstanceId) {

    return view("users/objectpermissions", Permission.class)
        .key(objectInstanceId)
        .docs();
  }

  private Set<String> toPermissionSet(List<Permission> permissions) {
    return permissions.stream().map(Permission::getObjectInstanceId).collect(Collectors.toSet());
  }
}
