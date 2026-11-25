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

import org.apache.streampipes.model.dashboard.DashboardItem;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dataset.DataExplorerWidgetModel;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardResourceManagerTest {

  @Test
  void compositeDashboardLoadsChartsByDataViewElementIdInWidgetOrder() {
    var dashboardStorage = mock(IDashboardStorage.class);
    var chartStorage = mock(IChartStorage.class);

    var dashboard = new DashboardModel();
    dashboard.setElementId("dashboard-1");
    dashboard.setRev("1-a");
    dashboard.setWidgets(List.of(dashboardItem("chart-b"), dashboardItem("chart-a")));
    when(dashboardStorage.getElementById("dashboard-1")).thenReturn(dashboard);

    var chartA = chart("chart-a", "3-c");
    var chartB = chart("chart-b", "2-b");
    when(chartStorage.getElementById("chart-a")).thenReturn(chartA);
    when(chartStorage.getElementById("chart-b")).thenReturn(chartB);

    var result = new DashboardResourceManager(
        dashboardStorage,
        chartStorage,
        mock(PermissionResourceManager.class),
        mock(ResourceDeletionManager.class))
        .getCompositeDashboard("dashboard-1");

    assertSame(dashboard, result.dashboard());
    assertEquals(List.of(chartB, chartA), result.widgets());
    assertEquals("1-a|2-b|3-c", result.getRevisionHash());
  }

  private DashboardItem dashboardItem(String chartId) {
    var item = new DashboardItem();
    item.setId("unique-widget-id-" + chartId);
    item.setDataViewElementId(chartId);
    return item;
  }

  private DataExplorerWidgetModel chart(String elementId, String rev) {
    var chart = new DataExplorerWidgetModel();
    chart.setElementId(elementId);
    chart.setRev(rev);
    return chart;
  }
}
