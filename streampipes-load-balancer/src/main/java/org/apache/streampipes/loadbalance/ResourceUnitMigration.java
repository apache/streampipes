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

import org.apache.streampipes.commons.exceptions.SpException;
import org.apache.streampipes.loadbalance.unit.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.loadbalance.unit.InvokeHttpRequest;
import org.apache.streampipes.loadbalance.unit.ResourceUnitStatsScanner;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnitStats;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResourceUnitMigration {

  private static final Logger logger = LoggerFactory.getLogger(ResourceUnitMigration.class);

  /**
   * Migrate pipeline resource unit for health recovery. Updates the service endpoint for all
   * elements in the unit.
   *
   * @param resourceUnit Resource unit to migrate
   * @param targetService Target service registration
   * @param sourceService Source service registration (for metrics reporting)
   */
  public static void migrationForHealth(LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit,
                                        SpServiceRegistration targetService,
                                        SpServiceRegistration sourceService) {

    logger.info("Migrating pipeline resource unit {} to service {} for health recovery",
                resourceUnit.getId(), targetService.getSvcId());

    resourceUnit.setServiceId(targetService.getSvcId());

    try {
      // Update endpoint URL for all elements and invoke them on new service
      for (InvocableStreamPipesEntity element : resourceUnit.getElements()) {
        String newEndpointUrl = getSelectedEndpoint(element, targetService.getServiceUrl());
        element.setSelectedEndpointUrl(newEndpointUrl);

        logger.debug("Invoking element {} on new endpoint {}", element.getElementId(),
                     newEndpointUrl);

        new InvokeHttpRequest().execute(element, newEndpointUrl, resourceUnit.getPipelineId());
      }

      // Update pipeline in storage with new endpoints
      updatePipelineEndpoints(resourceUnit);

      logger.info("Successfully migrated pipeline resource unit {} to service {}",
                  resourceUnit.getId(), targetService.getSvcId());

    } catch (Exception e) {
      logger.error("Failed to migrate pipeline resource unit {} to service {}: {}",
                   resourceUnit.getId(), targetService.getSvcId(), e.getMessage(), e);
      throw new RuntimeException("Migration failed for unit " + resourceUnit.getId(), e);
    }
  }

  /**
   * Migrate adapter resource unit for health recovery. Updates the service endpoint for the
   * adapter.
   *
   * @param resourceUnit Adapter resource unit to migrate
   * @param targetService Target service registration
   * @param sourceService Source service registration (for metrics reporting)
   */
  public static void migrateAdapterForHealth(LoadBalanceResourceUnit<AdapterDescription> resourceUnit,
                                             SpServiceRegistration targetService,
                                             SpServiceRegistration sourceService) {

    logger.info("Migrating adapter resource unit {} to service {} for health recovery",
                resourceUnit.getId(), targetService.getSvcId());

    resourceUnit.setServiceId(targetService.getSvcId());

    try {
      for (AdapterDescription adapter : resourceUnit.getElements()) {
        String oldEndpointUrl = adapter.getSelectedEndpointUrl();
        String newEndpointUrl = targetService.getServiceUrl();

        logger.debug("Updating adapter {} endpoint from {} to {}", adapter.getElementId(),
                     oldEndpointUrl, newEndpointUrl);

        // Update adapter endpoint
        adapter.setSelectedEndpointUrl(newEndpointUrl);

        // Update adapter in storage
        StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage()
            .updateElement(adapter);

        logger.debug("Successfully updated adapter {} in storage", adapter.getElementId());
      }

      logger.info("Successfully migrated adapter resource unit {} to service {}",
                  resourceUnit.getId(), targetService.getSvcId());

    } catch (Exception e) {
      logger.error("Failed to migrate adapter resource unit {} to service {}: {}",
                   resourceUnit.getId(), targetService.getSvcId(), e.getMessage(), e);
      throw new RuntimeException("Adapter migration failed for unit " + resourceUnit.getId(), e);
    }
  }

  /**
   * Update pipeline endpoints in storage.
   *
   * @param resourceUnit Resource unit with updated elements
   */
  private static void updatePipelineEndpoints(LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit) {
    Pipeline pipeline = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI()
        .getElementById(resourceUnit.getPipelineId());

    if (pipeline == null) {
      logger.warn("Pipeline {} not found in storage", resourceUnit.getPipelineId());
      return;
    }

    // Update processors
    if (pipeline.getSepas() != null) {
      for (DataProcessorInvocation processor : pipeline.getSepas()) {
        updateElementEndpoint(processor, resourceUnit.getElements());
      }
    }

    // Update sinks
    if (pipeline.getActions() != null) {
      for (DataSinkInvocation sink : pipeline.getActions()) {
        updateElementEndpoint(sink, resourceUnit.getElements());
      }
    }

    // Save updated pipeline
    StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updateElement(pipeline);

    logger.debug("Updated pipeline {} endpoints in storage", pipeline.getPipelineId());
  }

  /**
   * Update element endpoint if it's in the resource unit.
   *
   * @param pipelineElement Element in pipeline
   * @param resourceElements Elements in resource unit
   */
  private static void updateElementEndpoint(InvocableStreamPipesEntity pipelineElement,
                                            List<InvocableStreamPipesEntity> resourceElements) {

    for (InvocableStreamPipesEntity resourceElement : resourceElements) {
      if (pipelineElement.getElementId().equals(resourceElement.getElementId())) {
        pipelineElement.setSelectedEndpointUrl(resourceElement.getSelectedEndpointUrl());
        logger.debug("Updated endpoint for element {}: {}", pipelineElement.getElementId(),
                     resourceElement.getSelectedEndpointUrl());
        break;
      }
    }
  }



  /**
   * Migrate resource units from source to target service to balance load.
   *
   * @param sourceService Source service to migrate from
   * @param sourceLoad Current load of source service
   * @param targetService Target service to migrate to
   * @param targetLoad Current load of target service
   */
  public static void migration(SpServiceRegistration sourceService, double sourceLoad,
                               SpServiceRegistration targetService, double targetLoad) {

    logger.info("Starting migration from service {} (load: {}%) to service {} (load: {}%)",
                sourceService.getSvcId(), sourceLoad, targetService.getSvcId(), targetLoad);

    // Generate statistics for both services (on-demand, no cache)
    List<LoadBalanceResourceUnitStats> sourceStats =
        ResourceUnitStatsScanner.generateStatsForService(sourceService);
    List<LoadBalanceResourceUnitStats> targetStats =
        ResourceUnitStatsScanner.generateStatsForService(targetService);

    if (sourceStats.isEmpty()) {
      logger.info("No resource units found on source service {}", sourceService.getSvcId());
      return;
    }

    // Sort by event rate (highest first) to migrate busy units first
    sourceStats.sort((a, b) -> Double.compare(b.getEventRateOut() + b.getEventRateIn(),
                                              a.getEventRateOut() + b.getEventRateIn()));

    // Calculate how much load to transfer
    double transferTarget =
        calculateTransferTarget(sourceStats, targetStats, sourceLoad, targetLoad);

    logger.debug("Transfer target: {} event rate", transferTarget);

    // Migrate units until we reach the transfer target
    int migratedCount =
        migrateUnitsToTarget(sourceStats, targetService, sourceService, transferTarget);

    // Note: Migration metrics are reported by individual migration methods to avoid double counting
  }

  /**
   * Calculate how much load should be transferred.
   *
   * @param sourceStats Source service statistics
   * @param targetStats Target service statistics
   * @param sourceLoad Source service load percentage
   * @param targetLoad Target service load percentage
   * @return Transfer target in terms of event rate
   */
  private static double calculateTransferTarget(List<LoadBalanceResourceUnitStats> sourceStats,
                                                List<LoadBalanceResourceUnitStats> targetStats,
                                                double sourceLoad, double targetLoad) {

    // Calculate total event rates
    double sourceEventRate = sourceStats.stream()
        .mapToDouble(stats -> stats.getEventRateOut() + stats.getEventRateIn()).sum();

    double targetEventRate = targetStats.stream()
        .mapToDouble(stats -> stats.getEventRateOut() + stats.getEventRateOut()).sum();

    double totalEventRate = sourceEventRate + targetEventRate;

    // Calculate how much to transfer to balance loads
    // Transfer = (sourceEventRate) * (sourceLoad - targetLoad) / (2 * sourceLoad)
    double transferAmount =
        (totalEventRate - targetEventRate) * (sourceLoad - targetLoad) / (2 * sourceLoad);

    return Math.max(0, transferAmount);
  }

  /**
   * Migrate units from source to target until transfer target is reached.
   *
   * @param sourceStats Source statistics sorted by event rate
   * @param targetService Target service
   * @param transferTarget Target transfer amount
   * @return Number of units migrated
   */
  private static int migrateUnitsToTarget(List<LoadBalanceResourceUnitStats> sourceStats,
                                          SpServiceRegistration targetService,
                                          SpServiceRegistration sourceService,
                                          double transferTarget) {

    double transferredAmount = 0;
    int migratedCount = 0;

    // Iterate through stats (sorted by event rate)
    for (LoadBalanceResourceUnitStats stats : sourceStats) {
      if (transferredAmount >= transferTarget) {
        logger.info("Transfer target reached: {} >= {}", transferredAmount, transferTarget);
        break;
      }

      if (stats == null || stats.getUnit() == null) {
        continue;
      }
      LoadBalanceResourceUnit matchingUnit = stats.getUnit();
      if (matchingUnit.getElements().isEmpty()) {
        continue;
      }
      boolean isAdapter = matchingUnit.getElements().get(0) instanceof AdapterDescription;

      // Migrate the unit
      try {
        if (isAdapter) {
          migrateAdapterForHealth(matchingUnit, targetService, sourceService);
        } else {
          migrationForHealth(matchingUnit, targetService, sourceService);
        }
        transferredAmount += stats.getEventRateOut() + stats.getEventRateIn();
        migratedCount++;

        logger.debug("Migrated unit {} with event rate {}, total transferred: {}",
                     matchingUnit.getId(), stats.getEventRateOut(), transferredAmount);
      } catch (SpException e) {
        logger.warn("Failed to migrate unit {}: {}", matchingUnit.getId(), e.getMessage());
      }
    }

    logger.info("Migration completed: {} units migrated, total event rate transferred: {}",
                migratedCount, transferredAmount);

    return migratedCount;
  }

  /**
   * Find resource unit by ID.
   *
   * @param units List of resource units
   * @param resourceId Resource ID to find
   * @return Matching resource unit or null
   */
  private static LoadBalanceResourceUnit<InvocableStreamPipesEntity> findUnitById(List<LoadBalanceResourceUnit<InvocableStreamPipesEntity>> units,
                                                                                  String resourceId) {

    return units.stream().filter(unit -> unit.getId().equals(resourceId)).findFirst().orElse(null);
  }

  private static String getSelectedEndpoint(InvocableStreamPipesEntity pipelineElement,
                                            String url) {
    return ExtensionsServiceEndpointUtils.getPipelineElementType(pipelineElement)
        .getInvocationUrl(url, pipelineElement.getAppId());
  }
}
