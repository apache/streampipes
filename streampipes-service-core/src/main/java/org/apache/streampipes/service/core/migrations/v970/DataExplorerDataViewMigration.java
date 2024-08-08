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

package org.apache.streampipes.service.core.migrations.v970;

import org.apache.streampipes.model.client.user.PermissionBuilder;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Migrates data explorer dashboards and data views
 * Add permission object for each data view
 * Add time settings to data views
 * Add live settings to dashboards
 */
public class DataExplorerDataViewMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(DataExplorerDataViewMigration.class);

  private final CRUDStorage<DataExplorerWidgetModel> dataViewStorage;
  private final CRUDStorage<DashboardModel> dataExplorerDashboardStorage;
  private final PermissionResourceManager permissionResourceManager;

  public DataExplorerDataViewMigration() {
    var storage = StorageDispatcher.INSTANCE.getNoSqlStore();
    this.dataViewStorage = storage.getDataExplorerWidgetStorage();
    this.dataExplorerDashboardStorage = storage.getDataExplorerDashboardStorage();
    this.permissionResourceManager = new PermissionResourceManager();
  }

  @Override
  public boolean shouldExecute() {
    return !findDashboardsToMigrate().isEmpty();
  }

  @Override
  public void executeMigration() throws IOException {
    var allDashboards = findDashboardsToMigrate();
    var defaultLiveSettings = makeDefaultLiveSettings();

    LOG.info("Migrating {} data explorer dashboards", allDashboards.size());

    allDashboards
        .forEach(dashboard -> {
          var timeSettings = dashboard.getDashboardTimeSettings();
          dashboard.getDashboardGeneralSettings().put("globalTimeEnabled", true);
          dashboard.setDashboardLiveSettings(defaultLiveSettings);
          dashboard.getWidgets().forEach(widget -> {
            var dataView = dataViewStorage.getElementById(widget.getId());
            if (dataView != null) {
              dataView.setTimeSettings(timeSettings);
              dataViewStorage.updateElement(dataView);
              addPermissionsObject(dashboard, dataView);
            }
          });
          dataExplorerDashboardStorage.updateElement(dashboard);
        });
  }

  @Override
  public String getDescription() {
    return "Migrating data explorer data views";
  }

  private List<DashboardModel> findDashboardsToMigrate() {
    return dataExplorerDashboardStorage
        .findAll()
        .stream()
        .filter(dashboard -> !dashboard.getDashboardGeneralSettings().containsKey("globalTimeEnabled"))
        .toList();
  }

  private Map<String, Object> makeDefaultLiveSettings() {
    var map = new HashMap<String, Object>();
    map.put("refreshModeActive", false);
    map.put("refreshIntervalInSeconds", 10);
    map.put("label", "Off");

    return map;
  }

  private void addPermissionsObject(DashboardModel dashboard,
                                    DataExplorerWidgetModel dataView) {
    var dashboardPermissions = permissionResourceManager.findForObjectId(dashboard.getElementId());
    if (!dashboardPermissions.isEmpty()) {
      var dashboardPermission = dashboardPermissions.get(0);
      if (permissionResourceManager.find(dataView.getElementId()) == null) {
        var dataViewPermission = PermissionBuilder
            .create(dataView.getElementId(), DataExplorerWidgetModel.class, dashboardPermission.getOwnerSid())
            .publicElement(dashboardPermission.isPublicElement())
            .build();
        dataViewPermission.setGrantedAuthorities(dashboardPermission.getGrantedAuthorities());
        permissionResourceManager.create(dataViewPermission);
      }
    }
  }
}
