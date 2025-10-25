package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.List;
import java.util.Map;

/**
 * Pipeline migrator interface for load balancing
 * Implements strategies to redistribute pipeline elements across services
 * to balance load and prevent service overload
 */
public interface PipelineMigrator {
    
    /**
     * Perform load shedding by migrating pipelines between services
     * @param services List of available service registrations
     */
    void doLoadShedding(List<SpServiceRegistration> services);
}
