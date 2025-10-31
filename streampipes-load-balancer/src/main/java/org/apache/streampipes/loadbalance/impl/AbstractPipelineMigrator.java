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

import org.apache.streampipes.loadbalance.PipelineMigrator;
import org.apache.streampipes.loadbalance.ResourceUnitMigration;
import org.apache.streampipes.loadbalance.ServiceLoadCalculator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * Abstract base class for pipeline migrators
 * Provides common functionality for load calculation and migration execution
 */
public abstract class AbstractPipelineMigrator implements PipelineMigrator {

  private static final Logger logger = LoggerFactory.getLogger(AbstractPipelineMigrator.class);

  /**
   * Container for service load queues
   */
  protected static class ServiceLoadQueues {
    private final List<Float> loadValues;
    private final PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxLoadQueue;
    private final PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minLoadQueue;

    public ServiceLoadQueues() {
      this.loadValues = new ArrayList<>();
      this.maxLoadQueue = new PriorityQueue<>((a, b) -> Float.compare(b.getValue(), a.getValue()));
      this.minLoadQueue = new PriorityQueue<>((a, b) -> Float.compare(a.getValue(), b.getValue()));
    }

    public List<Float> getLoadValues() {
      return loadValues;
    }

    public PriorityQueue<Map.Entry<SpServiceRegistration, Float>> getMaxLoadQueue() {
      return maxLoadQueue;
    }

    public PriorityQueue<Map.Entry<SpServiceRegistration, Float>> getMinLoadQueue() {
      return minLoadQueue;
    }

    public void add(SpServiceRegistration service, float load) {
      loadValues.add(load);
      Map.Entry<SpServiceRegistration, Float> entry = Map.entry(service, load);
      maxLoadQueue.offer(entry);
      minLoadQueue.offer(entry);
    }
  }

  /**
   * Calculate load for all services and organize into priority queues
   * @param services List of service registrations
   * @return Service load queues
   */
  protected ServiceLoadQueues calculateServiceLoads(List<SpServiceRegistration> services) {
    ServiceLoadQueues queues = new ServiceLoadQueues();

    for (SpServiceRegistration service : services) {
      float load = ServiceLoadCalculator.calculateLoad(service);
      queues.add(service, load);
    }
    return queues;
  }

  /**
   * Perform migration from source to target service
   * @param source Source service with its load
   * @param target Target service with its load
   */
  protected void executeMigration(Map.Entry<SpServiceRegistration, Float> source,
                                 Map.Entry<SpServiceRegistration, Float> target) {
    logger.info("Migrating from service {} (load: {}) to {} (load: {})",
               source.getKey().getSvcId(), source.getValue(),
               target.getKey().getSvcId(), target.getValue());

    ResourceUnitMigration.migration(
      source.getKey(), source.getValue(),
      target.getKey(), target.getValue()
    );

  }

  /**
   * Perform migration between high-load and low-load services
   * @param maxLoadQueue Queue sorted by load (highest first)
   * @param minLoadQueue Queue sorted by load (lowest first)
   * @param totalServices Total number of services
   * @param maxMigrations Maximum number of migrations to perform
   */
  protected void performMigrationBatch(
      PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxLoadQueue,
      PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minLoadQueue,
      int totalServices,
      int maxMigrations) {

    int migrationsPerformed = 0;

    while (!maxLoadQueue.isEmpty() && !minLoadQueue.isEmpty()
        && maxLoadQueue.size() > totalServices / 2
        && migrationsPerformed < maxMigrations) {

      // Avoid migrating to the same service
      if (maxLoadQueue.peek().getKey().equals(minLoadQueue.peek().getKey())) {
        break;
      }

      Map.Entry<SpServiceRegistration, Float> source = maxLoadQueue.poll();
      Map.Entry<SpServiceRegistration, Float> target = minLoadQueue.poll();

      executeMigration(source, target);
      migrationsPerformed++;
    }

    logger.debug("Performed {} migrations", migrationsPerformed);
  }

  /**
   * Perform unlimited migrations between queues
   * @param maxLoadQueue Queue sorted by load (highest first)
   * @param minLoadQueue Queue sorted by load (lowest first)
   */
  protected void performMigrationBatch(
      PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxLoadQueue,
      PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minLoadQueue) {

    while (!maxLoadQueue.isEmpty() && !minLoadQueue.isEmpty()) {
      // Avoid migrating to the same service
      if (maxLoadQueue.peek().getKey().equals(minLoadQueue.peek().getKey())) {
        break;
      }

      Map.Entry<SpServiceRegistration, Float> source = maxLoadQueue.poll();
      Map.Entry<SpServiceRegistration, Float> target = minLoadQueue.poll();

      executeMigration(source, target);
    }
  }

  /**
   * Check if migration should be performed
   * @param services List of services
   * @return true if migration should proceed
   */
  protected boolean shouldMigrate(List<SpServiceRegistration> services) {
    if (services == null || services.size() <= 1) {
      logger.debug("Skipping migration: insufficient services (count: {})",
                  services == null ? 0 : services.size());
      return false;
    }
    return true;
  }

  /**
   * Calculate average load
   * @param loadValues List of load values
   * @return Average load
   */
  protected float calculateAverageLoad(List<Float> loadValues) {
    return ServiceLoadCalculator.calculateAverage(loadValues);
  }

  /**
   * Calculate variance of load values
   * @param loadValues List of load values
   * @param average Average value
   * @return Variance
   */
  protected float calculateVariance(List<Float> loadValues, float average) {
    if (loadValues == null || loadValues.isEmpty()) {
      return 0;
    }

    float squaredDiffSum = 0;
    for (float value : loadValues) {
      float diff = value - average;
      squaredDiffSum += diff * diff;
    }

    return squaredDiffSum / loadValues.size();
  }
}

