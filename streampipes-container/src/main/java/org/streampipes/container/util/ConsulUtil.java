package org.streampipes.container.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.orbitz.consul.*;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.cache.ServiceHealthKey;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.agent.Agent;
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
        } catch (UnirestException e) {
            LOG.error("Register Service: " + serviceName, " - " + e.toString());
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
        Consul consul = consulInstance();
        AgentClient agent = consul.agentClient();

        Map<String, Service> services = consul.agentClient().getServices();
        Map<String, HealthCheck> checks = agent.getChecks();

        Map<String, String> peServices = new HashMap();

        for(Map.Entry<String, Service> entry : services.entrySet()) {
            if(entry.getValue().getTags().contains(PE_SERVICE_NAME)) {
                String serviceId = entry.getValue().getId();
                String serviceStatus = "No service found!";
                if(checks.containsKey("service:" + entry.getKey())) {
                    serviceStatus = checks.get("service:" + entry.getKey()).getStatus();
                }
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
                keyValues.put(key, v);
            }
        }
        return keyValues;
    }

    public static void updateConfig(String key, String value, String description) {
        Consul consul = consulInstance();
        KeyValueClient keyValueClient = consul.keyValueClient();

        keyValueClient.putValue(key, value);
        keyValueClient.putValue(key + "_description", description);

    }

    //Work in process
    public static void getActivePEServicesRdfEndPoints() {
        Consul consul = consulInstance();
        HealthClient healthClient = consul.healthClient();

        List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances(PE_SERVICE_NAME).getResponse();
    }


    private static int registerServiceHttpClient(String body) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.put(consulURL().toString() + "/" + CONSUL_URL_REGISTER_SERVICE)
                .header("accept", "application/json")
                .body(body)
                .asJson();
        return jsonResponse.getStatus();
    }

    private static String createServiceRegisterBody(String name, String id, String url, int port, String tag) {
        String healthCheckURL = url + ":" + port;

        return "{" +
                "\"ID\": \"" + id + "\"," +
                "\"Name\": \"" + name + "\"," +
                "\"Tags\": [" +
                "    \"" + tag + "\"" +
                " ]," +
                " \"Address\": \""+ url + "\"," +
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
