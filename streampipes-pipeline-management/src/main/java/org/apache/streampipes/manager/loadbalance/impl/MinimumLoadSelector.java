package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.manager.loadbalance.ExtensionServiceSelector;
import org.apache.streampipes.manager.loadbalance.LoadManager;
import org.apache.streampipes.manager.loadbalance.ServiceLoadCalculator;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service selector that chooses the service with minimum load
 */
public class MinimumLoadSelector implements ExtensionServiceSelector {
    
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

        return serviceRegistrations.get(0);
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
}
