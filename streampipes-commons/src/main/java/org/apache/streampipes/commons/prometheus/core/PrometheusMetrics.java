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

import io.prometheus.client.Gauge;
import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Unified Prometheus Metrics Manager
 * Provides reusable metrics management functionality, eliminating duplicate code
 */
public abstract class PrometheusMetrics {

  protected final Map<String, Gauge> gauges = new ConcurrentHashMap<>();

  protected final String id;
  protected final String shortId;

  public PrometheusMetrics(String id) {
    this.id = validateId(id);
    this.shortId = extractShortId(id);
    registerGauges();
  }

  /**
   * Validate ID
   * @param id Metrics ID
   * @return Validated ID
   */
  private String validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("ID cannot be null or empty");
    }
    return id.trim();
  }

  /**
   * Extract short ID (last 6 characters)
   * @param id Full ID
   * @return Short ID
   */
  private String extractShortId(String id) {
    if (id.length() <= 6) {
      return id;
    }
    return id.substring(id.length() - 6);
  }

  /**
   * Register all Gauge metrics
   * Subclasses need to implement this method to register specific metrics
   */
  protected abstract void registerGauges();

  /**
   * Register single Gauge metric
   * @param name Metric name
   * @param help Help text
   * @return Registered Gauge
   */
  protected Gauge registerGauge(String name, String help) {
    String fullName = name + "_" + shortId;
    Gauge gauge = StreamPipesCollectorRegistry.registerGauge(fullName, help + " " + shortId);
    gauges.put(name, gauge);
    return gauge;
  }

  /**
   * Set metric value
   * @param name Metric name
   * @param value Metric value
   */
  protected void setGaugeValue(String name, double value) {
    Gauge gauge = gauges.get(name);
    if (gauge != null) {
      gauge.set(value);
    }
  }

  /**
   * Remove all metrics
   */
  public void remove() {
    for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
      Gauge gauge = entry.getValue();
      gauge.set(0);
      StreamPipesCollectorRegistry.remove(gauge);
    }
    gauges.clear();
  }

  /**
   * Get ID
   * @return ID
   */
  public String getId() {
    return id;
}

  /**
   * Get short ID
   * @return Short ID
   */
  public String getShortId() {
    return shortId;
}

  /**
   * Get number of registered metrics
   * @return Number of metrics
   */
  public int getMetricsCount() {
    return gauges.size();
}

  /**
   * Check if metric is registered
   * @param name Metric name
   * @return Whether registered
   */
  public boolean hasMetric(String name) {
    return gauges.containsKey(name);
}

  /**
   * Get metric
   * @param name Metric name
   * @return Metric
   */
  public Gauge getGauge(String name) {
    return gauges.get(name);
}
}
