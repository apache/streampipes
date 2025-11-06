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
 * Overload-based pipeline migrator for load balancing Migrates pipelines from services exceeding
 * absolute load threshold
 */
public class OverloadMigrator extends AbstractPipelineMigrator {

  private static final Logger logger = LoggerFactory.getLogger(OverloadMigrator.class);

  @Override
  public void doLoadShedding(List<SpServiceRegistration> services) {
    if (!shouldMigrate(services)) {
      return;
    }

    // Calculate loads for all services
    ServiceLoadQueues queues = calculateServiceLoads(services);

    // Identify and migrate from overloaded services
    migrateOverloadedServices(queues);

    logger.debug("Overload-based load shedding completed");
  }

  /**
   * Migrate from services exceeding absolute overload threshold
   * 
   * @param queues Service load queues
   */
  private void migrateOverloadedServices(ServiceLoadQueues queues) {
    Queue<Map.Entry<SpServiceRegistration, Float>> overloadedServices = new LinkedList<>();
    PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxLoadQueue = queues.getMaxLoadQueue();

    // Identify services exceeding absolute threshold
    while (!maxLoadQueue.isEmpty()
        && maxLoadQueue.peek().getValue() > LoadBalancerConfig.overloadedThresholdPercentage) {
      overloadedServices.offer(maxLoadQueue.poll());
    }

    if (overloadedServices.isEmpty()) {
      logger.debug("No overloaded services found (threshold: {})",
                   LoadBalancerConfig.overloadedThresholdPercentage);
      return;
    }

    logger.info("Found {} overloaded services above {}%", overloadedServices.size(),
                LoadBalancerConfig.overloadedThresholdPercentage);

    // Migrate to less loaded services
    PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minLoadQueue = queues.getMinLoadQueue();
    int migrationsPerformed = 0;

    while (!overloadedServices.isEmpty() && !minLoadQueue.isEmpty()) {
      if (overloadedServices.peek().getKey().equals(minLoadQueue.peek().getKey())) {
        break;
      }
      executeMigration(overloadedServices.poll(), minLoadQueue.poll());
      migrationsPerformed++;
    }

    logger.info("Migrated {} resource units from overloaded services", migrationsPerformed);
  }
}
