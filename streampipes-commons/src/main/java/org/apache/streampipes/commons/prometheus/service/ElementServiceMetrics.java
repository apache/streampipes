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
 * Service Metrics Manager Inherits PrometheusMetrics and implements service-related metric
 * registration
 */
public class ElementServiceMetrics {

  public static final Gauge CPU_USAGE = StreamPipesCollectorRegistry
      .registerGauge("cpu_usage", "Element CPU usage percentage", "serviceId");

  public static final Gauge MEMORY_USAGE = StreamPipesCollectorRegistry
      .registerGauge("memory_usage", "Element memory usage in bytes", "serviceId");
  public static final Gauge WEIGHT = StreamPipesCollectorRegistry
      .registerGauge("weight", "Weight of remaining available resources for element", "serviceId");
  public static final Gauge SYSTEM_LOAD = StreamPipesCollectorRegistry
      .registerGauge("system_load", "System load average over the last minute", "serviceId");
  public static final Gauge HISTORICAL_SYSTEM_LOAD = StreamPipesCollectorRegistry
      .registerGauge("historical_system_load", "Historical system load average", "serviceId");

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
  }
}
