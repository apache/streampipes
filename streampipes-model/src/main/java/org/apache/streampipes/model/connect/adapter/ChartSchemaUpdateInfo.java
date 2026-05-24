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

package org.apache.streampipes.model.connect.adapter;

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class ChartSchemaUpdateInfo {

  private String chartId;
  private String chartTitle;
  private String measureName;
  private boolean canAutoMigrate;
  private List<String> affectedFields;

  public ChartSchemaUpdateInfo() {
    this.affectedFields = new ArrayList<>();
  }

  public String getChartId() {
    return chartId;
  }

  public void setChartId(String chartId) {
    this.chartId = chartId;
  }

  public String getChartTitle() {
    return chartTitle;
  }

  public void setChartTitle(String chartTitle) {
    this.chartTitle = chartTitle;
  }

  public String getMeasureName() {
    return measureName;
  }

  public void setMeasureName(String measureName) {
    this.measureName = measureName;
  }

  public boolean isCanAutoMigrate() {
    return canAutoMigrate;
  }

  public void setCanAutoMigrate(boolean canAutoMigrate) {
    this.canAutoMigrate = canAutoMigrate;
  }

  public List<String> getAffectedFields() {
    return affectedFields;
  }

  public void setAffectedFields(List<String> affectedFields) {
    this.affectedFields = affectedFields;
  }
}
