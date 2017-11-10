package org.streampipes.container.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulServiceDiscovery {

    private static final String HEALTH_CHECK_INTERVAL = "10s";
    //private static final String HEALTH_CHECK_TTL = "15s";

    private static final String CONSUL_URL_REST_API = "http://localhost:8500/v1/";
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
