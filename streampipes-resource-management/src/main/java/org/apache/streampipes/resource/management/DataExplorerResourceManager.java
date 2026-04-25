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
import org.apache.streampipes.model.dashboard.DashboardItem;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.storage.api.explorer.IDataExplorerWidgetStorage;
import org.apache.streampipes.storage.api.explorer.IDataLakeMeasureStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataExplorerResourceManager extends CrudResourceManager<DashboardModel> {

  private final IDataExplorerWidgetStorage widgetStorage;
  private final IDataLakeMeasureStorage dataLakeMeasureStorage;

  public DataExplorerResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getDataExplorerDashboardStorage(), DashboardModel.class);
    this.widgetStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getDataExplorerWidgetStorage();
    this.dataLakeMeasureStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getDataLakeStorage();
  }

  public CompositeDashboardModel getCompositeDashboard(String dashboardId) {
    var dashboard = db.getElementById(dashboardId);
    var widgets = dashboard.getWidgets().stream()
        .map(DashboardItem::getDataViewElementId)
        .filter(Objects::nonNull)
        .filter(dataViewElementId -> !dataViewElementId.isBlank())
        .map(widgetStorage::getElementById)
        .filter(Objects::nonNull)
        .toList();
    var dataLakeMeasures = getMeasureNames(widgets).stream().map(dataLakeMeasureStorage::getByMeasureName).toList();

    return new CompositeDashboardModel(dashboard, widgets, dataLakeMeasures);
  }

  private List<String> getMeasureNames(List<DataExplorerWidgetModel> widgets) {
    return widgets.stream().map(DataExplorerWidgetModel::getDataConfig)
        .map(dataConfig -> (List<?>) ((Map<?, ?>) dataConfig).get("sourceConfigs"))
        .filter(List.class::isInstance)
        .map(List.class::cast)
        .filter(sourceConfigs -> !sourceConfigs.isEmpty())
        .map(sourceConfigs -> sourceConfigs.get(0))
        .filter(Map.class::isInstance)
        .map(Map.class::cast)
        .map(cfg -> cfg.get("measureName"))
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .toList();
  }
}
