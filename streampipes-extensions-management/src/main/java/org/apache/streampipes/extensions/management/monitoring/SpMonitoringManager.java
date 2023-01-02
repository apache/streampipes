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

package org.apache.streampipes.extensions.management.monitoring;

import org.apache.streampipes.model.monitoring.SpEndpointMonitoringInfo;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.model.monitoring.SpMetricsEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SpMonitoringManager {

  INSTANCE;

  private final Map<String, List<SpLogEntry>> logInfos;
  private final Map<String, SpMetricsEntry> metricsInfos;

  SpMonitoringManager() {
    this.logInfos = new HashMap<>();
    this.metricsInfos = new HashMap<>();
  }

  public void addErrorMessage(String resourceId,
                              SpLogEntry errorMessageEntry) {
    if (!logInfos.containsKey(resourceId)) {
      logInfos.put(resourceId, new ArrayList<>());
    }
    this.logInfos.get(resourceId).add(0, errorMessageEntry);
  }

  public void increaseInCounter(String resourceId,
                                String sourceInfo,
                                long timestamp) {
    var currentEntry = getMetricsEntry(resourceId, timestamp);
    currentEntry.addInMetrics(sourceInfo, timestamp);
    this.metricsInfos.put(resourceId, currentEntry);
  }


  public void increaseOutCounter(String resourceId,
                                 long timestamp) {
    var currentEntry = getMetricsEntry(resourceId, timestamp);
    currentEntry.addOutMetrics(timestamp);
    this.metricsInfos.put(resourceId, currentEntry);
  }

  public void resetCounter(String resourceId) {
    this.metricsInfos.put(resourceId, new SpMetricsEntry());
  }

  public void resetLogs(String resourceId) {
    if (this.logInfos.containsKey(resourceId)) {
      this.logInfos.get(resourceId).clear();
    }
  }

  public void reset(String resourceId) {
    this.resetCounter(resourceId);
    this.resetLogs(resourceId);
  }

  public SpMetricsEntry getMetricsEntry(String resourceId,
                                        long timestamp) {
    checkAndPrepareMetrics(resourceId);
    var currentEntry = this.metricsInfos.get(resourceId);
    currentEntry.setLastTimestamp(timestamp);

    return currentEntry;
  }

  public Map<String, List<SpLogEntry>> getAllLogs() {
    return logInfos;
  }

  public Map<String, SpMetricsEntry> getAllMetrics() {
    return this.metricsInfos;
  }

  public SpEndpointMonitoringInfo getMonitoringInfo() {
    return new SpEndpointMonitoringInfo(logInfos, metricsInfos);
  }

  private void checkAndPrepareMetrics(String resourceId) {
    if (!metricsInfos.containsKey(resourceId)) {
      addMetricsObject(resourceId);
    }
  }

  private void addMetricsObject(String resourceId) {
    this.metricsInfos.put(resourceId, new SpMetricsEntry());
  }


  public void clearAllLogs() {
    logInfos.forEach((key, value) -> value.clear());
  }
}
