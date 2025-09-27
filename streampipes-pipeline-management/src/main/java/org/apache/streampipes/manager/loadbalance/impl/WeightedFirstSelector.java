package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.commons.prometheus.service.ElementServiceStats;
import org.apache.streampipes.manager.loadbalance.ExtensionServiceSelector;
import org.apache.streampipes.manager.loadbalance.ServiceLoadCalculator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WeightedFirstSelector implements ExtensionServiceSelector {
    @Override
    public SpServiceRegistration select(List<SpServiceRegistration> availableServices, List<String> labels) {
        List<SpServiceRegistration> candidates = availableServices;
        if (labels != null && !labels.isEmpty()) {
            List<SpServiceRegistration> affinity = filterServices(availableServices, labels);
            if (!affinity.isEmpty()) {
                candidates = affinity;
            }
        }

        SpServiceRegistration best = candidates.get(0);
        double bestRemaining = Double.NEGATIVE_INFINITY;

        for (SpServiceRegistration s : candidates) {
            float loadPercent = ServiceLoadCalculator.calculateLoad(s.getSvcId());

            // 获取从 ServiceLoadCalculator 计算出的动态权重
            double dynamicWeight = getDynamicWeight(s.getSvcId(), s);

            double remaining = dynamicWeight * (1.0 - (loadPercent / 100.0));
            if (remaining > bestRemaining) {
                bestRemaining = remaining;
                best = s;
            }
        }
        return best;
    }

    /**
     * 获取服务的动态权重
     * 优先使用从 ServiceLoadCalculator 计算出的动态权重，
     * 如果没有则使用服务注册时的静态权重作为兜底
     */
    private double getDynamicWeight(String serviceId, SpServiceRegistration service) {
        ElementServiceStats stats = ElementServiceStats.get(serviceId);
        if (stats != null && stats.weight > 0) {
            // 使用动态计算的权重（基于实际CPU和内存资源）
            return stats.weight;
        }

        // 兜底：使用服务注册时的静态权重
        return service.getWeight();
    }

    private List<SpServiceRegistration> filterServices(List<SpServiceRegistration> availableServices, List<String> labels) {
        return availableServices.stream()
                .filter(service -> containsAnyLabel(service.getLabels(), labels))
                .collect(Collectors.toList());
    }

    private static boolean containsAnyLabel(Set<String> properties, List<String> labels) {
        return properties != null && properties.stream().anyMatch(labels::contains);
    }
}