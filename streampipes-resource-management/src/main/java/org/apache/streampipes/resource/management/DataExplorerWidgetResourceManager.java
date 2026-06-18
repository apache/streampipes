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

import org.apache.streampipes.model.datalake.ChartSummaryDto;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.resource.ResourceSummaryDto;
import org.apache.streampipes.storage.api.explorer.IDataExplorerWidgetStorage;

import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DataExplorerWidgetResourceManager extends CrudResourceManager<DataExplorerWidgetModel> {

  private final DataExplorerResourceManager dashboardManager;

  public DataExplorerWidgetResourceManager(DataExplorerResourceManager dashboardManager,
                                           IDataExplorerWidgetStorage db,
                                           PermissionResourceManager permissionResourceManager) {
    super(db, DataExplorerWidgetModel.class, permissionResourceManager);
    this.dashboardManager = dashboardManager;
  }

  public ResourceSummaryDto<ChartSummaryDto> getSummary(Authentication auth) {
    var charts = findAll()
        .stream()
        .filter(chart -> permissionEvaluator.hasPermission(auth, chart.getElementId(), "READ"))
        .map(chart -> new ChartSummaryDto(
            chart.getElementId(),
            getChartName(chart),
            getDatasetName(chart),
            getCreatedAt(chart),
            getLastModified(chart),
            chart.getWidgetType(),
            isMultiSourceChart(chart),
            chart.getHealthStatus()
        ))
        .toList();

    return new ResourceSummaryDto<>(charts, charts.size());
  }

  @Override
  public void delete(String elementId) {
    deleteDataViewsFromDashboard(elementId);
    super.delete(elementId);
  }

  private void deleteDataViewsFromDashboard(String widgetElementId) {
    dashboardManager.findAll().stream()
        .filter(dashboard -> dashboard.getWidgets().removeIf(w -> w.getDataViewElementId().equals(widgetElementId)))
        .forEach(dashboardManager::update);
  }

  private boolean isMultiSourceChart(DataExplorerWidgetModel chart) {
    if (chart == null || chart.getDataConfig() == null) {
      return false;
    }

    Object sourceConfigs = chart.getDataConfig().get("sourceConfigs");

    if (sourceConfigs instanceof Collection<?>) {
      return ((Collection<?>) sourceConfigs).size() > 1;
    }

    return false;
  }

  private String getDatasetName(DataExplorerWidgetModel chart) {
    if (chart == null || chart.getDataConfig() == null) {
      return null;
    }

    Object sourceConfigs = chart.getDataConfig().get("sourceConfigs");
    if (!(sourceConfigs instanceof List<?> configs) || configs.isEmpty()) {
      return null;
    }

    Object firstConfig = configs.get(0);
    if (!(firstConfig instanceof Map<?, ?> config)) {
      return null;
    }

    Object measureName = config.get("measureName");
    return measureName instanceof String ? (String) measureName : null;
  }

  private String getChartName(DataExplorerWidgetModel chart) {
    if (chart == null || chart.getBaseAppearanceConfig() == null) {
      return chart != null ? chart.getElementId() : null;
    }

    Object widgetTitle = chart.getBaseAppearanceConfig().get("widgetTitle");
    return widgetTitle != null ? widgetTitle.toString() : chart.getElementId();
  }

  private Long getCreatedAt(DataExplorerWidgetModel chart) {
    return chart != null && chart.getMetadata() != null
        ? chart.getMetadata().getCreatedAtEpochMs()
        : null;
  }

  private Long getLastModified(DataExplorerWidgetModel chart) {
    return chart != null && chart.getMetadata() != null
        ? chart.getMetadata().getLastModifiedEpochMs()
        : null;
  }
}
