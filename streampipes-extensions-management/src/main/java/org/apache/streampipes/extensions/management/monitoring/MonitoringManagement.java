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

import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.model.monitoring.SpEndpointMonitoringInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringManagement {

  private static final Logger LOG = LoggerFactory.getLogger(MonitoringManagement.class);

  private final SpMonitoringManager monitoringManager;

  public MonitoringManagement() {
    this(SpMonitoringManager.INSTANCE);
  }

  public MonitoringManagement(SpMonitoringManager monitoringManager) {
    this.monitoringManager = monitoringManager;
  }

  public SpEndpointMonitoringInfo getMonitoringInfos() {
    try {
      var monitoringInfo = monitoringManager.getMonitoringInfo();
      LOG.debug("Returning extension monitoring response: resourceCount={}, totalOutputCounter={}, "
              + "latestOutputTimestamp={}, thread={}",
          monitoringInfo.getMetricsInfos().size(),
          totalOutputCounter(monitoringInfo),
          latestOutputTimestamp(monitoringInfo),
          Thread.currentThread().getName());

      return monitoringInfo;
    } finally {
      monitoringManager.clearAllLogs();
    }
  }

  private long totalOutputCounter(SpEndpointMonitoringInfo monitoringInfo) {
    return monitoringInfo.getMetricsInfos()
        .values()
        .stream()
        .mapToLong(metricsEntry -> metricsEntry.getMessagesOut().getCounter())
        .sum();
  }

  private long latestOutputTimestamp(SpEndpointMonitoringInfo monitoringInfo) {
    return monitoringInfo.getMetricsInfos()
        .values()
        .stream()
        .mapToLong(metricsEntry -> metricsEntry.getMessagesOut().getLastTimestamp())
        .max()
        .orElse(0);
  }
}
