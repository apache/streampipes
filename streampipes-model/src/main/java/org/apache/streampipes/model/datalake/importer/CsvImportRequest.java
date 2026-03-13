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

package org.apache.streampipes.model.datalake.importer;

import java.util.ArrayList;
import java.util.List;

public class CsvImportRequest {

  private CsvImportConfiguration csvConfig;
  private List<String> headers = new ArrayList<>();
  private List<List<String>> rows = new ArrayList<>();
  private CsvImportTarget target;
  private String timestampColumn;
  private List<CsvImportColumn> columns = new ArrayList<>();

  public CsvImportConfiguration getCsvConfig() {
    return csvConfig;
  }

  public void setCsvConfig(CsvImportConfiguration csvConfig) {
    this.csvConfig = csvConfig;
  }

  public List<String> getHeaders() {
    return headers;
  }

  public void setHeaders(List<String> headers) {
    this.headers = headers;
  }

  public List<List<String>> getRows() {
    return rows;
  }

  public void setRows(List<List<String>> rows) {
    this.rows = rows;
  }

  public CsvImportTarget getTarget() {
    return target;
  }

  public void setTarget(CsvImportTarget target) {
    this.target = target;
  }

  public String getTimestampColumn() {
    return timestampColumn;
  }

  public void setTimestampColumn(String timestampColumn) {
    this.timestampColumn = timestampColumn;
  }

  public List<CsvImportColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<CsvImportColumn> columns) {
    this.columns = columns;
  }
}
