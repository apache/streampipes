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

@TsModel
public class AdapterHealthStatus {

  private String adapterId;
  private String adapterName;
  private HealthCheckStatus backendHealth;
  private String backendHealthMessage;
  private HealthCheckStatus dataSourceHealth;
  private String dataSourceHealthMessage;
  private String dataSourceHealthDetails;
  private HealthCheckStatus overallStatus;
  private long lastCheckTimestamp;
  private boolean dataSourceHealthSupported;
  private int consecutiveFailures;

  public AdapterHealthStatus() {
    this.backendHealth = HealthCheckStatus.UNKNOWN;
    this.dataSourceHealth = HealthCheckStatus.UNKNOWN;
    this.overallStatus = HealthCheckStatus.UNKNOWN;
  }

  public String getAdapterId() {
    return adapterId;
  }

  public void setAdapterId(String adapterId) {
    this.adapterId = adapterId;
  }

  public String getAdapterName() {
    return adapterName;
  }

  public void setAdapterName(String adapterName) {
    this.adapterName = adapterName;
  }

  public HealthCheckStatus getBackendHealth() {
    return backendHealth;
  }

  public void setBackendHealth(HealthCheckStatus backendHealth) {
    this.backendHealth = backendHealth;
  }

  public String getBackendHealthMessage() {
    return backendHealthMessage;
  }

  public void setBackendHealthMessage(String backendHealthMessage) {
    this.backendHealthMessage = backendHealthMessage;
  }

  public HealthCheckStatus getDataSourceHealth() {
    return dataSourceHealth;
  }

  public void setDataSourceHealth(HealthCheckStatus dataSourceHealth) {
    this.dataSourceHealth = dataSourceHealth;
  }

  public String getDataSourceHealthMessage() {
    return dataSourceHealthMessage;
  }

  public void setDataSourceHealthMessage(String dataSourceHealthMessage) {
    this.dataSourceHealthMessage = dataSourceHealthMessage;
  }

  public String getDataSourceHealthDetails() {
    return dataSourceHealthDetails;
  }

  public void setDataSourceHealthDetails(String dataSourceHealthDetails) {
    this.dataSourceHealthDetails = dataSourceHealthDetails;
  }

  public HealthCheckStatus getOverallStatus() {
    return overallStatus;
  }

  public void setOverallStatus(HealthCheckStatus overallStatus) {
    this.overallStatus = overallStatus;
  }

  public long getLastCheckTimestamp() {
    return lastCheckTimestamp;
  }

  public void setLastCheckTimestamp(long lastCheckTimestamp) {
    this.lastCheckTimestamp = lastCheckTimestamp;
  }

  public boolean isDataSourceHealthSupported() {
    return dataSourceHealthSupported;
  }

  public void setDataSourceHealthSupported(boolean dataSourceHealthSupported) {
    this.dataSourceHealthSupported = dataSourceHealthSupported;
  }

  public int getConsecutiveFailures() {
    return consecutiveFailures;
  }

  public void setConsecutiveFailures(int consecutiveFailures) {
    this.consecutiveFailures = consecutiveFailures;
  }

  public void updateOverallStatus() {
    if (backendHealth != HealthCheckStatus.HEALTHY) {
      overallStatus = HealthCheckStatus.UNHEALTHY;
    } else if (!dataSourceHealthSupported) {
      overallStatus = backendHealth;
    } else {
      overallStatus = dataSourceHealth;
    }
  }
}
