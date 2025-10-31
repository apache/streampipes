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
package org.apache.streampipes.loadbalance.impl;

import org.apache.streampipes.loadbalance.LoadBalancerConfig;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Threshold-based pipeline migrator for load balancing Migrates pipelines when services exceed
 * average load by a threshold
 */
public class ThresholdMigrator extends AbstractPipelineMigrator {

  private static final Logger logger = LoggerFactory.getLogger(ThresholdMigrator.class);
  private static final float LOAD_DIFFERENCE_THRESHOLD = 20.0f;

  @Override
  public void doLoadShedding(List<SpServiceRegistration> services) {
    if (!shouldMigrate(services)) {
      return;
    }

    // Calculate loads for all services
    ServiceLoadQueues queues = calculateServiceLoads(services);
    float averageLoad = calculateAverageLoad(queues.getLoadValues());

    // Phase 1: Migrate from services exceeding average + threshold
    migrateOverloadedServices(queues, averageLoad, services.size());

    // Phase 2: Balance underutilized and high-load services
    balanceUnderutilizedServices(queues, services.size());

    logger.debug("Threshold-based load shedding completed");
  }

  /**
   * Migrate from services that exceed average load by threshold
   * 
   * @param queues Service load queues
   * @param averageLoad Average load across all services
   * @param totalServices Total number of services
   */
  private void migrateOverloadedServices(ServiceLoadQueues queues, float averageLoad,
                                         int totalServices) {
    Queue<Map.Entry<SpServiceRegistration, Float>> overloadedServices = new LinkedList<>();
    PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxLoadQueue = queues.getMaxLoadQueue();

    // Identify services exceeding threshold
    float thresholdLoad = averageLoad + LoadBalancerConfig.thresholdMigratorPercentage;
    while (!maxLoadQueue.isEmpty() && maxLoadQueue.peek().getValue() > thresholdLoad
        && overloadedServices.size() < totalServices / 2) {
      overloadedServices.offer(maxLoadQueue.poll());
    }

    if (overloadedServices.isEmpty()) {
      logger.debug("No overloaded services found (threshold: {})", thresholdLoad);
      return;
    }

    logger.info("Found {} overloaded services above threshold {}", overloadedServices.size(),
                thresholdLoad);

    // Migrate to less loaded services
    PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minLoadQueue = queues.getMinLoadQueue();
    while (!overloadedServices.isEmpty() && !minLoadQueue.isEmpty()) {
      if (overloadedServices.peek().getKey().equals(minLoadQueue.peek().getKey())) {
        break;
      }
      executeMigration(overloadedServices.poll(), minLoadQueue.poll());
    }
  }

  /**
   * Balance services with significant load differences
   * 
   * @param queues Service load queues
   * @param totalServices Total number of services
   */
  private void balanceUnderutilizedServices(ServiceLoadQueues queues, int totalServices) {
    PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxLoadQueue = queues.getMaxLoadQueue();
    PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minLoadQueue = queues.getMinLoadQueue();

    int migrationsPerformed = 0;

    while (!maxLoadQueue.isEmpty() && !minLoadQueue.isEmpty()
        && maxLoadQueue.size() > totalServices / 2) {

      float minLoad = minLoadQueue.peek().getValue();
      float maxLoad = maxLoadQueue.peek().getValue();

      // Check if migration is beneficial
      if (minLoad >= LoadBalancerConfig.minMigratorPercentage) {
        logger.debug("Min load {} exceeds threshold, skipping balance", minLoad);
        break;
      }

      if (maxLoad <= minLoad + LOAD_DIFFERENCE_THRESHOLD) {
        logger.debug("Load difference {} too small, skipping balance", maxLoad - minLoad);
        break;
      }

      if (maxLoadQueue.peek().getKey().equals(minLoadQueue.peek().getKey())) {
        break;
      }

      executeMigration(maxLoadQueue.poll(), minLoadQueue.poll());
      migrationsPerformed++;
    }

    if (migrationsPerformed > 0) {
      logger.info("Balanced {} underutilized services", migrationsPerformed);
    }
  }
}
