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
package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.prometheus.loadbalancer.LoadBalancerStats;
import org.apache.streampipes.manager.loadbalance.impl.*;
import org.apache.streampipes.manager.loadbalance.unit.PipelineElementPartitioner;
import org.apache.streampipes.manager.loadbalance.unit.ResourceUnitScanner;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Load manager for handling load balancing operations.
 */
public class LoadManager {

  private static LoadBalancer loadBalancer;

  private static ReadWriteLock lock;

  private static LoadBalancerStats loadBalancerStats;
    
  /**
   * Initialize the load balancer with configuration from environment.
   */
  public static void initialize() {
    Environment environment = Environments.getEnvironment();
    ExtensionServiceSelector selector;
    PipelineMigrator migrator;

    LoadBalancerConfig.LoadTargetStd = environment.getLoadTargetStd().getValueOrDefault();
    LoadBalancerConfig.CPUResourceWeight = environment.getCpuResourceWeight().getValueOrDefault();
    LoadBalancerConfig.ThresholdMigratorPercentage = environment.getThresholdMigratorPercentage().getValueOrDefault();
    LoadBalancerConfig.MinMigratorPercentage = environment.getMinMigratorPercentage().getValueOrDefault();
    LoadBalancerConfig.OverloadedThresholdPercentage = environment.getOverloadedThresholdPercentage().getValueOrDefault();
    LoadBalancerConfig.HistoryResourcePercentage = environment.getHistoryResourcePercentage().getValueOrDefault();
    LoadBalancerConfig.MemoryResourceWeight = environment.getMemoryResourceWeight().getValueOrDefault();
    LoadBalancerConfig.DirMemoryResourceWeight = environment.getDirMemoryResourceWeight().getValueOrDefault();

    if (environment.getSelector().getValueOrDefault().equals("WeightedRandomSelector")) {
      selector = new WeightedRandomSelector();
    } else if (environment.getSelector().getValueOrDefault().equals("MinimumLoadSelector")) {
      selector = new MinimumLoadSelector();
    } else {
      selector = new WeightedFirstSelector();
    }

    if (environment.getMigrator().getValueOrDefault().equals("TransferMigrator")) {
      migrator = new TransferMigrator();
    } else if (environment.getMigrator().getValueOrDefault().equals("OverloadMigrator")) {
      migrator = new OverloadMigrator();
    } else {
      migrator = new ThresholdMigrator();
    }
    LoadManager.loadBalancer = new ExtensibleLoadManager(selector, migrator);

    LoadManager.lock = new ReentrantReadWriteLock();

    // Initialize load balancer statistics
    LoadManager.loadBalancerStats = new LoadBalancerStats();

    // Immediately update metrics to ensure they are visible in Prometheus
    loadBalancerStats.updateAllMetrics();
  }
    
  /**
   * Allocate a service for pipeline processing.
   *
   * @param serviceRegistrations Available service registrations
   * @param labels Labels for service selection
   * @return Selected service registration
   */
  public static SpServiceRegistration allocation(List<SpServiceRegistration> serviceRegistrations, List<String> labels) {
    return loadBalancer.allocation(serviceRegistrations, labels);
  }

  public static void tryLockForAdapter() {
    if (lock != null) {
      lock.readLock().lock();
    }
  }

  public static void unLockForAdapter() {
    if (lock != null) {
      lock.readLock().unlock();
    }
  }

  public static void tryLockForPipeline() {
    if (lock != null) {
      lock.readLock().lock();
    }
  }

  public static void unLockForPipeline() {
    if (lock != null) {
      lock.readLock().unlock();
    }
  }

  /**
   * Perform load shedding operations.
   */
  public static void doLoadShedding() {
    if (lock != null) {
      lock.writeLock().lock();
      try {
        loadBalancer.doLoadShedding();

        // Report load shedding metrics
        if (loadBalancerStats != null) {
          loadBalancerStats.reportLoadShedding();
        }
      } finally {
        lock.writeLock().unlock();
      }
    }
  }

  public static void migrateForHealthCheck(List<SpServiceRegistration> needDeletedServices) {
    if (lock != null) {
      lock.writeLock().lock();
      try {
        for (SpServiceRegistration service : needDeletedServices) {
          ResourceUnitScanner.ServiceResourceUnits serviceResourceUnits = ResourceUnitScanner.scanAndPartitionService(service);
          List<PipelineElementPartitioner.PartitionResult> resourceUnits =
              serviceResourceUnits.getPipelineUnits();

          for (PipelineElementPartitioner.PartitionResult resourceUnit : resourceUnits) {
            if (resourceUnit.isEmpty()) {
              continue;
            }

            for (PipelineElementPartitioner.ResourceUnitWithServices resourceUnitWithServices : resourceUnit.getResourceUnits()) {
              LoadBalanceResourceUnit<InvocableStreamPipesEntity> loadBalanceResourceUnit = resourceUnitWithServices.getResourceUnit();
              if (loadBalanceResourceUnit.getElements() == null || loadBalanceResourceUnit.getElements().isEmpty()) {
                continue;
              }

              SpServiceRegistration targetService = LoadManager.allocation(resourceUnitWithServices.getCompatibleServices(), loadBalanceResourceUnit.getLabels());
              if (targetService != null) {
                ResourceUnitMigration.migrationForHealth(loadBalanceResourceUnit, targetService);
              }
            }
          }

          List<PipelineElementPartitioner.AdapterResourceUnitWithServices> adapterResourceUnits =
              serviceResourceUnits.getAdapterUnits();

          for (PipelineElementPartitioner.AdapterResourceUnitWithServices resourceUnit : adapterResourceUnits) {

            SpServiceRegistration targetService = LoadManager.allocation(resourceUnit.getCompatibleServices(), Collections.EMPTY_LIST);
            if (targetService != null) {
              // Migrate resource unit to a healthy service
              ResourceUnitMigration.migrateAdapterForHealth(resourceUnit.getResourceUnit(), targetService);
            }
          }
        }
      } finally {
        lock.writeLock().unlock();
      }
    }
  }
    
  /**
   * Get load balancer statistics.
   *
   * @return LoadBalancerStats instance
   */
  public static LoadBalancerStats getLoadBalancerStats() {
    return loadBalancerStats;
  }
}

