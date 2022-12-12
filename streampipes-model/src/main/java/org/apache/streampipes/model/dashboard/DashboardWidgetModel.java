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

package org.apache.streampipes.model.dashboard;

import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public class DashboardWidgetModel extends DashboardEntity {

  private String widgetId;

  private String widgetType;

  private DashboardWidgetSettings dashboardWidgetSettings;

  private String pipelineId;
  private String visualizationName;

  public DashboardWidgetModel() {
    super();
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public DashboardWidgetSettings getDashboardWidgetSettings() {
    return dashboardWidgetSettings;
  }

  public void setDashboardWidgetSettings(DashboardWidgetSettings dashboardWidgetSettings) {
    this.dashboardWidgetSettings = dashboardWidgetSettings;
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public String getVisualizationName() {
    return visualizationName;
  }

  public void setVisualizationName(String visualizationName) {
    this.visualizationName = visualizationName;
  }

  public String getWidgetType() {
    return widgetType;
  }

  public void setWidgetType(String widgetType) {
    this.widgetType = widgetType;
  }
}
