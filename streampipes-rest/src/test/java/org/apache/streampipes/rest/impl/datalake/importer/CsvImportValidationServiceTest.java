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
import org.apache.streampipes.model.datalake.importer.CsvImportRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaIssueType;
import org.apache.streampipes.model.datalake.importer.CsvImportTarget;
import org.apache.streampipes.model.datalake.importer.CsvImportTargetMode;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsvImportValidationServiceTest {

  @Test
  void shouldRequireUploadIdOrInlineRowsForStoredImportRequest() {
    var service = new CsvImportValidationService(mock(IDataExplorerSchemaManagement.class));
    var request = new CsvImportRequest();
    request.setCsvConfig(makeConfig());
    request.setTarget(makeTarget(CsvImportTargetMode.NEW, "new-measure"));
    request.setTimestampColumn("timestamp");
    request.setColumns(List.of(makeImportColumn("timestamp", "LONG", "HEADER_PROPERTY", SO.DATE_TIME)));

    var result = service.validateStoredImportRequest(request);

    assertTrue(result.stream().anyMatch(message -> "uploadId".equals(message.getField())));
  }

  @Test
  void shouldRejectDuplicateMeasurementDuringPreviewValidation() {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    var existingMeasure = new DataLakeMeasure();
    existingMeasure.setMeasureName("existing-measure");
    when(schemaManagement.getExistingMeasureByName("existing-measure"))
        .thenReturn(Optional.of(existingMeasure));

    var service = new CsvImportValidationService(schemaManagement);
    var result = service.validatePreviewTarget(makeTarget(CsvImportTargetMode.NEW, "existing-measure"));

    assertTrue(result.stream().anyMatch(message -> message.getMessage().contains("already exists")));
  }

  @Test
  void shouldReportSchemaIssuesForExistingMeasurement() {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    var existingMeasure = new DataLakeMeasure();
    existingMeasure.setMeasureName("existing-measure");
    existingMeasure.setTimestampField("s0::timestamp");
    existingMeasure.setEventSchema(new EventSchema(List.of(
        makeEventProperty("timestamp", XSD.LONG.toString(), "HEADER_PROPERTY", SO.DATE_TIME),
        makeEventProperty("temperature", XSD.FLOAT.toString(), "MEASUREMENT_PROPERTY", null)
    )));
    when(schemaManagement.getExistingMeasureByName("existing-measure"))
        .thenReturn(Optional.of(existingMeasure));

    var service = new CsvImportValidationService(schemaManagement);
    var importSchema = new EventSchema(List.of(
        makeEventProperty("event_time", XSD.LONG.toString(), "HEADER_PROPERTY", SO.DATE_TIME),
        makeEventProperty("temperature", XSD.STRING.toString(), "DIMENSION_PROPERTY", null)
    ));

    var issues = service.validateSchemaTarget(
        makeTarget(CsvImportTargetMode.EXISTING, "existing-measure"),
        importSchema,
        "event_time"
    );

    assertEquals(4, issues.size());
    assertTrue(issues.stream().anyMatch(issue -> issue.getType() == CsvImportSchemaIssueType.TIMESTAMP_COLUMN_MISMATCH));
    assertTrue(issues.stream().anyMatch(issue -> issue.getType() == CsvImportSchemaIssueType.COLUMN_NAME_MISMATCH));
    assertTrue(issues.stream().anyMatch(issue -> issue.getType() == CsvImportSchemaIssueType.COLUMN_TYPE_MISMATCH));
    assertTrue(issues.stream().anyMatch(issue -> issue.getType() == CsvImportSchemaIssueType.COLUMN_SCOPE_MISMATCH));
  }

  @Test
  void shouldThrowWhenExistingMeasurementIsMissing() {
    var schemaManagement = mock(IDataExplorerSchemaManagement.class);
    when(schemaManagement.getExistingMeasureByName("missing-measure"))
        .thenReturn(Optional.empty());

    var service = new CsvImportValidationService(schemaManagement);

    var exception = assertThrows(
        CsvImportValidationException.class,
        () -> service.requireExistingMeasurement("missing-measure")
    );

    assertFalse(exception.getValidationMessages().isEmpty());
    assertTrue(exception.getValidationMessages().get(0).getMessage().contains("does not exist"));
  }

  private CsvImportConfiguration makeConfig() {
    var config = new CsvImportConfiguration();
    config.setDelimiter(",");
    config.setDecimalSeparator(".");
    config.setHasHeader(true);
    return config;
  }

  private CsvImportTarget makeTarget(CsvImportTargetMode mode, String measurementName) {
    var target = new CsvImportTarget();
    target.setMode(mode);
    target.setMeasurementName(measurementName);
    return target;
  }

  private CsvImportColumn makeImportColumn(
      String runtimeName,
      String runtimeType,
      String propertyScope,
      String semanticType
  ) {
    var column = new CsvImportColumn();
    column.setCsvColumn(runtimeName);
    column.setRuntimeName(runtimeName);
    column.setRuntimeType(runtimeType);
    column.setPropertyScope(propertyScope);
    column.setSemanticType(semanticType);
    return column;
  }

  private EventProperty makeEventProperty(
      String runtimeName,
      String runtimeType,
      String propertyScope,
      String semanticType
  ) {
    var property = new EventPropertyPrimitive();
    property.setRuntimeName(runtimeName);
    property.setRuntimeType(runtimeType);
    property.setPropertyScope(propertyScope);
    property.setSemanticType(semanticType);
    return property;
  }
}
