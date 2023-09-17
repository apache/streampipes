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
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpServiceDiscoveryCore implements ISpServiceDiscovery {

  private static final Logger LOG = LoggerFactory.getLogger(SpServiceDiscoveryCore.class);
  private static final int MAX_RETRIES = 3;

  private final CRUDStorage<String, SpServiceRegistration> serviceStorage;

  public SpServiceDiscoveryCore() {
    this.serviceStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();
  }

  @Override
  public List<String> getActivePipelineElementEndpoints() {
    LOG.info("Discovering active pipeline element service endpoints");
    return getServiceEndpoints(DefaultSpServiceTypes.EXT, true,
        Collections.singletonList(DefaultSpServiceTags.PE.asString()));
  }

  @Override
  public List<String> getServiceEndpoints(String serviceGroup,
                                          boolean restrictToHealthy,
                                          List<String> filterByTags) {
    List<SpServiceRegistration> activeServices = findService(0);

    return activeServices
        .stream()
        .filter(service -> allFiltersSupported(service, filterByTags))
        .filter(service -> !restrictToHealthy
            || service.isHealthy())
        .map(this::makeServiceUrl)
        .collect(Collectors.toList());
  }

  private String makeServiceUrl(SpServiceRegistration service) {
    return service.getScheme() + "://" + service.getHost() + ":" + service.getPort();
  }

  private boolean allFiltersSupported(SpServiceRegistration service,
                                      List<String> filterByTags) {
    return new HashSet<>(service.getTags())
        .stream()
        .anyMatch(tag -> filterByTags.contains(tag.asString()));
  }

  private List<SpServiceRegistration> findService(int retryCount) {
    var services = serviceStorage.getAll();
    if (services.isEmpty()) {
      if (retryCount < MAX_RETRIES) {
        try {
          retryCount++;
          LOG.info("Could not find any extensions services, retrying ({}/{})", retryCount, MAX_RETRIES);
          TimeUnit.MILLISECONDS.sleep(1000);
          return findService(retryCount);
        } catch (InterruptedException e) {
          e.printStackTrace();
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
