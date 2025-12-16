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

import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

import io.prometheus.client.Gauge;

/**
 * Service Metrics Manager Inherits PrometheusMetrics and implements
 * service-related metric
 * registration
 */
public class ElementServiceMetrics {
  @Deprecated
  public static final Gauge CPU_USAGE_LEGACY = StreamPipesCollectorRegistry
      .registerGauge("cpu_usage", "DEPRECATED: Use sp_extension_cpu_usage_percentage instead. Element CPU usage percentage", "serviceId");
  @Deprecated
  public static final Gauge MEMORY_USAGE_LEGACY = StreamPipesCollectorRegistry
      .registerGauge("memory_usage", "DEPRECATED: Use sp_extension_memory_usage_bytes instead. Element memory usage in bytes", "serviceId");
  @Deprecated
  public static final Gauge WEIGHT_LEGACY = StreamPipesCollectorRegistry
      .registerGauge("weight", "DEPRECATED: Use sp_extension_weight_count_total instead. Weight of remaining available resources for element", "serviceId");
  @Deprecated
  public static final Gauge SYSTEM_LOAD_LEGACY = StreamPipesCollectorRegistry
      .registerGauge("system_load", "DEPRECATED: Use sp_extension_system_load_last_minute instead. System load average over the last minute", "serviceId");
  @Deprecated
  public static final Gauge HISTORICAL_SYSTEM_LOAD_LEGACY = StreamPipesCollectorRegistry
      .registerGauge("historical_system_load", "DEPRECATED: Use sp_extension_system_load_historic_average instead. Historical system load average", "serviceId");

  public static final Gauge CPU_USAGE = StreamPipesCollectorRegistry
      .registerGauge("sp_extension_cpu_usage_percentage", "Element CPU usage percentage", "serviceId");
  public static final Gauge MEMORY_USAGE = StreamPipesCollectorRegistry
      .registerGauge("sp_extension_memory_usage_bytes", "Element memory usage in bytes", "serviceId");
  public static final Gauge WEIGHT = StreamPipesCollectorRegistry
      .registerGauge("sp_extension_weight_count_total", "Weight of remaining available resources for element",
          "serviceId");

  public static final Gauge SYSTEM_LOAD = StreamPipesCollectorRegistry
      .registerGauge("sp_extension_system_load_last_minute", "System load average over the last minute", "serviceId");

  public static final Gauge HISTORICAL_SYSTEM_LOAD = StreamPipesCollectorRegistry
      .registerGauge("sp_extension_system_load_historic_average", "Historical system load average", "serviceId");

  private final String id;

  public ElementServiceMetrics(String id) {
    this.id = id;
  }

  public void reportMetrics(double cpuUsage, double memoryUsage, double weight, double systemLoad,
      double historicalSystemLoad) {
    CPU_USAGE.labels(this.id).set(cpuUsage);
    MEMORY_USAGE.labels(this.id).set(memoryUsage);
    WEIGHT.labels(this.id).set(weight);
    SYSTEM_LOAD.labels(this.id).set(systemLoad);
    HISTORICAL_SYSTEM_LOAD.labels(this.id).set(historicalSystemLoad);

    CPU_USAGE_LEGACY.labels(this.id).set(cpuUsage);
    MEMORY_USAGE_LEGACY.labels(this.id).set(memoryUsage);
    WEIGHT_LEGACY.labels(this.id).set(weight);
    SYSTEM_LOAD_LEGACY.labels(this.id).set(systemLoad);
    HISTORICAL_SYSTEM_LOAD_LEGACY.labels(this.id).set(historicalSystemLoad);
  }
}
