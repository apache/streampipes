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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load Balancer Statistics. Follows the same pattern as ElementServiceStats.
 */
public class LoadBalancerStats {

  private static final Logger logger = LoggerFactory.getLogger(LoadBalancerStats.class);

  private final LoadBalancerMetrics metrics;

  public LoadBalancerStats() {
    this.metrics = new LoadBalancerMetrics();
    logger.info("Initializing LoadBalancerStats");
  }

  /**
   * Update all metrics for a specific service
   * 
   * @param serviceId Service ID
   * @param adapterCount Number of adapters for this service
   * @param pipelineCount Number of pipelines for this service
   */
  public void updateAllMetrics(String serviceId, int adapterCount, int pipelineCount) {
    metrics.reportMetrics(serviceId, adapterCount, pipelineCount);
  }

  public void reportMigrationTime(double seconds) {
    metrics.reportMigrationTime(seconds);
  }
}
