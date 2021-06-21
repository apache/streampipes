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

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceRegistrationRequest;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;
import org.apache.streampipes.svcdiscovery.consul.model.ConsulServiceRegistrationBody;
import org.apache.streampipes.svcdiscovery.consul.model.HealthCheckConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


public class SpConsulServiceDiscovery extends AbstractConsulService implements ISpServiceDiscovery {

  private static final Logger LOG = LoggerFactory.getLogger(SpConsulServiceDiscovery.class);

  private static final String HTTP_PROTOCOL = "http://";
  private static final String COLON = ":";
  private static final String SLASH = "/";
  private static final String HEALTH_CHECK_INTERVAL = "10s";
  private static final String PE_SVC_TAG = "pe";

  @Override
  public void registerService(SpServiceRegistrationRequest req) {
    boolean connected = false;

    while (!connected) {
      LOG.info("Trying to register service at Consul with svcGroup={}, svcId={} host={}, port={}. ", req.getSvcGroup(), req.getSvcId(), req.getHost(), req.getPort());
      ConsulServiceRegistrationBody svcRegistration = createRegistrationBody(req);
      connected = registerServiceHttpClient(svcRegistration);

      if (!connected) {
        LOG.info("Retrying in 1 second");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
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
    Consul consul = consulInstance();
    HealthClient healthClient = consul.healthClient();
    List<String> endpoints = new LinkedList<>();
    List<ServiceHealth> nodes;

    if (!restrictToHealthy) {
      nodes = healthClient.getAllServiceInstances(svcGroup).getResponse();
    } else {
      nodes = healthClient.getHealthyServiceInstances(svcGroup).getResponse();
    }
    for (ServiceHealth node : nodes) {
      if (node.getService().getTags().containsAll(filterByTags)) {
        String endpoint = node.getService().getAddress() + ":" + node.getService().getPort();
        LOG.info("Active " + svcGroup + " endpoint: " + endpoint);
        endpoints.add(endpoint);
      }
    }
    return endpoints;
  }

  @Override
  public Map<String, String> getPeServices() {
    LOG.info("Load pipeline element service status");
    Consul consul = consulInstance();
    AgentClient agent = consul.agentClient();

    Map<String, Service> services = consul.agentClient().getServices();
    Map<String, HealthCheck> checks = agent.getChecks();

    Map<String, String> peSvcs = new HashMap<>();

    for (Map.Entry<String, Service> entry : services.entrySet()) {
      if (entry.getValue().getTags().contains(PE_SVC_TAG)) {
        String serviceId = entry.getValue().getId();
        String serviceStatus = "critical";
        if (checks.containsKey("service:" + entry.getKey())) {
          serviceStatus = checks.get("service:" + entry.getKey()).getStatus();
        }
        LOG.info("Service id: " + serviceId + " service status: " + serviceStatus);
        peSvcs.put(serviceId, serviceStatus);
      }
    }
    return peSvcs;
  }

  @Override
  public void deregisterService(String svcId) {
    Consul consul = consulInstance();
    LOG.info("Deregister service: " + svcId);
    consul.agentClient().deregister(svcId);
  }

  /**
   * PUT REST call to Consul API to register new service.
   *
   * @param svcRegistration   service registration object used to register service endpoint
   * @return                  success or failure of service registration
   */
  private boolean registerServiceHttpClient(ConsulServiceRegistrationBody svcRegistration) {
    try {
      String endpoint = makeConsulEndpoint();
      String body = JacksonSerializer.getObjectMapper().writeValueAsString(svcRegistration);

      Request.Put(endpoint)
              .addHeader("accept", "application/json")
              .body(new StringEntity(body))
              .execute();

      return true;
    } catch (Exception e) {
      LOG.error("Could not register service at Consul");
    }
    return false;
  }

  private ConsulServiceRegistrationBody createRegistrationBody(SpServiceRegistrationRequest req) {
    ConsulServiceRegistrationBody body = new ConsulServiceRegistrationBody();
    body.setID(req.getSvcId());
    body.setName(req.getSvcGroup());
    body.setTags(asString(req.getTags()));
    body.setAddress(HTTP_PROTOCOL + req.getHost());
    body.setPort(req.getPort());
    body.setEnableTagOverride(true);
    body.setCheck(new HealthCheckConfiguration(
            "GET",
            (HTTP_PROTOCOL + req.getHost() + COLON + req.getPort() + req.getHealthCheckPath()),
            HEALTH_CHECK_INTERVAL,
            "60s"));

    return body;
  }
}
