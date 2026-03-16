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
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceHealthCheck implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceHealthCheck.class);
  private final ExtensionServiceRequestManager extensionRequestManager;

  private final ServiceRegistrationManager serviceRegistrationManager;
  private final int maxUnhealthyDurationBeforeRemovalMs;

  private final List<SpServiceRegistration> needDeletedServices = new ArrayList<>();

  public ServiceHealthCheck(IExtensionsServiceStorage storage,
                            ExtensionServiceRequestManager extensionRequestManager) {
    this.extensionRequestManager = extensionRequestManager;
    this.serviceRegistrationManager = new ServiceRegistrationManager(storage);
    this.maxUnhealthyDurationBeforeRemovalMs = Environments.getEnvironment()
        .getUnhealthyTimeBeforeServiceDeletionInMillis().getValueOrDefault();
  }

  @Override
  public void run() {
    try {
      Environment env = Environments.getEnvironment();

      var registeredServices = getRegisteredServices();
      registeredServices.forEach(this::checkServiceHealth);
      
      if (env.getLoadManagerEnable().getValueOrDefault()) {
        LoadManager.migrateForHealthCheck(needDeletedServices);
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
      var response = extensionRequestManager.request(ExtensionServiceRequests.serviceHealth(requestTarget));
      if (response.statusCode() != HttpStatus.SC_OK) {
        processUnhealthyService(service);
      } else {
        if (service.getStatus() == SpServiceStatus.UNHEALTHY) {
          serviceRegistrationManager.applyServiceStatus(service.getSvcId(),
                                                        SpServiceStatus.HEALTHY);
        }
      }
    } catch (IOException e) {
      processUnhealthyService(service);
    }
  }

  private void processUnhealthyService(SpServiceRegistration service) {
    if (service.getStatus() == SpServiceStatus.HEALTHY) {
      serviceRegistrationManager.applyServiceStatus(service.getSvcId(), SpServiceStatus.UNHEALTHY,
                                                    System.currentTimeMillis());
    }
    if (shouldDeleteService(service)) {
      LOG.info("Removing service {} which has been unhealthy for more than {} milliseconds.",
               service.getSvcId(), maxUnhealthyDurationBeforeRemovalMs);
      serviceRegistrationManager.removeService(service.getSvcId());
      needDeletedServices.add(service);
    }
  }

  private boolean shouldDeleteService(SpServiceRegistration service) {
    var currentTimeMillis = System.currentTimeMillis();
    return (currentTimeMillis
        - service.getFirstTimeSeenUnhealthy() > maxUnhealthyDurationBeforeRemovalMs);
  }

  private ExtensionServiceRequestTarget makeHealthCheckRequestTarget(SpServiceRegistration service) {
    return ExtensionServiceRequestTargets.serviceHealth(service, service.getHealthCheckPath());
  }

  private List<SpServiceRegistration> getRegisteredServices() {
    return serviceRegistrationManager.getAllServices();
  }
}
