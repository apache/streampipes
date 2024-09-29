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
package org.apache.streampipes.manager.health;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceHealthCheck implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceHealthCheck.class);

  private final ServiceRegistrationManager serviceRegistrationManager;
  private final int maxUnhealthyDurationBeforeRemovalMs;

  public ServiceHealthCheck() {
    var storage = StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();
    this.serviceRegistrationManager = new ServiceRegistrationManager(storage);
    this.maxUnhealthyDurationBeforeRemovalMs = Environments.getEnvironment()
            .getUnhealthyTimeBeforeServiceDeletionInMillis().getValueOrDefault();
  }

  @Override
  public void run() {
    var registeredServices = getRegisteredServices();
    registeredServices.forEach(this::checkServiceHealth);
  }

  private void checkServiceHealth(SpServiceRegistration service) {
    String healthCheckUrl = makeHealthCheckUrl(service);

    try {
      var request = ExtensionServiceExecutions.extServiceGetRequest(healthCheckUrl);
      var response = request.execute();
      if (response.returnResponse().getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        processUnhealthyService(service);
      } else {
        if (service.getStatus() == SpServiceStatus.UNHEALTHY) {
          serviceRegistrationManager.applyServiceStatus(service.getSvcId(), SpServiceStatus.HEALTHY);
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
      LOG.info("Removing service {} which has been unhealthy for more than {} milliseconds.", service.getSvcId(),
              maxUnhealthyDurationBeforeRemovalMs);
      serviceRegistrationManager.removeService(service.getSvcId());
    }
  }

  private boolean shouldDeleteService(SpServiceRegistration service) {
    var currentTimeMillis = System.currentTimeMillis();
    return (currentTimeMillis - service.getFirstTimeSeenUnhealthy() > maxUnhealthyDurationBeforeRemovalMs);
  }

  private String makeHealthCheckUrl(SpServiceRegistration service) {
    return service.getServiceUrl() + service.getHealthCheckPath();
  }

  private List<SpServiceRegistration> getRegisteredServices() {
    return serviceRegistrationManager.getAllServices();
  }
}
