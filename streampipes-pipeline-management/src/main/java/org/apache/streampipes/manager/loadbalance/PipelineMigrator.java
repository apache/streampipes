package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.List;

public interface PipelineMigrator {
    void doLoadShedding(List<SpServiceRegistration> spServiceRegistrations);
}
