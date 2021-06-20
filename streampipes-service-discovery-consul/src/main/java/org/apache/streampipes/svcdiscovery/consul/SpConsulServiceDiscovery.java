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
  public void registerService(String svcGroup,
                              String svcId,
                              String host,
                              int port,
                              List<SpServiceTag> tags) {
    boolean connected = false;

    while (!connected) {
      LOG.info("Trying to register service at Consul with svcGroup={}, svcId={} host={}, port={}. ", svcGroup, svcId, host, port);
      ConsulServiceRegistrationBody svcRegistration = createRegistrationBody(svcGroup, svcId, host, port, asString(tags));
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
    LOG.info("Successfully registered service at Consul: " + svcId);
  }

  private List<String> asString(List<SpServiceTag> tags) {
    return tags.stream().map(SpServiceTag::asString).collect(Collectors.toList());
  }

  @Override
  public List<String> getActivePeEndpoints() {
    LOG.info("Load active pipeline element service endpoints");
    return getServiceEndpoints(PE_SVC_TAG, true, Collections.singletonList(PE_SVC_TAG));
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

//      Consul client = Consul.builder().withHostAndPort(HostAndPort.fromParts("localhost", 8500)).build();
//      AgentClient agentClient = client.agentClient();
//
//      String serviceId = "2";
//      Registration service = ImmutableRegistration.builder()
//              .id(serviceId)
//              .name(svcRegistration.getName())
//              .port(svcRegistration.getPort())
//              .address("http://" + InetAddress.getLocalHost().getHostAddress())
//              .check(Registration.RegCheck.http(InetAddress.getLocalHost().getHostAddress() + COLON + svcRegistration.getPort(), 10000))
//                   //.check(Registration.RegCheck.ttl(3L)) // registers with a TTL of 3 seconds
//              .tags(Collections.singletonList("tag1"))
//              .meta(Collections.singletonMap("version", "1.0"))
//              .build();
//
//      agentClient.register(service);

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

  private static ConsulServiceRegistrationBody createRegistrationBody(String svcGroup, String id, String host,
                                                                      int port, List<String> tags) {
    ConsulServiceRegistrationBody body = new ConsulServiceRegistrationBody();
    body.setID(id);
    body.setName(svcGroup);
    body.setTags(tags);
    body.setAddress(HTTP_PROTOCOL + host);
    body.setPort(port);
    body.setEnableTagOverride(true);
    body.setCheck(new HealthCheckConfiguration("GET",
            (HTTP_PROTOCOL + host + COLON + port), HEALTH_CHECK_INTERVAL));

    return body;
  }
}
