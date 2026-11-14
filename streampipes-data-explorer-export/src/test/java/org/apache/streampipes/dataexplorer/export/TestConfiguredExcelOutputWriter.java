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

package org.apache.streampipes.dataexplorer.export;

import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.dataset.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.storage.api.system.IFileMetadataStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_XLSX_START_ROW;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_XLSX_TEMPLATE_ID;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_XLSX_USE_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestConfiguredExcelOutputWriter extends TestConfiguredOutputWriter {

  private static final String TEMPLATE_ID = "template-1";
  private static final String TEMPLATE_FILENAME = "template.xlsx";

  @TempDir
  Path filesDir;

  @Test
  public void testExcelOutputWriterWritesHeaderAndRows() throws IOException {
    var writer = new ConfiguredExcelOutputWriter(mock(IFileMetadataStorage.class),
        mock(ISpCoreConfigurationStorage.class));
    writer.configure(null, new ProvidedRestQueryParams(null, new HashMap<>()), true);

    try (var workbook = readWorkbook(export(writer))) {
      var sheet = workbook.getSheetAt(0);

      assertEquals(3, sheet.getPhysicalNumberOfRows());
      assertRow(sheet, 0, "time", "string", "number");
      assertRow(sheet, 1, "1668578077051", "test", "1");
      assertRow(sheet, 2, "1668578127050", "test2", "2");
    }
  }

  @Test
  public void testExcelOutputWriterFillsTemplateFromStartRow() throws IOException {
    writeTemplate();
    var writer = new ConfiguredExcelOutputWriter(fileMetadataStorage(), coreConfigurationStorage());
    writer.configure(null, new ProvidedRestQueryParams(null, Map.of(
        QP_XLSX_USE_TEMPLATE, "true",
        QP_XLSX_TEMPLATE_ID, TEMPLATE_ID,
        QP_XLSX_START_ROW, "2"
    )), true);

    try (var workbook = readWorkbook(export(writer))) {
      var sheet = workbook.getSheetAt(0);

      assertEquals("Template sheet", sheet.getSheetName());
      assertRow(sheet, 0, "Report title");
      assertRow(sheet, 2, "time", "string", "number");
      assertRow(sheet, 3, "1668578077051", "test", "1");
      assertRow(sheet, 4, "1668578127050", "test2", "2");
      assertEquals(4, sheet.getLastRowNum());
    }
  }

  private byte[] export(ConfiguredExcelOutputWriter writer) throws IOException {
    try (var outputStream = new ByteArrayOutputStream()) {
      writeRows(writer, outputStream);
      return outputStream.toByteArray();
    }
  }

  private void writeRows(ConfiguredExcelOutputWriter writer, OutputStream outputStream) throws IOException {
    writer.beforeFirstItem(outputStream);
    for (int i = 0; i < rows.size(); i++) {
      writer.writeItem(outputStream, rows.get(i), columns, i == 0);
    }
    writer.afterLastItem(outputStream);
  }

  private Workbook readWorkbook(byte[] content) throws IOException {
    return WorkbookFactory.create(new ByteArrayInputStream(content));
  }

  private void assertRow(Sheet sheet, int rowIndex, String... expectedValues) {
    var row = sheet.getRow(rowIndex);
    for (int i = 0; i < expectedValues.length; i++) {
      assertEquals(expectedValues[i], row.getCell(i).getStringCellValue(),
          "Unexpected value in row " + rowIndex + ", column " + i);
    }
  }

  private void writeTemplate() throws IOException {
    try (var template = new XSSFWorkbook();
         var outputStream = Files.newOutputStream(filesDir.resolve(TEMPLATE_FILENAME))) {
      template.createSheet("Template sheet").createRow(0).createCell(0).setCellValue("Report title");
      template.write(outputStream);
    }
  }

  private IFileMetadataStorage fileMetadataStorage() {
    var fileMetadata = new FileMetadata();
    fileMetadata.setFilename(TEMPLATE_FILENAME);
    var storage = mock(IFileMetadataStorage.class);
    when(storage.getElementById(TEMPLATE_ID)).thenReturn(fileMetadata);
    return storage;
  }

  private ISpCoreConfigurationStorage coreConfigurationStorage() {
    var configuration = new SpCoreConfiguration();
    configuration.setFilesDir(filesDir.toString());
    var storage = mock(ISpCoreConfigurationStorage.class);
    when(storage.get()).thenReturn(configuration);
    return storage;
  }
}
