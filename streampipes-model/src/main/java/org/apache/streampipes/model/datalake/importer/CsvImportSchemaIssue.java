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

public class CsvImportSchemaIssue {

  private CsvImportSchemaIssueType type;
  private String columnName;
  private String expected;
  private String actual;

  public CsvImportSchemaIssue() {
  }

  public CsvImportSchemaIssue(
      CsvImportSchemaIssueType type,
      String columnName,
      String expected,
      String actual
  ) {
    this.type = type;
    this.columnName = columnName;
    this.expected = expected;
    this.actual = actual;
  }

  public CsvImportSchemaIssueType getType() {
    return type;
  }

  public void setType(CsvImportSchemaIssueType type) {
    this.type = type;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getExpected() {
    return expected;
  }

  public void setExpected(String expected) {
    this.expected = expected;
  }

  public String getActual() {
    return actual;
  }

  public void setActual(String actual) {
    this.actual = actual;
  }
}
