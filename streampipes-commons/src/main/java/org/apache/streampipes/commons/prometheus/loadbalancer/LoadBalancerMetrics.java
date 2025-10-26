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
 * Load Balancer Metrics Manager.
 * Global static metrics for the load balancer (no instance needed).
 * Service-specific metrics use labels to distinguish between services.
 */
public class LoadBalancerMetrics {

  // Pipeline separation and migration metrics
  public static final Counter PIPELINE_SEPARATIONS_TOTAL = StreamPipesCollectorRegistry.registerCounter(
      "lb_pipeline_separations_total",
      "Total number of pipeline separations performed",
      "serviceId"
  );

  public static final Counter PIPELINE_MIGRATIONS_TOTAL = StreamPipesCollectorRegistry.registerCounter(
      "lb_pipeline_migrations_total",
      "Total number of pipeline migrations performed",
      "serviceId"
  );

  public static final Gauge PIPELINE_SEPARATION_RATE = StreamPipesCollectorRegistry.registerGauge(
      "lb_pipeline_separation_rate",
      "Rate of pipeline separations per second",
      "serviceId"
  );

  public static final Gauge PIPELINE_MIGRATION_RATE = StreamPipesCollectorRegistry.registerGauge(
      "lb_pipeline_migration_rate",
      "Rate of pipeline migrations per second",
      "serviceId"
  );

  // Service resource metrics
  public static final Gauge SERVICE_ADAPTER_COUNT = StreamPipesCollectorRegistry.registerGauge(
      "lb_service_adapter_count",
      "Number of adapters in each extension service",
      "serviceId", "serviceType"
  );

  public static final Gauge SERVICE_PIPELINE_COUNT = StreamPipesCollectorRegistry.registerGauge(
      "lb_service_pipeline_count",
      "Number of pipelines in each extension service",
      "serviceId", "serviceType"
  );

  public static final Gauge SERVICE_LOAD_WEIGHT = StreamPipesCollectorRegistry.registerGauge(
      "lb_service_load_weight",
      "Load weight of each extension service",
      "serviceId", "serviceType"
  );

  // Load shedding metrics
  public static final Counter LOAD_SHEDDING_OPERATIONS_TOTAL = StreamPipesCollectorRegistry.registerCounter(
      "lb_load_shedding_operations_total",
      "Total number of load shedding operations performed"
  );

  public static final Gauge LOAD_SHEDDING_RATE = StreamPipesCollectorRegistry.registerGauge(
      "lb_load_shedding_rate",
      "Rate of load shedding operations per second"
  );


  /**
   * Report pipeline separation metrics.
   *
   * @param serviceId the service identifier
   * @param rate the separation rate
   */
  public static void reportPipelineSeparation(String serviceId, double rate) {
    PIPELINE_SEPARATIONS_TOTAL.labels(serviceId).inc();
    PIPELINE_SEPARATION_RATE.labels(serviceId).set(rate);
  }

  /**
   * Report pipeline migration metrics.
   *
   * @param serviceId the service identifier
   * @param rate the migration rate
   */
  public static void reportPipelineMigration(String serviceId, double rate) {
    PIPELINE_MIGRATIONS_TOTAL.labels(serviceId).inc();
    PIPELINE_MIGRATION_RATE.labels(serviceId).set(rate);
  }

  /**
   * Report service resource metrics.
   *
   * @param serviceId the service identifier
   * @param serviceType the service type
   * @param adapterCount the adapter count
   * @param pipelineCount the pipeline count
   * @param loadWeight the load weight
   */
  public static void reportServiceResources(String serviceId, String serviceType,
                                          int adapterCount, int pipelineCount, double loadWeight) {
    SERVICE_ADAPTER_COUNT.labels(serviceId, serviceType).set(adapterCount);
    SERVICE_PIPELINE_COUNT.labels(serviceId, serviceType).set(pipelineCount);
    SERVICE_LOAD_WEIGHT.labels(serviceId, serviceType).set(loadWeight);
  }

  /**
   * Report load shedding metrics.
   *
   * @param rate the load shedding rate
   */
  public static void reportLoadShedding(double rate) {
    LOAD_SHEDDING_OPERATIONS_TOTAL.inc();
    LOAD_SHEDDING_RATE.set(rate);
  }

}