package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;

import java.util.List;

public interface LoadBalancer {
    SpServiceRegistration allocation(List<SpServiceRegistration> serviceRegistrations, List<String> label);

    void doLoadShedding();
}
