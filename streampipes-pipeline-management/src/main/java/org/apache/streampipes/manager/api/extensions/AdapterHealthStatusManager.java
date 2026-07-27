/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.manager.api.extensions;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterHealthStatus;
import org.apache.streampipes.model.connect.adapter.HealthCheckStatus;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AdapterHealthStatusManager {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterHealthStatusManager.class);
  private static final Set<String> SUPPORTED_ADAPTER_APP_IDS = Set.of(
      "org.apache.streampipes.connect.iiot.protocol.stream.kafka",
      "org.apache.streampipes.connect.iiot.adapters.opcua",
      "org.apache.streampipes.connect.iiot.protocol.stream.mqtt"
  );

  private final IExtensionsServiceStorage extensionsServiceStorage;
  private final ExtensionServiceRequestManager requestManager;
  private final SpResourceManager resourceManager;
  private final ObjectMapper objectMapper;

  public AdapterHealthStatusManager(IExtensionsServiceStorage extensionsServiceStorage,
                                    ExtensionServiceRequestManager requestManager,
                                    SpResourceManager resourceManager) {
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.requestManager = requestManager;
    this.resourceManager = resourceManager;
    this.objectMapper = JacksonSerializer.getObjectMapper();
  }

  public List<AdapterHealthStatus> getHealthStatuses(List<AdapterDescription> adapters) {
    var statusesByAdapterId = new HashMap<String, AdapterHealthStatus>();
    var adaptersByService = adapters.stream()
        .filter(adapter -> adapter.getSelectedServiceId() != null || adapter.getSelectedEndpointUrl() != null)
        .collect(Collectors.groupingBy(this::serviceKey));

    adaptersByService.values().forEach(serviceAdapters -> {
      var healthStatuses = fetchHealthStatuses(serviceAdapters.get(0));
      if (healthStatuses.isEmpty()) {
        serviceAdapters.forEach(adapter ->
            statusesByAdapterId.put(adapter.getElementId(), createUnavailableStatus(adapter)));
        return;
      }

      var endpointStatuses = healthStatuses.get().stream()
          .collect(Collectors.toMap(AdapterHealthStatus::getAdapterId, status -> status, (first, second) -> first));

      serviceAdapters.forEach(adapter -> {
        var status = endpointStatuses.getOrDefault(adapter.getElementId(), createFallbackStatus(adapter));
        normalizeStatus(status, adapter);
        statusesByAdapterId.put(adapter.getElementId(), status);
      });
    });

    adapters.stream()
        .filter(adapter -> !statusesByAdapterId.containsKey(adapter.getElementId()))
        .forEach(adapter -> statusesByAdapterId.put(adapter.getElementId(), createUnavailableStatus(adapter)));

    return new ArrayList<>(statusesByAdapterId.values());
  }

  public Map<String, HealthCheckStatus> getOverallHealthStatuses(List<AdapterDescription> adapters) {
    var statusesByAdapterId = new HashMap<String, HealthCheckStatus>();
    var adaptersByService = adapters.stream()
        .filter(adapter -> adapter.getSelectedServiceId() != null || adapter.getSelectedEndpointUrl() != null)
        .collect(Collectors.groupingBy(this::serviceKey));

    adaptersByService.values().forEach(serviceAdapters -> {
      var serviceStatuses = fetchOverallHealthStatuses(serviceAdapters.get(0));
      if (serviceStatuses.isEmpty()) {
        serviceAdapters.forEach(adapter ->
            statusesByAdapterId.put(adapter.getElementId(), HealthCheckStatus.UNHEALTHY));
        return;
      }

      serviceAdapters.forEach(adapter -> statusesByAdapterId.put(
          adapter.getElementId(),
          serviceStatuses.get().getOrDefault(adapter.getElementId(), HealthCheckStatus.UNKNOWN)
      ));
    });

    adapters.stream()
        .filter(adapter -> !statusesByAdapterId.containsKey(adapter.getElementId()))
        .forEach(adapter -> statusesByAdapterId.put(adapter.getElementId(), HealthCheckStatus.UNHEALTHY));

    return statusesByAdapterId;
  }

  public AdapterHealthStatus getHealthStatus(AdapterDescription adapter) {
    try {
      var target = ExtensionServiceRequestTargets.adapterHealth(resolveExtensionService(adapter), adapter.getElementId());
      var response = requestManager.request(ExtensionServiceRequests.adapterHealth(target, resourceManager));
      if (response.isSuccess()) {
        var status = objectMapper.readValue(response.responseBody(), AdapterHealthStatus.class);
        normalizeStatus(status, adapter);
        return status;
      }

      LOG.debug("Health request for adapter {} returned status {}", adapter.getElementId(), response.statusCode());
    } catch (IOException | IllegalArgumentException e) {
      LOG.debug("Failed to fetch health status for adapter {}", adapter.getElementId(), e);
    }

    return createUnavailableStatus(adapter);
  }

  public void triggerHealthCheck(AdapterDescription adapter) {
    try {
      var target = ExtensionServiceRequestTargets.adapterHealthTrigger(
          resolveExtensionService(adapter),
          adapter.getElementId()
      );
      requestManager.request(ExtensionServiceRequests.adapterHealthTrigger(target, resourceManager));
    } catch (IOException | IllegalArgumentException e) {
      LOG.error("Failed to trigger health check for adapter {}", adapter.getElementId(), e);
    }
  }

  private Optional<List<AdapterHealthStatus>> fetchHealthStatuses(AdapterDescription adapter) {
    try {
      var service = resolveExtensionService(adapter);
      var target = ExtensionServiceRequestTargets.adapterHealth(service);
      var response = requestManager.request(ExtensionServiceRequests.adapterHealth(target, resourceManager));
      if (response.isSuccess()) {
        return Optional.of(objectMapper.readValue(response.responseBody(), new TypeReference<>() {
        }));
      }

      LOG.debug("Health request to {} returned status {}", service.getServiceUrl(), response.statusCode());
    } catch (IOException | IllegalArgumentException e) {
      LOG.debug("Failed to fetch adapter health statuses from {}", adapter.getSelectedEndpointUrl(), e);
    }

    return Optional.empty();
  }

  private Optional<Map<String, HealthCheckStatus>> fetchOverallHealthStatuses(AdapterDescription adapter) {
    try {
      var service = resolveExtensionService(adapter);
      var target = ExtensionServiceRequestTargets.adapterHealthSummary(service);
      var response = requestManager.request(ExtensionServiceRequests.adapterHealth(target, resourceManager));
      if (response.isSuccess()) {
        return Optional.of(objectMapper.readValue(response.responseBody(), new TypeReference<>() {
        }));
      }

      LOG.debug("Health summary request to {} returned status {}", service.getServiceUrl(), response.statusCode());
    } catch (IOException | IllegalArgumentException e) {
      LOG.debug("Failed to fetch adapter health summary from {}", adapter.getSelectedEndpointUrl(), e);
    }

    return Optional.empty();
  }

  private SpServiceRegistration resolveExtensionService(AdapterDescription adapter) {
    return extensionsServiceStorage.findAll().stream()
        .filter(service -> adapter.getSelectedServiceId() != null
            ? adapter.getSelectedServiceId().equals(service.getSvcId())
            : adapter.getSelectedEndpointUrl().equals(service.getServiceUrl()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Could not resolve extension service for adapter " + adapter.getElementId()
        ));
  }

  private String serviceKey(AdapterDescription adapter) {
    return adapter.getSelectedServiceId() != null
        ? adapter.getSelectedServiceId()
        : adapter.getSelectedEndpointUrl();
  }

  private AdapterHealthStatus createFallbackStatus(AdapterDescription adapter) {
    var status = new AdapterHealthStatus();
    status.setAdapterId(adapter.getElementId());
    status.setAdapterName(adapter.getName());
    status.setBackendHealth(HealthCheckStatus.HEALTHY);
    status.setBackendHealthMessage("Extension service is running");
    status.setDataSourceHealth(HealthCheckStatus.UNKNOWN);
    status.setDataSourceHealthSupported(SUPPORTED_ADAPTER_APP_IDS.contains(adapter.getAppId()));
    status.setDataSourceHealthMessage(status.isDataSourceHealthSupported()
        ? "Waiting for first health check..."
        : "Data source health checks are not supported for this adapter type yet.");
    status.setDataSourceHealthDetails(null);
    status.setConsecutiveFailures(0);
    status.setLastCheckTimestamp(System.currentTimeMillis());
    status.updateOverallStatus();
    return status;
  }

  private AdapterHealthStatus createUnavailableStatus(AdapterDescription adapter) {
    var status = new AdapterHealthStatus();
    status.setAdapterId(adapter.getElementId());
    status.setAdapterName(adapter.getName());
    status.setBackendHealth(HealthCheckStatus.UNHEALTHY);
    status.setBackendHealthMessage("Extension service is not reachable");
    status.setDataSourceHealth(HealthCheckStatus.UNKNOWN);
    status.setDataSourceHealthSupported(SUPPORTED_ADAPTER_APP_IDS.contains(adapter.getAppId()));
    status.setDataSourceHealthMessage(
        "Data source health could not be checked because the extension service is not reachable."
    );
    status.setDataSourceHealthDetails(null);
    status.setConsecutiveFailures(0);
    status.setLastCheckTimestamp(System.currentTimeMillis());
    status.updateOverallStatus();
    return status;
  }

  private void normalizeStatus(AdapterHealthStatus status, AdapterDescription adapter) {
    status.setAdapterId(adapter.getElementId());
    status.setAdapterName(adapter.getName());

    if (!status.isDataSourceHealthSupported()) {
      status.setDataSourceHealth(HealthCheckStatus.UNKNOWN);
      status.setDataSourceHealthMessage("Data source health checks are not supported for this adapter type yet.");
      status.setDataSourceHealthDetails(null);
      status.setConsecutiveFailures(0);
    }

    if (status.getBackendHealth() == null) {
      status.setBackendHealth(HealthCheckStatus.HEALTHY);
      status.setBackendHealthMessage("Extension service is running");
    }

    status.updateOverallStatus();
  }
}
