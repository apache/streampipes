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
 * Inherits PrometheusMetrics and implements service-related metric registration
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
  protected void registerMetrics() {
    // Register all service-related Gauge metrics
    registerGauge(CPU_USAGE, "CPU usage percentage");
    registerGauge(MEMORY_USAGE, "Memory usage in bytes");
    registerGauge(WEIGHT, "Weight of remaining available resources for service");
    registerGauge(SYSTEM_LOAD, "System load average over the last minute");
    registerGauge(HISTORICAL_SYSTEM_LOAD, "Historical system load average");
    registerGauge(CURRENT_SYSTEM_LOAD, "Current system load average");
  }
}
