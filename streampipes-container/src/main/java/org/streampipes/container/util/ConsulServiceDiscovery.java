package org.streampipes.container.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.cache.ServiceHealthKey;
import com.orbitz.consul.model.agent.Agent;
import com.orbitz.consul.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ConsulServiceDiscovery {

    private static final String HEALTH_CHECK_INTERVAL = "10s";
    //private static final String HEALTH_CHECK_TTL = "15s";


    //TODO: Change URL
    private static final String CONSUL_URL = "http://localhost:8500/";
    private static final String CONSUL_URL_REST_API = CONSUL_URL + "v1/";
    private static final String CONSUL_URL_REGISTER_SERVICE = CONSUL_URL_REST_API + "agent/service/register";

    static Logger LOG = LoggerFactory.getLogger(ConsulServiceDiscovery.class);


    public static void registerPeService(String serviceName, String serviceID, String url, int port) {
        registerService(serviceID, serviceName, url, port, "PE");
    }

    public static void registerService(String serviceName, String serviceID, String url, int port, String tag) {
        String body = createServiceRegisterBody(serviceID, serviceName, url, port, tag);
        try {
            registerServiceHttpClient(body);
        } catch (UnirestException e) {
            LOG.error("Register Service: " + serviceName, " - " + e.toString());
        }
    }

    public static void subcribeHealthService() {
        Consul consul = Consul.builder().withUrl(CONSUL_URL).build();
        HealthClient healthClient = consul.healthClient();
        Agent agent = consul.agentClient().getAgent();

        String serviceName = "pe-flink-samples";

        ServiceHealthCache svHealth = ServiceHealthCache.newCache(healthClient, serviceName);

        svHealth.addListener(new ConsulCache.Listener<ServiceHealthKey, ServiceHealth>() {
            @Override
            public void notify(Map<ServiceHealthKey, ServiceHealth> map) {
                System.out.println("ad");
            }
        });
    }

    //Test
    public static void getActivePEServicesRdfEndPoints() {
        Consul consul = Consul.builder().withUrl(CONSUL_URL).build();
        HealthClient healthClient = consul.healthClient();

        List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances("pe-flink-samples").getResponse();
        System.out.println("ad");
    }

    private static int registerServiceHttpClient(String body) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.put(CONSUL_URL_REGISTER_SERVICE)
                .header("accept", "application/json")
                .body(body)
                .asJson();
        return jsonResponse.getStatus();
    }

    private static String createServiceRegisterBody(String id, String name, String url, int port, String tag) {
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
                " \"interval\":" + "\"" + HEALTH_CHECK_INTERVAL + "\"" + //"," +
                //" \"TTL\":" + "\"" + HEALTH_CHECK_TTL + "\"" +
                " }" +
                "}";
    }
}
