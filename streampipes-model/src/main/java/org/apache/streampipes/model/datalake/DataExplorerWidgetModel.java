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

package org.apache.streampipes.model.datalake;

import org.apache.streampipes.model.dashboard.DashboardEntity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

public class DataExplorerWidgetModel extends DashboardEntity {

  private String widgetId;

  private String widgetType;

  @JsonSerialize(using = CustomMapSerializer.class, as = Map.class)
  private Map<String, Object> baseAppearanceConfig;

  @JsonSerialize(using = CustomMapSerializer.class, as = Map.class)
  private Map<String, Object> visualizationConfig;

  @JsonSerialize(using = CustomMapSerializer.class, as = Map.class)
  private Map<String, Object> dataConfig;

  private String pipelineId;
  private String measureName;


  public DataExplorerWidgetModel() {
    super();
    this.baseAppearanceConfig = new HashMap<>();
    this.visualizationConfig = new HashMap<>();
    this.dataConfig = new HashMap<>();
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getWidgetType() {
    return widgetType;
  }

  public void setWidgetType(String widgetType) {
    this.widgetType = widgetType;
  }

  public Map<String, Object> getBaseAppearanceConfig() {
    return baseAppearanceConfig;
  }

  public void setBaseAppearanceConfig(Map<String, Object> baseAppearanceConfig) {
    this.baseAppearanceConfig = baseAppearanceConfig;
  }

  public Map<String, Object> getVisualizationConfig() {
    return visualizationConfig;
  }

  public void setVisualizationConfig(Map<String, Object> visualizationConfig) {
    this.visualizationConfig = visualizationConfig;
  }

  public Map<String, Object> getDataConfig() {
    return dataConfig;
  }

  public void setDataConfig(Map<String, Object> dataConfig) {
    this.dataConfig = dataConfig;
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public String getMeasureName() {
    return measureName;
  }

  public void setMeasureName(String measureName) {
    this.measureName = measureName;
  }

}
