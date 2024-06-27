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

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.storage.api.CRUDStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServiceRegistrationManager {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistrationManager.class);

  private final CRUDStorage<SpServiceRegistration> storage;

  public ServiceRegistrationManager(CRUDStorage<SpServiceRegistration> storage) {
    this.storage = storage;
  }

  public void applyServiceStatus(String serviceId,
                                 SpServiceStatus status,
                                 long firstTimeSeenUnhealthy) {
    var serviceRegistration = storage.getElementById(serviceId);
    serviceRegistration.setFirstTimeSeenUnhealthy(firstTimeSeenUnhealthy);
    applyServiceStatus(status, serviceRegistration);
  }

  public void applyServiceStatus(String serviceId,
                                 SpServiceStatus status) {
    var serviceRegistration = storage.getElementById(serviceId);
    applyServiceStatus(status, serviceRegistration);
  }

  private void applyServiceStatus(SpServiceStatus status,
                                  SpServiceRegistration serviceRegistration) {
    serviceRegistration.setStatus(status);
    storage.updateElement(serviceRegistration);
    logService(serviceRegistration);
  }

  public void addService(SpServiceRegistration serviceRegistration,
                         SpServiceStatus status) {
    serviceRegistration.setStatus(status);
    storage.persist(serviceRegistration);
    logService(serviceRegistration);
  }

  public List<SpServiceRegistration> getAllServices() {
    return storage.findAll();
  }

  public SpServiceRegistration getService(String serviceId) {
    return storage.getElementById(serviceId);
  }

  public boolean isAnyServiceMigrating() {
    return storage.findAll()
        .stream()
        .anyMatch(service -> service.getStatus() == SpServiceStatus.MIGRATING);
  }

  public void removeService(String serviceId) {
    var serviceRegistration = storage.getElementById(serviceId);
    storage.deleteElement(serviceRegistration);
    LOG.info(
        "Service {} (id={}) has been removed",
        serviceRegistration.getSvcGroup(),
        serviceRegistration.getSvcId())
    ;
  }

  public SpServiceStatus getServiceStatus(String serviceId) {
    return storage.getElementById(serviceId).getStatus();
  }

  private void logService(SpServiceRegistration serviceRegistration) {
    LOG.info(
        "Service {} (id={}) is now in {} state",
        serviceRegistration.getSvcGroup(),
        serviceRegistration.getSvcId(),
        serviceRegistration.getStatus()
    );
  }
}
