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

import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceRegistrationRequest;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTagPrefix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpServiceDiscoveryCore implements ISpServiceDiscovery {

  private static final Logger LOG = LoggerFactory.getLogger(SpServiceDiscoveryCore.class);
  private static final int MAX_RETRIES = 3;

  private final CRUDStorage<String, SpServiceRegistrationRequest> serviceStorage;

  public SpServiceDiscoveryCore() {
    this.serviceStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();
  }

  @Override
  public void registerService(SpServiceRegistrationRequest serviceRegistrationRequest) {
    // not needed
  }

  @Override
  public List<String> getActivePipelineElementEndpoints() {
    LOG.info("Discovering active pipeline element service endpoints");
    return getServiceEndpoints(DefaultSpServiceGroups.EXT, true,
        Collections.singletonList(DefaultSpServiceTags.PE.asString()));
  }

  @Override
  public List<String> getActiveConnectWorkerEndpoints() {
    LOG.info("Discovering active StreamPipes Connect worker service endpoints");
    return getServiceEndpoints(DefaultSpServiceGroups.EXT, true,
        Collections.singletonList(DefaultSpServiceTags.CONNECT_WORKER.asString()));
  }

  @Override
  public List<String> getServiceEndpoints(String serviceGroup,
                                          boolean restrictToHealthy,
                                          List<String> filterByTags) {
    List<SpServiceRegistrationRequest> activeServices = findService(serviceGroup, 0);

    return activeServices
        .stream()
        .filter(service -> allFiltersSupported(service, filterByTags))
        .filter(service -> !restrictToHealthy
            || service.isHealthy())
        .map(this::makeServiceUrl)
        .collect(Collectors.toList());
  }

  @Override
  public Map<String, String> getExtensionsServiceGroups() {
    return null;
  }

  @Override
  public void deregisterService(String svcId) {
    // not needed
  }

  private List<String> asString(List<SpServiceTag> tags) {
    return tags.stream().map(SpServiceTag::asString).collect(Collectors.toList());
  }

  private boolean hasExtensionsTag(List<String> tags) {
    return tags.stream().anyMatch(tag -> tag.equals(DefaultSpServiceTags.PE.asString())
        || tag.equals(DefaultSpServiceTags.CONNECT_WORKER.asString()));
  }

  private String extractServiceGroup(List<String> tags) {
    String groupTag = tags.stream().filter(tag -> tag.startsWith(SpServiceTagPrefix.SP_GROUP.asString())).findFirst()
        .orElse("unknown service group");
    return groupTag.replaceAll(SpServiceTagPrefix.SP_GROUP.asString() + ":", "");
  }

  private String makeServiceUrl(SpServiceRegistrationRequest service) {
    return service.getHost() + ":" + service.getPort();
  }

  private boolean allFiltersSupported(SpServiceRegistrationRequest service,
                                      List<String> filterByTags) {
    return new HashSet<>(service.getTags()).containsAll(filterByTags);
  }

  private List<SpServiceRegistrationRequest> findService(String serviceGroup,
                                                         int retryCount) {
    var services = serviceStorage.getAll();
    if (services.size() == 0) {
      if (retryCount < MAX_RETRIES) {
        try {
          retryCount++;
          TimeUnit.SECONDS.sleep(10);
          return findService(serviceGroup, retryCount);
        } catch (InterruptedException e) {
          e.printStackTrace();
          return Collections.emptyList();
        }
      } else {
        return Collections.emptyList();
      }
    } else {
      return services;
    }
  }
}
