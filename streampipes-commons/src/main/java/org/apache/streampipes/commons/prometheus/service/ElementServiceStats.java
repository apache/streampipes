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

import org.apache.streampipes.commons.prometheus.core.PrometheusStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Service Statistics Manager
 * Inherits unified statistics manager, eliminating duplicate code
 */
public class ElementServiceStats extends PrometheusStats<ElementServiceMetrics> {
    
private static final Logger log = LoggerFactory.getLogger(ElementServiceStats.class);

public double cpuUsage = 0.0;
public double memoryUsage = 0.0;
public double weight = 1.0;
public double systemLoad = 0.0;
public double historicalSystemLoad = 0.0;
public double currentSystemLoad = 0.0;

public ElementServiceStats(String id) {
    super(id);
}

  @Override
  protected ElementServiceMetrics createMetrics() {
    return new ElementServiceMetrics(id);
}

  @Override
  protected void updateMetrics() {
    metrics.updateAllMetrics(cpuUsage, memoryUsage, weight, systemLoad, historicalSystemLoad, currentSystemLoad);
  }

  /**
   * Update CPU usage
   * @param cpuUsage CPU usage
   */
  public void setCpuUsage(double cpuUsage) {
    this.cpuUsage = cpuUsage;
    metrics.updateCpuUsage(cpuUsage);
  }

  /**
   * Update memory usage
   * @param memoryUsage Memory usage
   */
  public void setMemoryUsage(double memoryUsage) {
    this.memoryUsage = memoryUsage;
    metrics.updateMemoryUsage(memoryUsage);
  }

  /**
   * Update weight
   * @param weight Weight
   */
  public void setWeight(double weight) {
    this.weight = weight;
    metrics.updateWeight(weight);
  }

  /**
   * Update system load
   * @param systemLoad System load
   */
  public void setSystemLoad(double systemLoad) {
    this.systemLoad = systemLoad;
    metrics.updateSystemLoad(systemLoad);
  }

  /**
   * Update historical system load
   * @param historicalSystemLoad Historical system load
   */
  public void setHistoricalSystemLoad(double historicalSystemLoad) {
    this.historicalSystemLoad = historicalSystemLoad;
    metrics.updateHistoricalSystemLoad(historicalSystemLoad);
  }

  /**
   * Update current system load
   * @param currentSystemLoad Current system load
   */
  public void setCurrentSystemLoad(double currentSystemLoad) {
    this.currentSystemLoad = currentSystemLoad;
    metrics.updateCurrentSystemLoad(currentSystemLoad);
  }

  /**
   * Update basic metrics
   * @param cpuUsage CPU usage
   * @param memoryUsage Memory usage
   * @param weight Weight
   */
  public void updateBasicMetrics(double cpuUsage, double memoryUsage, double weight) {
    this.cpuUsage = cpuUsage;
    this.memoryUsage = memoryUsage;
    this.weight = weight;
    metrics.updateBasicMetrics(cpuUsage, memoryUsage, weight);
  }

  /**
   * Update system load metrics
   * @param systemLoad System load
   * @param historicalSystemLoad Historical system load
   * @param currentSystemLoad Current system load
   */
  public void updateSystemLoadMetrics(double systemLoad, double historicalSystemLoad, double currentSystemLoad) {
    this.systemLoad = systemLoad;
    this.historicalSystemLoad = historicalSystemLoad;
    this.currentSystemLoad = currentSystemLoad;
    metrics.updateSystemLoadMetrics(systemLoad, historicalSystemLoad, currentSystemLoad);
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
    this.cpuUsage = cpuUsage;
    this.memoryUsage = memoryUsage;
    this.weight = weight;
    this.systemLoad = systemLoad;
    this.historicalSystemLoad = historicalSystemLoad;
    this.currentSystemLoad = currentSystemLoad;
    metrics.updateAllMetrics(cpuUsage, memoryUsage, weight, systemLoad, historicalSystemLoad, currentSystemLoad);
  }

  // Getters
  public double getCpuUsage() {
    return cpuUsage;
}

  public double getMemoryUsage() {
    return memoryUsage;
}

  public double getWeight() {
    return weight;
}

  public double getSystemLoad() {
    return systemLoad;
}

  public double getHistoricalSystemLoad() {
    return historicalSystemLoad;
}

  public double getCurrentSystemLoad() {
    return currentSystemLoad;
}

  /**
   * Update metrics by report (backward compatibility)
   * @param serviceId Service ID
   * @param cpuUsage CPU usage
   * @param memoryUsage Memory usage
   * @param weight Weight
   */
  public static void metricsByReport(String serviceId, double cpuUsage, double memoryUsage, double weight) {
    ElementServiceStats stats = getOrCreateStats(serviceId);
    stats.updateBasicMetrics(cpuUsage, memoryUsage, weight);
    updateServiceCount();
  }

  /**
   * Update all metrics (backward compatibility)
   */
  public static void metrics() {
    PrometheusStats.updateAllMetrics();
    updateServiceCount();
  }

  /**
   * Get or create statistics
   * @param serviceId Service ID
   * @return Statistics
   */
@SuppressWarnings("unchecked")
  private static ElementServiceStats getOrCreateStats(String serviceId) {
    ElementServiceStats stats = (ElementServiceStats) PrometheusStats.getStats(serviceId);
    if (stats == null) {
      stats = new ElementServiceStats(serviceId);
    }
    return stats;
  }

  /**
   * Check if service exists (backward compatibility)
   * @param serviceId Service ID
   * @return Whether exists
   */
  public static boolean containsKey(String serviceId) {
    return PrometheusStats.hasStats(serviceId);
}

  /**
   * Get service statistics (backward compatibility)
   * @param serviceId Service ID
   * @return Statistics
   */
@SuppressWarnings("unchecked")
  public static ElementServiceStats get(String serviceId) {
    return (ElementServiceStats) PrometheusStats.getStats(serviceId);
  }

  /**
   * Update service count
   */
  private static void updateServiceCount() {
    ElementServiceMetrics.serviceCount.set(PrometheusStats.getStatsCount());
 }
}
