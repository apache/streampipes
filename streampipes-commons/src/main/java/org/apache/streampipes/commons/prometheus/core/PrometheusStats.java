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

package org.apache.streampipes.commons.prometheus.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Unified Prometheus Statistics Manager
 * Uses a Map<serviceId, Stats> to store all statistics, with metrics as internal objects of stats
 */
public abstract class PrometheusStats {
    
  private static final Map<String, PrometheusStats> allStats = new ConcurrentHashMap<>();

  private final String id;
  private final PrometheusMetrics metrics;

  public PrometheusStats(String serviceId) {
    this.id = validateId(serviceId);
    this.metrics = createMetrics();
    // Register to global Map
    allStats.put(serviceId, this);
  }

  /**
   * Create metrics manager
   * Subclasses need to implement this method to create specific metrics managers
   * @return Metrics manager
   */
  protected abstract PrometheusMetrics createMetrics();

  /**
   * Validate ID
   * @param id Statistics ID
   * @return Validated ID
   */
  private String validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("ID cannot be null or empty");
    }
    return id.trim();
  }

  /**
   * Increment Counter value
   * @param name Metric name
   * @param value Increment value
   */
  public void incrementCounter(String name, double value) {
    metrics.incrementCounter(name, value);
  }

  /**
   * Set Gauge value
   * @param name Metric name
   * @param value Metric value
   */
  public void setGaugeValue(String name, double value) {
    metrics.setGaugeValue(name, value);
  }

  /**
   * Observe Histogram value
   * @param name Metric name
   * @param value Observed value
   */
  public void observeHistogram(String name, double value) {
    metrics.observeHistogram(name, value);
  }

  /**
   * Observe Summary value
   * @param name Metric name
   * @param value Observed value
   */
  public void observeSummary(String name, double value) {
    metrics.observeSummary(name, value);
  }

  /**
   * Get Counter metric
   * @param name Metric name
   * @return Counter metric
   */
  public io.prometheus.client.Counter getCounter(String name) {
    return metrics.getCounter(name);
  }

  /**
   * Get Gauge metric
   * @param name Metric name
   * @return Gauge metric
   */
  public io.prometheus.client.Gauge getGauge(String name) {
    return metrics.getGauge(name);
  }

  /**
   * Get Histogram metric
   * @param name Metric name
   * @return Histogram metric
   */
  public io.prometheus.client.Histogram getHistogram(String name) {
    return metrics.getHistogram(name);
  }

  /**
   * Get Summary metric
   * @param name Metric name
   * @return Summary metric
   */
  public io.prometheus.client.Summary getSummary(String name) {
    return metrics.getSummary(name);
  }

  /**
   * Check if metric exists
   * @param name Metric name
   * @return Whether exists
   */
  public boolean hasMetric(String name) {
    return metrics.hasMetric(name);
  }

  /**
   * Remove statistics
   */
  public void remove() {
    allStats.remove(id);
    if (metrics != null) {
      metrics.remove();
    }
  }

  /**
   * Update all statistics metrics
   */
  public static void updateAllMetrics() {
    for (Map.Entry<String, PrometheusStats> entry : allStats.entrySet()) {
      PrometheusStats stats = entry.getValue();
      if (stats != null) {
        // Specific metric update logic can be added here
      }
    }
  }

  /**
   * Get statistics
   * @param serviceId Service ID
   * @return Statistics
   */
  public static PrometheusStats getStats(String serviceId) {
    return allStats.get(serviceId);
  }

  /**
   * Get metrics manager
   * @param serviceId Service ID
   * @return Metrics manager
   */
  public static PrometheusMetrics getMetrics(String serviceId) {
    PrometheusStats stats = allStats.get(serviceId);
    return stats != null ? stats.metrics : null;
  }

  /**
   * Get statistics count
   * @return Statistics count
   */
  public static int getStatsCount() {
    return allStats.size();
  }

  /**
   * Get all statistics IDs
   * @return Set of statistics IDs
   */
  public static java.util.Set<String> getAllStatsIds() {
    return allStats.keySet();
  }

  /**
   * Check if statistics exist
   * @param serviceId Service ID
   * @return Whether exists
   */
  public static boolean hasStats(String serviceId) {
    return allStats.containsKey(serviceId);
  }

  /**
   * Clear all statistics
   */
  public static void clearAllStats() {
    for (PrometheusStats stats : allStats.values()) {
      stats.remove();
    }
    allStats.clear();
  }

  /**
   * Get ID
   * @return ID
   */
  public String getId() {
    return id;
  }

  /**
   * Get metrics manager
   * @return Metrics manager
   */
  public PrometheusMetrics getMetrics() {
    return metrics;
  }
}
