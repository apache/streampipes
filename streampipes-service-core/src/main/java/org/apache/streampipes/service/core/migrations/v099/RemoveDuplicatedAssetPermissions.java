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

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoveDuplicatedAssetPermissions implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(RemoveDuplicatedAssetPermissions.class);

  private final IPermissionStorage permissionStorage;

  private final IAssetStorage assetStorage =
      StorageDispatcher.INSTANCE.getNoSqlStore().getAssetStorage();

  public RemoveDuplicatedAssetPermissions(IPermissionStorage permissionStorage) {
    this.permissionStorage = permissionStorage;
  }

  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() throws IOException {
    AtomicInteger deletedPermissions = new AtomicInteger();
    assetStorage.findAll().forEach(asset -> {
      var permissions = permissionStorage.getUserPermissionsForObject(asset.getElementId());
      if (permissions.size() > 1) {

        // "Bad" permissions = no owner, empty grantedAuthorities, and public
        var badPermissions = permissions.stream()
            .filter(this::isAccidentalPermission)
            .toList();

        // "Good" permissions = everything else
        var goodPermissions = permissions.stream()
            .filter(p -> !isAccidentalPermission(p))
            .toList();

        // Choose what to keep:
        // - keep first GOOD if exists
        // - otherwise keep first BAD (meaning all are bad)
        var keep = !goodPermissions.isEmpty()
            ? goodPermissions.get(0)
            : badPermissions.get(0);

        // Delete everything else
        for (var p : permissions) {
          if (!(keep.getElementId().equals(p.getElementId()))) {
            permissionStorage.deleteElementById(p.getElementId());
            deletedPermissions.getAndIncrement();
          }
        }
      }
    });
    LOG.info("Deleted {} permissions", deletedPermissions.get());
  }

  private boolean isAccidentalPermission(Permission p) {
    if (p == null) {
      return false;
    }

    var owner = p.getOwnerSid();
    boolean noOwner = (owner == null) || owner.isBlank();

    var granted = p.getGrantedAuthorities();
    boolean emptyAuthorities = (granted == null) || granted.isEmpty();

    boolean isPublic = p.isPublicElement();

    return noOwner && emptyAuthorities && isPublic;
  }


  @Override
  public String getDescription() {
    return "Remove duplicated asset permissions";
  }
}
