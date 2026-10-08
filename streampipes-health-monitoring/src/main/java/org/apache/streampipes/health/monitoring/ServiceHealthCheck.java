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
package org.apache.streampipes.health.monitoring;


import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.loadbalance.LoadManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServiceHealthCheck implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceHealthCheck.class);
  private final ExtensionServiceRequestManager extensionRequestManager;

  private final ServiceRegistrationManager serviceRegistrationManager;
  private final int maxUnhealthyDurationBeforeRemovalMs;
  private final int failureThreshold;
  private final Clock clock;
  private final Map<String, Integer> consecutiveFailures = new HashMap<>();

  private final List<SpServiceRegistration> needDeletedServices = new ArrayList<>();
  private final SpResourceManager resourceManager;

  public ServiceHealthCheck(IExtensionsServiceStorage storage,
                            ExtensionServiceRequestManager extensionRequestManager,
                            SpResourceManager resourceManager) {
    this(storage, extensionRequestManager, resourceManager,
        Environments.getEnvironment().getUnhealthyTimeBeforeServiceDeletionInMillis().getValueOrDefault(),
        Environments.getEnvironment().getServiceHealthFailureThreshold().getValueOrDefault(),
        Clock.systemUTC());
  }

  ServiceHealthCheck(IExtensionsServiceStorage storage,
                     ExtensionServiceRequestManager extensionRequestManager,
                     SpResourceManager resourceManager,
                     int maxUnhealthyDurationBeforeRemovalMs,
                     int failureThreshold,
                     Clock clock) {
    if (failureThreshold < 1 || maxUnhealthyDurationBeforeRemovalMs < 0) {
      throw new IllegalArgumentException("Health failure threshold must be positive and grace period non-negative");
    }
    this.extensionRequestManager = extensionRequestManager;
    this.serviceRegistrationManager = new ServiceRegistrationManager(storage);
    this.maxUnhealthyDurationBeforeRemovalMs = maxUnhealthyDurationBeforeRemovalMs;
    this.failureThreshold = failureThreshold;
    this.clock = clock;
    this.resourceManager = resourceManager;
  }

  @Override
  public void run() {
    try {
      Environment env = Environments.getEnvironment();

      var registeredServices = getRegisteredServices();
      consecutiveFailures.keySet().retainAll(registeredServices.stream()
          .map(SpServiceRegistration::getSvcId).collect(Collectors.toSet()));
      registeredServices.forEach(service -> {
        try {
          checkServiceHealth(service);
        } catch (RuntimeException e) {
          LOG.warn("Could not apply health check for service {}", service.getSvcId(), e);
        }
      });
      
      if (env.getLoadManagerEnable().getValueOrDefault()) {
        LoadManager.migrateForHealthCheck(needDeletedServices, resourceManager);
      }
    } catch (Exception e) {
      LOG.error("Error while checking service health", e);
    } finally {
      needDeletedServices.clear();
    }
  }

  private void checkServiceHealth(SpServiceRegistration service) {
    var requestTarget = makeHealthCheckRequestTarget(service);

    try {
      var response = extensionRequestManager.request(
          ExtensionServiceRequests.serviceHealth(requestTarget, resourceManager)
      );
      if (!isCurrent(service)) {
        return;
      }
      if (response.statusCode() != HttpStatus.SC_OK) {
        processUnhealthyService(service);
      } else {
        consecutiveFailures.remove(service.getSvcId());
        if (service.getStatus() == SpServiceStatus.UNHEALTHY || service.getFirstTimeSeenUnhealthy() != 0) {
          var recoveredStatus = service.getStatus() == SpServiceStatus.UNHEALTHY
              ? SpServiceStatus.HEALTHY : service.getStatus();
          serviceRegistrationManager.applyServiceStatus(service, recoveredStatus, 0);
        }
      }
    } catch (IOException e) {
      processUnhealthyService(service);
    }
  }

  private boolean isCurrent(SpServiceRegistration snapshot) {
    var current = serviceRegistrationManager.getService(snapshot.getSvcId());
    if (current == null || !Objects.equals(current.getRev(), snapshot.getRev())) {
      consecutiveFailures.remove(snapshot.getSvcId());
      return false;
    }
    return true;
  }

  private void processUnhealthyService(SpServiceRegistration service) {
    if (!isCurrent(service)) {
      return;
    }
    int failures = consecutiveFailures.compute(service.getSvcId(),
        (id, previous) -> previous == null ? 1 : Math.min(previous + 1, failureThreshold));
    if (failures < failureThreshold) {
      return;
    }
    if (service.getStatus() == SpServiceStatus.HEALTHY || service.getFirstTimeSeenUnhealthy() <= 0) {
      // Reachability must not bypass registration or migration readiness. These services
      // retain their lifecycle state, but still expire after sustained failed probes.
      var failedStatus = service.getStatus() == SpServiceStatus.HEALTHY
          ? SpServiceStatus.UNHEALTHY : service.getStatus();
      serviceRegistrationManager.applyServiceStatus(service, failedStatus, clock.millis());
      // The supplied registration is a storage snapshot and still contains the old timestamp.
      // Start the grace period now; only a subsequent check may remove the service.
      return;
    }
    if (clock.millis() - service.getFirstTimeSeenUnhealthy() >= maxUnhealthyDurationBeforeRemovalMs) {
      LOG.info("Removing service {} which has been unhealthy for at least {} milliseconds.",
               service.getSvcId(), maxUnhealthyDurationBeforeRemovalMs);
      serviceRegistrationManager.removeService(service.getSvcId());
      consecutiveFailures.remove(service.getSvcId());
      needDeletedServices.add(service);
    }
  }

  private ExtensionServiceRequestTarget makeHealthCheckRequestTarget(SpServiceRegistration service) {
    return ExtensionServiceRequestTargets.serviceHealth(service, service.getHealthCheckPath());
  }

  private List<SpServiceRegistration> getRegisteredServices() {
    return serviceRegistrationManager.getAllServices();
  }
}
