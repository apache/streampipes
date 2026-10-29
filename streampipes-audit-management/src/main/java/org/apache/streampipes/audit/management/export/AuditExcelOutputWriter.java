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
import org.apache.streampipes.audit.api.AuditExportConfiguration;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;

final class AuditExcelOutputWriter implements AuditOutputWriter {
  private final AuditExportConfiguration configuration;
  private SXSSFWorkbook workbook;
  private Sheet sheet;
  private int rowIndex;

  AuditExcelOutputWriter(AuditExportConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void beforeFirstItem(OutputStream outputStream) {
    workbook = new SXSSFWorkbook();
    workbook.setCompressTempFiles(true);
    sheet = workbook.createSheet("Audit log");
    var header = sheet.createRow(rowIndex++);
    for (var index = 0; index < configuration.headers().size(); index++) {
      header.createCell(index).setCellValue(configuration.headers().get(index));
    }
  }

  @Override
  public void writeItem(AuditEntry entry, AuditEntryDetails details) throws IOException {
    var row = sheet.createRow(rowIndex++);
    for (var index = 0; index < configuration.columns().size(); index++) {
      String value = AuditExportValueFormatter.format(configuration.columns().get(index), entry, details);
      row.createCell(index).setCellValue(value == null ? "" : value);
    }
  }

  @Override
  public void afterLastItem(OutputStream outputStream) throws IOException {
    workbook.write(outputStream);
  }

  @Override
  public void close() throws IOException {
    if (workbook != null) {
      try {
        workbook.close();
      } finally {
        workbook.dispose();
      }
    }
  }
}
