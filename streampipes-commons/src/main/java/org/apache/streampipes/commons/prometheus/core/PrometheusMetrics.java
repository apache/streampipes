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

import io.prometheus.client.*;
import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Unified Prometheus Metrics Manager
 * Supports 4 types of Prometheus metrics: Counter, Gauge, Histogram, Summary
 */
public abstract class PrometheusMetrics {

  protected final Map<String, Counter> counters = new ConcurrentHashMap<>();
  protected final Map<String, Gauge> gauges = new ConcurrentHashMap<>();
  protected final Map<String, Histogram> histograms = new ConcurrentHashMap<>();
  protected final Map<String, Summary> summaries = new ConcurrentHashMap<>();

  protected final String id;
  protected final String shortId;

  public PrometheusMetrics(String id) {
    this.id = validateId(id);
    this.shortId = extractShortId(id);
    registerMetrics();
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
   * Register metrics
   * Subclasses need to implement this method to register specific metrics
   */
  protected abstract void registerMetrics();

  /**
   * Register Counter metric
   * @param name Metric name
   * @param help Help text
   * @return Registered Counter
   */
  protected Counter registerCounter(String name, String help) {
    String fullName = name + "_" + shortId;
    Counter counter = StreamPipesCollectorRegistry.registerCounter(fullName, help + " " + shortId);
    counters.put(name, counter);
    return counter;
  }

  /**
   * Register Gauge metric
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
   * Register Histogram metric
   * @param name Metric name
   * @param help Help text
   * @return Registered Histogram
   */
  protected Histogram registerHistogram(String name, String help) {
    String fullName = name + "_" + shortId;
    Histogram histogram = StreamPipesCollectorRegistry.registerHistogram(fullName, help + " " + shortId);
    histograms.put(name, histogram);
    return histogram;
  }

  /**
   * Register Summary metric
   * @param name Metric name
   * @param help Help text
   * @return Registered Summary
   */
  protected Summary registerSummary(String name, String help) {
    String fullName = name + "_" + shortId;
    Summary summary = StreamPipesCollectorRegistry.registerSummary(fullName, help + " " + shortId);
    summaries.put(name, summary);
    return summary;
  }

  /**
   * Increment Counter value
   * @param name Metric name
   * @param value Increment value
   */
  public void incrementCounter(String name, double value) {
    Counter counter = counters.get(name);
    if (counter != null) {
      counter.inc(value);
    }
  }

  /**
   * Set Gauge value
   * @param name Metric name
   * @param value Metric value
   */
  public void setGaugeValue(String name, double value) {
    Gauge gauge = gauges.get(name);
    if (gauge != null) {
      gauge.set(value);
    }
  }

  /**
   * Observe Histogram value
   * @param name Metric name
   * @param value Observed value
   */
  public void observeHistogram(String name, double value) {
    Histogram histogram = histograms.get(name);
    if (histogram != null) {
      histogram.observe(value);
    }
  }

  /**
   * Observe Summary value
   * @param name Metric name
   * @param value Observed value
   */
  public void observeSummary(String name, double value) {
    Summary summary = summaries.get(name);
    if (summary != null) {
      summary.observe(value);
    }
  }

  /**
   * Get Counter metric
   * @param name Metric name
   * @return Counter metric
   */
  public Counter getCounter(String name) {
    return counters.get(name);
  }

  /**
   * Get Gauge metric
   * @param name Metric name
   * @return Gauge metric
   */
  public Gauge getGauge(String name) {
    return gauges.get(name);
  }

  /**
   * Get Histogram metric
   * @param name Metric name
   * @return Histogram metric
   */
  public Histogram getHistogram(String name) {
    return histograms.get(name);
  }

  /**
   * Get Summary metric
   * @param name Metric name
   * @return Summary metric
   */
  public Summary getSummary(String name) {
    return summaries.get(name);
  }

  /**
   * Check if metric exists
   * @param name Metric name
   * @return Whether exists
   */
  public boolean hasMetric(String name) {
    return counters.containsKey(name) || gauges.containsKey(name) || 
           histograms.containsKey(name) || summaries.containsKey(name);
  }

  /**
   * Remove all metrics
   */
  public void remove() {
    // Remove Counter metrics
    for (Map.Entry<String, Counter> entry : counters.entrySet()) {
      StreamPipesCollectorRegistry.removeCounter(entry.getValue());
    }
    counters.clear();

    // Remove Gauge metrics
    for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
      Gauge gauge = entry.getValue();
      gauge.set(0);
      StreamPipesCollectorRegistry.removeGauge(gauge);
    }
    gauges.clear();

    // Remove Histogram metrics
    for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
      StreamPipesCollectorRegistry.removeHistogram(entry.getValue());
    }
    histograms.clear();

    // Remove Summary metrics
    for (Map.Entry<String, Summary> entry : summaries.entrySet()) {
      StreamPipesCollectorRegistry.removeSummary(entry.getValue());
    }
    summaries.clear();
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
   * Get total number of metrics
   * @return Total number of metrics
   */
  public int getMetricsCount() {
    return counters.size() + gauges.size() + histograms.size() + summaries.size();
  }

  /**
   * Get all metric names
   * @return Set of metric names
   */
  public java.util.Set<String> getAllMetricNames() {
    java.util.Set<String> allNames = new java.util.HashSet<>();
    allNames.addAll(counters.keySet());
    allNames.addAll(gauges.keySet());
    allNames.addAll(histograms.keySet());
    allNames.addAll(summaries.keySet());
    return allNames;
  }
}
