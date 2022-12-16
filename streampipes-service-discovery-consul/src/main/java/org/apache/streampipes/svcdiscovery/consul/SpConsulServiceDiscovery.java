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

import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceRegistrationRequest;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTagPrefix;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SpConsulServiceDiscovery extends AbstractConsulService implements ISpServiceDiscovery {

  private static final Logger LOG = LoggerFactory.getLogger(SpConsulServiceDiscovery.class);

  private static final String HTTP_PROTOCOL = "http://";
  private static final String COLON = ":";
  private static final String HEALTH_CHECK_INTERVAL = "10s";

  @Override
  public void registerService(SpServiceRegistrationRequest req) {
    consulInstance().agentClient().register((createRegistrationBody(req)));
    LOG.info("Successfully registered service at Consul: " + req.getSvcId());
  }

  private List<String> asString(List<SpServiceTag> tags) {
    return tags.stream().map(SpServiceTag::asString).collect(Collectors.toList());
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
  public List<String> getServiceEndpoints(String svcGroup, boolean restrictToHealthy, List<String> filterByTags) {
    return ConsulHealthServiceManager.INSTANCE.getServiceEndpoints(svcGroup, restrictToHealthy, filterByTags);
  }

  @Override
  public Map<String, String> getExtensionsServiceGroups() {
    LOG.info("Load pipeline element service status");
    Consul consul = consulInstance();
    AgentClient agent = consul.agentClient();

    Map<String, Service> services = consul.agentClient().getServices();
    Map<String, HealthCheck> checks = agent.getChecks();

    Map<String, String> peSvcs = new HashMap<>();

    for (Map.Entry<String, Service> entry : services.entrySet()) {
      if (hasExtensionsTag(entry.getValue().getTags())) {
        String serviceId = entry.getValue().getId();
        String serviceStatus = "critical";
        if (checks.containsKey("service:" + entry.getKey())) {
          serviceStatus = checks.get("service:" + entry.getKey()).getStatus();
        }
        LOG.info("Service id: " + serviceId + " service status: " + serviceStatus);
        String serviceGroup = extractServiceGroup(entry.getValue().getTags());
        peSvcs.put(serviceGroup, serviceStatus);
      }
    }
    return peSvcs;
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

  @Override
  public void deregisterService(String svcId) {
    Consul consul = consulInstance();
    LOG.info("Deregister service: " + svcId);
    consul.agentClient().deregister(svcId);
  }

  private Registration createRegistrationBody(SpServiceRegistrationRequest req) {
    return ImmutableRegistration.builder()
        .id(req.getSvcId())
        .name(req.getSvcGroup())
        .port(req.getPort())
        .address(HTTP_PROTOCOL + req.getHost())
        .check(ImmutableRegCheck.builder()
            .http(HTTP_PROTOCOL + req.getHost() + COLON + req.getPort() + req.getHealthCheckPath())
            .interval(HEALTH_CHECK_INTERVAL)
            .deregisterCriticalServiceAfter("120s")
            .status("passing")
            .build())
        .tags(asString(req.getTags()))
        .enableTagOverride(true)
        .build();
  }
}
