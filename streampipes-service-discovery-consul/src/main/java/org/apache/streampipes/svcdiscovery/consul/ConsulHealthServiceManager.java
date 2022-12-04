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
package org.apache.streampipes.svcdiscovery.consul;

import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;

import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.option.QueryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public enum ConsulHealthServiceManager {

  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(ConsulHealthServiceManager.class);
  private static final int MAX_RETRIES = 3;
  private final Consul client;
  private final Map<String, ServiceHealthCache> serviceHealthCaches;

  ConsulHealthServiceManager() {
    serviceHealthCaches = new HashMap<>();
    client = new ConsulProvider().consulInstance();
    initializeAll();
  }

  public void initializeAll() {
    initialize(DefaultSpServiceGroups.CORE);
    initialize(DefaultSpServiceGroups.EXT);
  }

  public void initialize(String serviceGroup) {
    HealthClient healthClient = client.healthClient();
    ServiceHealthCache svHealth = ServiceHealthCache.newCache(healthClient, serviceGroup, false, 9, QueryOptions.BLANK);
    svHealth.start();
    try {
      svHealth.awaitInitialized(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    serviceHealthCaches.put(serviceGroup, svHealth);
  }

  public List<String> getServiceEndpoints(String serviceGroup,
                                          boolean restrictToHealthy,
                                          List<String> filterByTags) {
    List<ServiceHealth> activeServices = findService(serviceGroup, 0);

    return activeServices
        .stream()
        .filter(service -> allFiltersSupported(service, filterByTags))
        .filter(service -> !restrictToHealthy
            || service.getChecks().stream().allMatch(check -> check.getStatus().equals("passing")))
        .map(this::makeServiceUrl)
        .collect(Collectors.toList());
  }

  private String makeServiceUrl(ServiceHealth service) {
    return service.getService().getAddress() + ":" + service.getService().getPort();
  }

  private boolean allFiltersSupported(ServiceHealth service,
                                      List<String> filterByTags) {
    return service.getService().getTags().containsAll(filterByTags);
  }

  private List<ServiceHealth> findService(String serviceGroup, int retryCount) {

    if (serviceHealthCaches.containsKey(serviceGroup)
        && serviceHealthCaches.get(serviceGroup).getMap() != null) {
      ServiceHealthCache cache = serviceHealthCaches.get(serviceGroup);
      return cache
          .getMap()
          .values()
          .stream()
          .filter((value) -> value.getService().getService().equals(serviceGroup))
          .collect(Collectors.toList());
    } else {
      if (retryCount < MAX_RETRIES) {
        try {
          retryCount++;
          TimeUnit.SECONDS.sleep(1);
          return findService(serviceGroup, retryCount);
        } catch (InterruptedException e) {
          e.printStackTrace();
          return Collections.emptyList();
        }
      } else {
        return Collections.emptyList();
      }
    }
  }

  public Consul consulInstance() {
    return this.client;
  }
}
