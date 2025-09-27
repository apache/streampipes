package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.manager.loadbalance.ExtensionServiceSelector;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WeightedRandomSelector implements ExtensionServiceSelector {
    @Override
    public SpServiceRegistration select(List<SpServiceRegistration> availableServices, List<String> labels) {
        if(labels.isEmpty()){
            List<SpServiceRegistration> affinityServices = filterServices(availableServices, labels);
            if(affinityServices.isEmpty()){
                return aResAlgorithm(availableServices);
            }
        }

        return aResAlgorithm(availableServices);
    }

    private List<SpServiceRegistration> filterServices(List<SpServiceRegistration> availableServices, List<String> labels) {
        return availableServices.stream()
                .filter(service -> containsAnyLabel(service.getLabels(), labels))
                .collect(Collectors.toList());
    }

    private static boolean containsAnyLabel(Set<String> properties, List<String> labels) {
        return properties.stream()
                .anyMatch(labels::contains);
    }

    private SpServiceRegistration aResAlgorithm(List<SpServiceRegistration> availableServices){
        SpServiceRegistration result = availableServices.get(0);
        double minK = Double.MAX_VALUE;

        for (SpServiceRegistration sample : availableServices) {
            double ki = Math.pow(ThreadLocalRandom.current().nextDouble(), (double) 1 / sample.getWeight());
            if (ki < minK) {
                minK = ki;
                result = sample;
            }
        }

        return result;
    }
}
