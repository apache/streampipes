package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.commons.prometheus.loadbalancer.LoadBalancerStats;
import org.apache.streampipes.manager.loadbalance.LoadBalancerConfig;
import org.apache.streampipes.manager.loadbalance.LoadManager;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Overload-based pipeline migrator for load balancing
 * Migrates pipelines from services exceeding absolute load threshold
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

    // Report load shedding metrics
    LoadBalancerStats stats = LoadManager.getLoadBalancerStats();
    if (stats != null) {
      stats.reportLoadShedding();
    }

    logger.debug("Overload-based load shedding completed");
  }

  /**
   * Migrate from services exceeding absolute overload threshold
   * @param queues Service load queues
   */
  private void migrateOverloadedServices(ServiceLoadQueues queues) {
    Queue<Map.Entry<SpServiceRegistration, Float>> overloadedServices = new LinkedList<>();
    PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxLoadQueue = queues.getMaxLoadQueue();

    // Identify services exceeding absolute threshold
    while (!maxLoadQueue.isEmpty()
        && maxLoadQueue.peek().getValue() > LoadBalancerConfig.OverloadedThresholdPercentage) {
      overloadedServices.offer(maxLoadQueue.poll());
    }

    if (overloadedServices.isEmpty()) {
      logger.debug("No overloaded services found (threshold: {})",
                  LoadBalancerConfig.OverloadedThresholdPercentage);
      return;
    }

    logger.info("Found {} overloaded services above {}%",
               overloadedServices.size(), LoadBalancerConfig.OverloadedThresholdPercentage);

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