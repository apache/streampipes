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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.health.monitoring.model.HealthCheckData;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ExtensionHealthCheck implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionHealthCheck.class);

  private final ResourceProvider resourceProvider;
  private final ExtensionServiceRequestManager extensionRequestManager;
  private final IExtensionsServiceStorage extensionsServiceStorage;
  private final SpResourceManager resourceManager;
  private final List<HealthCheck> registeredHealthChecks;
  private final PipelineRecoveryBackoff pipelineRecoveryBackoff;
  private final Map<String, Integer> consecutiveUnknownTicks = new HashMap<>();

  public ExtensionHealthCheck(ResourceProvider resourceProvider,
                              IExtensionsServiceStorage extensionsServiceStorage,
                              ExtensionServiceRequestManager extensionRequestManager,
                              SpResourceManager resourceManager,
                              List<HealthCheck> registeredHealthChecks) {
    this(
        resourceProvider,
        extensionsServiceStorage,
        extensionRequestManager,
        resourceManager,
        registeredHealthChecks,
        PipelineRecoveryBackoff.DEFAULT_INITIAL_DELAY
    );
  }

  public ExtensionHealthCheck(ResourceProvider resourceProvider,
                              IExtensionsServiceStorage extensionsServiceStorage,
                              ExtensionServiceRequestManager extensionRequestManager,
                              SpResourceManager resourceManager,
                              List<HealthCheck> registeredHealthChecks,
                              Duration healthCheckInterval) {
    this.resourceProvider = resourceProvider;
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.extensionRequestManager = extensionRequestManager;
    this.resourceManager = resourceManager;
    this.registeredHealthChecks = registeredHealthChecks;
    this.pipelineRecoveryBackoff = new PipelineRecoveryBackoff(healthCheckInterval);
  }

  @Override
  public void run() {
    if (ShutdownSignal.INSTANCE.isShutdownRequested()) {
      LOG.debug("Shutdown in progress, skipping extension health check");
      return;
    }
    try {
      var activeResources = resourceProvider.loadActiveResources();
      var activeCoreInstances = resourceProvider.loadActiveInstances(activeResources);

      var activeExtensionInstances = new HashMap<String, ExtensionInstanceHealth>();
      var unreachableServiceIds = new HashSet<String>();
      consecutiveUnknownTicks.keySet().retainAll(activeCoreInstances.keySet());
      activeCoreInstances.keySet().forEach(serviceId -> {
        var health = checkInstances(serviceId);
        if (health.isPresent()) {
          consecutiveUnknownTicks.remove(serviceId);
          activeExtensionInstances.put(serviceId, health.get());
        } else {
          int unknownTicks = consecutiveUnknownTicks.merge(serviceId, 1, Integer::sum);
          if (unknownTicks >= maxUnknownTicks()) {
            // The service answers its health probe but not the instance request, or the
            // service health check lags behind. Do not defer recovery forever.
            LOG.warn("Extension service {} did not answer {} instance health requests in a row, "
                + "treating its instances as not running", serviceId, unknownTicks);
            activeExtensionInstances.put(serviceId, new ExtensionInstanceHealth(Map.of(), Set.of()));
          } else {
            unreachableServiceIds.add(serviceId);
          }
        }
      });

      var healthCheckData = new HealthCheckData(
          resourceProvider,
          activeResources,
          activeCoreInstances,
          activeExtensionInstances,
          unreachableServiceIds
      );
      var healthChecks = getHealthChecks(healthCheckData);
      runHealthChecks(healthChecks);
    } catch (Exception e) {
      LOG.warn("An unhandled error occurred while running health check.", e);
    }
  }

  protected Optional<ExtensionInstanceHealth> checkInstances(String serviceId) {
    return new ExtensionInstanceAvailabilityCheck(
        extensionsServiceStorage, serviceId, extensionRequestManager, resourceManager
    ).checkReachableRunningInstances();
  }

  /**
   * How many consecutive ticks a registered service may leave its instance state unknown
   * before it is treated as not running anything. Aligned with the number of failed probes
   * after which the service health check declares a service unhealthy.
   */
  protected int maxUnknownTicks() {
    return Math.max(1, Environments.getEnvironment().getServiceHealthFailureThreshold().getValueOrDefault());
  }

  private void runHealthChecks(List<HealthCheck> healthChecks) {
    healthChecks.forEach(healthCheck -> {
      if (ShutdownSignal.INSTANCE.isShutdownRequested()) {
        LOG.debug("Shutdown in progress, skipping health check {}", healthCheck.getClass().getSimpleName());
        return;
      }
      try {
        healthCheck.runCheck();
      } catch (Exception e) {
        LOG.warn("An unhandled error occurred while running registered health check {}.",
            healthCheck.getClass().getSimpleName(), e);
      }
    });
  }

  private List<HealthCheck> getHealthChecks(HealthCheckData healthCheckData) {
    var healthChecks = new ArrayList<>(getBuiltInHealthChecks(healthCheckData));
    healthChecks.addAll(registeredHealthChecks);
    return healthChecks;
  }

  protected List<HealthCheck> getBuiltInHealthChecks(HealthCheckData healthCheckData) {
    return List.of(
        new PipelineHealthCheck(
            healthCheckData,
            extensionRequestManager,
            resourceProvider,
            resourceManager,
            pipelineRecoveryBackoff
        ),
        new AdapterHealthCheck(healthCheckData)
    );
  }
}
