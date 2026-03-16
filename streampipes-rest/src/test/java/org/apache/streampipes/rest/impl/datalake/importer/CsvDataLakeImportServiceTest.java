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

import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.importer.CsvImportColumn;
import org.apache.streampipes.model.datalake.importer.CsvImportConfiguration;
import org.apache.streampipes.model.datalake.importer.CsvImportPreviewRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaIssueType;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaValidationRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportTarget;
import org.apache.streampipes.model.datalake.importer.CsvImportTargetMode;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.rest.impl.datalake.DataLakeDataWriter;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CsvDataLakeImportServiceTest {

  @Test
  void shouldInferTypesAndRejectDuplicateNewMeasurementNames() {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    var dataWriter = mock(DataLakeDataWriter.class);
    var service = new CsvDataLakeImportService(schemaManagement, dataWriter);

    var existingMeasure = new DataLakeMeasure();
    existingMeasure.setMeasureName("existing-measure");
    when(schemaManagement.getExistingMeasureByName("existing-measure"))
        .thenReturn(Optional.of(existingMeasure));

    var result = service.preview(makePreviewRequest("existing-measure"));

    assertFalse(result.isValid());
    assertEquals("LONG", result.getColumns().get(0).getInferredType());
    assertEquals("FLOAT", result.getColumns().get(1).getInferredType());
    assertTrue(result.getValidationMessages()
        .stream()
        .anyMatch(message -> message.getMessage().contains("already exists")));
  }

  @Test
  void shouldReportSchemaMismatchInDedicatedValidationCall() {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    var dataWriter = mock(DataLakeDataWriter.class);
    var service = new CsvDataLakeImportService(schemaManagement, dataWriter);

    var existingMeasure = new DataLakeMeasure();
    existingMeasure.setMeasureName("existing-measure");
    existingMeasure.setTimestampField("s0::timestamp");
    existingMeasure.setEventSchema(makeExistingSchema());

    when(schemaManagement.getExistingMeasureByName("existing-measure"))
        .thenReturn(Optional.of(existingMeasure));

    var request = new CsvImportSchemaValidationRequest();
    request.setTarget(makeTarget(CsvImportTargetMode.EXISTING, "existing-measure"));
    request.setTimestampColumn("timestamp");
    request.setColumns(makeImportRequest(CsvImportTargetMode.EXISTING, "existing-measure").getColumns());
    request.getColumns().get(1).setRuntimeType("STRING");

    var result = service.validateSchema(request);

    assertFalse(result.isValid());
    assertTrue(result.getIssues()
        .stream()
        .anyMatch(issue -> issue.getType() == CsvImportSchemaIssueType.COLUMN_TYPE_MISMATCH));
  }

  @Test
  void shouldCreateNewMeasurementAndReuseSharedWriter() {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    var dataWriter = mock(DataLakeDataWriter.class);
    var service = new CsvDataLakeImportService(schemaManagement, dataWriter);

    when(schemaManagement.getExistingMeasureByName("new-measure"))
        .thenReturn(Optional.empty());
    when(schemaManagement.createOrUpdateMeasurement(any(DataLakeMeasure.class), any()))
        .thenAnswer(invocation -> {
          var measure = invocation.getArgument(0, DataLakeMeasure.class);
          measure.setElementId("measure-id");
          return measure;
        });

    var result = service.importData(makeImportRequest(CsvImportTargetMode.NEW, "new-measure"), "sid");

    assertTrue(result.isCreatedNewMeasurement());
    assertEquals(2, result.getImportedRowCount());
    assertEquals("new-measure", result.getMeasurementName());
    verify(dataWriter).writeData(any(DataLakeMeasure.class), anyList(), anyList());
  }

  @Test
  void shouldPreviewOnceAndImportFromStoredUpload() throws Exception {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    var dataWriter = mock(DataLakeDataWriter.class);
    var uploadStorage = new CsvImportUploadStorage();
    var service = new CsvDataLakeImportService(schemaManagement, dataWriter, uploadStorage);

    when(schemaManagement.getExistingMeasureByName("uploaded-measure"))
        .thenReturn(Optional.empty());
    when(schemaManagement.createOrUpdateMeasurement(any(DataLakeMeasure.class), any()))
        .thenAnswer(invocation -> {
          var measure = invocation.getArgument(0, DataLakeMeasure.class);
          measure.setElementId("measure-id");
          return measure;
        });

    var previewRequest = new CsvImportPreviewRequest();
    previewRequest.setCsvConfig(makeCsvConfigWithCommaDelimiter());
    previewRequest.setTarget(makeTarget(CsvImportTargetMode.NEW, "uploaded-measure"));

    var multipartFile = mock(MultipartFile.class);
    when(multipartFile.getOriginalFilename()).thenReturn("upload.csv");
    when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(
        "timestamp,temperature\n1710000000000,21.3\n1710000060000,22.1\n".getBytes()
    ));

    var previewResult = service.preview(
        multipartFile,
        previewRequest,
        "sid"
    );

    assertTrue(previewResult.isValid());
    assertEquals(2, previewResult.getPreviewRows().size());
    assertTrue(previewResult.getUploadId() != null && !previewResult.getUploadId().isBlank());

    var importRequest = new CsvImportRequest();
    importRequest.setUploadId(previewResult.getUploadId());
    importRequest.setCsvConfig(makeCsvConfigWithCommaDelimiter());
    importRequest.setTarget(makeTarget(CsvImportTargetMode.NEW, "uploaded-measure"));
    importRequest.setTimestampColumn("timestamp");
    importRequest.setColumns(previewResult.getColumns());

    var importResult = service.importData(importRequest, "sid");

    assertTrue(importResult.isCreatedNewMeasurement());
    assertEquals(2, importResult.getImportedRowCount());
    verify(dataWriter).writeData(any(DataLakeMeasure.class), anyList(), anyList());
  }

  @Test
  void shouldRejectMissingTimestampValuesInUploadedCsv() throws Exception {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    var dataWriter = mock(DataLakeDataWriter.class);
    var uploadStorage = new CsvImportUploadStorage();
    var service = new CsvDataLakeImportService(schemaManagement, dataWriter, uploadStorage);

    when(schemaManagement.getExistingMeasureByName("uploaded-measure"))
        .thenReturn(Optional.empty());
    when(schemaManagement.createOrUpdateMeasurement(any(DataLakeMeasure.class), any()))
        .thenAnswer(invocation -> {
          var measure = invocation.getArgument(0, DataLakeMeasure.class);
          measure.setElementId("measure-id");
          return measure;
        });

    var previewRequest = new CsvImportPreviewRequest();
    previewRequest.setCsvConfig(makeCsvConfigWithCommaDelimiter());
    previewRequest.setTarget(makeTarget(CsvImportTargetMode.NEW, "uploaded-measure"));

    var multipartFile = mock(MultipartFile.class);
    when(multipartFile.getOriginalFilename()).thenReturn("upload.csv");
    when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(
        "timestamp,temperature\n1710000000000,21.3\n,22.1\n".getBytes()
    ));

    var previewResult = service.preview(multipartFile, previewRequest, "sid");

    var importRequest = new CsvImportRequest();
    importRequest.setUploadId(previewResult.getUploadId());
    importRequest.setCsvConfig(makeCsvConfigWithCommaDelimiter());
    importRequest.setTarget(makeTarget(CsvImportTargetMode.NEW, "uploaded-measure"));
    importRequest.setTimestampColumn("timestamp");
    importRequest.setColumns(previewResult.getColumns());

    var exception = assertThrows(
        CsvImportValidationException.class,
        () -> service.importData(importRequest, "sid")
    );

    assertTrue(exception.getValidationMessages()
        .stream()
        .anyMatch(message -> message.getMessage().contains("missing a value for timestamp column")));
  }

  @Test
  void shouldRejectSchemaMismatchForExistingMeasurement() {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    var dataWriter = mock(DataLakeDataWriter.class);
    var service = new CsvDataLakeImportService(schemaManagement, dataWriter);

    var existingMeasure = new DataLakeMeasure();
    existingMeasure.setMeasureName("existing-measure");
    existingMeasure.setTimestampField("s0::timestamp");
    existingMeasure.setEventSchema(makeExistingSchema());

    when(schemaManagement.getExistingMeasureByName("existing-measure"))
        .thenReturn(Optional.of(existingMeasure));

    var request = makeImportRequest(CsvImportTargetMode.EXISTING, "existing-measure");
    request.getColumns().get(1).setRuntimeType("STRING");

    var exception = assertThrows(
        CsvImportValidationException.class,
        () -> service.importData(request, "sid")
    );

    assertTrue(exception.getValidationMessages()
        .stream()
        .anyMatch(message -> message.getMessage().contains("exactly match")));
  }

  private CsvImportPreviewRequest makePreviewRequest(String measurementName) {
    var request = new CsvImportPreviewRequest();
    request.setCsvConfig(makeCsvConfig());
    request.setHeaders(List.of("timestamp", "temperature"));
    request.setRows(List.of(
        List.of("1710000000000", "21.3"),
        List.of("1710000060000", "22.1")
    ));
    request.setTarget(makeTarget(CsvImportTargetMode.NEW, measurementName));
    return request;
  }

  private CsvImportRequest makeImportRequest(CsvImportTargetMode mode, String measurementName) {
    var timestampColumn = new CsvImportColumn();
    timestampColumn.setCsvColumn("timestamp");
    timestampColumn.setRuntimeName("timestamp");
    timestampColumn.setRuntimeType("LONG");
    timestampColumn.setPropertyScope("HEADER_PROPERTY");
    timestampColumn.setSemanticType(SO.DATE_TIME);

    var measurementColumn = new CsvImportColumn();
    measurementColumn.setCsvColumn("temperature");
    measurementColumn.setRuntimeName("temperature");
    measurementColumn.setRuntimeType("FLOAT");
    measurementColumn.setPropertyScope("MEASUREMENT_PROPERTY");

    var request = new CsvImportRequest();
    request.setCsvConfig(makeCsvConfig());
    request.setHeaders(List.of("timestamp", "temperature"));
    request.setRows(List.of(
        List.of("1710000000000", "21.3"),
        List.of("1710000060000", "22.1")
    ));
    request.setTarget(makeTarget(mode, measurementName));
    request.setTimestampColumn("timestamp");
    request.setColumns(List.of(timestampColumn, measurementColumn));
    return request;
  }

  private CsvImportTarget makeTarget(CsvImportTargetMode mode, String measurementName) {
    var target = new CsvImportTarget();
    target.setMode(mode);
    target.setMeasurementName(measurementName);
    return target;
  }

  private CsvImportConfiguration makeCsvConfig() {
    var config = new CsvImportConfiguration();
    config.setDelimiter(";");
    config.setDecimalSeparator(".");
    config.setHasHeader(true);
    return config;
  }

  private CsvImportConfiguration makeCsvConfigWithCommaDelimiter() {
    var config = new CsvImportConfiguration();
    config.setDelimiter(",");
    config.setDecimalSeparator(".");
    config.setHasHeader(true);
    return config;
  }

  private EventSchema makeExistingSchema() {
    var timestamp = new EventPropertyPrimitive();
    timestamp.setRuntimeName("timestamp");
    timestamp.setRuntimeType(XSD.LONG.toString());
    timestamp.setPropertyScope("HEADER_PROPERTY");
    timestamp.setSemanticType(SO.DATE_TIME);

    var temperature = new EventPropertyPrimitive();
    temperature.setRuntimeName("temperature");
    temperature.setRuntimeType(XSD.FLOAT.toString());
    temperature.setPropertyScope("MEASUREMENT_PROPERTY");

    return new EventSchema(List.of(timestamp, temperature));
  }
}
