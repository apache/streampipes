package org.apache.streampipes.manager.loadbalance.unit;

import org.apache.streampipes.manager.monitoring.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnitStats;
import org.apache.streampipes.model.monitoring.MessageCounter;
import org.apache.streampipes.model.monitoring.SpMetricsEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resource unit statistics scanner
 * Generates statistics for resource units on-demand by scanning metrics from extensions
 */
public class ResourceUnitStatsScanner {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUnitStatsScanner.class);

    /**
     * Generate statistics for all resource units on a service
     * Scans pipelines and calculates metrics for each resource unit
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
            LoadBalanceResourceUnitStats<InvocableStreamPipesEntity> stats = generateStatsForPipelineUnit(unit);
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
     * @param resourceUnit Pipeline resource unit
     * @return Resource unit statistics
     */
    private static LoadBalanceResourceUnitStats<InvocableStreamPipesEntity> generateStatsForPipelineUnit(
            LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit) {
        
        LoadBalanceResourceUnitStats<InvocableStreamPipesEntity> stats = new LoadBalanceResourceUnitStats();
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
        
        logger.debug("Generated stats for resource unit {}: in={}, out={}", 
                    resourceUnit.getId(), totalCountIn, totalCountOut);
        
        return stats;
    }

    /**
     * Generate statistics for an adapter resource unit
     * @param resourceUnit Adapter resource unit
     * @return Resource unit statistics
     */
    private static LoadBalanceResourceUnitStats<AdapterDescription> generateStatsForAdapterUnit(
            LoadBalanceResourceUnit<AdapterDescription> resourceUnit) {
        
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
        
        logger.debug("Generated stats for adapter unit {}: in={}, out={}", 
                    resourceUnit.getId(), totalCountIn, totalCountOut);
        
        return stats;
    }

    /**
     * Generate statistics for multiple services
     * @param services List of services
     * @return Map of service ID to list of statistics
     */
    public static Map<String, List<LoadBalanceResourceUnitStats>> generateStatsForServices(
            List<SpServiceRegistration> services) {
        
        logger.info("Generating statistics for {} services", services.size());
        
        return services.stream()
                .collect(java.util.stream.Collectors.toMap(
                        SpServiceRegistration::getSvcId,
                        ResourceUnitStatsScanner::generateStatsForService
                ));
    }

    /**
     * Generate statistics for a single resource unit
     * @param resourceUnit Pipeline resource unit
     * @return Resource unit statistics
     */
    public static LoadBalanceResourceUnitStats<InvocableStreamPipesEntity> generateStats(
            LoadBalanceResourceUnit<InvocableStreamPipesEntity> resourceUnit) {
        return generateStatsForPipelineUnit(resourceUnit);
    }

    /**
     * Generate statistics for a single adapter unit
     * @param adapterUnit Adapter resource unit
     * @return Resource unit statistics
     */
    public static LoadBalanceResourceUnitStats<AdapterDescription> generateAdapterStats(
            LoadBalanceResourceUnit<AdapterDescription> adapterUnit) {
        return generateStatsForAdapterUnit(adapterUnit);
    }
}

