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

public class DataExplorerWidgetResourceManager extends CrudResourceManager<DataExplorerWidgetModel> {

  private final DataExplorerResourceManager dashboardManager;

  public DataExplorerWidgetResourceManager(DataExplorerResourceManager dashboardManager,
                                           IDataExplorerWidgetStorage db) {
    super(db, DataExplorerWidgetModel.class);
    this.dashboardManager = dashboardManager;
  }

  public ResourceSummaryDto<ChartSummaryDto> getSummary(Authentication auth) {
    var charts = findAll()
        .stream()
        .filter(chart -> permissionEvaluator.hasPermission(auth, chart.getElementId(), "READ"))
        .map(chart -> new ChartSummaryDto(
            chart.getElementId(),
            chart.getBaseAppearanceConfig().get("widgetTitle").toString(),
            chart.getMetadata().getCreatedAtEpochMs(),
            chart.getMetadata().getLastModifiedEpochMs(),
            chart.getWidgetType(),
            isMultiSourceChart(chart)
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
}
