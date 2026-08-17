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

package org.apache.streampipes.rest.impl.dataset;

import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.model.dataset.DatasetSummaryDto;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.resource.ResourceSummaryDto;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.explorer.IChartStorage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/v4/dataset/measure")
public class DatasetMeasureResource extends AbstractDatasetResource {

  private final SpResourceManager resourceManager;

  public DatasetMeasureResource(IChartStorage chartStorage,
                                 SpResourceManager resourceManager) {
    super(new ChartSchemaUpdateCoordinator(chartStorage), resourceManager);
    this.resourceManager = resourceManager;
  }

  @GetMapping(path = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResourceSummaryDto<DatasetSummaryDto> getDatasetSummary() {
    return resourceManager.manageDataLakeMeasures().getSummary(getAuthentication());
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<?> addDataset(@RequestBody DatasetMeasure datasetMeasure) {
    try {
      DatasetMeasure result = this.datasetMeasureManagement.createOrUpdateMeasurement(
          datasetMeasure,
          getAuthenticatedUserSid()
      );
      return ok(result);
    } catch (RuntimeException e) {
      return badRequest(SpLogMessage.from(e));
    }
  }

  /**
   * Handles HTTP GET requests to retrieve the entry count of a specified
   * measurement.
   *
   * @param elementId The measurement id to return the count for.
   * @return The entry count of the measurement.
   */
  @Operation(
      summary = "Retrieve measurement count",
      description = "Retrieves the entry count for the specified measurement from the data lake."
  )
  @GetMapping(path = "{id}/count", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority() and hasPermission(#elementId, 'READ')")
  public ResponseEntity<?> getEntryCountOfMeasurement(
      @Parameter(description = "The measurement id to return the count for.")
      @PathVariable("id") String elementId,
      @Parameter(description = "The number of days from today where the count should start")
      @RequestParam(value = "daysBack", defaultValue = "-1") int daysBack) {
    var measure = this.datasetMeasureManagement.getById(elementId);
    if (Objects.isNull(measure)) {
      return notFound();
    }

    var allMeasurements = this.datasetMeasureManagement.getAllMeasurements();
    return ok(new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getMeasurementCounter(
            allMeasurements,
            List.of(measure.getMeasureName()),
            daysBack)
        .countMeasurementSizes()
        .getOrDefault(measure.getMeasureName(), 0));
  }

  @Operation(
      summary = "Deprecated measurement count endpoint",
      description = "Use /api/v4/dataset/measure/{id}/count instead.",
      deprecated = true
  )
  @GetMapping(path = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  @Deprecated(since = "0.99.0", forRemoval = true)
  public ResponseEntity<?> getDeprecatedEntryCountOfMeasurement() {
    return ResponseEntity
        .status(HttpStatus.GONE)
        .body(SpLogMessage.warn(
            "Deprecated endpoint",
            "Use /api/v4/dataset/measure/{id}/count instead."
        ));
  }

  @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority() and hasPermission(#elementId, 'READ')")
  public ResponseEntity<?> getDatasetMeasure(@PathVariable("id") String elementId) {
    var measure = this.datasetMeasureManagement.getById(elementId);
    if (Objects.nonNull(measure)) {
      return ok(measure);
    } else {
      return notFound();
    }
  }

  @GetMapping(path = "byName/{measureName}", produces = MediaType.APPLICATION_JSON_VALUE)
 @PreAuthorize("this.hasReadAuthority() and this.checkPermissionByName(#measureName, 'READ')")
  public ResponseEntity<?> getDatasetMeasureByName(@PathVariable("measureName") String measureName) {
    var measureOpt = this.datasetMeasureManagement.getExistingMeasureByName(measureName);
    if (measureOpt.isPresent()) {
      return ok(measureOpt.get());
    } else {
      return notFound();
    }
  }

  @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
   @PreAuthorize("this.hasWriteAuthority() and hasPermission(#elementId, 'WRITE')")
  public ResponseEntity<?> updateDatasetMeasure(
      @PathVariable("id") String elementId,
      @RequestBody DatasetMeasure measure) {
    if (elementId.equals(measure.getElementId())) {
      try {
        this.datasetMeasureManagement.updateMeasurement(measure);
        return ok();
      } catch (IllegalArgumentException e) {
        return badRequest(e.getMessage());
      }
    }
    return badRequest();
  }

  @DeleteMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   @PreAuthorize("this.hasWriteAuthority() and hasPermission(#elementId, 'READ')")
  public ResponseEntity<?> deleteDatasetMeasure(@PathVariable("id") String elementId) {
    try {
      this.datasetMeasureManagement.deleteMeasurement(elementId);
      return ok();
    } catch (IllegalArgumentException e) {
      return badRequest(e.getMessage());
    }
  }
}
