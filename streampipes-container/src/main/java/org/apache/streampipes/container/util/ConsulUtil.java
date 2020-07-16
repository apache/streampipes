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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
import org.apache.streampipes.config.consul.ConsulSpConfig;
import org.apache.streampipes.config.model.ConfigItem;
import org.apache.streampipes.container.model.consul.ConsulServiceRegistrationBody;
import org.apache.streampipes.container.model.consul.HealthCheckConfiguration;
import org.apache.streampipes.sdk.helpers.Tuple2;
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
import java.util.stream.Collectors;

public class ConsulUtil {

  private static final String PROTOCOL = "http://";

  private static final String HEALTH_CHECK_INTERVAL = "10s";
  //private static final String HEALTH_CHECK_TTL = "15s";
  //private static final String CONSUL_DEREGISTER_SERIVER_AFTER = "10s";
  private static final String PE_SERVICE_NAME = "pe";
  private static final String NODE_SERVICE_NAME = "node";

  private static final String CONSUL_ENV_LOCATION = "CONSUL_LOCATION";
  private static final String CONSUL_URL_REGISTER_SERVICE = "v1/agent/service/register";

  private static final String PRIMARY_PE_IDENTIFIER = "primary";
  private static final String SECONDARY_PE_IDENTIFIER = "secondary";
  private static final String NODE_ID_IDENTIFIER = "SP_NODE_ID";
  private static final String SLASH = "/";

  static Logger LOG = LoggerFactory.getLogger(ConsulUtil.class);

  public static Consul consulInstance() {
    return Consul.builder().withUrl(consulURL()).build();
  }

  public static void registerPeService(String serviceID, String url, int port) {
    String serviceLocationTag = System.getenv(NODE_ID_IDENTIFIER) == null ? PRIMARY_PE_IDENTIFIER : SECONDARY_PE_IDENTIFIER;
    String uniquePEServiceId = url + SLASH + serviceID;
    registerService(PE_SERVICE_NAME, uniquePEServiceId, url, port, Arrays.asList("pe", serviceLocationTag));
  }

  public static void registerService(String serviceName, String serviceID, String url, int port, String tag) {
    registerService(serviceName, serviceID, url, port, Collections.singletonList(tag));
  }

  public static void registerService(String serviceName, String serviceID, String url, int port, List<String> tag) {
    String body = createServiceRegisterBody(serviceName, serviceID, url, port, tag);
    try {
      registerServiceHttpClient(body);
      LOG.info("Register service " + serviceID +" successful");
    } catch (IOException e) {
      LOG.error("Register service: " + serviceID, " - " + e.toString());
    }
  }

  public static void registerNodeControllerService(String serviceID, String url, int port) {
    String uniqueNodeServiceId = url + SLASH + serviceID;
    registerService(NODE_SERVICE_NAME, uniqueNodeServiceId, url, port, "node");
  }

  //NOT TESTED
 /*   public static void subcribeHealthService() {
        Consul consul = consulInstance();
        HealthClient healthClient = consul.healthClient();
        Agent agent = consul.agentClient().getAgent();


        ServiceHealthCache svHealth = ServiceHealthCache.newCache(healthClient, PE_SERVICE_NAME);

        svHealth.addListener(new ConsulCache.Listener<ServiceHealthKey, ServiceHealth>() {
            @Override
            public void notify(Map<ServiceHealthKey, ServiceHealth> map) {
                System.out.println("ad");
            }
        });
    }
    */

  public static Map<String, Tuple2<String, String>> getPEServices() {
    LOG.info("Load PE service status");
    Consul consul = consulInstance();
    AgentClient agent = consul.agentClient();

    Map<String, Service> services = consul.agentClient().getServices();
    Map<String, HealthCheck> checks = agent.getChecks();

    Map<String, Tuple2<String,String>> peServices = new HashMap<>();

    for (Map.Entry<String, Service> entry : services.entrySet()) {
      if (entry.getValue().getTags().contains(PE_SERVICE_NAME)) {
        String serviceId = entry.getValue().getId();
        String serviceStatus = "critical";
        String serviceTag = "primary";
        if (checks.containsKey("service:" + entry.getKey())) {
          serviceStatus = checks.get("service:" + entry.getKey()).getStatus();
          if (checks.get("service:" + entry.getKey()).getServiceTags().stream().noneMatch(e -> e.contains("primary"))) {
            serviceTag = "secondary";
          }
        }

        LOG.info("Service id: " + serviceId + " service status: " + serviceStatus + " service tag: " + serviceTag);
        peServices.put(serviceId, new Tuple2(serviceStatus, serviceTag));
      }
    }
    return peServices;
  }

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
        LOG.info("Load key: " + route + " value: " + v);
        keyValues.put(key, v);
      }
    }
    return keyValues;
  }


  public static int getElementEndpointPort(String route) {
    String value = ConsulUtil.getKeyValue(route)
            .values()
            .stream()
            .findFirst()
            .get();

//    List<String> list = ConsulUtil.getKeyValue(route)
//            .entrySet()
//            .stream()
//            .map(m -> {
//              return new Gson().fromJson(m.getValue(), ConfigItem.class).getValue();
//            })
//            .collect(Collectors.toList());

    return Integer.parseInt(new Gson().fromJson(value, ConfigItem.class).getValue());
  }

  public static String getElementEndpointHostname(String route) {
    String value = ConsulUtil.getKeyValue(route)
            .values()
            .stream()
            .findFirst()
            .get();

    return new Gson().fromJson(value, ConfigItem.class).getValue();
  }

  public static void updateConfig(String key, String entry, boolean password) {
    Consul consul = consulInstance();
    KeyValueClient keyValueClient = consul.keyValueClient();

    if (!password) {
      keyValueClient.putValue(key, entry);
    }

//        keyValueClient.putValue(key + "_description", description);
//        keyValueClient.putValue(key + "_type", valueType);
    LOG.info("Updated config - key:" + key +
            " value: " + entry);
//        +
//                " description: " + description +
//                " type: " + valueType);
  }

  public static List<String> getActivePEServicesEndPoints() {
    LOG.info("Load active PE service endpoints");
    return getServiceEndpoints(PE_SERVICE_NAME, true, Collections.singletonList(PRIMARY_PE_IDENTIFIER));
  }

  public static List<String> getActiveNodeEndpoints() {
    LOG.info("Load active node service endpoints");
    // TODO set restrictToHealthy to true, this is just for debugging
    return getServiceEndpoints(NODE_SERVICE_NAME, false, new ArrayList<>());
  }

  public static List<String> getServiceEndpoints(String serviceGroup, boolean restrictToHealthy,
                                                 List<String> filterByTags) {
    Consul consul = consulInstance();
    HealthClient healthClient = consul.healthClient();
    List<String> endpoints = new LinkedList<>();

    List<ServiceHealth> nodes;
    if (!restrictToHealthy) {
      nodes = healthClient.getAllServiceInstances(serviceGroup).getResponse();
    } else {
      nodes = healthClient.getHealthyServiceInstances(serviceGroup).getResponse();
    }
    for (ServiceHealth node : nodes) {
      if (node.getService().getTags().containsAll(filterByTags)) {
        String endpoint = node.getService().getAddress() + ":" + node.getService().getPort();
        LOG.info("Active" + serviceGroup + " endpoint:" + endpoint);
        endpoints.add(endpoint);
      }
    }
    return endpoints;
  }

  public static void deregisterService(String serviceId) {
    Consul consul = consulInstance();

    LOG.info("Deregister Service: " + serviceId);
    consul.agentClient().deregister(serviceId);
  }

  public static void deleteKeys(String serviceId) {
    Consul consul = consulInstance();

    LOG.info("Delete keys: {}", serviceId);
    // TODO: namespace should not be hardcoded
    consul.keyValueClient().deleteKeys("/sp/v1/" + serviceId);
  }

  private static int registerServiceHttpClient(String body) throws IOException {
    return Request.Put(consulURL().toString() + "/" + CONSUL_URL_REGISTER_SERVICE)
            .addHeader("accept", "application/json")
            .body(new StringEntity(body))
            .execute()
            .returnResponse()
            .getStatusLine().getStatusCode();
  }

  private static String createServiceRegisterBody(String name, String id, String url, int port, List<String> tags) {
    String healthCheckURL = PROTOCOL + url + ":" + port;
    ConsulServiceRegistrationBody body = new ConsulServiceRegistrationBody();
    body.setID(id);
    body.setName(name);
    body.setTags(tags);
    body.setAddress(PROTOCOL + url);
    body.setPort(port);
    body.setEnableTagOverride(true);
    body.setCheck(new HealthCheckConfiguration("GET", healthCheckURL, HEALTH_CHECK_INTERVAL));

    return new Gson().toJson(body);

//    return "{" +
//            "\"ID\": \"" + id + "\"," +
//            "\"Name\": \"" + name + "\"," +
//            "\"Tags\": [" +
//            "    \"" + tag + "\"" + ",\"urlprefix-/" + id + " strip=/" + id + "\"" +
//            " ]," +
//            " \"Address\": \"" + PROTOCOL + url + "\"," +
//            " \"Port\":" + port + "," +
//            " \"EnableTagOverride\": true" + "," +
//            "\"Check\": {" +
//            " \"Method\": \"GET\"" + "," +
//            " \"http\":" + "\"" + healthCheckURL + "\"," +
//            //  " \"DeregisterCriticalServiceAfter\":" +  "\"" + CONSUL_DEREGISTER_SERIVER_AFTER + "\"," +
//            " \"interval\":" + "\"" + HEALTH_CHECK_INTERVAL + "\"" + //"," +
//            //" \"TTL\":" + "\"" + HEALTH_CHECK_TTL + "\"" +
//            " }" +
//            "}";
  }

  private static URL consulURL() {
    Map<String, String> env = System.getenv();
    URL url = null;

    if (env.containsKey(CONSUL_ENV_LOCATION)) {
      try {
        url = new URL("http", env.get(CONSUL_ENV_LOCATION), 8500, "");
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    } else {
      try {
        url = new URL("http", "localhost", 8500, "");
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
    return url;
  }
}
