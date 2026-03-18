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

import org.apache.streampipes.connect.management.util.EventSchemaUtils;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.importer.CsvImportColumn;
import org.apache.streampipes.model.datalake.importer.CsvImportConfiguration;
import org.apache.streampipes.model.datalake.importer.CsvImportRequest;
import org.apache.streampipes.model.datalake.importer.CsvImportValidationMessage;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.rest.impl.datalake.DataLakeDataWriter;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

class CsvImportParser {

  private static final int IMPORT_BATCH_SIZE = 5000;

  List<String> sanitizeHeaders(List<String> headers) {
    return headers.stream()
        .map(header -> header == null ? "" : header.trim())
        .collect(Collectors.toList());
  }

  List<CsvImportColumn> sanitizeImportColumns(List<CsvImportColumn> columns) {
    return columns.stream().map(column -> {
      column.setRuntimeName(column.getRuntimeName().trim());
      return column;
    }).collect(Collectors.toList());
  }

  EventSchema buildEventSchema(
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

  EventSchema buildConfiguredEventSchema(
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

  List<CsvImportColumn> inferColumns(
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

  List<List<Object>> toImportRows(CsvImportRequest request) {
    var rows = new ArrayList<List<Object>>();
    for (int rowIndex = 0; rowIndex < request.getRows().size(); rowIndex++) {
      rows.add(convertRow(request.getRows().get(rowIndex), request, rowIndex + 1));
    }
    return rows;
  }

  int importCsvFile(
      Path path,
      CsvImportRequest request,
      DataLakeMeasure measure,
      DataLakeDataWriter dataWriter
  ) throws IOException {
    var runtimeHeaders = request.getColumns().stream()
        .map(CsvImportColumn::getRuntimeName)
        .collect(Collectors.toList());
    var batch = new ArrayList<List<Object>>();
    var importedRows = new int[]{0};

    parseCsvFile(path, request.getCsvConfig(), new CsvRowConsumer() {
      private List<String> parsedHeaders;

      @Override
      public void onHeaders(List<String> headers) {
        parsedHeaders = headers;
        validateUploadedHeaders(headers, request.getColumns());
      }

      @Override
      public void onRow(int rowNumber, List<String> row) {
        if (row.size() != parsedHeaders.size()) {
          throw new CsvImportValidationException(List.of(
              message("rows", "Row " + rowNumber + " does not match the header size.")
          ));
        }
        batch.add(convertRow(row, request, rowNumber));
        if (batch.size() >= IMPORT_BATCH_SIZE) {
          dataWriter.writeData(measure, runtimeHeaders, new ArrayList<>(batch));
          importedRows[0] += IMPORT_BATCH_SIZE;
          batch.clear();
        }
      }
    });

    if (!batch.isEmpty()) {
      var batchSize = batch.size();
      dataWriter.writeData(measure, runtimeHeaders, new ArrayList<>(batch));
      importedRows[0] += batchSize;
    }

    return importedRows[0];
  }

  CsvFileSample readCsvSample(Path path, CsvImportConfiguration config, int maxRows) throws IOException {
    var headers = new ArrayList<String>();
    var rows = new ArrayList<List<String>>();

    parseCsvFile(path, config, new CsvRowConsumer() {
      @Override
      public void onHeaders(List<String> parsedHeaders) {
        headers.addAll(parsedHeaders);
      }

      @Override
      public void onRow(int rowNumber, List<String> row) {
        if (row.size() != headers.size()) {
          throw new CsvImportValidationException(List.of(
              message("rows", "Row " + rowNumber + " does not match the header size.")
          ));
        }
        if (rows.size() < maxRows) {
          rows.add(row);
        }
      }
    });

    if (headers.isEmpty()) {
      throw new CsvImportValidationException(List.of(message("headers", "At least one header must be provided.")));
    }
    if (rows.isEmpty()) {
      throw new CsvImportValidationException(List.of(message("rows", "At least one row must be provided.")));
    }

    return new CsvFileSample(headers, rows);
  }

  private List<Object> convertRow(List<String> row, CsvImportRequest request, int rowNumber) {
    var converted = new ArrayList<Object>();
    for (int i = 0; i < row.size(); i++) {
      converted.add(convertValue(
          row.get(i),
          request.getColumns().get(i),
          request.getCsvConfig(),
          request.getTimestampColumn(),
          rowNumber
      ));
    }
    return converted;
  }

  private void validateUploadedHeaders(List<String> headers, List<CsvImportColumn> columns) {
    if (headers.size() != columns.size()) {
      throw new CsvImportValidationException(List.of(
          message("headers", "The uploaded CSV file no longer matches the previewed column count.")
      ));
    }

    for (int i = 0; i < headers.size(); i++) {
      if (!Objects.equals(headers.get(i), columns.get(i).getCsvColumn())) {
        throw new CsvImportValidationException(List.of(
            message("headers", "The uploaded CSV file no longer matches the previewed headers.")
        ));
      }
    }
  }

  private Object sampleValue(
      List<List<String>> rows,
      int columnIndex,
      CsvImportColumn column,
      CsvImportConfiguration config,
      String selectedTimestampColumn
  ) {
    for (var row : rows) {
      var converted = convertValue(row.get(columnIndex), column, config, selectedTimestampColumn, null);
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

    var timestampCandidate = isTimestampCandidate(rows, columnIndex, config);
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
      String timestampColumn,
      Integer rowNumber
  ) {
    if (rawValue == null || rawValue.isBlank()) {
      if (rowNumber != null && Objects.equals(column.getRuntimeName(), timestampColumn)) {
        throw new CsvImportValidationException(List.of(
            message(
                "rows",
                "Row " + rowNumber + " is missing a value for timestamp column \"" + column.getCsvColumn() + "\"."
            )
        ));
      }
      return null;
    }

    var trimmed = rawValue.trim();
    try {
      if (Objects.equals(column.getRuntimeName(), timestampColumn)) {
        return parseTimestamp(trimmed, config);
      }

      return switch (finalRuntimeType(column, timestampColumn)) {
        case "BOOLEAN" -> Boolean.parseBoolean(trimmed.toLowerCase(Locale.ENGLISH));
        case "LONG" -> Long.parseLong(normalizeNumber(trimmed, config));
        case "FLOAT" -> Double.parseDouble(normalizeNumber(trimmed, config));
        default -> trimmed;
      };
    } catch (RuntimeException e) {
      if (rowNumber == null) {
        throw e;
      }

      throw new CsvImportValidationException(List.of(
          message(
              "rows",
              "Row " + rowNumber + " contains an invalid value for column \"" + column.getCsvColumn() + "\"."
          )
      ));
    }
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

  private void parseCsvFile(Path path, CsvImportConfiguration config, CsvRowConsumer consumer) throws IOException {
    try (var reader = new PushbackReader(Files.newBufferedReader(path, StandardCharsets.UTF_8), 1)) {
      var delimiter = normalizeDelimiter(config == null ? null : config.getDelimiter());
      var hasHeader = config == null || config.isHasHeader();
      List<String> headers = null;
      int rowNumber = 0;
      List<String> row;

      while ((row = readNextRow(reader, delimiter)) != null) {
        if (isBlankRow(row)) {
          continue;
        }

        if (headers == null) {
          if (hasHeader) {
            headers = normalizeHeaders(row);
            consumer.onHeaders(headers);
          } else {
            headers = generateHeaders(row.size());
            consumer.onHeaders(headers);
            rowNumber += 1;
            consumer.onRow(rowNumber, row);
          }
        } else {
          rowNumber += 1;
          consumer.onRow(rowNumber, row);
        }
      }
    }
  }

  private List<String> readNextRow(PushbackReader reader, char delimiter) throws IOException {
    var currentRow = new ArrayList<String>();
    var currentValue = new StringBuilder();
    var inQuotes = false;
    var readAny = false;

    while (true) {
      var nextInt = reader.read();
      if (nextInt == -1) {
        if (!readAny && currentValue.length() == 0 && currentRow.isEmpty()) {
          return null;
        }
        currentRow.add(currentValue.toString());
        return currentRow;
      }

      readAny = true;
      var currentChar = (char) nextInt;
      if (currentChar == '"') {
        if (inQuotes) {
          var escapedCandidate = reader.read();
          if (escapedCandidate == '"') {
            currentValue.append('"');
          } else {
            inQuotes = false;
            if (escapedCandidate != -1) {
              reader.unread(escapedCandidate);
            }
          }
        } else {
          inQuotes = true;
        }
      } else if (!inQuotes && currentChar == delimiter) {
        currentRow.add(currentValue.toString());
        currentValue = new StringBuilder();
      } else if (!inQuotes && (currentChar == '\n' || currentChar == '\r')) {
        if (currentChar == '\r') {
          var maybeLineFeed = reader.read();
          if (maybeLineFeed != '\n' && maybeLineFeed != -1) {
            reader.unread(maybeLineFeed);
          }
        }
        currentRow.add(currentValue.toString());
        return currentRow;
      } else {
        currentValue.append(currentChar);
      }
    }
  }

  private char normalizeDelimiter(String delimiter) {
    if (delimiter == null || delimiter.isEmpty()) {
      return ',';
    }
    if ("\\t".equals(delimiter)) {
      return '\t';
    }
    return delimiter.charAt(0);
  }

  private List<String> normalizeHeaders(List<String> headers) {
    var normalized = new ArrayList<String>();
    for (int i = 0; i < headers.size(); i++) {
      var value = headers.get(i);
      if (i == 0) {
        value = stripBom(value);
      }
      var trimmed = value == null ? "" : value.trim();
      normalized.add(trimmed.isEmpty() ? "column_" + (i + 1) : trimmed);
    }
    return normalized;
  }

  private String stripBom(String value) {
    return value == null ? null : value.replace("\uFEFF", "");
  }

  private List<String> generateHeaders(int size) {
    var headers = new ArrayList<String>();
    for (int i = 0; i < size; i++) {
      headers.add("column_" + (i + 1));
    }
    return headers;
  }

  private boolean isBlankRow(List<String> row) {
    return row.stream().allMatch(cell -> cell == null || cell.trim().isEmpty());
  }

  @FunctionalInterface
  private interface CsvRowConsumer {
    default void onHeaders(List<String> headers) {
    }

    void onRow(int rowNumber, List<String> row);
  }

  static final class CsvFileSample {

    private final List<String> headers;
    private final List<List<String>> rows;

    CsvFileSample(List<String> headers, List<List<String>> rows) {
      this.headers = headers;
      this.rows = rows;
    }

    List<String> headers() {
      return headers;
    }

    List<List<String>> rows() {
      return rows;
    }
  }
}
