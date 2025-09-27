package org.apache.streampipes.manager.loadbalance;


import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnitStats;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;

import java.util.List;
import java.util.Map;

public class LoadData {

    private  Map<String, ServiceLoadDataReport> ServiceLoadData;


    private Map<String, List<LoadBalanceResourceUnitStats>> resourceUnitStats;


    public LoadData(Map<String, ServiceLoadDataReport> ServiceLoadData, Map<String, List<LoadBalanceResourceUnitStats>> resourceUnitStats) {
        this.ServiceLoadData = ServiceLoadData;
        this.resourceUnitStats = resourceUnitStats;
    }


    public ServiceLoadDataReport getServiceUsage(String id){
        return ServiceLoadData.get(id);
    }

    public List<LoadBalanceResourceUnitStats> getResourceUnitStats(String id){
        return resourceUnitStats.get(id);
    }
}
