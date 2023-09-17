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

import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ServiceHealthCheck implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceHealthCheck.class);

  private static final int MAX_UNHEALTHY_DURATION_BEFORE_REMOVAL_MS = 60000;

  private final CRUDStorage<String, SpServiceRegistration> storage;

  public ServiceHealthCheck() {
    this.storage = StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();
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
      if (response.returnResponse().getStatusLine().getStatusCode() != 200) {
        processUnhealthyService(service);
      } else {
        if (!service.isHealthy()) {
          service.setHealthy(true);
          updateService(service);
        }
      }
    } catch (IOException e) {
      processUnhealthyService(service);
    }
  }

  private void processUnhealthyService(SpServiceRegistration service) {
    if (service.isHealthy()) {
      service.setHealthy(false);
      service.setFirstTimeSeenUnhealthy(System.currentTimeMillis());
      updateService(service);
    }
    if (shouldDeleteService(service)) {
      LOG.info("Removing service {} which has been unhealthy for more than {} seconds.",
          service.getSvcId(), MAX_UNHEALTHY_DURATION_BEFORE_REMOVAL_MS / 1000);
      storage.deleteElement(service);
    }
  }

  private boolean shouldDeleteService(SpServiceRegistration service) {
    var currentTimeMillis = System.currentTimeMillis();
    return (currentTimeMillis - service.getFirstTimeSeenUnhealthy() > MAX_UNHEALTHY_DURATION_BEFORE_REMOVAL_MS);
  }

  private void updateService(SpServiceRegistration service) {
    storage.updateElement(service);
  }

  private String makeHealthCheckUrl(SpServiceRegistration service) {
    return service.getScheme() + "://" + service.getHost() + ":" + service.getPort() + service.getHealthCheckPath();
  }

  private List<SpServiceRegistration> getRegisteredServices() {
    return storage.getAll();
  }
}
