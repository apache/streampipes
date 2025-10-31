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

import org.apache.streampipes.loadbalance.ExtensionServiceSelector;
import org.apache.streampipes.loadbalance.ServiceLoadCalculator;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Weighted first service selector that considers both service load and weight
 */
public class WeightedFirstSelector implements ExtensionServiceSelector {

  private static final Logger log = LoggerFactory.getLogger(WeightedFirstSelector.class);

  @Override
  public SpServiceRegistration select(List<SpServiceRegistration> availableServices,
                                      List<String> labels) {
    if (availableServices == null || availableServices.isEmpty()) {
      throw new IllegalArgumentException("Available services list cannot be null or empty");
    }

    List<SpServiceRegistration> candidates = availableServices;
    if (labels != null && !labels.isEmpty()) {
      List<SpServiceRegistration> affinity = filterServices(availableServices, labels);
      if (!affinity.isEmpty()) {
        candidates = affinity;
      }
    }

    SpServiceRegistration best = candidates.get(0);
    double bestRemaining = Double.NEGATIVE_INFINITY;

    for (SpServiceRegistration service : candidates) {
      float loadPercent = ServiceLoadCalculator.calculateLoad(service);

      // Get dynamic weight calculated from ServiceLoadCalculator
      double dynamicWeight = getDynamicWeight(service);

      double remaining = dynamicWeight * (1.0 - (loadPercent / 100.0));
      if (remaining > bestRemaining) {
        bestRemaining = remaining;
        best = service;
      }
    }
    return best;
  }

  private double getDynamicWeight(SpServiceRegistration service) {
    return service.getWeight();
  }

  /**
   * Filter services that contain any of the specified labels
   * 
   * @param availableServices List of available services
   * @param labels Labels to filter by
   * @return Filtered list of services
   */
  private List<SpServiceRegistration> filterServices(List<SpServiceRegistration> availableServices,
                                                     List<String> labels) {
    return availableServices.stream()
        .filter(service -> containsAnyLabel(service.getLabels(), labels))
        .collect(Collectors.toList());
  }

  /**
   * Check if any label from the list is contained in the service properties
   * 
   * @param serviceLabels Service labels
   * @param labels Labels to check
   * @return True if any label matches
   */
  private static boolean containsAnyLabel(Set<String> serviceLabels, List<String> labels) {
    return serviceLabels != null && serviceLabels.stream().anyMatch(labels::contains);
  }

  @Override
  public Map<SpServiceRegistration, List<InvocableStreamPipesEntity>> allocateSinksAndProcessors(List<InvocableStreamPipesEntity> sinksAndProcessors,
                                                                                                 List<SpServiceRegistration> availableServices) {

    if (sinksAndProcessors == null || sinksAndProcessors.isEmpty()) {
      return new HashMap<>();
    }
    if (availableServices == null || availableServices.isEmpty()) {
      throw new IllegalArgumentException("Available services list cannot be null or empty");
    }

    Map<SpServiceRegistration, List<InvocableStreamPipesEntity>> allocationMap = new HashMap<>();

    // Allocate each element using weighted first selection (considers load and weight)
    for (InvocableStreamPipesEntity element : sinksAndProcessors) {
      SpServiceRegistration selectedService = selectBestService(availableServices);
      allocationMap.computeIfAbsent(selectedService, k -> new ArrayList<>()).add(element);
    }

    return allocationMap;
  }

  @Override
  public Map<SpServiceRegistration, List<AdapterDescription>> allocateAdapters(List<AdapterDescription> adapters,
                                                                               List<SpServiceRegistration> availableServices) {

    if (adapters == null || adapters.isEmpty()) {
      return new HashMap<>();
    }
    if (availableServices == null || availableServices.isEmpty()) {
      throw new IllegalArgumentException("Available services list cannot be null or empty");
    }

    Map<SpServiceRegistration, List<AdapterDescription>> allocationMap = new HashMap<>();

    // Allocate each adapter using weighted first selection (considers load and weight)
    for (AdapterDescription adapter : adapters) {
      SpServiceRegistration selectedService = selectBestService(availableServices);
      allocationMap.computeIfAbsent(selectedService, k -> new ArrayList<>()).add(adapter);
    }

    return allocationMap;
  }

  /**
   * Select the best service based on remaining capacity (weight * (1 - load))
   * 
   * @param availableServices List of available services
   * @return Best service for allocation
   */
  private SpServiceRegistration selectBestService(List<SpServiceRegistration> availableServices) {
    SpServiceRegistration best = availableServices.get(0);
    double bestRemaining = Double.NEGATIVE_INFINITY;

    for (SpServiceRegistration service : availableServices) {
      float loadPercent = ServiceLoadCalculator.calculateLoad(service);
      double dynamicWeight = getDynamicWeight(service);
      double remaining = dynamicWeight * (1.0 - (loadPercent / 100.0));

      if (remaining > bestRemaining) {
        bestRemaining = remaining;
        best = service;
      }
    }

    return best;
  }
}
