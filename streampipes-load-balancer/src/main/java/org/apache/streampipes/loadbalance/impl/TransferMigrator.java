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

import java.util.List;

/**
 * Transfer-based pipeline migrator for load balancing Uses multiple strategies based on load
 * variance and distribution
 */
public class TransferMigrator extends AbstractPipelineMigrator {

  private static final Logger logger = LoggerFactory.getLogger(TransferMigrator.class);
  private static final float MIN_LOAD_DIFFERENCE = 10.0f;

  @Override
  public void doLoadShedding(List<SpServiceRegistration> services) {
    if (!shouldMigrate(services)) {
      return;
    }

    // Calculate loads for all services
    ServiceLoadQueues queues = calculateServiceLoads(services);
    List<Float> loadValues = queues.getLoadValues();
    float averageLoad = calculateAverageLoad(loadValues);
    float variance = calculateVariance(loadValues, averageLoad);

    logger.debug("Load statistics - Average: {}, Variance: {}, Target Std: {}", averageLoad,
                 variance, LoadBalancerConfig.loadTargetStd);

    // Try different migration strategies based on load distribution
    if (tryVarianceBasedMigration(queues, variance, services.size())) {
      return;
    }

    if (tryUnderutilizedMigration(queues, services.size())) {
      return;
    }

    tryOverloadMigration(queues, services.size());
  }

  /**
   * Strategy 1: Migrate when variance exceeds target
   * 
   * @param queues Service load queues
   * @param variance Current load variance
   * @param totalServices Total number of services
   * @return true if migration was performed
   */
  private boolean tryVarianceBasedMigration(ServiceLoadQueues queues, float variance,
                                            int totalServices) {
    if (variance <= LoadBalancerConfig.loadTargetStd) {
      return false;
    }

    logger.info("Variance {} exceeds target {}, performing variance-based migration", variance,
                LoadBalancerConfig.loadTargetStd);

    performMigrationBatch(queues.getMaxLoadQueue(), queues.getMinLoadQueue(), totalServices,
                          Integer.MAX_VALUE // No limit on migrations for variance reduction
    );

    return true;
  }

  /**
   * Strategy 2: Migrate when services are significantly underutilized
   * 
   * @param queues Service load queues
   * @param totalServices Total number of services
   * @return true if migration was performed
   */
  private boolean tryUnderutilizedMigration(ServiceLoadQueues queues, int totalServices) {
    float minLoad = queues.getMinLoadQueue().peek().getValue();
    float maxLoad = queues.getMaxLoadQueue().peek().getValue();

    if (minLoad >= LoadBalancerConfig.minMigratorPercentage) {
      return false;
    }

    if (maxLoad <= minLoad + MIN_LOAD_DIFFERENCE) {
      return false;
    }

    logger.info("Underutilized service detected (min: {}%, max: {}%), migrating from high to low",
                minLoad, maxLoad);

    performMigrationBatch(queues.getMaxLoadQueue(), queues.getMinLoadQueue(), totalServices,
                          totalServices / 2 // Limit migrations for underutilization
    );

    return true;
  }

  /**
   * Strategy 3: Migrate when services are overloaded
   * 
   * @param queues Service load queues
   * @param totalServices Total number of services
   * @return true if migration was performed
   */
  private boolean tryOverloadMigration(ServiceLoadQueues queues, int totalServices) {
    float maxLoad = queues.getMaxLoadQueue().peek().getValue();
    float minLoad = queues.getMinLoadQueue().peek().getValue();

    if (maxLoad <= LoadBalancerConfig.overloadedThresholdPercentage) {
      logger.debug("No overloaded services found (max load: {}%)", maxLoad);
      return false;
    }

    if (maxLoad <= minLoad + MIN_LOAD_DIFFERENCE) {
      return false;
    }

    logger.info("Overloaded service detected ({}%), redistributing load", maxLoad);

    performMigrationBatch(queues.getMaxLoadQueue(), queues.getMinLoadQueue(), totalServices,
                          totalServices / 2 // Limit migrations for overload
    );

    return true;
  }
}
