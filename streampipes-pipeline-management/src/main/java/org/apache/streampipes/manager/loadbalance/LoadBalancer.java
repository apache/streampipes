package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.ResourceUnit;

import java.util.List;

public interface LoadBalancer {
    SpServiceRegistration allocation(ResourceUnit<InvocableStreamPipesEntity> resourceUnit, List<SpServiceRegistration> serviceRegistrations,List<String> label);

    SpServiceRegistration allocationPe(ResourceUnit<AdapterDescription> resourceUnit, List<SpServiceRegistration> serviceRegistrations, List<String> label);

    public LoadData getLoadData();

    public LoadData getHistoricalLoadData();

    public void stopPipeline(String pipId);

    public void stopAdapter(String pipId);

    public void doLoadShedding();

    public void updateAll();
}
