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
import org.apache.streampipes.model.datalake.importer.CsvImportConfiguration;
import org.apache.streampipes.model.datalake.importer.CsvImportPreviewRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaIssue;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaIssueType;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaValidationRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportTarget;
import org.apache.streampipes.model.datalake.importer.CsvImportTargetMode;
import org.apache.streampipes.model.datalake.importer.CsvImportValidationMessage;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.vocabulary.SO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class CsvImportValidationService {

  private final IDataExplorerSchemaManagement schemaManagement;

  CsvImportValidationService(IDataExplorerSchemaManagement schemaManagement) {
    this.schemaManagement = schemaManagement;
  }

  List<CsvImportValidationMessage> validatePreviewConfiguration(CsvImportPreviewRequest request) {
    var messages = new ArrayList<CsvImportValidationMessage>();
    if (request == null) {
      messages.add(message("request", "Import request must be provided."));
      return messages;
    }
    validateCsvConfig(request.getCsvConfig(), messages);
    return messages;
  }

  List<CsvImportValidationMessage> validatePreviewRequest(CsvImportPreviewRequest request) {
    var messages = validatePreviewConfiguration(request);
    if (request == null) {
      return messages;
    }
    if (request.getHeaders() == null || request.getHeaders().isEmpty()) {
      messages.add(message("headers", "At least one header must be provided."));
    }
    if (request.getRows() == null || request.getRows().isEmpty()) {
      messages.add(message("rows", "At least one row must be provided."));
    }
    validateRowsMatchHeaders(request.getHeaders(), request.getRows(), messages);
    return messages;
  }

  List<CsvImportValidationMessage> validateInlineImportRequest(CsvImportRequest request) {
    var messages = validateStoredImportRequest(request);
    if (request == null) {
      return messages;
    }
    if (request.getHeaders() == null || request.getHeaders().isEmpty()) {
      messages.add(message("headers", "At least one header must be provided."));
    }
    if (request.getRows() == null || request.getRows().isEmpty()) {
      messages.add(message("rows", "At least one row must be provided."));
    }
    validateRowsMatchHeaders(request.getHeaders(), request.getRows(), messages);
    return messages;
  }

  List<CsvImportValidationMessage> validateStoredImportRequest(CsvImportRequest request) {
    var messages = new ArrayList<CsvImportValidationMessage>();
    if (request == null) {
      messages.add(message("request", "Import request must be provided."));
      return messages;
    }
    validateCsvConfig(request.getCsvConfig(), messages);

    if (request.getTarget() == null || request.getTarget().getMode() == null) {
      messages.add(message("target", "An import target must be selected."));
    }
    if (request.getTimestampColumn() == null || request.getTimestampColumn().isBlank()) {
      messages.add(message("timestampColumn", "A timestamp column must be selected."));
    }
    if (request.getColumns() == null || request.getColumns().isEmpty()) {
      messages.add(message("columns", "Column configuration must be provided."));
    }
    if (!hasUploadId(request)
        && (request.getRows() == null || request.getRows().isEmpty())
        && (request.getHeaders() == null || request.getHeaders().isEmpty())) {
      messages.add(message("uploadId", "Either an uploadId or inline CSV rows must be provided."));
    }
    return messages;
  }

  List<CsvImportValidationMessage> validateSchemaRequest(CsvImportSchemaValidationRequest request) {
    var messages = new ArrayList<CsvImportValidationMessage>();
    if (request == null) {
      messages.add(message("request", "Schema validation request must be provided."));
      return messages;
    }
    if (request.getTarget() == null || request.getTarget().getMode() == null) {
      messages.add(message("target", "An import target must be selected."));
    }
    if (request.getTimestampColumn() == null || request.getTimestampColumn().isBlank()) {
      messages.add(message("timestampColumn", "A timestamp column must be selected."));
    }
    if (request.getColumns() == null || request.getColumns().isEmpty()) {
      messages.add(message("columns", "Column configuration must be provided."));
    }
    return messages;
  }

  List<CsvImportValidationMessage> validatePreviewTarget(CsvImportTarget target) {
    var messages = new ArrayList<CsvImportValidationMessage>();
    if (target == null || target.getMode() == null) {
      return messages;
    }

    var measurementName = target.getMeasurementName();
    if (measurementName == null || measurementName.isBlank()) {
      messages.add(message("target.measurementName", "A measurement name must be provided."));
      return messages;
    }

    if (target.getMode() == CsvImportTargetMode.NEW) {
      if (schemaManagement.getExistingMeasureByName(measurementName.trim()).isPresent()) {
        messages.add(message("target.measurementName", "A measurement with this name already exists."));
      }
    } else if (schemaManagement.getExistingMeasureByName(measurementName.trim()).isEmpty()) {
      messages.add(message("target.measurementName", "The selected measurement does not exist."));
    }

    return messages;
  }

  List<CsvImportSchemaIssue> validateSchemaTarget(
      CsvImportTarget target,
      EventSchema importSchema,
      String timestampColumn
  ) {
    var issues = new ArrayList<CsvImportSchemaIssue>();
    if (target == null || target.getMode() == null) {
      return issues;
    }

    var measurementName = target.getMeasurementName();
    if (measurementName == null || measurementName.isBlank() || target.getMode() == CsvImportTargetMode.NEW) {
      return issues;
    }

    var existingOpt = schemaManagement.getExistingMeasureByName(measurementName.trim());
    if (existingOpt.isPresent() && importSchema != null) {
      var effectiveTimestampColumn = timestampColumn;
      if (effectiveTimestampColumn == null) {
        effectiveTimestampColumn = importSchema.getEventProperties().stream()
            .filter(property -> SO.DATE_TIME.equals(property.getSemanticType()))
            .map(EventProperty::getRuntimeName)
            .findFirst()
            .orElse(null);
      }
      if (effectiveTimestampColumn != null) {
        issues.addAll(compareSchemas(existingOpt.get(), importSchema, effectiveTimestampColumn));
      }
    }
    return issues;
  }

  List<CsvImportValidationMessage> validateImportTarget(
      CsvImportTarget target,
      EventSchema importSchema,
      String timestampColumn
  ) {
    var messages = new ArrayList<CsvImportValidationMessage>();
    if (target == null || target.getMode() == null) {
      return messages;
    }

    var measurementName = target.getMeasurementName();
    if (measurementName == null || measurementName.isBlank()) {
      messages.add(message("target.measurementName", "A measurement name must be provided."));
      return messages;
    }

    if (target.getMode() == CsvImportTargetMode.NEW) {
      if (schemaManagement.getExistingMeasureByName(measurementName.trim()).isPresent()) {
        messages.add(message("target.measurementName", "A measurement with this name already exists."));
      }
      return messages;
    }

    var existingOpt = schemaManagement.getExistingMeasureByName(measurementName.trim());
    if (existingOpt.isEmpty()) {
      messages.add(message("target.measurementName", "The selected measurement does not exist."));
    } else if (importSchema != null && !compareSchemas(existingOpt.get(), importSchema, timestampColumn).isEmpty()) {
      messages.add(message("columns", "Imported columns must exactly match the existing measurement schema."));
    }
    return messages;
  }

  DataLakeMeasure requireExistingMeasurement(String measurementName) {
    return schemaManagement.getExistingMeasureByName(measurementName)
        .orElseThrow(() -> new CsvImportValidationException(List.of(
            message("target.measurementName", "The selected measurement does not exist.")
        )));
  }

  private List<CsvImportSchemaIssue> compareSchemas(
      DataLakeMeasure existingMeasure,
      EventSchema importSchema,
      String timestampColumn
  ) {
    var issues = new ArrayList<CsvImportSchemaIssue>();

    if (!Objects.equals(existingMeasure.getTimestampFieldName(), timestampColumn)) {
      issues.add(issue(
          CsvImportSchemaIssueType.TIMESTAMP_COLUMN_MISMATCH,
          timestampColumn,
          existingMeasure.getTimestampFieldName(),
          timestampColumn
      ));
    }

    var existingProperties = existingMeasure.getEventSchema()
        .getEventProperties()
        .stream()
        .collect(Collectors.toMap(EventProperty::getRuntimeName, property -> property));
    var importedProperties = importSchema.getEventProperties()
        .stream()
        .collect(Collectors.toMap(EventProperty::getRuntimeName, property -> property));

    importedProperties.keySet().stream()
        .filter(property -> !existingProperties.containsKey(property))
        .sorted()
        .forEach(property -> issues.add(issue(
            CsvImportSchemaIssueType.COLUMN_NAME_MISMATCH,
            property,
            null,
            property
        )));

    for (var entry : existingProperties.entrySet()) {
      var imported = importedProperties.get(entry.getKey());
      if (imported == null) {
        continue;
      }

      if (!Objects.equals(getRuntimeType(entry.getValue()), getRuntimeType(imported))) {
        issues.add(issue(
            CsvImportSchemaIssueType.COLUMN_TYPE_MISMATCH,
            entry.getKey(),
            getRuntimeType(entry.getValue()),
            getRuntimeType(imported)
        ));
      }

      if (!Objects.equals(entry.getValue().getPropertyScope(), imported.getPropertyScope())) {
        issues.add(issue(
            CsvImportSchemaIssueType.COLUMN_SCOPE_MISMATCH,
            entry.getKey(),
            entry.getValue().getPropertyScope(),
            imported.getPropertyScope()
        ));
      }
    }

    return issues;
  }

  private void validateRowsMatchHeaders(
      List<String> headers,
      List<List<String>> rows,
      List<CsvImportValidationMessage> messages
  ) {
    if (headers == null || rows == null) {
      return;
    }
    for (int i = 0; i < rows.size(); i++) {
      if (rows.get(i).size() != headers.size()) {
        messages.add(message("rows", "Row " + (i + 1) + " does not match the header size."));
        return;
      }
    }
  }

  private void validateCsvConfig(CsvImportConfiguration csvConfig, List<CsvImportValidationMessage> messages) {
    if (csvConfig == null) {
      messages.add(message("csvConfig", "CSV configuration must be provided."));
      return;
    }
    if (csvConfig.getDecimalSeparator() == null
        || (!",".equals(csvConfig.getDecimalSeparator()) && !".".equals(csvConfig.getDecimalSeparator()))) {
      messages.add(message("csvConfig.decimalSeparator", "Decimal separator must be '.' or ','."));
    }
  }

  private boolean hasUploadId(CsvImportRequest request) {
    return request != null && request.getUploadId() != null && !request.getUploadId().isBlank();
  }

  private CsvImportValidationMessage message(String field, String message) {
    return new CsvImportValidationMessage(field, message);
  }

  private CsvImportSchemaIssue issue(
      CsvImportSchemaIssueType type,
      String columnName,
      String expected,
      String actual
  ) {
    return new CsvImportSchemaIssue(type, columnName, expected, actual);
  }

  private String getRuntimeType(EventProperty property) {
    if (property instanceof EventPropertyPrimitive primitive) {
      return primitive.getRuntimeType();
    }
    return null;
  }
}
