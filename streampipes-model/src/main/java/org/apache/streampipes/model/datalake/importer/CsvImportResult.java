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

public class CsvImportResult {

  private String measurementName;
  private String measurementId;
  private boolean createdNewMeasurement;
  private int importedRowCount;
  private List<CsvImportValidationMessage> validationMessages = new ArrayList<>();

  public String getMeasurementName() {
    return measurementName;
  }

  public void setMeasurementName(String measurementName) {
    this.measurementName = measurementName;
  }

  public String getMeasurementId() {
    return measurementId;
  }

  public void setMeasurementId(String measurementId) {
    this.measurementId = measurementId;
  }

  public boolean isCreatedNewMeasurement() {
    return createdNewMeasurement;
  }

  public void setCreatedNewMeasurement(boolean createdNewMeasurement) {
    this.createdNewMeasurement = createdNewMeasurement;
  }

  public int getImportedRowCount() {
    return importedRowCount;
  }

  public void setImportedRowCount(int importedRowCount) {
    this.importedRowCount = importedRowCount;
  }

  public List<CsvImportValidationMessage> getValidationMessages() {
    return validationMessages;
  }

  public void setValidationMessages(List<CsvImportValidationMessage> validationMessages) {
    this.validationMessages = validationMessages;
  }
}
