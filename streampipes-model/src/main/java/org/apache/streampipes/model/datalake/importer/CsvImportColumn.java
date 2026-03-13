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

public class CsvImportColumn {

  private String csvColumn;
  private String runtimeName;
  private String runtimeType;
  private String propertyScope;
  private String semanticType;
  private String label;
  private String description;
  private String inferredType;
  private boolean timestampCandidate;

  public String getCsvColumn() {
    return csvColumn;
  }

  public void setCsvColumn(String csvColumn) {
    this.csvColumn = csvColumn;
  }

  public String getRuntimeName() {
    return runtimeName;
  }

  public void setRuntimeName(String runtimeName) {
    this.runtimeName = runtimeName;
  }

  public String getRuntimeType() {
    return runtimeType;
  }

  public void setRuntimeType(String runtimeType) {
    this.runtimeType = runtimeType;
  }

  public String getPropertyScope() {
    return propertyScope;
  }

  public void setPropertyScope(String propertyScope) {
    this.propertyScope = propertyScope;
  }

  public String getSemanticType() {
    return semanticType;
  }

  public void setSemanticType(String semanticType) {
    this.semanticType = semanticType;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getInferredType() {
    return inferredType;
  }

  public void setInferredType(String inferredType) {
    this.inferredType = inferredType;
  }

  public boolean isTimestampCandidate() {
    return timestampCandidate;
  }

  public void setTimestampCandidate(boolean timestampCandidate) {
    this.timestampCandidate = timestampCandidate;
  }
}
