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

package org.apache.streampipes.rest.impl.datalake.importer;

import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.model.datalake.importer.CsvImportPreviewRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportPreviewResult;
import org.apache.streampipes.model.datalake.importer.CsvImportRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportResult;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaValidationRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaValidationResult;
import org.apache.streampipes.model.datalake.importer.CsvImportTargetMode;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.impl.datalake.AbstractDataLakeResource;
import org.apache.streampipes.storage.api.explorer.IChartStorage;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v4/datalake/import")
public class DataLakeImportResource extends AbstractDataLakeResource {

  private final CsvDataLakeImportService importService;

  public DataLakeImportResource(IChartStorage chartStorage,
                                SpResourceManager resourceManager) {
    super(new ChartSchemaUpdateCoordinator(chartStorage), resourceManager);
    this.importService = new CsvDataLakeImportService(
        getDataLakeMeasureManagement(),
        resourceManager.manageDataLakeMeasures().getDb()
    );
  }

  /**
   * Generates a CSV preview from inline JSON content or reuses a previously uploaded file via {@code uploadId}.
   * <p>
   * This endpoint supports the second step of the single-upload workflow: the UI uploads the file once through the
   * multipart preview endpoint, receives an {@code uploadId}, and can then call this JSON endpoint again when preview
   * settings change without re-uploading the CSV.
   */
  @PostMapping(
      path = "/preview",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<CsvImportPreviewResult> preview(@RequestBody CsvImportPreviewRequest request) {
    if (!hasWritePermission(request.getTarget())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return ok(importService.preview(request, getAuthenticatedUserSid()));
  }

  /**
   * Uploads a CSV file, stores it temporarily on the server, and returns a preview together with an {@code uploadId}.
   * <p>
   * The returned {@code uploadId} is later used by the final import call so large files only need to be transferred
   * once.
   */
  @PostMapping(
      path = "/preview",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<CsvImportPreviewResult> preview(
      @RequestPart("file") MultipartFile file,
      @RequestPart("request") CsvImportPreviewRequest request
  ) throws IOException {
    if (!hasWritePermission(request.getTarget())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return ok(importService.preview(file, request, getAuthenticatedUserSid()));
  }

  /**
   * Validates the configured CSV column mapping against an existing target measurement schema.
   */
  @PostMapping(
      path = "/validate-schema",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<CsvImportSchemaValidationResult> validateSchema(
      @RequestBody CsvImportSchemaValidationRequest request
  ) {
    if (!hasWritePermission(request.getTarget())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return ok(importService.validateSchema(request));
  }

  /**
   * Imports CSV data either from inline JSON rows or from a previously uploaded file referenced by {@code uploadId}.
   * <p>
   * In the single-upload workflow, clients first call the multipart preview endpoint, keep the returned
   * {@code uploadId}, and then call this endpoint with that identifier and the final import configuration.
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<CsvImportResult> importData(@RequestBody CsvImportRequest request) {
    if (!hasWritePermission(request.getTarget())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      return ok(importService.importData(request, getAuthenticatedUserSid()));
    } catch (CsvImportValidationException e) {
      var result = new CsvImportResult();
      result.setValidationMessages(e.getValidationMessages());
      return ResponseEntity.badRequest().body(result);
    }
  }

  private boolean hasWritePermission(org.apache.streampipes.model.datalake.importer.CsvImportTarget target) {
    return target == null
        || target.getMode() != CsvImportTargetMode.EXISTING
        || getDataLakeMeasureManagement().getExistingMeasureByName(target.getMeasurementName()).isEmpty()
        || this.checkPermissionByName(target.getMeasurementName(), "WRITE");
  }
}
