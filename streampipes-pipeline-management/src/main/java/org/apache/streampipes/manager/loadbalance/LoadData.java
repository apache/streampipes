package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnitStats;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;

import java.util.List;
import java.util.Map;

/**
 * Container for load balancing data including service usage and resource unit statistics
 */
public class LoadData {

    private final Map<String, ServiceLoadDataReport> serviceLoadData;
    private final Map<String, List<LoadBalanceResourceUnitStats>> resourceUnitStats;

    /**
     * Constructor
     * @param serviceLoadData Service load data map
     * @param resourceUnitStats Resource unit statistics map
     */
    public LoadData(Map<String, ServiceLoadDataReport> serviceLoadData, 
                   Map<String, List<LoadBalanceResourceUnitStats>> resourceUnitStats) {
        this.serviceLoadData = serviceLoadData;
        this.resourceUnitStats = resourceUnitStats;
    }

    /**
     * Get service usage data by service ID
     * @param serviceId Service ID
     * @return Service load data report
     */
    public ServiceLoadDataReport getServiceUsage(String serviceId) {
        return serviceLoadData.get(serviceId);
    }

    /**
     * Get resource unit statistics by service ID
     * @param serviceId Service ID
     * @return List of resource unit statistics
     */
    public List<LoadBalanceResourceUnitStats> getResourceUnitStats(String serviceId) {
        return resourceUnitStats.get(serviceId);
    }
}
