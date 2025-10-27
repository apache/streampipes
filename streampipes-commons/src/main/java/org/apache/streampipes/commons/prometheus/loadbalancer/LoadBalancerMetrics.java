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

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

/**
 * Load Balancer Metrics Manager. Follows the same pattern as ElementServiceMetrics.
 */
public class LoadBalancerMetrics {
  // OK
  public static final Counter PIPELINE_SEPARATIONS_TOTAL = StreamPipesCollectorRegistry
      .registerCounter("lb_pipeline_separations_total",
                       "Total number of pipeline separations performed", "serviceId");
  // OK?
  public static final Counter PIPELINE_MIGRATIONS_TOTAL = StreamPipesCollectorRegistry
      .registerCounter("lb_pipeline_migrations_total",
                       "Total number of pipeline migrations performed", "serviceId");

  public static final Gauge SERVICE_ADAPTER_COUNT = StreamPipesCollectorRegistry
      .registerGauge("lb_service_adapter_count", "Number of adapters in each extension service",
                     "serviceId");
  // OK
  public static final Gauge SERVICE_PIPELINE_COUNT = StreamPipesCollectorRegistry
      .registerGauge("lb_service_pipeline_count", "Number of pipelines in each extension service",
                     "serviceId");

  public LoadBalancerMetrics() {}

  public void reportMetrics(String serviceId, int serviceAdapterCount, int servicePipelineCount) {
    SERVICE_ADAPTER_COUNT.labels(serviceId).set(serviceAdapterCount);
    SERVICE_PIPELINE_COUNT.labels(serviceId).set(servicePipelineCount);
  }

  /**
   * Report pipeline separation event (increment counter)
   */
  public void reportPipelineSeparation(String serviceId) {
    PIPELINE_SEPARATIONS_TOTAL.labels(serviceId).inc();
  }

  /**
   * Report pipeline migration event (increment counter)
   */
  public void reportPipelineMigration(String serviceId) {
    PIPELINE_MIGRATIONS_TOTAL.labels(serviceId).inc();
  }
}
