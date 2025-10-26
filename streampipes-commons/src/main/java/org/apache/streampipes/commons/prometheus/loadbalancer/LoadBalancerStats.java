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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load Balancer Statistics.
 * Follows the same pattern as ElementServiceStats for consistency.
 */
public class LoadBalancerStats {

  private static final Logger logger = LoggerFactory.getLogger(LoadBalancerStats.class);


  // Pipeline operation statistics
  private double pipelineSeparationRate = 0.0;
  private double pipelineMigrationRate = 0.0;
  private double loadSheddingRate = 0.0;

  // Service resource statistics
  private final Map<String, ServiceResourceStats> serviceStats = new ConcurrentHashMap<>();

  // Timing and counters for rate calculations
  private final AtomicLong lastSeparationTime = new AtomicLong(System.currentTimeMillis());
  private final AtomicLong lastMigrationTime = new AtomicLong(System.currentTimeMillis());
  private final AtomicLong lastLoadSheddingTime = new AtomicLong(System.currentTimeMillis());
  private final AtomicLong separationCount = new AtomicLong(0);
  private final AtomicLong migrationCount = new AtomicLong(0);
  private final AtomicLong loadSheddingCount = new AtomicLong(0);

  public LoadBalancerStats() {
    // Initialize with default values to ensure metrics are always present
    logger.info("Initializing LoadBalancerStats with default values");

    // Set initial rate values to 0
    this.pipelineSeparationRate = 0.0;
    this.pipelineMigrationRate = 0.0;
    this.loadSheddingRate = 0.0;

    // Initialize timing to current time
    long currentTime = System.currentTimeMillis();
    lastSeparationTime.set(currentTime);
    lastMigrationTime.set(currentTime);
    lastLoadSheddingTime.set(currentTime);

    // Report initial metrics to ensure they appear in Prometheus
    reportInitialMetrics();
  }

  /**
   * Report initial metrics to ensure they are visible in Prometheus.
   */
  private void reportInitialMetrics() {
    try {
      // Report initial rate metrics with 0 values
      LoadBalancerMetrics.reportPipelineSeparation("system", 0.0);
      LoadBalancerMetrics.reportPipelineMigration("system", 0.0);
      LoadBalancerMetrics.reportLoadShedding(0.0);

      // Report initial service resource metrics with default values
      LoadBalancerMetrics.reportServiceResources("system", "core", 0, 0, 0.0);

      // Force immediate metric update
      LoadBalancerMetrics.PIPELINE_SEPARATION_RATE.labels("system").set(0.0);
      LoadBalancerMetrics.PIPELINE_MIGRATION_RATE.labels("system").set(0.0);
      LoadBalancerMetrics.LOAD_SHEDDING_RATE.set(0.0);
      LoadBalancerMetrics.SERVICE_ADAPTER_COUNT.labels("system", "core").set(0);
      LoadBalancerMetrics.SERVICE_PIPELINE_COUNT.labels("system", "core").set(0);
      LoadBalancerMetrics.SERVICE_LOAD_WEIGHT.labels("system", "core").set(0.0);

      logger.info("Initial load balancer metrics reported to Prometheus");
    } catch (Exception e) {
      logger.warn("Failed to report initial metrics: {}", e.getMessage());
    }
  }


  /**
   * Update pipeline separation rate.
   *
   * @param rate the separation rate
   */
  public void setPipelineSeparationRate(double rate) {
    this.pipelineSeparationRate = rate;
  }

  /**
   * Update pipeline migration rate.
   *
   * @param rate the migration rate
   */
  public void setPipelineMigrationRate(double rate) {
    this.pipelineMigrationRate = rate;
  }

  /**
   * Update load shedding rate.
   *
   * @param rate the load shedding rate
   */
  public void setLoadSheddingRate(double rate) {
    this.loadSheddingRate = rate;
  }


  /**
   * Update service resource statistics.
   *
   * @param serviceId the service identifier
   * @param serviceType the service type
   * @param adapterCount the adapter count
   * @param pipelineCount the pipeline count
   * @param loadWeight the load weight
   */
  public void updateServiceResources(String serviceId, String serviceType,
                                   int adapterCount, int pipelineCount, double loadWeight) {
    serviceStats.put(serviceId, new ServiceResourceStats(serviceId, serviceType,
                                                       adapterCount, pipelineCount, loadWeight));
  }

  /**
   * Report pipeline separation.
   *
   * @param serviceId the service identifier
   */
  public void reportPipelineSeparation(String serviceId) {
    // Increment counter and calculate rate
    separationCount.incrementAndGet();
    double currentRate = calculateSeparationRate();
    LoadBalancerMetrics.reportPipelineSeparation(serviceId, currentRate);
  }

  /**
   * Report pipeline migration.
   *
   * @param serviceId the service identifier
   */
  public void reportPipelineMigration(String serviceId) {
    // Increment counter and calculate rate
    migrationCount.incrementAndGet();
    double currentRate = calculateMigrationRate();
    LoadBalancerMetrics.reportPipelineMigration(serviceId, currentRate);
  }

  /**
   * Report load shedding.
   */
  public void reportLoadShedding() {
    // Increment counter and calculate rate
    loadSheddingCount.incrementAndGet();
    double currentRate = calculateLoadSheddingRate();
    LoadBalancerMetrics.reportLoadShedding(currentRate);
  }


  /**
   * Update all metrics.
   */
  public void updateAllMetrics() {
    // Update service resource metrics
    for (ServiceResourceStats stats : serviceStats.values()) {
      LoadBalancerMetrics.reportServiceResources(stats.serviceId, stats.serviceType,
                                               stats.adapterCount, stats.pipelineCount, stats.loadWeight);
    }

    // Update rate metrics even if no new events occurred
    // This ensures the metrics are always present in Prometheus
    updateRateMetrics();

    // Ensure all metrics have at least default values
    ensureAllMetricsHaveValues();
  }

  /**
   * Ensure all metrics have at least default values to prevent "No data" in Grafana.
   */
  private void ensureAllMetricsHaveValues() {
    try {
      // Always report system metrics to ensure they are visible
      LoadBalancerMetrics.reportServiceResources("system", "core", 0, 0, 0.0);

      // Ensure rate metrics are always present
      LoadBalancerMetrics.PIPELINE_SEPARATION_RATE.labels("system").set(0.0);
      LoadBalancerMetrics.PIPELINE_MIGRATION_RATE.labels("system").set(0.0);
      LoadBalancerMetrics.LOAD_SHEDDING_RATE.set(0.0);

      // If no service stats exist, report default system metrics
      if (serviceStats.isEmpty()) {
        logger.debug("No service stats available, using system defaults");
      } else {
        logger.debug("Found {} service stats, reporting them", serviceStats.size());
      }

      logger.debug("Ensured all load balancer metrics have values");
    } catch (Exception e) {
      logger.warn("Failed to ensure metric values: {}", e.getMessage());
    }
  }

  /**
   * Update rate metrics to ensure they are always present.
   */
  private void updateRateMetrics() {
    // Update separation rate
    double separationRate = calculateSeparationRate();
    if (separationRate > 0) {
      pipelineSeparationRate = separationRate;
    }

    // Update migration rate
    double migrationRate = calculateMigrationRate();
    if (migrationRate > 0) {
      pipelineMigrationRate = migrationRate;
    }

    // Update load shedding rate
    double loadSheddingRate = calculateLoadSheddingRate();
    if (loadSheddingRate > 0) {
      this.loadSheddingRate = loadSheddingRate;
    }
  }

  // Getters

  /**
   * Get the pipeline separation rate.
   *
   * @return the pipeline separation rate
   */
  public double getPipelineSeparationRate() {
    return pipelineSeparationRate;
  }

  /**
   * Get the pipeline migration rate.
   *
   * @return the pipeline migration rate
   */
  public double getPipelineMigrationRate() {
    return pipelineMigrationRate;
  }

  /**
   * Get the load shedding rate.
   *
   * @return the load shedding rate
   */
  public double getLoadSheddingRate() {
    return loadSheddingRate;
  }


  /**
   * Get the service statistics map.
   *
   * @return the service statistics map
   */
  public Map<String, ServiceResourceStats> getServiceStats() {
    return serviceStats;
  }
  
  /**
   * Calculate pipeline separation rate per second.
   *
   * @return the separation rate
   */
  private double calculateSeparationRate() {
    long currentTime = System.currentTimeMillis();
    long timeDiff = currentTime - lastSeparationTime.get();
    if (timeDiff > 1000) { // Only calculate rate if at least 1 second has passed
      double rate = (double) separationCount.get() * 1000.0 / timeDiff;
      lastSeparationTime.set(currentTime);
      separationCount.set(0);
      return rate;
    }
    return 0.0;
  }

  /**
   * Calculate pipeline migration rate per second.
   *
   * @return the migration rate
   */
  private double calculateMigrationRate() {
    long currentTime = System.currentTimeMillis();
    long timeDiff = currentTime - lastMigrationTime.get();
    if (timeDiff > 1000) { // Only calculate rate if at least 1 second has passed
      double rate = (double) migrationCount.get() * 1000.0 / timeDiff;
      lastMigrationTime.set(currentTime);
      migrationCount.set(0);
      return rate;
    }
    return 0.0;
  }

  /**
   * Calculate load shedding rate per second.
   *
   * @return the load shedding rate
   */
  private double calculateLoadSheddingRate() {
    long currentTime = System.currentTimeMillis();
    long timeDiff = currentTime - lastLoadSheddingTime.get();
    if (timeDiff > 1000) { // Only calculate rate if at least 1 second has passed
      double rate = (double) loadSheddingCount.get() * 1000.0 / timeDiff;
      lastLoadSheddingTime.set(currentTime);
      loadSheddingCount.set(0);
      return rate;
    }
    return 0.0;
  }

  /**
   * Service resource statistics holder.
   *
   * @param serviceId the service identifier
   * @param serviceType the service type
   * @param adapterCount the adapter count
   * @param pipelineCount the pipeline count
   * @param loadWeight the load weight
   */
  public record ServiceResourceStats(String serviceId, String serviceType, int adapterCount, int pipelineCount,
                                         double loadWeight) {
  }
}