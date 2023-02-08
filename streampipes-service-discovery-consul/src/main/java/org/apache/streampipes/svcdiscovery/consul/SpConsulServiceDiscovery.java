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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceRegistrationRequest;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTagPrefix;

import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.Check;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
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

  private ConsulHealthServiceManager healthServiceManager;

  public SpConsulServiceDiscovery(Environment environment) {
    super(environment);
    this.healthServiceManager = new ConsulHealthServiceManager(environment);
  }

  @Override
  public void registerService(SpServiceRegistrationRequest req) {
    consulInstance().agentServiceRegister(createRegistrationBody(req));
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
    return healthServiceManager.getServiceEndpoints(svcGroup, restrictToHealthy, filterByTags);
  }

  @Override
  public Map<String, String> getExtensionsServiceGroups() {
    LOG.info("Load pipeline element service status");
    var consul = consulInstance();

    Response<Map<String, Service>> servicesResp = consul.getAgentServices();
    Response<Map<String, Check>> checksResp = consul.getAgentChecks();

    Map<String, String> peSvcs = new HashMap<>();
    if (servicesResp.getValue() != null) {
      var services = servicesResp.getValue();
      var checks = checksResp.getValue();
      for (Map.Entry<String, Service> entry : services.entrySet()) {
        if (hasExtensionsTag(entry.getValue().getTags())) {
          String serviceId = entry.getValue().getId();
          String serviceStatus = "critical";
          if (checks.containsKey("service:" + entry.getKey())) {
            serviceStatus = checks.get("service:" + entry.getKey()).getStatus().name();
          }
          LOG.info("Service id: " + serviceId + " service status: " + serviceStatus);
          String serviceGroup = extractServiceGroup(entry.getValue().getTags());
          peSvcs.put(serviceGroup, serviceStatus);
        }
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
    var consul = consulInstance();
    LOG.info("Deregister service: " + svcId);
    consul.agentServiceDeregister(svcId);
  }

  private NewService createRegistrationBody(SpServiceRegistrationRequest req) {
    var service = new NewService();
    service.setId(req.getSvcId());
    service.setName(req.getSvcGroup());
    service.setPort(req.getPort());
    service.setAddress(HTTP_PROTOCOL + req.getHost());
    service.setCheck(createServiceCheck(req));

    service.setTags(asString(req.getTags()));
    service.setEnableTagOverride(true);

    return service;
  }

  private NewService.Check createServiceCheck(SpServiceRegistrationRequest req) {
    var serviceCheck = new NewService.Check();

    serviceCheck.setHttp(HTTP_PROTOCOL + req.getHost() + COLON + req.getPort() + req.getHealthCheckPath());
    serviceCheck.setInterval(HEALTH_CHECK_INTERVAL);
    serviceCheck.setDeregisterCriticalServiceAfter("120s");
    serviceCheck.setStatus("passing");

    return serviceCheck;
  }
}
