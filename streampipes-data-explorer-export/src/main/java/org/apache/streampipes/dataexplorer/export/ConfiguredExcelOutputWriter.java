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

import org.apache.streampipes.manager.file.FileManager;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.param.SupportedRestQueryParams;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.storage.api.CRUDStorage;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class ConfiguredExcelOutputWriter extends ConfiguredOutputWriter {

  private final CRUDStorage<FileMetadata> storage;

  private SXSSFWorkbook wb;
  private Sheet ws;
  private boolean useTemplate = false;
  private int startRow = 0;
  private String templateId;
  private DataLakeMeasure schema;
  private String headerColumnNameStrategy;

  public ConfiguredExcelOutputWriter(CRUDStorage<FileMetadata> fileMetadataStorage) {
    this.storage = fileMetadataStorage;
  }

  @Override
  public void configure(DataLakeMeasure schema,
                        ProvidedRestQueryParams params,
                        boolean ignoreMissingValues) {
    var qp = params.getProvidedParams();
    this.schema = schema;
    this.headerColumnNameStrategy = qp.getOrDefault(SupportedRestQueryParams.QP_HEADER_COLUMN_NAME, "key");
    if (qp.containsKey(SupportedRestQueryParams.QP_XLSX_USE_TEMPLATE)) {
      this.useTemplate = true;
      this.startRow = Integer.parseInt(qp.getOrDefault(SupportedRestQueryParams.QP_XLSX_START_ROW, "0"));
      this.templateId = qp.getOrDefault(SupportedRestQueryParams.QP_XLSX_TEMPLATE_ID, null);
    }
  }

  @Override
  public void beforeFirstItem(OutputStream outputStream) {
    if (useTemplate && Objects.nonNull(templateId)) {
      var fileMetadata = storage.getElementById(templateId);
      if (fileMetadata != null) {
        var path = new FileManager().getFile(fileMetadata.getFilename()).getAbsoluteFile().toPath();
        try (InputStream is = Files.newInputStream(path)) {
          XSSFWorkbook templateWorkbook = new XSSFWorkbook(is);
          wb = new SXSSFWorkbook(templateWorkbook);
          ws = wb.getSheetAt(0);
        } catch (IOException e) {
          createNewWorkbook();
        }
      }
    } else {
      createNewWorkbook();
    }
  }

  private void createNewWorkbook() {
    wb = new SXSSFWorkbook();
    ws = wb.createSheet();
  }

  @Override
  public void afterLastItem(OutputStream outputStream) throws IOException {
    wb.write(outputStream);
    wb.close();
  }

  @Override
  public void writeItem(OutputStream outputStream,
                        List<Object> row,
                        List<String> columnNames, boolean firstObject) throws IOException {
    var excelRow = ws.createRow(startRow);
    int columnIndex = 0;
    if (firstObject) {
      for (String column : columnNames) {
        var cell = excelRow.createCell(columnIndex);
        var headerName = getHeaderName(schema, String.valueOf(column), headerColumnNameStrategy);
        cell.setCellValue(headerName);
        columnIndex++;
      }
      startRow++;
    }
    for (Object column : row) {
      var cell = excelRow.createCell(columnIndex);
      String entry = ExportUtils.formatValue(column);
      cell.setCellValue(entry);
      columnIndex++;
    }
    startRow++;
  }
}
