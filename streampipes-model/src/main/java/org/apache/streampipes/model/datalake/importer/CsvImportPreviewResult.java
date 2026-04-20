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

import org.apache.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;

public class CsvImportPreviewResult {

  private String uploadId;
  private Boolean isExistingTarget;

  public Boolean getIsExistingTarget() {
    return isExistingTarget;
  }

  public void setIsExistingTarget(Boolean isExistingTarget) {
    this.isExistingTarget = isExistingTarget;
  }

  private List<String> headers = new ArrayList<>();
  private List<List<String>> previewRows = new ArrayList<>();
  private List<CsvImportColumn> columns = new ArrayList<>();
  private EventSchema guessedEventSchema;
  private List<String> timestampCandidates = new ArrayList<>();
  private boolean valid;
  private List<CsvImportValidationMessage> validationMessages = new ArrayList<>();
  private List<CsvImportSchemaIssue> validationSchemaMessages = new ArrayList<>();

  public String getUploadId() {
    return uploadId;
  }

  public void setUploadId(String uploadId) {
    this.uploadId = uploadId;
  }

  public List<String> getHeaders() {
    return headers;
  }

  public void setHeaders(List<String> headers) {
    this.headers = headers;
  }

  public List<List<String>> getPreviewRows() {
    return previewRows;
  }

  public void setPreviewRows(List<List<String>> previewRows) {
    this.previewRows = previewRows;
  }

  public List<CsvImportColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<CsvImportColumn> columns) {
    this.columns = columns;
  }

  public EventSchema getGuessedEventSchema() {
    return guessedEventSchema;
  }

  public void setGuessedEventSchema(EventSchema guessedEventSchema) {
    this.guessedEventSchema = guessedEventSchema;
  }

  public List<String> getTimestampCandidates() {
    return timestampCandidates;
  }

  public void setTimestampCandidates(List<String> timestampCandidates) {
    this.timestampCandidates = timestampCandidates;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public List<CsvImportValidationMessage> getValidationMessages() {
    return validationMessages;
  }

  public void setValidationMessages(List<CsvImportValidationMessage> validationMessages) {
    this.validationMessages = validationMessages;
  }

  public List<CsvImportSchemaIssue> getSchemaValidationMessages() {
    return validationSchemaMessages;
  }

  public void setSchemaValidationMessages(List<CsvImportSchemaIssue> validationSchemaMessages) {
    this.validationSchemaMessages = validationSchemaMessages;
  }
}
