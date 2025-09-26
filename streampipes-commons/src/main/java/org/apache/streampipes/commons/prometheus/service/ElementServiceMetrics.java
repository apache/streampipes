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

package org.apache.streampipes.commons.prometheus.service;

import io.prometheus.client.Gauge;
import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;
import org.apache.streampipes.commons.prometheus.core.PrometheusMetrics;

/**
 * Service Metrics Manager
 * Inherits unified metrics manager, eliminating duplicate code
 */
public class ElementServiceMetrics extends PrometheusMetrics {
    
  // Metric name constants
  private static final String CPU_USAGE = "cpu_usage";
  private static final String MEMORY_USAGE = "memory_usage";
  private static final String WEIGHT = "weight";
  private static final String SYSTEM_LOAD = "system_load";
  private static final String HISTORICAL_SYSTEM_LOAD = "historical_system_load";
  private static final String CURRENT_SYSTEM_LOAD = "current_system_load";

  // Global service counter
  public static final Gauge serviceCount = StreamPipesCollectorRegistry.registerGauge(
        "serviceCount",
        "Total number of registered services"
  );

  public ElementServiceMetrics(String id) {
    super(id);
}

  @Override
  protected void registerGauges() {
    registerGauge(CPU_USAGE, "CPU usage percentage");
    registerGauge(MEMORY_USAGE, "Memory usage in bytes");
    registerGauge(WEIGHT, "Weight of remaining available resources for service");
    registerGauge(SYSTEM_LOAD, "System load average over the last minute");
    registerGauge(HISTORICAL_SYSTEM_LOAD, "Historical system load average");
    registerGauge(CURRENT_SYSTEM_LOAD, "Current system load average");
  }

  /**
   * Update CPU usage metric
   * @param cpuUsage CPU usage
   */
  public void updateCpuUsage(double cpuUsage) {
    setGaugeValue(CPU_USAGE, cpuUsage);
}

  /**
   * Update memory usage metric
   * @param memoryUsage Memory usage
   */
  public void updateMemoryUsage(double memoryUsage) {
    setGaugeValue(MEMORY_USAGE, memoryUsage);
}

  /**
   * Update weight metric
   * @param weight Weight
   */
  public void updateWeight(double weight) {
    setGaugeValue(WEIGHT, weight);
}

  /**
   * Update system load metric
   * @param systemLoad System load
   */
  public void updateSystemLoad(double systemLoad) {
    setGaugeValue(SYSTEM_LOAD, systemLoad);
}

  /**
   * Update historical system load metric
   * @param historicalSystemLoad Historical system load
   */
  public void updateHistoricalSystemLoad(double historicalSystemLoad) {
    setGaugeValue(HISTORICAL_SYSTEM_LOAD, historicalSystemLoad);
  }

  /**
   * Update current system load metric
   * @param currentSystemLoad Current system load
   */
  public void updateCurrentSystemLoad(double currentSystemLoad) {
    setGaugeValue(CURRENT_SYSTEM_LOAD, currentSystemLoad);
  }

  /**
   * Update basic metrics
   * @param cpuUsage CPU usage
   * @param memoryUsage Memory usage
   * @param weight Weight
   */
  public void updateBasicMetrics(double cpuUsage, double memoryUsage, double weight) {
    updateCpuUsage(cpuUsage);
    updateMemoryUsage(memoryUsage);
    updateWeight(weight);
  }

  /**
   * Update system load metrics
   * @param systemLoad System load
   * @param historicalSystemLoad Historical system load
   * @param currentSystemLoad Current system load
   */
  public void updateSystemLoadMetrics(double systemLoad, double historicalSystemLoad, double currentSystemLoad) {
    updateSystemLoad(systemLoad);
    updateHistoricalSystemLoad(historicalSystemLoad);
    updateCurrentSystemLoad(currentSystemLoad);
  }

  /**
   * Update all metrics
   * @param cpuUsage CPU usage
   * @param memoryUsage Memory usage
   * @param weight Weight
   * @param systemLoad System load
   * @param historicalSystemLoad Historical system load
   * @param currentSystemLoad Current system load
   */
  public void updateAllMetrics(double cpuUsage, double memoryUsage, double weight,
                            double systemLoad, double historicalSystemLoad, double currentSystemLoad) {
    updateBasicMetrics(cpuUsage, memoryUsage, weight);
    updateSystemLoadMetrics(systemLoad, historicalSystemLoad, currentSystemLoad);
  }

  // Getters for backward compatibility
  public Gauge getCpuUsageGauge() {
    return getGauge(CPU_USAGE);
}

  public Gauge getMemoryUsageGauge() {
    return getGauge(MEMORY_USAGE);
}

  public Gauge getWeightGauge() {
    return getGauge(WEIGHT);
}

  public Gauge getSystemLoadGauge() {
    return getGauge(SYSTEM_LOAD);
}

  public Gauge getHistoricalSystemLoadGauge() {
    return getGauge(HISTORICAL_SYSTEM_LOAD);
}

  public Gauge getCurrentSystemLoadGauge() {
    return getGauge(CURRENT_SYSTEM_LOAD);
}
}
