package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;

import java.util.List;

public interface LoadBalancer {
    SpServiceRegistration allocation(LoadBalanceResourceUnit<InvocableStreamPipesEntity> loadBalanceResourceUnit, List<SpServiceRegistration> serviceRegistrations, List<String> label);

    SpServiceRegistration allocationPe(LoadBalanceResourceUnit<AdapterDescription> loadBalanceResourceUnit, List<SpServiceRegistration> serviceRegistrations, List<String> label);

    public LoadData getLoadData();

    public LoadData getHistoricalLoadData();

    public void stopPipeline(String pipId);

    public void stopAdapter(String pipId);

    public void doLoadShedding();

    public void updateAll();
}
