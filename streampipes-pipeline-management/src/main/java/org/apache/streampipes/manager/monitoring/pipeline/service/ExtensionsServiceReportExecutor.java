package org.apache.streampipes.manager.monitoring.pipeline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.fluent.Request;
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.model.extensions.svcdiscovery.DefaultSpServiceTags;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ExtensionsServiceReportExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(ExtensionsServiceReportExecutor.class);

    private static final String LOG_PATH = "/serviceMonitor";

    private static final Map<String, ServiceLoadDataReport> map = new ConcurrentHashMap<>();

    public Map<String, ServiceLoadDataReport> run(){
        List<SpServiceRegistration> serviceEndpoints = getActiveExtensionsEndpoints();
        Map<String,ServiceLoadDataReport> serviceLoadDataReportMap = new HashMap<>();
        serviceEndpoints.forEach(serviceEndpoint -> {
            try {
                String response = makeRequest(serviceEndpoint.getServiceUrl())
                        .execute()
                        .returnContent()
                        .asString();
                ServiceLoadDataReport serviceLoadDataReport = parseLogResponse(response);
                serviceLoadDataReportMap.put(serviceEndpoint.getSvcId(),serviceLoadDataReport);
                System.out.println("serviceLoadDataReportMap" + serviceLoadDataReportMap);
            } catch (IOException e) {
                e.printStackTrace();
                LOG.info("Could not fetch info from endpoint {}", serviceEndpoint);
            }
        });
        return serviceLoadDataReportMap;
    }

    private List<SpServiceRegistration> getActiveExtensionsEndpoints() {
        return getServiceEndpoints(
                DefaultSpServiceTypes.EXT,
                true,
                List.of(DefaultSpServiceTags.PE.asString(), DefaultSpServiceTags.CONNECT_WORKER.asString())
        );
    }

    private List<SpServiceRegistration> getServiceEndpoints(String serviceGroup,boolean restrictToHealthy,List<String> filterByTags) {
        List<SpServiceRegistration> activeServices = SpServiceDiscovery.getServiceDiscovery().findAll();
        return activeServices
                .stream()
                .filter(service -> allFiltersSupported(service, filterByTags))
                .filter(service -> !restrictToHealthy
                        || service.getStatus() != SpServiceStatus.UNHEALTHY)
                .collect(Collectors.toList());
    }

    private boolean allFiltersSupported(SpServiceRegistration service,
                                        List<String> filterByTags) {
        return new HashSet<>(service.getTags())
                .stream()
                .anyMatch(tag -> filterByTags.contains(tag.asString()));
    }

    private Request makeRequest(String serviceEndpointUrl) {
        return ExtensionServiceExecutions.extServiceGetRequest(makeLogUrl(serviceEndpointUrl));
    }

    private String makeLogUrl(String baseUrl) {
        return baseUrl + LOG_PATH;
    }

    private ServiceLoadDataReport parseLogResponse(String response) throws JsonProcessingException {
        return JacksonSerializer.getObjectMapper().readValue(response, ServiceLoadDataReport.class);
    }

}

