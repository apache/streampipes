package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.commons.prometheus.loadbalancer.LoadBalancerStats;
import org.apache.streampipes.manager.loadbalance.ExtensionServiceSelector;
import org.apache.streampipes.manager.loadbalance.LoadManager;
import org.apache.streampipes.manager.loadbalance.unit.ResourceUnitScanner;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Weighted random service selector using A-Res algorithm
 */
public class WeightedRandomSelector implements ExtensionServiceSelector {

  private static final Logger log = LoggerFactory.getLogger(WeightedRandomSelector.class);

  @Override
  public SpServiceRegistration select(List<SpServiceRegistration> availableServices, List<String> labels) {
    if (availableServices == null || availableServices.isEmpty()) {
      throw new IllegalArgumentException("Available services list cannot be null or empty");
    }

    SpServiceRegistration selectedService;
    if (labels != null && !labels.isEmpty()) {
      List<SpServiceRegistration> affinityServices = filterServices(availableServices, labels);
      if (!affinityServices.isEmpty()) {
        selectedService = aResAlgorithm(affinityServices);
      } else {
        selectedService = aResAlgorithm(availableServices);
      }
    } else {
      selectedService = aResAlgorithm(availableServices);
    }

    // Report service resource metrics for selected service
    LoadBalancerStats stats = LoadManager.getLoadBalancerStats();
    if (stats != null) {
      // Calculate actual service resource metrics
      int adapterCount = calculateAdapterCount(selectedService);
      int pipelineCount = calculatePipelineCount(selectedService);
      double loadWeight = selectedService.getWeight();
      String serviceType = "extension"; // Default service type
      
      stats.updateServiceResources(selectedService.getSvcId(), serviceType, adapterCount, pipelineCount, loadWeight);
    }

    return selectedService;
  }

  /**
   * Filter services that contain any of the specified labels
   * @param availableServices List of available services
   * @param labels Labels to filter by
   * @return Filtered list of services
   */
  private List<SpServiceRegistration> filterServices(List<SpServiceRegistration> availableServices, List<String> labels) {
    return availableServices.stream()
        .filter(service -> containsAnyLabel(service.getLabels(), labels))
        .collect(Collectors.toList());
  }

  /**
   * Check if any label from the list is contained in the service properties
   * @param serviceLabels Service labels
   * @param labels Labels to check
   * @return True if any label matches
   */
  private static boolean containsAnyLabel(Set<String> serviceLabels, List<String> labels) {
    return serviceLabels.stream()
        .anyMatch(labels::contains);
  }

  /**
   * A-Res (Acceptance-Rejection) algorithm for weighted random selection
   * @param availableServices List of available services
   * @return Selected service
   */
  private SpServiceRegistration aResAlgorithm(List<SpServiceRegistration> availableServices) {
    SpServiceRegistration result = availableServices.get(0);
    double minK = Double.MAX_VALUE;

    for (SpServiceRegistration service : availableServices) {
      double ki = Math.pow(ThreadLocalRandom.current().nextDouble(), (double) 1 / service.getWeight());
      if (ki < minK) {
        minK = ki;
        result = service;
      }
    }

    return result;
  }

  @Override
  public Map<SpServiceRegistration, List<InvocableStreamPipesEntity>> allocateSinksAndProcessors(
      List<InvocableStreamPipesEntity> sinksAndProcessors,
      List<SpServiceRegistration> availableServices) {

    if (sinksAndProcessors == null || sinksAndProcessors.isEmpty()) {
      return new HashMap<>();
    }
    if (availableServices == null || availableServices.isEmpty()) {
      throw new IllegalArgumentException("Available services list cannot be null or empty");
    }

    Map<SpServiceRegistration, List<InvocableStreamPipesEntity>> allocationMap = new HashMap<>();

    // Allocate each element using weighted random selection
    for (InvocableStreamPipesEntity element : sinksAndProcessors) {
      SpServiceRegistration selectedService = aResAlgorithm(availableServices);
      allocationMap.computeIfAbsent(selectedService, k -> new ArrayList<>()).add(element);
    }

    return allocationMap;
  }

  @Override
  public Map<SpServiceRegistration, List<AdapterDescription>> allocateAdapters(
      List<AdapterDescription> adapters,
      List<SpServiceRegistration> availableServices) {

    if (adapters == null || adapters.isEmpty()) {
      return new HashMap<>();
    }
    if (availableServices == null || availableServices.isEmpty()) {
      throw new IllegalArgumentException("Available services list cannot be null or empty");
    }

    Map<SpServiceRegistration, List<AdapterDescription>> allocationMap = new HashMap<>();

    // Allocate each adapter using weighted random selection
    for (AdapterDescription adapter : adapters) {
      SpServiceRegistration selectedService = aResAlgorithm(availableServices);
      allocationMap.computeIfAbsent(selectedService, k -> new ArrayList<>()).add(adapter);
    }

    return allocationMap;
  }

  /**
   * Calculate adapter count for a service
   * @param service Service registration
   * @return Number of adapters
   */
  private int calculateAdapterCount(SpServiceRegistration service) {
    try {
      // Get adapter units for this service
      List<LoadBalanceResourceUnit<AdapterDescription>> adapterUnits =
        ResourceUnitScanner.findAdapterUnitsForService(service);

      int totalAdapters = 0;
      for (LoadBalanceResourceUnit<AdapterDescription> unit : adapterUnits) {
        totalAdapters += unit.getElements().size();
      }

      return totalAdapters;
    } catch (Exception e) {
      log.warn("Failed to calculate adapter count for service {}: {}", service.getSvcId(), e.getMessage());
      return 0;
    }
  }

  /**
   * Calculate pipeline count for a service
   * @param service Service registration
   * @return Number of pipelines
   */
  private int calculatePipelineCount(SpServiceRegistration service) {
    try {
      // Get pipeline units for this service
      List<LoadBalanceResourceUnit<InvocableStreamPipesEntity>> pipelineUnits =
        ResourceUnitScanner.findResourceUnitsForService(service);

      int totalPipelines = 0;
      for (LoadBalanceResourceUnit<InvocableStreamPipesEntity> unit : pipelineUnits) {
        totalPipelines += unit.getElements().size();
      }

      return totalPipelines;
    } catch (Exception e) {
      log.warn("Failed to calculate pipeline count for service {}: {}", service.getSvcId(), e.getMessage());
      return 0;
    }
  }
}
