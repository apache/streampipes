/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.container.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.orbitz.consul.*;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.model.kv.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConsulUtil {

    private static final String PROTOCOL = "http://";

    private static final String HEALTH_CHECK_INTERVAL = "10s";
    //private static final String HEALTH_CHECK_TTL = "15s";
    //private static final String CONSUL_DEREGISTER_SERIVER_AFTER = "10s";
    private static final String PE_SERVICE_NAME = "pe";

    private static final String CONSUL_ENV_LOCATION = "CONSUL_LOCATION";
    private static final String CONSUL_URL_REGISTER_SERVICE = "v1/agent/service/register";

    static Logger LOG = LoggerFactory.getLogger(ConsulUtil.class);

    public static Consul consulInstance() {
        return Consul.builder().withUrl(consulURL()).build();
    }

    public static void registerPeService(String serviceID, String url, int port) {
        registerService(PE_SERVICE_NAME, serviceID, url, port, "pe");
    }

    public static void registerService(String serviceName, String serviceID, String url, int port, String tag) {
        String body = createServiceRegisterBody(serviceName, serviceID, url, port, tag);
        try {
            registerServiceHttpClient(body);
            LOG.info("Register service " + serviceID, "succesful");
        } catch (UnirestException e) {
            LOG.error("Register service: " + serviceID, " - " + e.toString());
        }
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

    public static Map<String, String>  getPEServices() {
        LOG.info("Load PE service status");
        Consul consul = consulInstance();
        AgentClient agent = consul.agentClient();

        Map<String, Service> services = consul.agentClient().getServices();
        Map<String, HealthCheck> checks = agent.getChecks();

        Map<String, String> peServices = new HashMap();

        for(Map.Entry<String, Service> entry : services.entrySet()) {
            if(entry.getValue().getTags().contains(PE_SERVICE_NAME)) {
                String serviceId = entry.getValue().getId();
                String serviceStatus = "critical";
                if(checks.containsKey("service:" + entry.getKey())) {
                    serviceStatus = checks.get("service:" + entry.getKey()).getStatus();
                }
                LOG.info("Service id: " + serviceId + " service status: " + serviceStatus);
                peServices.put(serviceId, serviceStatus);
            }
        }
        return peServices;
    }

    public static Map<String, String> getKeyValue(String route) {
        Consul consul = consulInstance();
        KeyValueClient keyValueClient = consul.keyValueClient();

        Map<String, String> keyValues = new HashMap<>();

        ConsulResponse<List<Value>> consulResponseWithValues = keyValueClient.getConsulResponseWithValues(route);

        if(consulResponseWithValues.getResponse() != null) {
            for (Value value: consulResponseWithValues.getResponse()) {
                String key = value.getKey();
                String v = "";
                if(value.getValueAsString().isPresent()) {
                    v = value.getValueAsString().get();
                }
                LOG.info("Load key: " + route + " value: " + v);
                keyValues.put(key, v);
            }
        }
        return keyValues;
    }

    public static void updateConfig(String key, String value, String valueType, String description, boolean password) {
        Consul consul = consulInstance();
        KeyValueClient keyValueClient = consul.keyValueClient();

        if(!password) {
            keyValueClient.putValue(key, value);
        } else if(!value.equals("")) {
            keyValueClient.putValue(key, value);
        }

        keyValueClient.putValue(key + "_description", description);
        keyValueClient.putValue(key + "_type", valueType);
        LOG.info("Updated config - key:" + key +
                " value: " + value +
                " description: " + description +
                " type: " + valueType);
    }

    public static List<String> getActivePEServicesEndPoints() {
        LOG.info("Load active PE services endpoints");
        Consul consul = consulInstance();
        HealthClient healthClient = consul.healthClient();
        List<String> endpoints = new LinkedList<>();

        List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances(PE_SERVICE_NAME).getResponse();
        for (ServiceHealth node: nodes) {
            String endpoint = node.getService().getAddress() + ":" + node.getService().getPort();
            LOG.info("Active PE endpoint:" +  endpoint);
            endpoints.add(endpoint);
        }
        return endpoints;
    }

    public static void deregisterService(String serviceId) {
        Consul consul = consulInstance();

        consul.agentClient().deregister(serviceId);
        LOG.info("Deregistered Service: " + serviceId);
    }

    private static int registerServiceHttpClient(String body) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.put(consulURL().toString() + "/" + CONSUL_URL_REGISTER_SERVICE)
                .header("accept", "application/json")
                .body(body)
                .asJson();
        return jsonResponse.getStatus();
    }

    private static String createServiceRegisterBody(String name, String id, String url, int port, String tag) {
        String healthCheckURL = PROTOCOL + url + ":" + port;

        return "{" +
                "\"ID\": \"" + id + "\"," +
                "\"Name\": \"" + name + "\"," +
                "\"Tags\": [" +
                "    \"" + tag + "\"" + ",\"urlprefix-/" +id +" strip=/" +id +"\"" +
                " ]," +
                " \"Address\": \""+ PROTOCOL +url + "\"," +
                " \"Port\":" + port + "," +
                " \"EnableTagOverride\": true" + "," +
                "\"Check\": {" +
                " \"Method\": \"GET\"" + "," +
                " \"http\":" +  "\"" + healthCheckURL + "\"," +
              //  " \"DeregisterCriticalServiceAfter\":" +  "\"" + CONSUL_DEREGISTER_SERIVER_AFTER + "\"," +
                " \"interval\":" + "\"" + HEALTH_CHECK_INTERVAL + "\"" + //"," +
                //" \"TTL\":" + "\"" + HEALTH_CHECK_TTL + "\"" +
                " }" +
                "}";
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
