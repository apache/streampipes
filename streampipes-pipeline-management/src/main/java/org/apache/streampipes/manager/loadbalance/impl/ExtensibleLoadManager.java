package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.manager.health.ServiceRegistrationManager;
import org.apache.streampipes.manager.loadbalance.*;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.*;

public class ExtensibleLoadManager implements LoadBalancer {

    ExtensionServiceSelector selector;

    ServiceRegistrationManager serviceManager;

    PipelineMigrator migrator;

    public ExtensibleLoadManager(ExtensionServiceSelector selector, PipelineMigrator pipelineMigrator) {
        this.selector = selector;
        this.migrator = pipelineMigrator;
        serviceManager = new ServiceRegistrationManager(
                StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage());
    }

    @Override
    public SpServiceRegistration allocation(List<SpServiceRegistration> serviceRegistrations, List<String> labels) {
        return selector.select(serviceRegistrations, labels);
    }

    public void doLoadShedding() {
        migrator.doLoadShedding(serviceManager.getAivServices());
    }
}
