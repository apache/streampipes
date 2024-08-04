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

package org.apache.streampipes.rest.impl.dashboard;

import org.apache.streampipes.model.dashboard.DashboardWidgetModel;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.storage.api.CRUDStorage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/dashboard/widgets")
public class DashboardWidget extends AbstractRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DashboardWidgetModel>> getAllDashboardWidgets() {
    return ok(getDashboardWidgetStorage().findAll());
  }

  @GetMapping(path = "/{widgetId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DashboardWidgetModel> getDashboardWidget(@PathVariable("widgetId") String widgetId) {
    return ok(getDashboardWidgetStorage().getElementById(widgetId));
  }

  @PutMapping(
      path = "/{widgetId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> modifyDashboardWidget(@RequestBody DashboardWidgetModel dashboardWidgetModel) {
    getDashboardWidgetStorage().updateElement(dashboardWidgetModel);
    return ok();
  }

  @DeleteMapping(path = "/{widgetId}")
  public ResponseEntity<Void> deleteDashboardWidget(@PathVariable("widgetId") String widgetId) {
    getDashboardWidgetStorage().deleteElementById(widgetId);
    return ok();
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<DashboardWidgetModel> createDashboardWidget(
      @RequestBody DashboardWidgetModel dashboardWidgetModel) {
    String elementId = UUID.randomUUID().toString();
    dashboardWidgetModel.setElementId(elementId);
    getDashboardWidgetStorage().persist(dashboardWidgetModel);
    return ok(getDashboardWidgetStorage().getElementById(elementId));
  }

  private CRUDStorage<DashboardWidgetModel> getDashboardWidgetStorage() {
    return getNoSqlStorage().getDashboardWidgetStorage();
  }
}
