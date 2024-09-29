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
package org.apache.streampipes.svcdiscovery;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpServiceDiscoveryCore implements ISpServiceDiscovery {

  private static final Logger LOG = LoggerFactory.getLogger(SpServiceDiscoveryCore.class);
  private static final int MAX_RETRIES = 3;

  private final CRUDStorage<SpServiceRegistration> serviceStorage;

  public SpServiceDiscoveryCore() {
    this.serviceStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();
  }

  @Override
  public Set<SpServiceTag> getCustomServiceTags(boolean restrictToHealthy) {
    var activeServices = findServices(0);
    return activeServices.stream()
            .filter(service -> !restrictToHealthy || service.getStatus() != SpServiceStatus.UNHEALTHY)
            .flatMap(service -> service.getTags().stream())
            .filter(serviceTag -> serviceTag.getPrefix() == SpServiceTagPrefix.CUSTOM).collect(Collectors.toSet());
  }

  @Override
  public List<String> getServiceEndpoints(String serviceGroup, boolean restrictToHealthy, List<String> filterByTags) {
    List<SpServiceRegistration> activeServices = findServices(0);

    return activeServices.stream().filter(service -> allFiltersSupported(service, filterByTags))
            .filter(service -> !restrictToHealthy || service.getStatus() != SpServiceStatus.UNHEALTHY)
            .map(this::makeServiceUrl).collect(Collectors.toList());
  }

  private String makeServiceUrl(SpServiceRegistration service) {
    return service.getServiceUrl();
  }

  /**
   * Checks if all the tags specified in the filter are supported by the service.
   */
  private boolean allFiltersSupported(SpServiceRegistration service, List<String> filterByTags) {
    if (filterByTags.isEmpty()) {
      return true;
    }

    Set<String> serviceTags = service.getTags().stream().map(SpServiceTag::asString).collect(Collectors.toSet());
    return serviceTags.containsAll(filterByTags);
  }

  private List<SpServiceRegistration> findServices(int retryCount) {
    var services = serviceStorage.findAll();
    if (services.isEmpty()) {
      if (retryCount < MAX_RETRIES) {
        try {
          retryCount++;
          LOG.info("Could not find any extensions services, retrying ({}/{})", retryCount, MAX_RETRIES);
          TimeUnit.MILLISECONDS.sleep(1000);
          return findServices(retryCount);
        } catch (InterruptedException e) {
          LOG.warn("Could not find a service currently due to exception {}", e.getMessage());
          return Collections.emptyList();
        }
      } else {
        LOG.info("No service found");
        return Collections.emptyList();
      }
    } else {
      return services;
    }
  }
}
