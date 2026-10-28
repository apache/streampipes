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

package org.apache.streampipes.audit.management.export;

import org.apache.streampipes.audit.api.AuditEntry;
import org.apache.streampipes.audit.api.AuditEntryDetails;
import org.apache.streampipes.audit.api.AuditEventReader;
import org.apache.streampipes.audit.api.AuditExportColumn;
import org.apache.streampipes.audit.api.AuditExportConfiguration;
import org.apache.streampipes.audit.api.AuditExportFormat;
import org.apache.streampipes.audit.api.AuditPage;
import org.apache.streampipes.audit.api.AuditQuery;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditExportServiceTest {

  @Test
  void exportsSelectedColumnsWithProvidedHeaders() throws Exception {
    var entry = new AuditEntry("event-1", "2026-10-16T10:00:00Z", "user.login", "succeeded", "admin",
        null, null, "locator-1");
    var configuration = new AuditExportConfiguration(
        AuditExportFormat.XLSX, List.of(AuditExportColumn.EVENT_ID, AuditExportColumn.ACTOR),
        List.of("Event ID", "Actor"), ',');
    var output = new ByteArrayOutputStream();

    new AuditExportService(reader(entry)).export(query(), configuration, output);

    try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(output.toByteArray()))) {
      var sheet = workbook.getSheetAt(0);
      assertEquals("Event ID", sheet.getRow(0).getCell(0).getStringCellValue());
      assertEquals("Actor", sheet.getRow(0).getCell(1).getStringCellValue());
      assertEquals("event-1", sheet.getRow(1).getCell(0).getStringCellValue());
      assertEquals("admin", sheet.getRow(1).getCell(1).getStringCellValue());
    }
  }

  @Test
  void exportsCsvWithConfiguredDelimiterAndSafeFormulaValues() throws Exception {
    var entry = new AuditEntry("=event-1", "2026-10-16T10:00:00Z", "user.login", "succeeded", "ad\"min",
        null, null, "locator-1");
    var configuration = new AuditExportConfiguration(
        AuditExportFormat.CSV, List.of(AuditExportColumn.EVENT_ID, AuditExportColumn.ACTOR),
        List.of("Event ID", "Actor"), ';');
    var output = new ByteArrayOutputStream();

    new AuditExportService(reader(entry)).export(query(), configuration, output);

    assertEquals("\"Event ID\";\"Actor\"\r\n\"'=event-1\";\"ad\"\"min\"\r\n",
        output.toString(StandardCharsets.UTF_8));
  }

  @Test
  void exportsJsonWithTheSelectedColumnIds() throws Exception {
    var entry = new AuditEntry("event-1", "2026-10-16T10:00:00Z", "user.login", "succeeded", "admin",
        null, null, "locator-1");
    var configuration = new AuditExportConfiguration(
        AuditExportFormat.JSON, List.of(AuditExportColumn.EVENT_ID, AuditExportColumn.ACTOR),
        List.of("Ignored", "Ignored"), ',');
    var output = new ByteArrayOutputStream();

    new AuditExportService(reader(entry)).export(query(), configuration, output);

    assertEquals("[{\"eventId\":\"event-1\",\"actor\":\"admin\"}]",
        output.toString(StandardCharsets.UTF_8));
  }

  private AuditEventReader reader(AuditEntry entry) {
    return new AuditEventReader() {
      @Override
      public AuditPage query(AuditQuery query) {
        return new AuditPage(List.of(entry), null);
      }

      @Override
      public Optional<AuditEntryDetails> find(UUID eventId, String locator) {
        return Optional.empty();
      }
    };
  }

  private AuditQuery query() {
    return new AuditQuery(Instant.parse("2026-10-16T00:00:00Z"), Instant.parse("2026-10-17T00:00:00Z"),
        null, null, null, 200, null);
  }
}
