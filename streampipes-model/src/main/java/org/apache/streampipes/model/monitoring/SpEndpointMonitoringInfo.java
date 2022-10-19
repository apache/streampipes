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

package org.apache.streampipes.model.monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpEndpointMonitoringInfo {

  private Map<String, List<SpLogEntry>> logInfos;
  private Map<String, SpMetricsEntry> metricsInfos;

  public SpEndpointMonitoringInfo() {
    this.logInfos = new HashMap<>();
    this.metricsInfos = new HashMap<>();
  }

  public SpEndpointMonitoringInfo(Map<String, List<SpLogEntry>> logInfos,
                                  Map<String, SpMetricsEntry> metricsInfos) {
    this.logInfos = logInfos;
    this.metricsInfos = metricsInfos;
  }

  public Map<String, List<SpLogEntry>> getLogInfos() {
    return logInfos;
  }

  public void setLogInfos(Map<String, List<SpLogEntry>> logInfos) {
    this.logInfos = logInfos;
  }

  public Map<String, SpMetricsEntry> getMetricsInfos() {
    return metricsInfos;
  }

  public void setMetricsInfos(Map<String, SpMetricsEntry> metricsInfos) {
    this.metricsInfos = metricsInfos;
  }
}
