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

package org.apache.streampipes.container.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.model.kv.Value;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.apache.streampipes.config.model.ConfigItem;
import org.apache.streampipes.container.model.PeConfig;
import org.apache.streampipes.container.model.consul.ConsulServiceRegistrationBody;
import org.apache.streampipes.container.model.consul.HealthCheckConfiguration;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConsulUtil {

  private static final Logger LOG = LoggerFactory.getLogger(ConsulUtil.class);

  private static final String HTTP_PROTOCOL = "http://";
  private static final String COLON = ":";
  private static final String SLASH = "/";
  private static final String HEALTH_CHECK_INTERVAL = "10s";
  private static final String PE_SVC_TAG = "pe";
  private static final String NODE_SVC_TAG = "node";
  private static final String CONSUL_ENV_LOCATION = "CONSUL_LOCATION";
  private static final int CONSUL_DEFAULT_PORT = 8500;
  private static final String CONSUL_NAMESPACE = "/sp/v1/";
  private static final String CONSUL_URL_REGISTER_SERVICE = "v1/agent/service/register";
  private static final String PRIMARY_PE_IDENTIFIER_TAG = "primary";
  private static final String SECONDARY_PE_IDENTIFIER_TAG = "secondary";
  private static final String ENV_NODE_CONTROLLER_ID_KEY = "SP_NODE_CONTROLLER_ID";

  public static Consul consulInstance() {
    return Consul.builder().withUrl(consulURL()).build();
  }

  /**
   * Method called by {@link org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter} to register
   * new pipeline element service endpoint.
   *
   * @param svcId unique service id
   * @param host  host address of pipeline element service endpoint
   * @param port  port of pipeline element service endpoint
   */
  public static void registerPeService(String svcId, String host, int port) {
    registerService(PE_SVC_TAG, makeUniqueSvcId(host, svcId), host, port, "", makeSvcTags());
  }

  /**
   * Method called by {@link org.apache.streampipes.node.controller.container.NodeControllerInit} to
   * register new node controller service endpoint.
   *
   * @param svcId unique service id
   * @param host  host address of node controller service endpoint
   * @param port  port of node controller service endpoint
   * @param healthCheckEndpoint endpoint to perform healthcheck
   */
  public static void registerNodeService(String svcId, String host, int port, String healthCheckEndpoint) {
    registerService(NODE_SVC_TAG, makeUniqueSvcId(host, svcId), host, port, healthCheckEndpoint,
            Collections.singletonList(NODE_SVC_TAG));
  }

  /**
   * Register service at Consul.
   *
   * @param svcGroup  service group for registered service
   * @param svcId     unique service id
   * @param host      host address of service endpoint
   * @param port      port of service endpoint
   * @param tags      tags of service
   */
  public static void registerService(String svcGroup, String svcId, String host, int port,
                                     String healthcheckRoute, List<String> tags) {
    boolean connected = false;

    while (!connected) {
      LOG.info("Trying to register service at Consul: " + svcId);
      ConsulServiceRegistrationBody svcRegistration = createRegistrationBody(svcGroup, svcId, host, port,
              healthcheckRoute, tags);
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

  /**
   * PUT REST call to Consul API to register new service.
   *
   * @param svcRegistration   service registration object used to register service endpoint
   * @return                  success or failure of service registration
   */
  private static boolean registerServiceHttpClient(ConsulServiceRegistrationBody svcRegistration) {
    try {
      String endpoint = makeConsulEndpoint();
      String body = JacksonSerializer.getObjectMapper().writeValueAsString(svcRegistration);

      Request.Put(endpoint)
              .addHeader("accept", "application/json")
              .body(new StringEntity(body))
              .execute();

      return true;
    } catch (IOException e) {
      LOG.error("Could not register service at Consul");
    }
    return false;
  }

  // GET methods

  /**
   * Get all pipeline element service endpoints
   *
   * @return list of pipline element service endpoints
   */
  public static Map<String, Tuple2<String, String>> getPeServices() {
    LOG.info("Load pipeline element service status");
    Consul consul = consulInstance();
    AgentClient agent = consul.agentClient();

    Map<String, Service> services = consul.agentClient().getServices();
    Map<String, HealthCheck> checks = agent.getChecks();

    Map<String, Tuple2<String,String>> peSvcs = new HashMap<>();

    for (Map.Entry<String, Service> entry : services.entrySet()) {
      if (entry.getValue().getTags().contains(PE_SVC_TAG)) {
        String svcId = entry.getValue().getId();

        String svcStatus = "critical";
        String svcTag = PRIMARY_PE_IDENTIFIER_TAG;

        if (checks.containsKey("service:" + entry.getKey())) {
          svcStatus = checks.get("service:" + entry.getKey()).getStatus();
          if (checks.get("service:" + entry.getKey())
                  .getServiceTags().stream().noneMatch(e -> e.contains(PRIMARY_PE_IDENTIFIER_TAG))) {
            svcTag = SECONDARY_PE_IDENTIFIER_TAG;
          }
        }

        LOG.info("Service id: " + svcId + " service status: " + svcStatus + " service tag: " + svcTag);
        peSvcs.put(svcId, new Tuple2<>(svcStatus, svcTag));
      }
    }
    consul.destroy();
    return peSvcs;
  }

  /**
   * Get active pipeline element service endpoints
   *
   * @return list of pipeline element endpoints
   */
  public static List<String> getActivePeEndpoints() {
    LOG.info("Load active pipeline element service endpoints");
    return getServiceEndpoints(PE_SVC_TAG, true, Collections.singletonList(PRIMARY_PE_IDENTIFIER_TAG));
  }

  /**
   * Get active node controller service endpoints
   *
   * @return list of active node controller endpoints
   */
  public static List<String> getActiveNodeEndpoints() {
    LOG.info("Load active node service endpoints");
    return getServiceEndpoints(NODE_SVC_TAG, true, new ArrayList<>());
  }


  /**
   * Get service endpoints
   *
   * @param svcGroup            service group for registered service
   * @param restrictToHealthy   retrieve healthy or all registered services for a service group
   * @param filterByTags        filter param to filter list of registered services
   * @return                    list of services
   */
  public static List<String> getServiceEndpoints(String svcGroup, boolean restrictToHealthy,
                                                 List<String> filterByTags) {
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
    consul.destroy();
    return endpoints;
  }

  /**
   * Get key-value entries for a given route
   *
   * @param route route to retrieve key-value entries in Consul
   * @return      key-value entries
   */
  public static Map<String, String> getKeyValue(String route) {
    Consul consul = consulInstance();
    KeyValueClient keyValueClient = consul.keyValueClient();

    Map<String, String> keyValues = new HashMap<>();

    ConsulResponse<List<Value>> consulResponseWithValues = keyValueClient.getConsulResponseWithValues(route);

    if (consulResponseWithValues.getResponse() != null) {
      for (Value value : consulResponseWithValues.getResponse()) {
        String key = value.getKey();
        String v = "";
        if (value.getValueAsString().isPresent()) {
          v = value.getValueAsString().get();
        }
        keyValues.put(key, v);
      }
    }
    consul.destroy();
    return keyValues;
  }

  /**
   * Get specific value for a key in route
   *
   * @param route   route to retrieve value
   * @param type    data type of return value, e.g. Integer.class, String.class
   * @return        value for key
   */
  public static <T> T getValueForRoute(String route, Class<T> type) {
    try {
      String entry = getKeyValue(route)
              .values()
              .stream()
              .findFirst()
              .orElse(null);

      if (type.equals(Integer.class)) {
        return (T) Integer.valueOf(JacksonSerializer.getObjectMapper().readValue(entry, ConfigItem.class).getValue());
      } else if (type.equals(Boolean.class)) {
        return (T) Boolean.valueOf(JacksonSerializer.getObjectMapper().readValue(entry, ConfigItem.class).getValue());
      } else {
        return type.cast(JacksonSerializer.getObjectMapper().readValue(entry, ConfigItem.class).getValue());
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    throw new IllegalArgumentException("Cannot get entry from Consul");
  }

  // Update

  /**
   * Update key-value config in Consul
   *
   * @param key       key to be updated
   * @param entry     new entry
   * @param password  wether value is a password, here only non-sensitive values are updated
   */
  public static void updateConfig(String key, String entry, boolean password) {
    Consul consul = consulInstance();
    if (!password) {
      LOG.info("Updated config - key:" + key + " value: " + entry);
      consul.keyValueClient().putValue(key, entry);
    }
    consul.destroy();
  }

  /**
   * Deregister registered service endpoint in Consul
   *
   * @param svcId     service id of endpoint to be deregistered
   */
  public static void deregisterService(String svcId) {
    Consul consul = consulInstance();
    LOG.info("Deregister service: " + svcId);
    consul.agentClient().deregister(svcId);
    consul.destroy();
  }

  /**
   * Delete config in Consul
   *
   * @param key     key to be deleted
   */
  public static void deleteConfig(String key) {
    Consul consul = consulInstance();
    LOG.info("Delete config: {}", key);
    consul.keyValueClient().deleteKeys(CONSUL_NAMESPACE + key);
    consul.destroy();
  }

  // Helper methods

  private static URL consulURL() {
    Map<String, String> env = System.getenv();
    URL url = null;

    if (env.containsKey(CONSUL_ENV_LOCATION)) {
      try {
        url = new URL("http", env.get(CONSUL_ENV_LOCATION), CONSUL_DEFAULT_PORT, "");
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    } else {
      try {
        url = new URL("http", "localhost", CONSUL_DEFAULT_PORT, "");
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
    return url;
  }

  private static ConsulServiceRegistrationBody createRegistrationBody(String svcGroup, String id, String host,
                                                                      int port, String healthcheckRoute,
                                                                      List<String> tags) {
    String endpoint = HTTP_PROTOCOL + host + COLON + port;
    if (healthcheckRoute != null) {
      endpoint = endpoint + healthcheckRoute;
    }
    ConsulServiceRegistrationBody body = new ConsulServiceRegistrationBody();
    body.setID(id);
    body.setName(svcGroup);
    body.setTags(tags);
    body.setAddress(HTTP_PROTOCOL + host);
    body.setPort(port);
    body.setEnableTagOverride(true);
    body.setCheck(new HealthCheckConfiguration("GET", endpoint, HEALTH_CHECK_INTERVAL));

    return body;
  }

  private static String makeUniqueSvcId(String host, String serviceID) {
    return host + SLASH + serviceID;
  }

  private static List<String> makeSvcTags() {
    return Arrays.asList(PE_SVC_TAG, System.getenv(ENV_NODE_CONTROLLER_ID_KEY) == null ?
            PRIMARY_PE_IDENTIFIER_TAG : SECONDARY_PE_IDENTIFIER_TAG);
  }

  private static String makeConsulEndpoint() {
    return consulURL().toString() + "/" + CONSUL_URL_REGISTER_SERVICE;
  }
}
