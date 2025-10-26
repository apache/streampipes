package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.commons.prometheus.loadbalancer.LoadBalancerStats;
import org.apache.streampipes.manager.loadbalance.ExtensionServiceSelector;
import org.apache.streampipes.manager.loadbalance.LoadManager;
import org.apache.streampipes.manager.loadbalance.ServiceLoadCalculator;
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

/**
 * Service selector that chooses the service with minimum load
 */
public class MinimumLoadSelector implements ExtensionServiceSelector {

  private static final Logger log = LoggerFactory.getLogger(MinimumLoadSelector.class);

  @Override
  public SpServiceRegistration select(List<SpServiceRegistration> availableServices, List<String> labels) {
    if (availableServices == null || availableServices.isEmpty()) {
      throw new IllegalArgumentException("Available services list cannot be null or empty");
    }

    List<SpServiceRegistration> serviceRegistrations = new ArrayList<>(availableServices);

    // Sort services by load in ascending order
    serviceRegistrations.sort((serviceA, serviceB) -> {
      float loadA = ServiceLoadCalculator.calculateLoad(serviceA);
      float loadB = ServiceLoadCalculator.calculateLoad(serviceB);
      return Float.compare(loadA, loadB);
    });

    SpServiceRegistration selectedService = serviceRegistrations.get(0);

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

    // Allocate each element to the service with minimum load
    for (InvocableStreamPipesEntity element : sinksAndProcessors) {
      SpServiceRegistration selectedService = selectMinLoadService(availableServices);
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

    // Allocate each adapter to the service with minimum load
    for (AdapterDescription adapter : adapters) {
      SpServiceRegistration selectedService = selectMinLoadService(availableServices);
      allocationMap.computeIfAbsent(selectedService, k -> new ArrayList<>()).add(adapter);
    }

    return allocationMap;
  }

  /**
   * Select service with minimum load from available services
   * @param availableServices List of available services
   * @return Service with minimum load
   */
  private SpServiceRegistration selectMinLoadService(List<SpServiceRegistration> availableServices) {
    SpServiceRegistration minLoadService = availableServices.get(0);
    float minLoad = ServiceLoadCalculator.calculateLoad(minLoadService);

    for (SpServiceRegistration service : availableServices) {
      float load = ServiceLoadCalculator.calculateLoad(service);
      if (load < minLoad) {
        minLoad = load;
        minLoadService = service;
      }
    }

    return minLoadService;
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
