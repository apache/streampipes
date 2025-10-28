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

package org.apache.streampipes.service.core.migrations.v0980;

import org.apache.streampipes.model.shared.api.Storable;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * This migration is required because the export/import process sanitizes resource ids for the permissions.
 * This breaks the link between resources and their permissions after import.
 */
public class FixImportedPermissionsMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(FixImportedPermissionsMigration.class);

  private final IPermissionStorage permissionStorage =
      StorageDispatcher.INSTANCE.getNoSqlStore()
                                .getPermissionStorage();

  @Override
  public boolean shouldExecute() {
    return true;
  }

  @Override
  public void executeMigration() throws IOException {
    migrateDashboardPermissions();
    migrateChartsPermissions();
    migrateDataStreamPermissions();
  }

  private void migrateDashboardPermissions() {
    LOG.debug("Start migrate permissions for dashboards");
    var dataExplorerDashboardStorage = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getDataExplorerDashboardStorage();
    var dashboards = dataExplorerDashboardStorage.findAll();
    migrateResourcePermissions(dashboards);
    LOG.debug("Finished migrate permissions for dashboards");
  }

  private void migrateChartsPermissions() {
    LOG.debug("Start migrate permissions for charts");
    var dataExplorerWidgetStorage = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getDataExplorerWidgetStorage();
    var charts = dataExplorerWidgetStorage.findAll();
    migrateResourcePermissions(charts);
    LOG.debug("Finished migrate permissions for charts");
  }

  private void migrateDataStreamPermissions() {
    LOG.debug("Start migrate permissions for data streams");
    var dataStreamStorage =
        StorageDispatcher.INSTANCE.getNoSqlStore()
                                  .getDataStreamStorage();
    var dataStreams = dataStreamStorage.findAll();
    migrateResourcePermissions(dataStreams);
    LOG.debug("Finished migrate permissions for data streams");
  }

  /**
   * Migrate permissions for the given resources, by replacing the sanitized id with the original id.
   */
  private void migrateResourcePermissions(List<? extends Storable> resources) {
    resources.forEach(resource -> {
      // This uses the same sanitization logic as the exporter
      var sanitizedId = sanitize(resource.getElementId());
      var permissions = permissionStorage.getUserPermissionsForObject(sanitizedId);

      permissions.forEach(permission -> {
        permission.setObjectInstanceId(resource.getElementId());
        permissionStorage.updateElement(permission);
        LOG.info(
            "Updated permission id from {} to {}",
            sanitizedId,
            resource.getElementId()
        );
      });
    });
  }

  @Override
  public String getDescription() {
    return "Fix permissions of imported data streams, dashboards and charts";
  }

  private String sanitize(String resourceId) {
    return resourceId.replaceAll(":", "")
                     .replaceAll("\\.", "");
  }
}
