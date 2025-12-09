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
package org.apache.streampipes.commons.prometheus.loadbalancer;

import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

import io.prometheus.client.Gauge;

/**
 * Load Balancer Metrics Manager. Follows the same pattern as ElementServiceMetrics.
 */
public class LoadBalancerMetrics {

  @Deprecated
  public static final Gauge SERVICE_ADAPTER_COUNT_LEGACY = StreamPipesCollectorRegistry
      .registerGauge("lb_service_adapter_count", "DEPRECATED: Use sp_extension_adapter_count_total instead. Number of adapters in each extension service",
                     "serviceId");
  @Deprecated
  public static final Gauge SERVICE_PIPELINE_COUNT_LEGACY = StreamPipesCollectorRegistry
      .registerGauge("lb_service_pipeline_count", "DEPRECATED: Use sp_extension_pipeline_count_total instead.Number of pipelines in each extension service",
                     "serviceId");
  @Deprecated
  public static final Gauge MIGRATION_TIME_SECONDS_LEGACY = StreamPipesCollectorRegistry
      .registerGauge("lb_migration_time_seconds", "DEPRECATED: Use sp_core_migration_time_seconds instead. Time taken for pipeline migration in seconds");
  
  public static final Gauge SERVICE_ADAPTER_COUNT = StreamPipesCollectorRegistry
      .registerGauge("sp_extension_adapter_count_total", "Number of adapters in each extension service",
                     "serviceId");
  public static final Gauge SERVICE_PIPELINE_COUNT = StreamPipesCollectorRegistry
      .registerGauge("sp_extension_pipeline_count_total", "Number of pipelines in each extension service",
                     "serviceId");
  public static final Gauge MIGRATION_TIME_SECONDS = StreamPipesCollectorRegistry
      .registerGauge("sp_core_migration_time_seconds", "Time taken for pipeline migration in seconds");

  public LoadBalancerMetrics() {}

  public void reportMetrics(String serviceId, int serviceAdapterCount, int servicePipelineCount) {
    SERVICE_ADAPTER_COUNT.labels(serviceId).set(serviceAdapterCount);
    SERVICE_PIPELINE_COUNT.labels(serviceId).set(servicePipelineCount);

    SERVICE_ADAPTER_COUNT_LEGACY.labels(serviceId).set(serviceAdapterCount);
    SERVICE_PIPELINE_COUNT_LEGACY.labels(serviceId).set(servicePipelineCount);
  }

  public void reportMigrationTime(double seconds) {
    MIGRATION_TIME_SECONDS.set(seconds);
    MIGRATION_TIME_SECONDS_LEGACY.set(seconds);
  }
}
