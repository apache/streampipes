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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TsModel
public class DashboardModel {

  @JsonProperty("_id")
  private @SerializedName("_id") String couchDbId;

  @JsonProperty("_rev")
  private @SerializedName("_rev") String couchDbRev;

  private String id;
  private String name;
  private String description;
  private boolean displayHeader;

  private Map<String, Object> dashboardTimeSettings;
  private Map<String, Object> dashboardGeneralSettings;

  private List<DashboardItem> widgets;

  public DashboardModel() {
    this.dashboardTimeSettings = new HashMap<>();
    this.dashboardGeneralSettings = new HashMap<>();
  }

  public String getCouchDbId() {
    return couchDbId;
  }

  public void setCouchDbId(String couchDbId) {
    this.couchDbId = couchDbId;
  }

  public String getCouchDbRev() {
    return couchDbRev;
  }

  public void setCouchDbRev(String couchDbRev) {
    this.couchDbRev = couchDbRev;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<DashboardItem> getWidgets() {
    return widgets;
  }

  public void setWidgets(List<DashboardItem> widgets) {
    this.widgets = widgets;
  }

  public boolean isDisplayHeader() {
    return displayHeader;
  }

  public void setDisplayHeader(boolean displayHeader) {
    this.displayHeader = displayHeader;
  }

  public Map<String, Object> getDashboardTimeSettings() {
    return dashboardTimeSettings;
  }

  public void setDashboardTimeSettings(Map<String, Object> dashboardTimeSettings) {
    this.dashboardTimeSettings = dashboardTimeSettings;
  }

  public Map<String, Object> getDashboardGeneralSettings() {
    return dashboardGeneralSettings;
  }

  public void setDashboardGeneralSettings(Map<String, Object> dashboardGeneralSettings) {
    this.dashboardGeneralSettings = dashboardGeneralSettings;
  }
}
