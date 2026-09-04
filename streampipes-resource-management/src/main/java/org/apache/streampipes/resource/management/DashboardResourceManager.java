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

import org.apache.streampipes.model.dashboard.CompositeDashboardModel;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dashboard.DashboardSummaryDto;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.resource.ResourceSummaryDto;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;
import org.apache.streampipes.storage.api.explorer.IDataLakeMeasureStorage;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public class DashboardResourceManager extends CrudResourceManager<DashboardModel, IDashboardStorage> {

  private final IChartStorage widgetStorage;
  private final IDataLakeMeasureStorage dataLakeMeasureStorage;

  public DashboardResourceManager(IDashboardStorage dashboardStorage,
                                   IChartStorage widgetStorage,
                                   IDataLakeMeasureStorage dataLakeMeasureStorage,
                                   PermissionResourceManager permissionResourceManager) {
    super(dashboardStorage, DashboardModel.class, permissionResourceManager);
    this.widgetStorage = widgetStorage;
    this.dataLakeMeasureStorage = dataLakeMeasureStorage;
  }

  public ResourceSummaryDto<DashboardSummaryDto> getSummary(Authentication auth) {
    var dashboards = findAll()
        .stream()
        .filter(dashboard -> permissionEvaluator.hasPermission(auth, dashboard.getElementId(), "READ"))
        .map(dashboard -> new DashboardSummaryDto(
            dashboard.getElementId(),
            getDashboardName(dashboard),
            getDashboardDescription(dashboard),
            getCreatedAt(dashboard),
            getLastModified(dashboard)))
        .toList();

    return new ResourceSummaryDto<>(dashboards, dashboards.size());
  }

  public CompositeDashboardModel getCompositeDashboard(String dashboardId) {
    var dashboard = db.getElementById(dashboardId);
    var widgets = dashboard.getWidgets().stream()
        .map(w -> widgetStorage.getElementById(w.getDataViewElementId())).toList();
    var dataLakeMeasures = getMeasureNames(widgets).stream().map(dataLakeMeasureStorage::getByMeasureName).toList();

    return new CompositeDashboardModel(dashboard, widgets, dataLakeMeasures);
  }

  private List<String> getMeasureNames(List<DataExplorerWidgetModel> widgets) {
    return widgets.stream().map(DataExplorerWidgetModel::getDataConfig)
        .map(dataConfig -> (List<?>) ((Map<?, ?>) dataConfig).get("sourceConfigs"))
        .filter(Map.class::isInstance)
        .map(Map.class::cast)
        .map(cfg -> cfg.get("measureName"))
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .toList();
  }

  private String getDashboardName(DashboardModel dashboard) {
    if (dashboard == null) {
      return null;
    }

    return dashboard.getName() != null ? dashboard.getName() : dashboard.getElementId();
  }

  private String getDashboardDescription(DashboardModel dashboard) {
    return dashboard != null && dashboard.getDescription() != null
        ? dashboard.getDescription()
        : "";
  }

  private Long getCreatedAt(DashboardModel dashboard) {
    return dashboard != null && dashboard.getMetadata() != null
        ? dashboard.getMetadata().getCreatedAtEpochMs()
        : null;
  }

  private Long getLastModified(DashboardModel dashboard) {
    return dashboard != null && dashboard.getMetadata() != null
        ? dashboard.getMetadata().getLastModifiedEpochMs()
        : null;
  }
}
