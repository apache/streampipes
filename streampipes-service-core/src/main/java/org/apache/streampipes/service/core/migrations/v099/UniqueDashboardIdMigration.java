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

import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.Objects;

public class UniqueDashboardIdMigration implements Migration {

  private final CRUDStorage<DashboardModel> dashboardStorage;
  private static final String Prefix = "sp:dataexplorerwidgetmodel";

  public UniqueDashboardIdMigration() {
    this.dashboardStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getDataExplorerDashboardStorage();
  }

  @Override
  public boolean shouldExecute() {
    return dashboardStorage
        .findAll()
        .stream()
        .anyMatch(d -> d.getWidgets()
            .stream()
            .anyMatch(w -> Objects.nonNull(w.getId()) && w.getId().startsWith(Prefix)));
  }

  @Override
  public void executeMigration() throws IOException {
    var allDashboards = dashboardStorage.findAll();

    allDashboards.forEach(d -> {
      d.getWidgets().forEach(w -> {
        if (Objects.nonNull(w.getId()) && w.getId().startsWith(Prefix)) {
          w.setDataViewElementId(w.getId());
          var uniqueDashboardWidgetId = Objects.nonNull(w.getId())
              ? w.getId()
              : RandomStringUtils.randomAlphanumeric(16);
          w.setId(uniqueDashboardWidgetId);
          w.setWidgetId(null);
        }
      });
      dashboardStorage.updateElement(d);
    });
  }

  @Override
  public String getDescription() {
    return "Moving dashboard widget IDs to dataViewElementId field and generating new unique IDs for widgets.";
  }
}
