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

import java.util.HashSet;
import java.util.Set;

@TsModel
public class StreamPipesApplicationPackage {

  private Set<String> requiredProcessorAppIds;
  private Set<String> requiredDataSinkAppIds;
  private Set<String> requiredAdapterAppIds;

  private Set<String> assets;
  private Set<String> adapters;
  private Set<String> dashboards;
  private Set<String> dashboardWidgets;
  private Set<String> dataViews;
  private Set<String> dataViewWidgets;
  private Set<String> dataLakeMeasures;
  private Set<String> dataSources;
  private Set<String> pipelines;
  private Set<String> files;

  public StreamPipesApplicationPackage() {
    this.requiredProcessorAppIds = new HashSet<>();
    this.requiredDataSinkAppIds = new HashSet<>();
    this.requiredAdapterAppIds = new HashSet<>();

    this.adapters = new HashSet<>();
    this.assets = new HashSet<>();
    this.dashboards = new HashSet<>();
    this.dashboardWidgets = new HashSet<>();
    this.dataViews = new HashSet<>();
    this.dataViewWidgets = new HashSet<>();
    this.dataLakeMeasures = new HashSet<>();
    this.dataSources = new HashSet<>();
    this.pipelines = new HashSet<>();
    this.files = new HashSet<>();
  }

  public Set<String> getRequiredProcessorAppIds() {
    return requiredProcessorAppIds;
  }

  public void setRequiredProcessorAppIds(Set<String> requiredProcessorAppIds) {
    this.requiredProcessorAppIds = requiredProcessorAppIds;
  }

  public Set<String> getRequiredDataSinkAppIds() {
    return requiredDataSinkAppIds;
  }

  public void setRequiredDataSinkAppIds(Set<String> requiredDataSinkAppIds) {
    this.requiredDataSinkAppIds = requiredDataSinkAppIds;
  }

  public Set<String> getRequiredAdapterAppIds() {
    return requiredAdapterAppIds;
  }

  public void setRequiredAdapterAppIds(Set<String> requiredAdapterAppIds) {
    this.requiredAdapterAppIds = requiredAdapterAppIds;
  }

  public Set<String> getAdapters() {
    return adapters;
  }

  public void setAdapters(Set<String> adapters) {
    this.adapters = adapters;
  }

  public void addAdapter(String adapter) {
    this.adapters.add(adapter);
  }

  public Set<String> getDashboards() {
    return dashboards;
  }

  public void setDashboards(Set<String> dashboards) {
    this.dashboards = dashboards;
  }

  public void addDashboard(String dashboard) {
    this.dashboards.add(dashboard);
  }

  public Set<String> getDashboardWidgets() {
    return dashboardWidgets;
  }

  public void setDashboardWidgets(Set<String> dashboardWidgets) {
    this.dashboardWidgets = dashboardWidgets;
  }

  public void addDashboardWidget(String dashboardWidget) {
    this.dashboardWidgets.add(dashboardWidget);
  }

  public Set<String> getDataViews() {
    return dataViews;
  }

  public void setDataViews(Set<String> dataViews) {
    this.dataViews = dataViews;
  }

  public void addDataView(String dataView) {
    this.dataViews.add(dataView);
  }

  public Set<String> getDataViewWidgets() {
    return dataViewWidgets;
  }

  public void setDataViewWidgets(Set<String> dataViewWidgets) {
    this.dataViewWidgets = dataViewWidgets;
  }

  public void addDataViewWidget(String dataViewWidget) {
    this.dataViewWidgets.add(dataViewWidget);
  }

  public Set<String> getDataLakeMeasures() {
    return dataLakeMeasures;
  }

  public void setDataLakeMeasures(Set<String> dataLakeMeasures) {
    this.dataLakeMeasures = dataLakeMeasures;
  }

  public void addDataLakeMeasure(String dataLakeMeasure) {
    this.dataLakeMeasures.add(dataLakeMeasure);
  }

  public Set<String> getDataSources() {
    return dataSources;
  }

  public void setDataSources(Set<String> dataSources) {
    this.dataSources = dataSources;
  }

  public void addDataSource(String dataSource) {
    this.dataSources.add(dataSource);
  }

  public Set<String> getPipelines() {
    return pipelines;
  }

  public void setPipelines(Set<String> pipelines) {
    this.pipelines = pipelines;
  }

  public void addPipeline(String pipeline) {
    this.pipelines.add(pipeline);
  }

  public Set<String> getFiles() {
    return files;
  }

  public void setFiles(Set<String> files) {
    this.files = files;
  }

  public void addFile(String file) {
    this.files.add(file);
  }

  public Set<String> getAssets() {
    return assets;
  }

  public void setAssets(Set<String> assets) {
    this.assets = assets;
  }

  public void addAsset(String asset) {
    this.assets.add(asset);
  }
}
