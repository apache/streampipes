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

    public static ServiceLoadDataReport getServiceLoadDataReport(SpServiceRegistration serviceRegistration) {
            try {
                String response = makeRequest(serviceRegistration.getServiceUrl())
                        .execute()
                        .returnContent()
                        .asString();
                return parseLogResponse(response);
            } catch (IOException e) {
                LOG.info("Could not fetch info from endpoint {}", serviceRegistration.getServiceUrl());
            }
        return new ServiceLoadDataReport();
    }

    private static Request makeRequest(String serviceEndpointUrl) {
        return ExtensionServiceExecutions.extServiceGetRequest(makeLogUrl(serviceEndpointUrl));
    }

    private static String makeLogUrl(String baseUrl) {
        return baseUrl + LOG_PATH;
    }

    private static ServiceLoadDataReport parseLogResponse(String response) throws JsonProcessingException {
        return JacksonSerializer.getObjectMapper().readValue(response, ServiceLoadDataReport.class);
    }

}

