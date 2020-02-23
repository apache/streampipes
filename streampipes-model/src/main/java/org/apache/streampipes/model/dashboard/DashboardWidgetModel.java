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

import org.apache.streampipes.vocabulary.StreamPipes;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.DASHBOARD_WIDGET_MODEL)
@Entity
public class DashboardWidgetModel extends DashboardEntity {

  @RdfProperty(StreamPipes.HAS_DASHBOARD_WIDGET_ID)
  private String widgetId;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_DASHBOARD_WIDGET_SETTINGS)
  private DashboardWidgetSettings dashboardWidgetSettings;

  @RdfProperty(StreamPipes.HAS_PIPELINE_ID)
  private String visualizablePipelineId;

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

  public String getVisualizablePipelineId() {
    return visualizablePipelineId;
  }

  public void setVisualizablePipelineId(String visualizablePipelineId) {
    this.visualizablePipelineId = visualizablePipelineId;
  }
}
