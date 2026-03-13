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

import org.apache.streampipes.model.datalake.importer.CsvImportPreviewRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportPreviewResult;
import org.apache.streampipes.model.datalake.importer.CsvImportRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportResult;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaValidationRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaValidationResult;
import org.apache.streampipes.model.datalake.importer.CsvImportTargetMode;

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

  public DataLakeImportResource() {
    super();
    this.importService = new CsvDataLakeImportService(this.dataLakeMeasureManagement);
  }

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
        || this.dataLakeMeasureManagement.getExistingMeasureByName(target.getMeasurementName()).isEmpty()
        || this.checkPermissionByName(target.getMeasurementName(), "WRITE");
  }
}
