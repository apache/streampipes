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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.prometheus.loadbalancer.LoadBalancerStats;
import org.apache.streampipes.loadbalance.impl.ExtensibleLoadManager;
import org.apache.streampipes.loadbalance.impl.MinimumLoadSelector;
import org.apache.streampipes.loadbalance.impl.OverloadMigrator;
import org.apache.streampipes.loadbalance.impl.ThresholdMigrator;
import org.apache.streampipes.loadbalance.impl.TransferMigrator;
import org.apache.streampipes.loadbalance.impl.WeightedFirstSelector;
import org.apache.streampipes.loadbalance.impl.WeightedRandomSelector;
import org.apache.streampipes.loadbalance.unit.PipelineElementPartitioner;
import org.apache.streampipes.loadbalance.unit.ResourceUnitScanner;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
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

  private static final Environment environment = Environments.getEnvironment();

  /**
   * Initialize the load balancer with configuration from environment.
   */
  public static void initialize() {
    if (!environment.getLoadManagerEnable().getValueOrDefault()) {
      return;
    }

    ExtensionServiceSelector selector;
    PipelineMigrator migrator;

    LoadBalancerConfig.loadTargetStd = environment.getLoadTargetStd().getValueOrDefault();
    LoadBalancerConfig.cpuResourceWeight = environment.getCpuResourceWeight().getValueOrDefault();
    LoadBalancerConfig.thresholdMigratorPercentage =
        environment.getThresholdMigratorPercentage().getValueOrDefault();
    LoadBalancerConfig.minMigratorPercentage =
        environment.getMinMigratorPercentage().getValueOrDefault();
    LoadBalancerConfig.overloadedThresholdPercentage =
        environment.getOverloadedThresholdPercentage().getValueOrDefault();
    LoadBalancerConfig.historyResourcePercentage =
        environment.getHistoryResourcePercentage().getValueOrDefault();
    LoadBalancerConfig.memoryResourceWeight =
        environment.getMemoryResourceWeight().getValueOrDefault();
    LoadBalancerConfig.dirMemoryResourceWeight =
        environment.getDirMemoryResourceWeight().getValueOrDefault();

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

    LoadManager.loadBalancerStats = new LoadBalancerStats();
  }

  /**
   * Allocate a service for pipeline processing.
   *
   * @param serviceRegistrations Available service registrations
   * @param labels Labels for service selection
   * @return Selected service registration
   */
  public static SpServiceRegistration allocation(List<SpServiceRegistration> serviceRegistrations,
                                                 List<String> labels) {
    if (!environment.getLoadManagerEnable().getValueOrDefault() || loadBalancer == null) {
      return serviceRegistrations.isEmpty() ? null : serviceRegistrations.get(0);
    }
    return loadBalancer.allocation(serviceRegistrations, labels);
  }

  public static void tryLockForAdapter() {
    if (!environment.getLoadManagerEnable().getValueOrDefault() || loadBalancer == null) {
      return;
    }
    if (lock != null) {
      lock.readLock().lock();
    }
  }

  public static void unLockForAdapter() {
    if (!environment.getLoadManagerEnable().getValueOrDefault() || loadBalancer == null) {
      return;
    }
    if (lock != null) {
      lock.readLock().unlock();
    }
  }

  public static void tryLockForPipeline() {
    tryLockForAdapter();
  }

  public static void unLockForPipeline() {
    unLockForAdapter();
  }

  /**
   * Perform load shedding operations.
   */
  public static void doLoadShedding() {
    if (!environment.getLoadManagerEnable().getValueOrDefault() || loadBalancer == null) {
      return;
    }
    if (lock != null) {
      double startTime = System.currentTimeMillis();
      lock.writeLock().lock();
      try {
        loadBalancer.doLoadShedding();
      } finally {
        lock.writeLock().unlock();
        double endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;
        LoadBalancerStats stats = LoadManager.getLoadBalancerStats();
        if (stats != null) {
          stats.reportMigrationTime(durationSeconds);
        }
      }
    }
  }

  public static void migrateForHealthCheck(List<SpServiceRegistration> needDeletedServices) {
    if (!environment.getLoadManagerEnable().getValueOrDefault() || loadBalancer == null) {
      return;
    }

    if (lock != null) {
      double startTime = System.currentTimeMillis();
      lock.writeLock().lock();
      try {
        for (SpServiceRegistration service : needDeletedServices) {
          ResourceUnitScanner.ServiceResourceUnits serviceResourceUnits =
              ResourceUnitScanner.scanAndPartitionService(service);
          List<PipelineElementPartitioner.PartitionResult> resourceUnits =
              serviceResourceUnits.getPipelineUnits();

          for (PipelineElementPartitioner.PartitionResult resourceUnit : resourceUnits) {
            if (resourceUnit.isEmpty()) {
              continue;
            }

            for (PipelineElementPartitioner.ResourceUnitWithServices resourceUnitWithServices : resourceUnit
                .getResourceUnits()) {
              LoadBalanceResourceUnit<InvocableStreamPipesEntity> loadBalanceResourceUnit =
                  resourceUnitWithServices.getResourceUnit();
              if (loadBalanceResourceUnit.getElements() == null
                  || loadBalanceResourceUnit.getElements().isEmpty()) {
                continue;
              }

              SpServiceRegistration targetService =
                  LoadManager.allocation(resourceUnitWithServices.getCompatibleServices(),
                                         loadBalanceResourceUnit.getLabels());
              if (targetService != null) {
                ResourceUnitMigration.migrationForHealth(loadBalanceResourceUnit, targetService,
                                                         service);
              }
            }
          }

          List<PipelineElementPartitioner.AdapterResourceUnitWithServices> adapterResourceUnits =
              serviceResourceUnits.getAdapterUnits();

          for (PipelineElementPartitioner.AdapterResourceUnitWithServices resourceUnit : adapterResourceUnits) {

            SpServiceRegistration targetService = LoadManager
                .allocation(resourceUnit.getCompatibleServices(), Collections.EMPTY_LIST);
            if (targetService != null) {
              // Migrate resource unit to a healthy service
              ResourceUnitMigration.migrateAdapterForHealth(resourceUnit.getResourceUnit(),
                                                            targetService, service);
            }
          }
        }
      } finally {
        lock.writeLock().unlock();
        double endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;
        LoadBalancerStats stats = LoadManager.getLoadBalancerStats();
        if (stats != null) {
            stats.reportMigrationTime(durationSeconds);
        }
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
