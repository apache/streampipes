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

package org.apache.streampipes.model.export;

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class OverviewExport {

  private long generatedAt;
  private List<AdapterOverviewEntry> adapters;
  private List<PipelineOverviewEntry> pipelines;
  private List<DashboardOverviewEntry> dashboards;
  private List<ChartOverviewEntry> charts;

  public OverviewExport() {
    this.adapters = new ArrayList<>();
    this.pipelines = new ArrayList<>();
    this.dashboards = new ArrayList<>();
    this.charts = new ArrayList<>();
  }

  public long getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(long generatedAt) {
    this.generatedAt = generatedAt;
  }

  public List<AdapterOverviewEntry> getAdapters() {
    return adapters;
  }

  public void setAdapters(List<AdapterOverviewEntry> adapters) {
    this.adapters = adapters;
  }

  public List<PipelineOverviewEntry> getPipelines() {
    return pipelines;
  }

  public void setPipelines(List<PipelineOverviewEntry> pipelines) {
    this.pipelines = pipelines;
  }

  public List<DashboardOverviewEntry> getDashboards() {
    return dashboards;
  }

  public void setDashboards(List<DashboardOverviewEntry> dashboards) {
    this.dashboards = dashboards;
  }

  public List<ChartOverviewEntry> getCharts() {
    return charts;
  }

  public void setCharts(List<ChartOverviewEntry> charts) {
    this.charts = charts;
  }
}
