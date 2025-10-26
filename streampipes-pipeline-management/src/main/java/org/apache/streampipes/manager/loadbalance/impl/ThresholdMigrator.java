package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.manager.loadbalance.LoadBalancerConfig;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Threshold-based pipeline migrator for load balancing
 * Migrates pipelines when services exceed average load by a threshold
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
   * @param queues Service load queues
   * @param averageLoad Average load across all services
   * @param totalServices Total number of services
   */
  private void migrateOverloadedServices(ServiceLoadQueues queues, float averageLoad, int totalServices) {
    Queue<Map.Entry<SpServiceRegistration, Float>> overloadedServices = new LinkedList<>();
    PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxLoadQueue = queues.getMaxLoadQueue();

    // Identify services exceeding threshold
    float thresholdLoad = averageLoad + LoadBalancerConfig.ThresholdMigratorPercentage;
    while (!maxLoadQueue.isEmpty()
        && maxLoadQueue.peek().getValue() > thresholdLoad
        && overloadedServices.size() < totalServices / 2) {
      overloadedServices.offer(maxLoadQueue.poll());
    }

    if (overloadedServices.isEmpty()) {
      logger.debug("No overloaded services found (threshold: {})", thresholdLoad);
      return;
    }

    logger.info("Found {} overloaded services above threshold {}",
               overloadedServices.size(), thresholdLoad);

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
      if (minLoad >= LoadBalancerConfig.MinMigratorPercentage) {
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