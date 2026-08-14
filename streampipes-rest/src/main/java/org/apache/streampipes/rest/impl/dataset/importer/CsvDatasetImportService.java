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

package org.apache.streampipes.rest.impl.dataset.importer;

import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.model.dataset.importer.CsvImportColumn;
import org.apache.streampipes.model.dataset.importer.CsvImportJobStartResult;
import org.apache.streampipes.model.dataset.importer.CsvImportJobStatus;
import org.apache.streampipes.model.dataset.importer.CsvImportPreviewRequest;
import org.apache.streampipes.model.dataset.importer.CsvImportPreviewResult;
import org.apache.streampipes.model.dataset.importer.CsvImportRequest;
import org.apache.streampipes.model.dataset.importer.CsvImportResult;
import org.apache.streampipes.model.dataset.importer.CsvImportSchemaIssue;
import org.apache.streampipes.model.dataset.importer.CsvImportSchemaValidationRequest;
import org.apache.streampipes.model.dataset.importer.CsvImportSchemaValidationResult;
import org.apache.streampipes.model.dataset.importer.CsvImportTarget;
import org.apache.streampipes.model.dataset.importer.CsvImportTargetMode;
import org.apache.streampipes.model.dataset.importer.CsvImportValidationMessage;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.rest.impl.dataset.DatasetDataWriter;
import org.apache.streampipes.storage.api.explorer.IDatasetMeasureStorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

public class CsvDatasetImportService {

  private static final int MAX_PREVIEW_ROWS = 50;
  private static final int MAX_ANALYSIS_ROWS = 200;
  private static final String STREAM_PREFIX = "s0::";

  private final DatasetDataWriter dataWriter;
  private final CsvImportUploadStorage uploadStorage;
  private final CsvImportParser parser;
  private final CsvImportValidationService validationService;
  private final IDataExplorerSchemaManagement schemaManagement;
  private final CsvImportJobManager jobManager;

  public CsvDatasetImportService(IDataExplorerSchemaManagement schemaManagement,
                                  IDatasetMeasureStorage datasetStorage) {
    this(
        schemaManagement,
        new DatasetDataWriter(false, true, datasetStorage),
        new CsvImportUploadStorage(),
        new CsvImportParser());
  }

  CsvDatasetImportService(
      IDataExplorerSchemaManagement schemaManagement,
      DatasetDataWriter dataWriter) {
    this(schemaManagement, dataWriter, new CsvImportUploadStorage(), new CsvImportParser());
  }

  CsvDatasetImportService(
      IDataExplorerSchemaManagement schemaManagement,
      DatasetDataWriter dataWriter,
      CsvImportUploadStorage uploadStorage) {
    this(schemaManagement, dataWriter, uploadStorage, new CsvImportParser());
  }

  CsvDatasetImportService(
      IDataExplorerSchemaManagement schemaManagement,
      DatasetDataWriter dataWriter,
      CsvImportUploadStorage uploadStorage,
      CsvImportParser parser) {
    this.schemaManagement = schemaManagement;
    this.dataWriter = dataWriter;
    this.uploadStorage = uploadStorage;
    this.parser = parser;
    this.validationService = new CsvImportValidationService(schemaManagement);
    this.jobManager = new CsvImportJobManager(this);
  }

  public CsvImportPreviewResult preview(CsvImportPreviewRequest request) {
    var validationMessages = validationService.validatePreviewRequest(request);
    var headers = parser.sanitizeHeaders(request.getHeaders());
    var rows = Optional.ofNullable(request.getRows()).orElseGet(Collections::emptyList);
    return buildPreviewResult(request, headers, rows, rows.size(), validationMessages, null);
  }

  public CsvImportPreviewResult preview(CsvImportPreviewRequest request, String principalSid) {
    if (!hasUploadId(request)) {
      return preview(request);
    }

    var validationMessages = validationService.validatePreviewConfiguration(request);
    if (!validationMessages.isEmpty()) {
      return buildInvalidPreviewResult(validationMessages, request.getUploadId());
    }

    try {
      var upload = resolveUpload(request.getUploadId(), principalSid);
      var csvSample = parser.readCsvSample(upload.path(), request.getCsvConfig(), MAX_ANALYSIS_ROWS);
      uploadStorage.updateTotalRows(upload.uploadId(), csvSample.totalRows());
      return buildPreviewResult(
          request,
          csvSample.headers(),
          csvSample.rows(),
          csvSample.totalRows(),
          validationMessages,
          upload.uploadId());
    } catch (CsvImportValidationException e) {
      return buildInvalidPreviewResult(e.getValidationMessages(), request.getUploadId());
    } catch (IOException | UncheckedIOException e) {
      return buildInvalidPreviewResult(
          List.of(message("file", "The CSV file could not be parsed with the current settings.")),
          request.getUploadId());
    }
  }

  public CsvImportPreviewResult preview(MultipartFile file, CsvImportPreviewRequest request, String principalSid)
      throws IOException {
    var validationMessages = validationService.validatePreviewConfiguration(request);
    if (!validationMessages.isEmpty()) {
      return buildInvalidPreviewResult(validationMessages, null);
    }

    var upload = uploadStorage.store(file, principalSid);
    try {
      var csvSample = parser.readCsvSample(upload.path(), request.getCsvConfig(), MAX_ANALYSIS_ROWS);
      uploadStorage.updateTotalRows(upload.uploadId(), csvSample.totalRows());
      return buildPreviewResult(
          request,
          csvSample.headers(),
          csvSample.rows(),
          csvSample.totalRows(),
          validationMessages,
          upload.uploadId());
    } catch (IOException | UncheckedIOException e) {
      uploadStorage.remove(upload.uploadId());
      throw e;
    }
  }

  public CsvImportSchemaValidationResult validateSchema(CsvImportSchemaValidationRequest request) {
    var validationMessages = validationService.validateSchemaRequest(request);
    var issues = new ArrayList<CsvImportSchemaIssue>();
    if (validationMessages.isEmpty()) {
      var eventSchema = parser.buildConfiguredEventSchema(
          parser.sanitizeImportColumns(request.getColumns()),
          request.getTimestampColumn());
      issues.addAll(validationService.validateSchemaTarget(
          request.getTarget(),
          eventSchema,
          request.getTimestampColumn()));
    }

    var result = new CsvImportSchemaValidationResult();
    result.setValidationMessages(validationMessages);
    result.setIssues(issues);
    result.setValid(validationMessages.isEmpty() && issues.isEmpty());
    return result;
  }

  public CsvImportResult importData(CsvImportRequest request, String principalSid) {
    return importData(request, principalSid, importedRows -> {
    });
  }

  CsvImportResult importData(CsvImportRequest request, String principalSid, IntConsumer progressConsumer) {
    if (hasUploadId(request)) {
      return importUploadedData(request, principalSid, progressConsumer);
    }

    var validationMessages = validationService.validateInlineImportRequest(request);
    if (!validationMessages.isEmpty()) {
      throw new CsvImportValidationException(validationMessages);
    }

    var eventSchema = parser.buildEventSchema(
        parser.sanitizeImportColumns(request.getColumns()),
        request.getRows(),
        request.getCsvConfig(),
        request.getTimestampColumn());

    validationMessages.addAll(
        validationService.validateImportTarget(request.getTarget(), eventSchema, request.getTimestampColumn()));
    if (!validationMessages.isEmpty()) {
      throw new CsvImportValidationException(validationMessages);
    }

    var measure = resolveTargetMeasurement(request, principalSid, eventSchema);
    var rows = parser.toImportRows(request);
    dataWriter.writeData(
        measure.measure(),
        request.getColumns().stream().map(CsvImportColumn::getRuntimeName).collect(Collectors.toList()),
        rows);
    progressConsumer.accept(rows.size());

    return buildImportResult(measure, rows.size());
  }

  public CsvImportJobStartResult startImportJob(CsvImportRequest request, String principalSid) {
    return jobManager.start(request, principalSid, getTotalRows(request, principalSid));
  }

  public Optional<CsvImportJobStatus> getImportJobStatus(String jobId, String principalSid) {
    return jobManager.getStatus(jobId, principalSid);
  }

  void cleanupUpload(CsvImportRequest request) {
    if (hasUploadId(request)) {
      uploadStorage.remove(request.getUploadId());
    }
  }

  private CsvImportResult importUploadedData(
      CsvImportRequest request,
      String principalSid,
      IntConsumer progressConsumer) {
    var validationMessages = validationService.validateStoredImportRequest(request);
    if (!validationMessages.isEmpty()) {
      throw new CsvImportValidationException(validationMessages);
    }

    var upload = resolveUpload(request.getUploadId(), principalSid);
    var sanitizedColumns = parser.sanitizeImportColumns(request.getColumns());
    var eventSchema = parser.buildConfiguredEventSchema(sanitizedColumns, request.getTimestampColumn());

    validationMessages.addAll(
        validationService.validateImportTarget(request.getTarget(), eventSchema, request.getTimestampColumn()));
    if (!validationMessages.isEmpty()) {
      throw new CsvImportValidationException(validationMessages);
    }

    var measure = resolveTargetMeasurement(request, principalSid, eventSchema);

    try {
      var importedRowCount = parser.importCsvFile(
          upload.path(),
          request,
          measure.measure(),
          dataWriter,
          progressConsumer);
      return buildImportResult(measure, importedRowCount);
    } catch (IOException | UncheckedIOException e) {
      throw new CsvImportValidationException(List.of(
          message("file", "The CSV file could not be parsed with the current settings.")));
    } finally {
      uploadStorage.remove(upload.uploadId());
    }
  }

  private CsvImportPreviewResult buildPreviewResult(
      CsvImportPreviewRequest request,
      List<String> headers,
      List<List<String>> rows,
      int totalRows,
      List<CsvImportValidationMessage> validationMessages,
      String uploadId) {
    var messages = new ArrayList<>(validationMessages);
    var columns = parser.inferColumns(headers, rows, request.getCsvConfig());
    columns = alignColumnsWithExistingTarget(request.getTarget(), columns);
    var eventSchema = parser.buildEventSchema(columns, rows, request.getCsvConfig(), null);
    messages.addAll(validationService.validatePreviewTarget(request.getTarget()));

    var result = new CsvImportPreviewResult();
    result.setUploadId(uploadId);
    result.setHeaders(headers);
    result.setPreviewRows(rows.stream().limit(MAX_PREVIEW_ROWS).collect(Collectors.toList()));
    result.setColumns(columns);
    result.setGuessedEventSchema(eventSchema);
    result.setTotalRows(totalRows);
    result.setTimestampCandidates(columns.stream()
        .filter(CsvImportColumn::isTimestampCandidate)
        .map(CsvImportColumn::getRuntimeName)
        .collect(Collectors.toList()));
    result.setValidationMessages(messages);
    result.setValid(messages.isEmpty());
    return result;
  }

  private CsvImportPreviewResult buildInvalidPreviewResult(
      List<CsvImportValidationMessage> validationMessages,
      String uploadId) {
    var result = new CsvImportPreviewResult();
    result.setUploadId(uploadId);
    result.setValidationMessages(validationMessages);
    result.setValid(false);
    return result;
  }

  private int getTotalRows(CsvImportRequest request, String principalSid) {
    if (hasUploadId(request)) {
      return resolveUpload(request.getUploadId(), principalSid).totalRows();
    }
    return Optional.ofNullable(request.getRows()).orElseGet(Collections::emptyList).size();
  }

  private CsvImportUploadStorage.StoredUpload resolveUpload(String uploadId, String principalSid) {
    var upload = uploadStorage.get(uploadId).orElseThrow(() -> new CsvImportValidationException(List.of(
        message("uploadId", "The uploaded CSV file was not found. Please upload the file again."))));

    if (!Objects.equals(upload.ownerSid(), principalSid)) {
      throw new CsvImportValidationException(List.of(
          message("uploadId", "The uploaded CSV file is no longer available for this user.")));
    }

    return upload;
  }

  private boolean hasUploadId(CsvImportPreviewRequest request) {
    return request != null && request.getUploadId() != null && !request.getUploadId().isBlank();
  }

  private boolean hasUploadId(CsvImportRequest request) {
    return request != null && request.getUploadId() != null && !request.getUploadId().isBlank();
  }

  private CsvImportValidationMessage message(String field, String message) {
    return new CsvImportValidationMessage(field, message);
  }

  private List<CsvImportColumn> alignColumnsWithExistingTarget(
      CsvImportTarget target,
      List<CsvImportColumn> columns) {
    if (target == null
        || target.getMode() != CsvImportTargetMode.EXISTING) {
      return columns;
    }

    var existingMeasure = schemaManagement.getExistingMeasureByName(target.getMeasurementName().trim());
    if (existingMeasure.isEmpty()) {
      return columns;
    }

    Map<String, CsvImportColumn> existingByName = CsvImportColumnMapper
        .fromEventSchema(existingMeasure.get().getEventSchema())
        .stream()
        .collect(Collectors.toMap(CsvImportColumn::getRuntimeName, Function.identity()));

    columns.forEach(column -> {
      var existingColumn = existingByName.get(column.getRuntimeName());
      if (existingColumn != null) {
        column.setRuntimeType(existingColumn.getRuntimeType());
        column.setPropertyScope(existingColumn.getPropertyScope());
        column.setSemanticType(existingColumn.getSemanticType());
      }
    });

    return columns;
  }

  private StoredMeasure resolveTargetMeasurement(
      CsvImportRequest request,
      String principalSid,
      EventSchema eventSchema) {
    if (request.getTarget().getMode() == CsvImportTargetMode.NEW) {
      var measure = new DatasetMeasure();
      measure.setMeasureName(request.getTarget().getMeasurementName().trim());
      measure.setTimestampField(STREAM_PREFIX + request.getTimestampColumn());
      measure.setEventSchema(removeTimestampProperty(eventSchema, request.getTimestampColumn()));
      return new StoredMeasure(schemaManagement.createOrUpdateMeasurement(measure, principalSid), true);
    }

    return new StoredMeasure(
        validationService.requireExistingMeasurement(request.getTarget().getMeasurementName()),
        false);
  }

  private CsvImportResult buildImportResult(StoredMeasure measure, int importedRowCount) {
    var result = new CsvImportResult();
    result.setMeasurementId(measure.measure().getElementId());
    result.setMeasurementName(measure.measure().getMeasureName());
    result.setCreatedNewMeasurement(measure.createdNewMeasurement());
    result.setImportedRowCount(importedRowCount);
    result.setValidationMessages(List.of());
    return result;
  }

  private EventSchema removeTimestampProperty(EventSchema eventSchema, String timestampColumn) {
    var sanitizedSchema = new EventSchema();
    sanitizedSchema.setEventProperties(eventSchema.getEventProperties()
        .stream()
        .filter(property -> !Objects.equals(property.getRuntimeName(), timestampColumn))
        .collect(Collectors.toList()));
    return sanitizedSchema;
  }

  private record StoredMeasure(DatasetMeasure measure, boolean createdNewMeasurement) {
  }
}
