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
package org.apache.streampipes.loadbalance.unit;

import org.apache.streampipes.commons.prometheus.loadbalancer.LoadBalancerStats;
import org.apache.streampipes.loadbalance.LoadManager;
import org.apache.streampipes.loadbalance.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnitStats;
import org.apache.streampipes.model.monitoring.MessageCounter;
import org.apache.streampipes.model.monitoring.SpMetricsEntry;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resource unit statistics scanner Generates statistics for resource units on-demand by scanning
 * metrics from extensions
 */
public class ResourceUnitStatsScanner {

  private static final Logger logger = LoggerFactory.getLogger(ResourceUnitStatsScanner.class);

  /**
   * Generate statistics for all resource units on a service Scans pipelines and calculates metrics
   * for each resource unit
   * 
   * @param service Service registration
   * @return List of resource unit statistics
   */
  public static List<LoadBalanceResourceUnitStats> generateStatsForService(SpServiceRegistration service) {
    if (service == null) {
      return new ArrayList<>();
    }

    logger.debug("Generating statistics for service {}", service.getSvcId());

    List<LoadBalanceResourceUnitStats> allStats = new ArrayList<>();

    // Generate stats for pipeline elements (sinks and processors)
    List<LoadBalanceResourceUnit<InvocableStreamPipesEntity>> pipelineUnits =
        ResourceUnitScanner.findResourceUnitsForService(service);

    for (LoadBalanceResourceUnit<InvocableStreamPipesEntity> unit : pipelineUnits) {
      LoadBalanceResourceUnitStats<InvocableStreamPipesEntity> stats =
          generateStatsForPipelineUnit(unit);
      allStats.add(stats);
    }

    // Generate stats for adapters
    List<LoadBalanceResourceUnit<AdapterDescription>> adapterUnits =
        ResourceUnitScanner.findAdapterUnitsForService(service);

    for (LoadBalanceResourceUnit<AdapterDescription> unit : adapterUnits) {
      LoadBalanceResourceUnitStats<AdapterDescription> stats = generateStatsForAdapterUnit(unit);
      allStats.add(stats);
    }

    logger.debug("Generated {} statistics for service {}", allStats.size(), service.getSvcId());

    return allStats;
  }

  /**
   * Generate statistics for a pipeline resource unit
   * 
   * @param resourceUnit Pipeline resource unit
   * @return Resource unit statistics
   */
  private static LoadBalanceResourceUnitStats<InvocableStreamPipesEntity> generateStatsForPipelineUnit(LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit) {

    LoadBalanceResourceUnitStats<InvocableStreamPipesEntity> stats =
        new LoadBalanceResourceUnitStats();
    stats.setUnit(resourceUnit);

    ExtensionsLogProvider provider = ExtensionsLogProvider.INSTANCE;

    long totalCountIn = 0;
    long totalCountOut = 0;
    long totalThroughputIn = 0;
    long totalThroughputOut = 0;

    // Aggregate metrics from all elements in the resource unit
    for (InvocableStreamPipesEntity element : resourceUnit.getElements()) {
      SpMetricsEntry metricsEntry = provider.getMetricInfosForResource(element.getElementId());

      if (metricsEntry == null) {
        logger.warn("No metrics found for element {}", element.getElementId());
        continue;
      }

      // Process incoming messages
      for (Map.Entry<String, MessageCounter> entry : metricsEntry.getMessagesIn().entrySet()) {
        totalCountIn += entry.getValue().getCounter();
        totalThroughputIn += entry.getValue().getSize();
      }

      // Process outgoing messages
      MessageCounter messagesOut = metricsEntry.getMessagesOut();
      if (messagesOut != null) {
        totalCountOut += messagesOut.getCounter();
        totalThroughputOut += messagesOut.getSize();
      }
    }

    // Set aggregated statistics
    stats.setEventRateIn((double) totalCountIn);
    stats.setEventRateOut((double) totalCountOut);
    stats.setEventThroughputIn((double) totalThroughputIn);
    stats.setEventThroughputOut((double) totalThroughputOut);

    logger.debug("Generated stats for resource unit {}: in={}, out={}", resourceUnit.getId(),
                 totalCountIn, totalCountOut);

    return stats;
  }

  /**
   * Generate statistics for an adapter resource unit
   * 
   * @param resourceUnit Adapter resource unit
   * @return Resource unit statistics
   */
  private static LoadBalanceResourceUnitStats<AdapterDescription> generateStatsForAdapterUnit(LoadBalanceResourceUnit<AdapterDescription> resourceUnit) {

    LoadBalanceResourceUnitStats<AdapterDescription> stats = new LoadBalanceResourceUnitStats<>();
    stats.setUnit(resourceUnit);

    ExtensionsLogProvider provider = ExtensionsLogProvider.INSTANCE;

    long totalCountIn = 0;
    long totalCountOut = 0;
    long totalThroughputIn = 0;
    long totalThroughputOut = 0;

    // Aggregate metrics from all adapters in the resource unit
    for (AdapterDescription adapter : resourceUnit.getElements()) {
      SpMetricsEntry metricsEntry = provider.getMetricInfosForResource(adapter.getElementId());

      if (metricsEntry == null) {
        logger.warn("No metrics found for adapter {}", adapter.getElementId());
        continue;
      }

      // Process incoming messages
      for (Map.Entry<String, MessageCounter> entry : metricsEntry.getMessagesIn().entrySet()) {
        totalCountIn += entry.getValue().getCounter();
        totalThroughputIn += entry.getValue().getSize();
      }

      // Process outgoing messages
      MessageCounter messagesOut = metricsEntry.getMessagesOut();
      if (messagesOut != null) {
        totalCountOut += messagesOut.getCounter();
        totalThroughputOut += messagesOut.getSize();
      }
    }

    // Set aggregated statistics
    stats.setEventRateIn((double) totalCountIn);
    stats.setEventRateOut((double) totalCountOut);
    stats.setEventThroughputIn((double) totalThroughputIn);
    stats.setEventThroughputOut((double) totalThroughputOut);

    logger.debug("Generated stats for adapter unit {}: in={}, out={}", resourceUnit.getId(),
                 totalCountIn, totalCountOut);

    return stats;
  }

  /**
   * Generate statistics for multiple services
   * 
   * @param services List of services
   * @return Map of service ID to list of statistics
   */
  public static Map<String, List<LoadBalanceResourceUnitStats>> generateStatsForServices(List<SpServiceRegistration> services) {

    logger.info("Generating statistics for {} services", services.size());

    return services.stream().collect(java.util.stream.Collectors
        .toMap(SpServiceRegistration::getSvcId, ResourceUnitStatsScanner::generateStatsForService));
  }

  /**
   * Generate statistics for a single resource unit
   * 
   * @param resourceUnit Pipeline resource unit
   * @return Resource unit statistics
   */
  public static LoadBalanceResourceUnitStats<InvocableStreamPipesEntity> generateStats(LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit) {
    return generateStatsForPipelineUnit(resourceUnit);
  }

  /**
   * Generate statistics for a single adapter unit
   * 
   * @param adapterUnit Adapter resource unit
   * @return Resource unit statistics
   */
  public static LoadBalanceResourceUnitStats<AdapterDescription> generateAdapterStats(LoadBalanceResourceUnit<AdapterDescription> adapterUnit) {
    return generateStatsForAdapterUnit(adapterUnit);
  }

  /**
   * Collect load balancer metrics for all services using database queries This is more efficient
   * than scanning resource units
   */
  public static void collectAllLoadBalancerMetrics() {
    try {
      LoadBalancerStats stats = LoadManager.getLoadBalancerStats();
      if (stats == null) {
        logger
            .warn("LoadBalancerStats is null - LoadManager may not be initialized or load balancing is disabled");
        return;
      }

      // Get all services
      List<SpServiceRegistration> services =
          SpServiceDiscovery.getServiceDiscovery().getService(true);
      if (services.isEmpty()) {
        logger.debug("No services found for metrics collection");
        return;
      }

      logger.debug("Collecting load balancer metrics for {} services", services.size());

      for (SpServiceRegistration service : services) {
        try {
          // Count adapters for this service using database query
          int adapterCount = countAdaptersForService(service);

          // Count pipelines for this service using database query
          int pipelineCount = countPipelinesForService(service);

          // Update metrics
          stats.updateAllMetrics(service.getSvcId(), adapterCount, pipelineCount);

          logger.debug("Collected metrics for service {}: adapters={}, pipelines={}",
                       service.getSvcId(), adapterCount, pipelineCount);

        } catch (Exception e) {
          logger.warn("Failed to collect metrics for service {}: {}", service.getSvcId(),
                      e.getMessage(), e);
        }
      }

    } catch (Exception e) {
      logger.warn("Failed to collect load balancer metrics: {}", e.getMessage(), e);
    }
  }

  /**
   * Count adapters running on a specific service using database query
   */
  private static int countAdaptersForService(SpServiceRegistration service) {
    try {
      String serviceUrl = service.getServiceUrl();
      var adapterStorage =
          StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage();
      List<AdapterDescription> allAdapters = adapterStorage.findAll();

      return (int) allAdapters.stream()
          .filter(adapter -> adapter.isRunning() && serviceUrl != null
              && adapter.getSelectedEndpointUrl() != null
              && adapter.getSelectedEndpointUrl().startsWith(serviceUrl))
          .count();
    } catch (Exception e) {
      logger.warn("Failed to count adapters for service {}: {}", service.getSvcId(),
                  e.getMessage());
      return 0;
    }
  }

  /**
   * Count pipelines running on a specific service using database query
   */
  private static int countPipelinesForService(SpServiceRegistration service) {
    try {
      String serviceUrl = service.getServiceUrl();
      List<Pipeline> allPipelines =  StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().findAll();

      return (int) allPipelines.stream()
          .filter(pipeline -> pipeline.isRunning() && pipelineUsesService(pipeline, serviceUrl))
          .count();
    } catch (Exception e) {
      logger.warn("Failed to count pipelines for service {}: {}", service.getSvcId(),
                  e.getMessage());
      return 0;
    }
  }

  /**
   * Check if a pipeline uses a specific service
   */
  private static boolean pipelineUsesService(Pipeline pipeline, String serviceUrl) {
    if (serviceUrl == null) {
      return false;
    }

    boolean sepaUsesService = pipeline.getSepas() != null
        && pipeline.getSepas().stream().anyMatch(sepa -> matchesSelectedEndpoint(sepa, serviceUrl));

    boolean actionUsesService = pipeline.getActions() != null && pipeline.getActions().stream()
        .anyMatch(action -> matchesSelectedEndpoint(action, serviceUrl));

    return sepaUsesService || actionUsesService;
  }

  /**
   * Check if an entity's selected endpoint matches the service URL
   */
  private static boolean matchesSelectedEndpoint(InvocableStreamPipesEntity entity,
                                                 String serviceUrl) {
    if (entity == null || entity.getSelectedEndpointUrl() == null || serviceUrl == null) {
      return false;
    }
    return entity.getSelectedEndpointUrl().startsWith(serviceUrl);
  }
}
