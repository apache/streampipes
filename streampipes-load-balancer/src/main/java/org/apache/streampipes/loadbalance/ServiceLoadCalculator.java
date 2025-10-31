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
package org.apache.streampipes.loadbalance;

import org.apache.streampipes.commons.prometheus.service.ElementServiceStats;
import org.apache.streampipes.loadbalance.service.ExtensionsServiceReportExecutor;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service load calculator for computing service load metrics. Always fetches fresh data from
 * network instead of using cache.
 */
public class ServiceLoadCalculator {

  private static final Logger logger = LoggerFactory.getLogger(ServiceLoadCalculator.class);
  /**
   * Calculate weighted load based on historical and current load.
   *
   * @param historicalLoad Historical load value
   * @param load Current load value
   * @param historicalLoadFactor Weight factor for historical load
   * @return Calculated weighted load
   */
  private static float calculate(float historicalLoad, float load, float historicalLoadFactor) {
    return (1 - historicalLoadFactor) * load + (historicalLoadFactor) * historicalLoad;
  }

  /**
   * Calculate service load from resource snapshot.
   *
   * @param snapshot Resource snapshot
   * @return Calculated service load
   */
  private static float calculateFromSnapshot(ServiceLoadDataReport.ResourceSnapshot snapshot) {
    if (snapshot == null || !snapshot.isComplete()) {
      return 0.0F;
    }

    double cpuLoad = snapshot.getCpu().percentUsage() * LoadBalancerConfig.cpuResourceWeight;
    double memoryLoad =
        snapshot.getMemory().percentUsage() * LoadBalancerConfig.memoryResourceWeight;

    return (float) Math.max(cpuLoad, memoryLoad);
  }

  /**
   * Calculate average of a list of float values.
   *
   * @param list List of float values
   * @return Average value
   */
  public static float calculateAverage(List<Float> list) {
    if (list == null || list.isEmpty()) {
      return 0.0F;
    }
    float sum = 0.0F;
    for (float value : list) {
      sum += value;
    }
    return sum / list.size();
  }

  /**
   * Calculate load for a specific service by fetching fresh data from network. Combines real-time
   * network data with historical data for smooth load calculation.
   *
   * @param service Service registration
   * @return Calculated load value
   */
  public static float calculateLoad(SpServiceRegistration service) {
    // Fetch fresh data from network (includes current and historical snapshots)
    ServiceLoadDataReport report = fetchServiceLoadFromNetwork(service);

    if (report == null || !report.isComplete()) {
      logger.warn("Failed to fetch valid load data for service {}, returning 0",
                  service.getSvcId());
      return 0.0f;
    }

    // Calculate current load from current snapshot
    float currentLoad = calculateFromSnapshot(report.getCurrent());

    // Calculate historical load from historical snapshot
    float historicalLoad;
    if (report.hasHistoricalData()) {
      ElementServiceStats tempStats = new ElementServiceStats(service.getSvcId());
      historicalLoad = calculateFromSnapshot(report.getHistorical());
      logger.debug("Using historical snapshot from report for service {}", service.getSvcId());
    } else {
      // Fallback: use stored historical load in stats
      historicalLoad = 0.0F;
      logger.debug("Using stored historical data for service {}", service.getSvcId());
    }

    // Apply exponential smoothing: weighted average of historical and current
    float smoothedLoad =
        calculate(historicalLoad, currentLoad, LoadBalancerConfig.historyResourcePercentage);

    logger.debug("Service {} load: current={}%, historical={}%, smoothed={}%", service.getSvcId(),
                 currentLoad, historicalLoad, smoothedLoad);

    return smoothedLoad;
  }

  /**
   * Fetch service load data from network in real-time.
   *
   * @param service Service registration
   * @return Service load data report, or null if fetch fails
   */
  private static ServiceLoadDataReport fetchServiceLoadFromNetwork(SpServiceRegistration service) {
    return ExtensionsServiceReportExecutor.getServiceLoadDataReport(service);
  }
}
