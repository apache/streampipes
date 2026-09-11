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

package org.apache.streampipes.model.dataset.importer;

import java.util.ArrayList;
import java.util.List;

public class CsvImportJobStatus {

  private String jobId;
  private CsvImportJobState state;
  private int processedRows;
  private int totalRows;
  private int progress;
  private CsvImportResult result;
  private List<CsvImportValidationMessage> validationMessages = new ArrayList<>();

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public CsvImportJobState getState() {
    return state;
  }

  public void setState(CsvImportJobState state) {
    this.state = state;
  }

  public int getProcessedRows() {
    return processedRows;
  }

  public void setProcessedRows(int processedRows) {
    this.processedRows = processedRows;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public CsvImportResult getResult() {
    return result;
  }

  public void setResult(CsvImportResult result) {
    this.result = result;
  }

  public List<CsvImportValidationMessage> getValidationMessages() {
    return validationMessages;
  }

  public void setValidationMessages(List<CsvImportValidationMessage> validationMessages) {
    this.validationMessages = validationMessages;
  }
}
