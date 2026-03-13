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

import org.apache.streampipes.connect.management.util.EventSchemaUtils;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataSeriesBuilder;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.SpQueryResultBuilder;
import org.apache.streampipes.model.datalake.importer.CsvImportColumn;
import org.apache.streampipes.model.datalake.importer.CsvImportConfiguration;
import org.apache.streampipes.model.datalake.importer.CsvImportPreviewRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportPreviewResult;
import org.apache.streampipes.model.datalake.importer.CsvImportRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportResult;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaIssue;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaIssueType;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaValidationRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportSchemaValidationResult;
import org.apache.streampipes.model.datalake.importer.CsvImportTarget;
import org.apache.streampipes.model.datalake.importer.CsvImportTargetMode;
import org.apache.streampipes.model.datalake.importer.CsvImportValidationMessage;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CsvDataLakeImportService {

  private static final int MAX_PREVIEW_ROWS = 50;
  private static final String STREAM_PREFIX = "s0::";

  private final IDataExplorerSchemaManagement schemaManagement;
  private final DataLakeDataWriter dataWriter;

  public CsvDataLakeImportService(IDataExplorerSchemaManagement schemaManagement) {
    this(
        schemaManagement,
        new DataLakeDataWriter(false)
    );
  }

  CsvDataLakeImportService(
      IDataExplorerSchemaManagement schemaManagement,
      DataLakeDataWriter dataWriter
  ) {
    this.schemaManagement = schemaManagement;
    this.dataWriter = dataWriter;
  }

  public CsvImportPreviewResult preview(CsvImportPreviewRequest request) {
    var validationMessages = validatePreviewRequest(request);
    var headers = sanitizeHeaders(request.getHeaders());
    var rows = Optional.ofNullable(request.getRows()).orElseGet(Collections::emptyList);

    var columns = inferColumns(headers, rows, request.getCsvConfig());
    var eventSchema = buildEventSchema(columns, rows, request.getCsvConfig(), null);
    validationMessages.addAll(validatePreviewTarget(request.getTarget()));

    var result = new CsvImportPreviewResult();
    result.setHeaders(headers);
    result.setPreviewRows(rows.stream().limit(MAX_PREVIEW_ROWS).collect(Collectors.toList()));
    result.setColumns(columns);
    result.setGuessedEventSchema(eventSchema);
    result.setTimestampCandidates(columns.stream()
        .filter(CsvImportColumn::isTimestampCandidate)
        .map(CsvImportColumn::getRuntimeName)
        .collect(Collectors.toList()));
    result.setValidationMessages(validationMessages);
    result.setValid(validationMessages.isEmpty());
    return result;
  }

  public CsvImportSchemaValidationResult validateSchema(CsvImportSchemaValidationRequest request) {
    var validationMessages = validateSchemaRequest(request);
    var issues = new ArrayList<CsvImportSchemaIssue>();
    if (validationMessages.isEmpty()) {
      var eventSchema = buildConfiguredEventSchema(
          sanitizeImportColumns(request.getColumns()),
          request.getTimestampColumn()
      );
      issues.addAll(validateSchemaTarget(
          request.getTarget(),
          eventSchema,
          request.getTimestampColumn()
      ));
    }

    var result = new CsvImportSchemaValidationResult();
    result.setValidationMessages(validationMessages);
    result.setIssues(issues);
    result.setValid(validationMessages.isEmpty() && issues.isEmpty());
    return result;
  }

  public CsvImportResult importData(CsvImportRequest request, String principalSid) {
    var validationMessages = validateImportRequest(request);
    if (!validationMessages.isEmpty()) {
      throw new CsvImportValidationException(validationMessages);
    }

    var eventSchema = buildEventSchema(
        sanitizeImportColumns(request.getColumns()),
        request.getRows(),
        request.getCsvConfig(),
        request.getTimestampColumn()
    );

    validationMessages.addAll(validateImportTarget(request.getTarget(), eventSchema, request.getTimestampColumn()));
    if (!validationMessages.isEmpty()) {
      throw new CsvImportValidationException(validationMessages);
    }

    var createdNewMeasurement = false;
    DataLakeMeasure measure;

    if (request.getTarget().getMode() == CsvImportTargetMode.NEW) {
      measure = new DataLakeMeasure();
      measure.setMeasureName(request.getTarget().getMeasurementName().trim());
      measure.setTimestampField(STREAM_PREFIX + request.getTimestampColumn());
      measure.setEventSchema(eventSchema);
      measure = schemaManagement.createOrUpdateMeasurement(measure, principalSid);
      createdNewMeasurement = true;
    } else {
      measure = requireExistingMeasurement(request.getTarget().getMeasurementName());
    }

    var queryResult = toQueryResult(request);
    dataWriter.writeData(measure, queryResult);

    var result = new CsvImportResult();
    result.setMeasurementId(measure.getElementId());
    result.setMeasurementName(measure.getMeasureName());
    result.setCreatedNewMeasurement(createdNewMeasurement);
    result.setImportedRowCount(request.getRows().size());
    result.setValidationMessages(List.of());
    return result;
  }

  private List<CsvImportValidationMessage> validatePreviewRequest(CsvImportPreviewRequest request) {
    var messages = new ArrayList<CsvImportValidationMessage>();
    if (request == null) {
      messages.add(message("request", "Import request must be provided."));
      return messages;
    }
    if (request.getHeaders() == null || request.getHeaders().isEmpty()) {
      messages.add(message("headers", "At least one header must be provided."));
    }
    if (request.getRows() == null || request.getRows().isEmpty()) {
      messages.add(message("rows", "At least one row must be provided."));
    }
    validateRowsMatchHeaders(request.getHeaders(), request.getRows(), messages);
    validateCsvConfig(request.getCsvConfig(), messages);
    return messages;
  }

  private List<CsvImportValidationMessage> validateImportRequest(CsvImportRequest request) {
    var messages = new ArrayList<CsvImportValidationMessage>();
    if (request == null) {
      messages.add(message("request", "Import request must be provided."));
      return messages;
    }
    if (request.getHeaders() == null || request.getHeaders().isEmpty()) {
      messages.add(message("headers", "At least one header must be provided."));
    }
    if (request.getRows() == null || request.getRows().isEmpty()) {
      messages.add(message("rows", "At least one row must be provided."));
    }
    validateRowsMatchHeaders(request.getHeaders(), request.getRows(), messages);
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
    return messages;
  }

  private List<CsvImportValidationMessage> validateSchemaRequest(CsvImportSchemaValidationRequest request) {
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

  private List<CsvImportValidationMessage> validatePreviewTarget(CsvImportTarget target) {
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

  private List<CsvImportSchemaIssue> validateSchemaTarget(
      CsvImportTarget target,
      EventSchema importSchema,
      String timestampColumn
  ) {
    var issues = new ArrayList<CsvImportSchemaIssue>();
    if (target == null || target.getMode() == null) {
      return issues;
    }

    var measurementName = target.getMeasurementName();
    if (measurementName == null || measurementName.isBlank()) {
      return issues;
    }

    if (target.getMode() == CsvImportTargetMode.NEW) {
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

  private List<CsvImportValidationMessage> validateImportTarget(
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
    } else if (importSchema != null) {
      var issues = compareSchemas(existingOpt.get(), importSchema, timestampColumn);
      if (!issues.isEmpty()) {
        messages.add(message("columns", "Imported columns must exactly match the existing measurement schema."));
      }
    }
    return messages;
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

    var unexpectedProperties = importedProperties.keySet().stream()
        .filter(property -> !existingProperties.containsKey(property))
        .sorted()
        .toList();

    if (!unexpectedProperties.isEmpty()) {
      unexpectedProperties.forEach(property -> issues.add(issue(
          CsvImportSchemaIssueType.COLUMN_NAME_MISMATCH,
          property,
          null,
          property
      )));
    }

    for (var entry : existingProperties.entrySet()) {
      var imported = importedProperties.get(entry.getKey());
      if (imported == null) {
        continue;
      }

      if (!Objects.equals(getRuntimeType(entry.getValue()), getRuntimeType(imported))
      ) {
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

  private DataLakeMeasure requireExistingMeasurement(String measurementName) {
    return schemaManagement.getExistingMeasureByName(measurementName)
        .orElseThrow(() -> new CsvImportValidationException(List.of(
            message("target.measurementName", "The selected measurement does not exist.")
        )));
  }

  private SpQueryResult toQueryResult(CsvImportRequest request) {
    var headers = request.getColumns().stream()
        .map(CsvImportColumn::getRuntimeName)
        .collect(Collectors.toList());
    var rows = new ArrayList<List<Object>>();
    for (var row : request.getRows()) {
      var converted = new ArrayList<Object>();
      for (int i = 0; i < row.size(); i++) {
        converted.add(convertValue(row.get(i), request.getColumns().get(i), request.getCsvConfig(), request.getTimestampColumn()));
      }
      rows.add(converted);
    }

    var dataSeries = DataSeriesBuilder.create()
        .withHeaders(headers)
        .withRows(rows)
        .build();
    return SpQueryResultBuilder.create(headers)
        .withDataSeries(dataSeries)
        .build();
  }

  private EventSchema buildEventSchema(
      List<CsvImportColumn> columns,
      List<List<String>> rows,
      CsvImportConfiguration config,
      String selectedTimestampColumn
  ) {
    var sampleEvent = new LinkedHashMap<String, Object>();
    for (int i = 0; i < columns.size(); i++) {
      sampleEvent.put(columns.get(i).getRuntimeName(), sampleValue(rows, i, columns.get(i), config, selectedTimestampColumn));
    }

    var eventSchema = EventSchemaUtils.guessEventSchema(sampleEvent);
    var properties = eventSchema.getEventProperties()
        .stream()
        .collect(Collectors.toMap(EventProperty::getRuntimeName, property -> property, (a, b) -> a, LinkedHashMap::new));

    for (var column : columns) {
      var property = (EventPropertyPrimitive) properties.get(column.getRuntimeName());
      if (property == null) {
        continue;
      }
      property.setRuntimeType(toRuntimeTypeUri(finalRuntimeType(column, selectedTimestampColumn)));
      property.setPropertyScope(finalPropertyScope(column, selectedTimestampColumn));
      property.setSemanticType(finalSemanticType(column, selectedTimestampColumn));
      property.setLabel(Optional.ofNullable(column.getLabel()).orElse(""));
      property.setDescription(Optional.ofNullable(column.getDescription()).orElse(""));
    }

    eventSchema.setEventProperties(new ArrayList<>(properties.values()));
    return eventSchema;
  }

  private EventSchema buildConfiguredEventSchema(
      List<CsvImportColumn> columns,
      String selectedTimestampColumn
  ) {
    var properties = new ArrayList<EventProperty>();
    for (var column : columns) {
      var property = new EventPropertyPrimitive();
      property.setRuntimeName(column.getRuntimeName());
      property.setRuntimeType(toRuntimeTypeUri(finalRuntimeType(column, selectedTimestampColumn)));
      property.setPropertyScope(finalPropertyScope(column, selectedTimestampColumn));
      property.setSemanticType(finalSemanticType(column, selectedTimestampColumn));
      property.setLabel(Optional.ofNullable(column.getLabel()).orElse(""));
      property.setDescription(Optional.ofNullable(column.getDescription()).orElse(""));
      properties.add(property);
    }
    return new EventSchema(properties);
  }

  private List<CsvImportColumn> inferColumns(
      List<String> headers,
      List<List<String>> rows,
      CsvImportConfiguration config
  ) {
    var columns = new ArrayList<CsvImportColumn>();
    for (int i = 0; i < headers.size(); i++) {
      var runtimeName = headers.get(i);
      var inferredType = inferType(rows, i, config);
      var timestampCandidate = isTimestampCandidate(rows, i, config);
      var column = new CsvImportColumn();
      column.setCsvColumn(headers.get(i));
      column.setRuntimeName(runtimeName);
      column.setRuntimeType(inferredType);
      column.setInferredType(inferredType);
      column.setPropertyScope(timestampCandidate ? PropertyScope.HEADER_PROPERTY.name() : suggestedScope(inferredType));
      column.setSemanticType(timestampCandidate ? SO.DATE_TIME : null);
      column.setTimestampCandidate(timestampCandidate);
      columns.add(column);
    }
    return columns;
  }

  private List<CsvImportColumn> sanitizeImportColumns(List<CsvImportColumn> columns) {
    return columns.stream().map(column -> {
      column.setRuntimeName(column.getRuntimeName().trim());
      return column;
    }).collect(Collectors.toList());
  }

  private List<String> sanitizeHeaders(List<String> headers) {
    return headers.stream()
        .map(header -> header == null ? "" : header.trim())
        .collect(Collectors.toList());
  }

  private Object sampleValue(
      List<List<String>> rows,
      int columnIndex,
      CsvImportColumn column,
      CsvImportConfiguration config,
      String selectedTimestampColumn
  ) {
    for (var row : rows) {
      var converted = convertValue(row.get(columnIndex), column, config, selectedTimestampColumn);
      if (converted != null) {
        return converted;
      }
    }
    return null;
  }

  private String inferType(List<List<String>> rows, int columnIndex, CsvImportConfiguration config) {
    var allBoolean = true;
    var allLong = true;
    var allNumber = true;
    var timestampCandidate = false;

    for (var row : rows) {
      var value = row.get(columnIndex);
      if (value == null || value.isBlank()) {
        continue;
      }
      if (!isBoolean(value)) {
        allBoolean = false;
      }
      if (!isLong(value, config)) {
        allLong = false;
      }
      if (!isNumber(value, config)) {
        allNumber = false;
      }
    }

    timestampCandidate = isTimestampCandidate(rows, columnIndex, config);

    if (timestampCandidate || allLong) {
      return "LONG";
    } else if (allBoolean) {
      return "BOOLEAN";
    } else if (allNumber) {
      return "FLOAT";
    }
    return "STRING";
  }

  private boolean isTimestampCandidate(List<List<String>> rows, int columnIndex, CsvImportConfiguration config) {
    var hasValue = false;
    for (var row : rows) {
      var value = row.get(columnIndex);
      if (value == null || value.isBlank()) {
        continue;
      }
      hasValue = true;
      if (!canParseTimestamp(value, config)) {
        return false;
      }
    }
    return hasValue;
  }

  private boolean canParseTimestamp(String value, CsvImportConfiguration config) {
    if (isLong(value, config)) {
      return true;
    }
    var timestampFormat = config == null ? null : config.getTimestampFormat();
    if (timestampFormat == null || timestampFormat.isBlank()) {
      return false;
    }
    try {
      DateTimeFormatter.ofPattern(timestampFormat, Locale.ENGLISH).parse(value.trim());
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  private Object convertValue(
      String rawValue,
      CsvImportColumn column,
      CsvImportConfiguration config,
      String timestampColumn
  ) {
    if (rawValue == null || rawValue.isBlank()) {
      return null;
    }
    var trimmed = rawValue.trim();
    if (Objects.equals(column.getRuntimeName(), timestampColumn)) {
      return parseTimestamp(trimmed, config);
    }

    return switch (finalRuntimeType(column, timestampColumn)) {
      case "BOOLEAN" -> Boolean.parseBoolean(trimmed.toLowerCase(Locale.ENGLISH));
      case "LONG" -> Long.parseLong(normalizeNumber(trimmed, config));
      case "FLOAT" -> Double.parseDouble(normalizeNumber(trimmed, config));
      default -> trimmed;
    };
  }

  private long parseTimestamp(String value, CsvImportConfiguration config) {
    if (isLong(value, config)) {
      return Long.parseLong(normalizeNumber(value, config));
    }

    var formatter = DateTimeFormatter.ofPattern(config.getTimestampFormat(), Locale.ENGLISH);
    try {
      return LocalDateTime.parse(value, formatter).toInstant(ZoneOffset.UTC).toEpochMilli();
    } catch (DateTimeParseException e) {
      return Instant.from(formatter.parse(value)).toEpochMilli();
    }
  }

  private boolean isBoolean(String value) {
    return "true".equalsIgnoreCase(value.trim()) || "false".equalsIgnoreCase(value.trim());
  }

  private boolean isNumber(String value, CsvImportConfiguration config) {
    try {
      Double.parseDouble(normalizeNumber(value, config));
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private boolean isLong(String value, CsvImportConfiguration config) {
    try {
      var normalized = normalizeNumber(value, config);
      if (normalized.contains(".")) {
        return false;
      }
      Long.parseLong(normalized);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private String normalizeNumber(String value, CsvImportConfiguration config) {
    if (config != null && ",".equals(config.getDecimalSeparator())) {
      return value.replace(".", "").replace(',', '.');
    }
    return value.replace(",", "");
  }

  private String suggestedScope(String inferredType) {
    return switch (inferredType) {
      case "LONG", "FLOAT" -> PropertyScope.MEASUREMENT_PROPERTY.name();
      case "BOOLEAN", "STRING" -> PropertyScope.DIMENSION_PROPERTY.name();
      default -> PropertyScope.MEASUREMENT_PROPERTY.name();
    };
  }

  private String finalRuntimeType(CsvImportColumn column, String timestampColumn) {
    if (Objects.equals(column.getRuntimeName(), timestampColumn)) {
      return "LONG";
    }
    return Optional.ofNullable(column.getRuntimeType())
        .orElseGet(column::getInferredType);
  }

  private String finalPropertyScope(CsvImportColumn column, String timestampColumn) {
    if (Objects.equals(column.getRuntimeName(), timestampColumn)) {
      return PropertyScope.HEADER_PROPERTY.name();
    }
    return Optional.ofNullable(column.getPropertyScope()).orElse(PropertyScope.MEASUREMENT_PROPERTY.name());
  }

  private String finalSemanticType(CsvImportColumn column, String timestampColumn) {
    if (Objects.equals(column.getRuntimeName(), timestampColumn)) {
      return SO.DATE_TIME;
    }
    return column.getSemanticType();
  }

  private String toRuntimeTypeUri(String runtimeType) {
    return switch (runtimeType) {
      case "BOOLEAN" -> XSD.BOOLEAN.toString();
      case "LONG" -> XSD.LONG.toString();
      case "FLOAT" -> XSD.FLOAT.toString();
      default -> XSD.STRING.toString();
    };
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

  private String normalize(String value) {
    return value == null ? null : value.trim().toLowerCase(Locale.ENGLISH);
  }

  private String getRuntimeType(EventProperty property) {
    if (property instanceof EventPropertyPrimitive primitive) {
      return primitive.getRuntimeType();
    }
    return null;
  }
}
