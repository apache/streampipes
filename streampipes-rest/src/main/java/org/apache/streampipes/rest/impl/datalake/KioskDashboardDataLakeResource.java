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

package org.apache.streampipes.rest.impl.datalake;

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v3/datalake/dashboard/kiosk")
public class KioskDashboardDataLakeResource extends AbstractAuthGuardedRestResource {

  private final IDataExplorerQueryManagement dataExplorerQueryManagement;
  private final IDataExplorerSchemaManagement dataExplorerSchemaManagement;
  private final CRUDStorage<DashboardModel> dashboardStorage =
      StorageDispatcher.INSTANCE.getNoSqlStore().getDataExplorerDashboardStorage();
  private final CRUDStorage<DataExplorerWidgetModel> dataExplorerWidgetStorage;
  private final IPermissionStorage permissionStorage;

  public KioskDashboardDataLakeResource() {
    this.dataExplorerSchemaManagement = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getSchemaManagement();
    this.dataExplorerQueryManagement = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getQueryManagement(this.dataExplorerSchemaManagement);
    this.dataExplorerWidgetStorage = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getDataExplorerWidgetStorage();
    this.permissionStorage = getNoSqlStorage().getPermissionStorage();
  }

  @PostMapping(path = "/{dashboardId}/{widgetId}/data",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthorityOrAnonymous(#dashboardId) and hasPermission(#dashboardId, 'READ')")
  public ResponseEntity<?> getData(@PathVariable("dashboardId") String dashboardId,
                                   @PathVariable("widgetId") String widgetId,
                                   @RequestBody Map<String, String> queryParams) {
    var dashboard = dashboardStorage.getElementById(dashboardId);
    if (dashboard.getWidgets().stream().noneMatch(w -> w.getDataViewElementId().equals(widgetId))) {
      return badRequest(String.format("Widget with id %s not found in dashboard", widgetId));
    }
    var widget = dataExplorerWidgetStorage.getElementById(widgetId);
    var measureName = queryParams.get("measureName");
    if (!checkMeasureNameInWidget(widget, measureName)) {
     return badRequest("Measure name not found in widget configuration");
    } else {
      ProvidedRestQueryParams sanitizedParams = new ProvidedRestQueryParams(measureName, queryParams);
      try {
        SpQueryResult result =
            this.dataExplorerQueryManagement.getData(sanitizedParams, true);
        return ok(result);
      } catch (RuntimeException e) {
        return badRequest(SpLogMessage.from(e));
      }
    }
  }

  private boolean checkMeasureNameInWidget(DataExplorerWidgetModel widget,
                                           String measureName) {
    var sourceConfigs = widget.getDataConfig().get("sourceConfigs");
    if (sourceConfigs instanceof List<?>) {
      return ((List<?>) sourceConfigs)
          .stream()
          .anyMatch(config -> {
            if (!(config instanceof Map<?, ?>)) {
              return false;
            } else {
              return ((Map<?, ?>) config).get("measureName").equals(measureName);
            }
          });
    } else {
      return false;
    }
  }

  public boolean hasReadAuthorityOrAnonymous(String dashboardId) {
    return hasReadAuthority()
        || hasAnonymousAccessAuthority(dashboardId);
  }

  private boolean hasReadAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_READ_DASHBOARD_VALUE);
  }

  private boolean hasAnonymousAccessAuthority(String dashboardId) {
    var perms = permissionStorage.getUserPermissionsForObject(dashboardId);
    return !perms.isEmpty() && perms.get(0).isReadAnonymous();
  }
}

