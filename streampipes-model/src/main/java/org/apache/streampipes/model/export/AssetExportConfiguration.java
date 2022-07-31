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

import java.util.HashSet;
import java.util.Set;

public class AssetExportConfiguration {

  private String assetId;
  private String assetName;

  private Set<ExportItem> adapters;
  private Set<ExportItem> dashboards;
  private Set<ExportItem> dataViews;
  private Set<ExportItem> dataLakeMeasures;
  private Set<ExportItem> dataSources;
  private Set<ExportItem> pipelines;
  private Set<ExportItem> files;

  public AssetExportConfiguration() {
    this.adapters = new HashSet<>();
    this.dashboards = new HashSet<>();
    this.dataViews = new HashSet<>();
    this.dataLakeMeasures = new HashSet<>();
    this.dataSources = new HashSet<>();
    this.pipelines = new HashSet<>();
    this.files = new HashSet<>();
  }

  public Set<ExportItem> getAdapters() {
    return adapters;
  }

  public void setAdapters(Set<ExportItem> adapters) {
    this.adapters = adapters;
  }

  public Set<ExportItem> getDashboards() {
    return dashboards;
  }

  public void setDashboards(Set<ExportItem> dashboards) {
    this.dashboards = dashboards;
  }

  public Set<ExportItem> getDataViews() {
    return dataViews;
  }

  public void setDataViews(Set<ExportItem> dataViews) {
    this.dataViews = dataViews;
  }

  public Set<ExportItem> getDataLakeMeasures() {
    return dataLakeMeasures;
  }

  public void setDataLakeMeasures(Set<ExportItem> dataLakeMeasures) {
    this.dataLakeMeasures = dataLakeMeasures;
  }

  public Set<ExportItem> getDataSources() {
    return dataSources;
  }

  public void setDataSources(Set<ExportItem> dataSources) {
    this.dataSources = dataSources;
  }

  public String getAssetId() {
    return assetId;
  }

  public void setAssetId(String assetId) {
    this.assetId = assetId;
  }

  public Set<ExportItem> getPipelines() {
    return pipelines;
  }

  public void setPipelines(Set<ExportItem> pipelines) {
    this.pipelines = pipelines;
  }

  public Set<ExportItem> getFiles() {
    return files;
  }

  public void setFiles(Set<ExportItem> files) {
    this.files = files;
  }

  public String getAssetName() {
    return assetName;
  }

  public void setAssetName(String assetName) {
    this.assetName = assetName;
  }


}
