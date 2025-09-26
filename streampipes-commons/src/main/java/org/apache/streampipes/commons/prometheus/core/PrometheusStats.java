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
 * Provides reusable statistics management functionality, eliminating duplicate code
 */
public abstract class PrometheusStats<T extends PrometheusMetrics> {
    
  protected static final Map<String, PrometheusStats<?>> allStats = new ConcurrentHashMap<>();
  protected static final Map<String, PrometheusMetrics> allMetrics = new ConcurrentHashMap<>();

  public final String id;
  public final T metrics;

  public PrometheusStats(String id) {
    this.id = validateId(id);
    this.metrics = createMetrics();

    // Register to global Map
    allStats.put(id, this);
    allMetrics.put(id, metrics);
  }

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
   * Create metrics manager
   * @return Metrics manager
   */
  protected abstract T createMetrics();

  /**
   * Update metrics
   * Subclasses need to implement this method to update specific metrics
   */
  protected abstract void updateMetrics();

  /**
   * Remove statistics
   */
  public void remove() {
    allStats.remove(id);
    PrometheusMetrics metrics = allMetrics.remove(id);
    if (metrics != null) {
      metrics.remove();
    }
  }

  /**
   * Update metrics for all statistics
   */
  public static void updateAllMetrics() {
    for (Map.Entry<String, PrometheusStats<?>> entry : allStats.entrySet()) {
      PrometheusStats<?> stats = entry.getValue();
      PrometheusMetrics metrics = allMetrics.get(stats.id);
      if (metrics != null) {
        stats.updateMetrics();
      }
    }
  }

  /**
   * Get statistics
   * @param id Statistics ID
   * @return Statistics
   */
@SuppressWarnings("unchecked")
  public static <T extends PrometheusStats<?>> T getStats(String id) {
    return (T) allStats.get(id);
}

  /**
   * Get metrics manager
   * @param id Metrics ID
   * @return Metrics manager
   */
  public static PrometheusMetrics getMetrics(String id) {
    return allMetrics.get(id);
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
   * @return Statistics ID set
   */
  public static java.util.Set<String> getAllStatsIds() {
    return allStats.keySet();
}

  /**
   * Check if statistics exist
   * @param id Statistics ID
   * @return Whether exists
   */
  public static boolean hasStats(String id) {
    return allStats.containsKey(id);
}

  /**
   * Clear all statistics
   */
  public static void clearAllStats() {
    for (PrometheusStats<?> stats : allStats.values()) {
      stats.remove();
    }
    allStats.clear();
    allMetrics.clear();
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
public T getMetrics() {
    return metrics;
}
}
