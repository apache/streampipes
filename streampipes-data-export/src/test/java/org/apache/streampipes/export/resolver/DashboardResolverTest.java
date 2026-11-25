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
package org.apache.streampipes.export.resolver;

import org.apache.streampipes.model.dashboard.DashboardItem;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.resource.management.DashboardResourceManager;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardResolverTest {

  @Test
  void getChartsReturnsReferencedChartIdsInsteadOfTileIds() {
    var resolver = resolverFor(
        item("tile-1", "chart-a"),
        item("tile-2", "chart-b"),
        // the same chart placed twice on the dashboard is exported once
        item("tile-3", "chart-a"));

    assertEquals(List.of("chart-a", "chart-b"), resolver.getCharts("dashboard-1"));
  }

  @Test
  void getChartsFallsBackToIdForLegacyItemsWithoutDataViewElementId() {
    var resolver = resolverFor(
        item("tile-1", "chart-a"),
        // pre-0.99 dashboard item: id holds the chart id, dataViewElementId is not set
        item("legacy-chart", null),
        item(null, null));

    assertEquals(List.of("chart-a", "legacy-chart"), resolver.getCharts("dashboard-1"));
  }

  private DashboardResolver resolverFor(DashboardItem... items) {
    var dashboard = new DashboardModel();
    dashboard.setElementId("dashboard-1");
    dashboard.setWidgets(List.of(items));

    var storage = mock(IDashboardStorage.class);
    when(storage.getElementById("dashboard-1")).thenReturn(dashboard);
    var resourceManager = mock(DashboardResourceManager.class);
    when(resourceManager.getDb()).thenReturn(storage);

    return new DashboardResolver(resourceManager);
  }

  private DashboardItem item(String id, String dataViewElementId) {
    var item = new DashboardItem();
    item.setId(id);
    item.setDataViewElementId(dataViewElementId);
    return item;
  }
}
