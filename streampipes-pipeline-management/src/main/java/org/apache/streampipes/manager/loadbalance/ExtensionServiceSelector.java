package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.List;
import java.util.Map;

/**
 * Service selector interface for load balancing
 * Provides methods to allocate pipeline elements and adapters to services
 */
public interface ExtensionServiceSelector {
    
    /**
     * Select a single service for an element
     * @param availableServices List of available services
     * @param labels Labels for service selection
     * @return Selected service registration
     */
    SpServiceRegistration select(List<SpServiceRegistration> availableServices, List<String> labels);
    
    /**
     * Allocate sinks and processors to services
     * @param sinksAndProcessors List of sinks and processors to allocate
     * @param availableServices List of available services
     * @return Map of service registration to list of allocated elements
     */
    Map<SpServiceRegistration, List<InvocableStreamPipesEntity>> allocateSinksAndProcessors(
            List<InvocableStreamPipesEntity> sinksAndProcessors,
            List<SpServiceRegistration> availableServices
    );
    
    /**
     * Allocate adapters to services
     * @param adapters List of adapters to allocate
     * @param availableServices List of available services
     * @return Map of service registration to list of allocated adapters
     */
    Map<SpServiceRegistration, List<AdapterDescription>> allocateAdapters(
            List<AdapterDescription> adapters,
            List<SpServiceRegistration> availableServices
    );
}
