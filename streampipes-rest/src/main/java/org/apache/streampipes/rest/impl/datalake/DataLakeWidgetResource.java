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

import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.storage.api.IDataExplorerWidgetStorage;

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

@RestController
@RequestMapping("/api/v3/datalake/dashboard/widgets")
public class DataLakeWidgetResource extends AbstractRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DataExplorerWidgetModel>> getAllDataExplorerWidgets() {
    return ok(getDataExplorerWidgetStorage().getAllDataExplorerWidgets());
  }

  @GetMapping(path = "/{widgetId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DataExplorerWidgetModel> getDataExplorerWidget(@PathVariable("widgetId") String widgetId) {
    return ok(getDataExplorerWidgetStorage().getDataExplorerWidget(widgetId));
  }

  @PutMapping(
      path = "/{widgetId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DataExplorerWidgetModel> modifyDataExplorerWidget(
      @RequestBody DataExplorerWidgetModel dataExplorerWidgetModel) {
    getDataExplorerWidgetStorage().updateDataExplorerWidget(dataExplorerWidgetModel);
    return ok(getDataExplorerWidgetStorage().getDataExplorerWidget(dataExplorerWidgetModel.getId()));
  }

  @DeleteMapping(path = "/{widgetId}")
  public ResponseEntity<Void> deleteDataExplorerWidget(@PathVariable("widgetId") String widgetId) {
    getDataExplorerWidgetStorage().deleteDataExplorerWidget(widgetId);
    return ok();
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<DataExplorerWidgetModel> createDataExplorerWidget(
      @RequestBody DataExplorerWidgetModel dataExplorerWidgetModel) {
    String widgetId = getDataExplorerWidgetStorage().storeDataExplorerWidget(dataExplorerWidgetModel);
    return ok(getDataExplorerWidgetStorage().getDataExplorerWidget(widgetId));
  }

  private IDataExplorerWidgetStorage getDataExplorerWidgetStorage() {
    return getNoSqlStorage().getDataExplorerWidgetStorage();
  }

}
