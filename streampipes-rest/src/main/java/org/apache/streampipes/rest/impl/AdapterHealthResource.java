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

package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterHealthStatus;
import org.apache.streampipes.model.connect.adapter.HealthCheckStatus;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.SpPermissionEvaluator;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/adapter-health")
public class AdapterHealthResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterHealthResource.class);
  private static final String KAFKA_ADAPTER_APP_ID = "org.apache.streampipes.connect.iiot.protocol.stream.kafka";
  private static final String OPC_UA_ADAPTER_APP_ID = "org.apache.streampipes.connect.iiot.adapters.opcua";
  private static final String MQTT_ADAPTER_APP_ID = "org.apache.streampipes.connect.iiot.protocol.stream.mqtt";
  private static final Set<String> SUPPORTED_ADAPTER_APP_IDS = Set.of(
      KAFKA_ADAPTER_APP_ID,
      OPC_UA_ADAPTER_APP_ID,
      MQTT_ADAPTER_APP_ID
  );
  private static final String ADAPTER_HEALTH_PATH = "/api/v1/adapter-health";

  private final IAdapterStorage adapterStorage;
  private final ObjectMapper objectMapper;
  private final HttpClient httpClient;

  public AdapterHealthResource() {
    this.adapterStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage();
    this.objectMapper = new ObjectMapper();
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<List<AdapterHealthStatus>> getAllAdapterHealth() {
    var runningAdapters = adapterStorage.findAll().stream()
        .filter(AdapterDescription::isRunning)
        .filter(adapter -> checkAdapterPermission(adapter, "READ"))
        .toList();

    var groupedByEndpoint = runningAdapters.stream()
        .filter(adapter -> adapter.getSelectedEndpointUrl() != null && !adapter.getSelectedEndpointUrl().isBlank())
        .collect(Collectors.groupingBy(AdapterDescription::getSelectedEndpointUrl));

    var statusesByAdapterId = new HashMap<String, AdapterHealthStatus>();
    groupedByEndpoint.forEach((endpoint, adapters) -> {
      var endpointStatuses = fetchHealthStatuses(endpoint).stream()
          .collect(Collectors.toMap(AdapterHealthStatus::getAdapterId, status -> status, (s1, s2) -> s1));

      adapters.forEach(adapter -> {
        var status = endpointStatuses.get(adapter.getElementId());
        if (status == null) {
          status = createFallbackStatus(adapter);
        }
        normalizeStatus(status, adapter);
        statusesByAdapterId.put(adapter.getElementId(), status);
      });
    });

    runningAdapters.stream()
        .filter(adapter -> !statusesByAdapterId.containsKey(adapter.getElementId()))
        .forEach(adapter -> statusesByAdapterId.put(adapter.getElementId(), createFallbackStatus(adapter)));

    return ok(new ArrayList<>(statusesByAdapterId.values()));
  }

  private List<AdapterHealthStatus> fetchHealthStatuses(String endpointUrl) {
    try {
      var requestBuilder = HttpRequest.newBuilder()
          .uri(URI.create(healthEndpoint(endpointUrl)))
          .timeout(Duration.ofSeconds(10))
          .header("Accept", "application/json")
          .GET();

      var token = AuthTokenUtils.getAuthTokenForCurrentUser();
      if (token != null && !token.isBlank()) {
        requestBuilder.header("Authorization", token);
      }

      var response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        return objectMapper.readValue(response.body(), new TypeReference<>() {
        });
      }

      LOG.debug("Health request to {} returned status {}", endpointUrl, response.statusCode());
    } catch (IOException | InterruptedException | IllegalArgumentException e) {
      LOG.debug("Failed to fetch adapter health statuses from {}", endpointUrl, e);
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
    }

    return List.of();
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

  private String healthEndpoint(String endpointUrl) {
    return endpointUrl.endsWith("/")
        ? endpointUrl + ADAPTER_HEALTH_PATH.substring(1)
        : endpointUrl + ADAPTER_HEALTH_PATH;
  }

  @org.springframework.web.bind.annotation.PostMapping(value = "/{adapterId}/trigger", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<Void> triggerAdapterHealthCheck(@org.springframework.web.bind.annotation.PathVariable String adapterId) {
    try {
      var adapter = adapterStorage.getElementById(adapterId);
      if (adapter != null && adapter.isRunning() && adapter.getSelectedEndpointUrl() != null) {
        var endpointUrl = adapter.getSelectedEndpointUrl();
        var triggerUrl = endpointUrl.endsWith("/")
            ? endpointUrl + ADAPTER_HEALTH_PATH.substring(1) + "/" + adapterId + "/trigger"
            : endpointUrl + ADAPTER_HEALTH_PATH + "/" + adapterId + "/trigger";

        var requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(triggerUrl))
            .timeout(Duration.ofSeconds(10))
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.noBody());

        var token = AuthTokenUtils.getAuthTokenForCurrentUser();
        if (token != null && !token.isBlank()) {
          requestBuilder.header("Authorization", token);
        }

        httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.discarding());
      }
    } catch (IOException | InterruptedException e) {
      LOG.error("Failed to trigger health check for adapter {}", adapterId, e);
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
    }
    return ResponseEntity.ok().build();
  }

  /**
   * required by Spring expression
   */
  public boolean hasReadAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_READ_ADAPTER_VALUE);
  }

  private boolean checkAdapterPermission(AdapterDescription adapterDescription,
                                         String permission) {
    var spPermissionEvaluator = new SpPermissionEvaluator();
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return spPermissionEvaluator.hasPermission(
        authentication,
        adapterDescription.getCorrespondingDataStreamElementId(),
        permission);
  }
}
