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

import org.apache.streampipes.extensions.api.connect.IDataSourceHealthCheck;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterHealthStatus;
import org.apache.streampipes.model.connect.adapter.HealthCheckStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public enum AdapterHealthCheckManager {
  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(AdapterHealthCheckManager.class);
  private static final long INITIAL_INTERVAL_MS = 60_000;
  private static final long MAX_INTERVAL_MS = 86_400_000;
  private static final double BACKOFF_MULTIPLIER = 2.0;

  private final Map<String, AdapterHealthStatus> healthStatuses = new ConcurrentHashMap<>();
  private final Map<String, ScheduledFuture<?>> scheduledChecks = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
  private final RunningAdapterInstances runningAdapters;
  private final Map<String, StreamPipesAdapter> adapterInstances = new ConcurrentHashMap<>();

  AdapterHealthCheckManager() {
    this.runningAdapters = RunningAdapterInstances.INSTANCE;
  }

  public void registerAdapter(String adapterId, StreamPipesAdapter adapter, AdapterDescription description) {
    adapterInstances.put(adapterId, adapter);
    var status = new AdapterHealthStatus();
    status.setAdapterId(adapterId);
    status.setAdapterName(description.getName());
    status.setBackendHealth(HealthCheckStatus.HEALTHY);
    status.setDataSourceHealthSupported(adapter instanceof IDataSourceHealthCheck);
    status.setDataSourceHealth(status.isDataSourceHealthSupported() ? HealthCheckStatus.UNKNOWN : HealthCheckStatus.UNKNOWN);
    status.setLastCheckTimestamp(System.currentTimeMillis());
    status.updateOverallStatus();
    healthStatuses.put(adapterId, status);
    scheduleHealthCheck(adapterId, 15_000);
  }

  public void triggerHealthCheck(String adapterId) {
    scheduleHealthCheck(adapterId, 0);
  }

  public void unregisterAdapter(String adapterId) {
    healthStatuses.remove(adapterId);
    adapterInstances.remove(adapterId);
    var future = scheduledChecks.remove(adapterId);
    if (future != null) {
      future.cancel(false);
    }
  }

  public AdapterHealthStatus getHealthStatus(String adapterId) {
    return healthStatuses.getOrDefault(adapterId, createUnknownStatus(adapterId));
  }

  public List<AdapterHealthStatus> getAllHealthStatuses() {
    return List.copyOf(healthStatuses.values());
  }

  private void scheduleHealthCheck(String adapterId, long delayMs) {
    var future = scheduledChecks.remove(adapterId);
    if (future != null) {
      future.cancel(false);
    }
    
    var status = healthStatuses.get(adapterId);
    if (status != null) {
      status.setNextCheckTimestamp(System.currentTimeMillis() + delayMs);
    }
    
    var newFuture = scheduler.schedule(() -> performHealthCheck(adapterId), delayMs, TimeUnit.MILLISECONDS);
    scheduledChecks.put(adapterId, newFuture);
  }

  private void performHealthCheck(String adapterId) {
    var adapter = adapterInstances.get(adapterId);
    var status = healthStatuses.get(adapterId);
    if (adapter == null || status == null) {
      return;
    }

    status.setBackendHealth(HealthCheckStatus.HEALTHY);
    status.setBackendHealthMessage("Extension service is running");

    if (adapter instanceof IDataSourceHealthCheck healthCheck) {
      try {
        var result = healthCheck.checkDataSourceHealth();
        status.setDataSourceHealth(result.healthy() ? HealthCheckStatus.HEALTHY : HealthCheckStatus.UNHEALTHY);
        status.setDataSourceHealthMessage(result.message());
        status.setDataSourceHealthDetails(result.details());
        status.setConsecutiveFailures(result.healthy() ? 0 : status.getConsecutiveFailures() + 1);
      } catch (Exception e) {
        LOG.warn("Health check failed for adapter {}: {}", adapterId, e.getMessage());
        status.setDataSourceHealth(HealthCheckStatus.UNHEALTHY);
        status.setDataSourceHealthMessage("Health check exception: " + e.getMessage());
        status.setConsecutiveFailures(status.getConsecutiveFailures() + 1);
      }
    }

    status.setLastCheckTimestamp(System.currentTimeMillis());
    status.updateOverallStatus();

    var nextInterval = calculateNextInterval(status.getConsecutiveFailures());
    scheduleHealthCheck(adapterId, nextInterval);
  }

  private long calculateNextInterval(int failures) {
    if (failures == 0) {
      return INITIAL_INTERVAL_MS;
    }
    var interval = (long) (INITIAL_INTERVAL_MS * Math.pow(BACKOFF_MULTIPLIER, failures));
    return Math.min(interval, MAX_INTERVAL_MS);
  }

  private AdapterHealthStatus createUnknownStatus(String adapterId) {
    var status = new AdapterHealthStatus();
    status.setAdapterId(adapterId);
    status.setBackendHealth(HealthCheckStatus.UNKNOWN);
    status.setDataSourceHealth(HealthCheckStatus.UNKNOWN);
    status.setOverallStatus(HealthCheckStatus.UNKNOWN);
    return status;
  }

  public void shutdown() {
    scheduler.shutdown();
  }
}
