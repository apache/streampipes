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

package org.apache.streampipes.ps;

import org.apache.streampipes.dataexplorer.DataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.influx.DataLakeMeasurementCount;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v4/datalake/measure")
public class DataLakeMeasureResourceV4 extends AbstractAuthGuardedRestResource {

  private final IDataExplorerSchemaManagement dataLakeMeasureManagement;

  public DataLakeMeasureResourceV4() {
    var dataLakeStorage = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getDataLakeStorage();
    this.dataLakeMeasureManagement = new DataExplorerSchemaManagement(dataLakeStorage);
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<DataLakeMeasure> addDataLake(@RequestBody DataLakeMeasure dataLakeMeasure) {
    DataLakeMeasure result = this.dataLakeMeasureManagement.createOrUpdateMeasurement(dataLakeMeasure);
    return ok(result);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Integer>> getDataLakeInfos(
      @RequestParam(value = "filter", required = false) List<String> measurementNames) {
    var allMeasurements = this.dataLakeMeasureManagement.getAllMeasurements();
    return ok(new DataLakeMeasurementCount(allMeasurements, measurementNames).countMeasurementSizes());
  }

  @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getDataLakeMeasure(@PathVariable("id") String elementId) {
    var measure = this.dataLakeMeasureManagement.getById(elementId);
    if (Objects.nonNull(measure)) {
      return ok(measure);
    } else {
      return notFound();
    }
  }

  @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateDataLakeMeasure(@PathVariable("id") String elementId,
                                                 @RequestBody DataLakeMeasure measure) {
    if (elementId.equals(measure.getElementId())) {
      try {
        this.dataLakeMeasureManagement.updateMeasurement(measure);
        return ok();
      } catch (IllegalArgumentException e) {
        return badRequest(e.getMessage());
      }
    }
    return badRequest();
  }

  @DeleteMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteDataLakeMeasure(@PathVariable("id") String elementId) {
    try {
      this.dataLakeMeasureManagement.deleteMeasurement(elementId);
      return ok();
    } catch (IllegalArgumentException e) {
      return badRequest(e.getMessage());
    }
  }
}
