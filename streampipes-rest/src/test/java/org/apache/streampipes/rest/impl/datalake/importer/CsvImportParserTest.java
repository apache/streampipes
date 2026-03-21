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

import org.apache.streampipes.model.datalake.importer.CsvImportColumn;
import org.apache.streampipes.model.datalake.importer.CsvImportConfiguration;
import org.apache.streampipes.model.datalake.importer.CsvImportRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvImportParserTest {

  @TempDir
  Path tempDir;

  private final CsvImportParser parser = new CsvImportParser();

  @Test
  void shouldInferTimestampAndFloatColumns() {
    var config = makeConfig(",", ".", true);
    var headers = List.of("timestamp", "temperature", "active");
    var rows = List.of(
        List.of("1710000000000", "21.3", "true"),
        List.of("1710000060000", "22.1", "false")
    );

    var columns = parser.inferColumns(headers, rows, config);

    assertEquals("LONG", columns.get(0).getInferredType());
    assertTrue(columns.get(0).isTimestampCandidate());
    assertEquals("FLOAT", columns.get(1).getInferredType());
    assertEquals("BOOLEAN", columns.get(2).getInferredType());
    assertFalse(columns.get(1).isTimestampCandidate());
  }

  @Test
  void shouldReadQuotedCsvRows() throws Exception {
    var path = tempDir.resolve("quoted.csv");
    Files.writeString(path, "timestamp,text\n1710000000000,\"a, b\"\n1710000060000,\"escaped \"\"quote\"\"\"\n");

    var sample = parser.readCsvSample(path, makeConfig(",", ".", true), 10);

    assertEquals(List.of("timestamp", "text"), sample.headers());
    assertEquals("a, b", sample.rows().get(0).get(1));
    assertEquals("escaped \"quote\"", sample.rows().get(1).get(1));
  }

  @Test
  void shouldGenerateHeadersWhenCsvHasNoHeader() throws Exception {
    var path = tempDir.resolve("no-header.csv");
    Files.writeString(path, "1710000000000\t21.3\n1710000060000\t22.1\n");

    var sample = parser.readCsvSample(path, makeConfig("\\t", ".", false), 10);

    assertEquals(List.of("column_1", "column_2"), sample.headers());
    assertEquals("1710000000000", sample.rows().get(0).get(0));
    assertEquals("21.3", sample.rows().get(0).get(1));
  }

  @Test
  void shouldRejectMissingTimestampValuesWhenConvertingRows() {
    var request = new CsvImportRequest();
    request.setCsvConfig(makeConfig(",", ".", true));
    request.setTimestampColumn("timestamp");
    request.setColumns(List.of(
        makeColumn("timestamp", "timestamp", "LONG"),
        makeColumn("temperature", "temperature", "FLOAT")
    ));
    request.setRows(List.of(
        List.of("1710000000000", "21.3"),
        List.of("", "22.1")
    ));

    var exception = assertThrows(CsvImportValidationException.class, () -> parser.toImportRows(request));

    assertTrue(exception.getValidationMessages().get(0).getMessage().contains("missing a value for timestamp"));
  }

  private CsvImportConfiguration makeConfig(String delimiter, String decimalSeparator, boolean hasHeader) {
    var config = new CsvImportConfiguration();
    config.setDelimiter(delimiter);
    config.setDecimalSeparator(decimalSeparator);
    config.setHasHeader(hasHeader);
    return config;
  }

  private CsvImportColumn makeColumn(String csvColumn, String runtimeName, String runtimeType) {
    var column = new CsvImportColumn();
    column.setCsvColumn(csvColumn);
    column.setRuntimeName(runtimeName);
    column.setRuntimeType(runtimeType);
    return column;
  }
}
