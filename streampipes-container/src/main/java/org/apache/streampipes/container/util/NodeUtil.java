package org.apache.streampipes.container.util;/*
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

import com.google.gson.Gson;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.streampipes.container.model.consul.ConsulServiceRegistrationBody;
import org.apache.streampipes.container.model.consul.HealthCheckConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NodeUtil {
    static Logger LOG = LoggerFactory.getLogger(NodeUtil.class);

    private static final String PROTOCOL = "http://";

    private static final String HEALTH_CHECK_INTERVAL = "10s";
    private static final String PE_SERVICE_NAME = "pe";
    private static final String PRIMARY_PE_IDENTIFIER = "primary";
    private static final String SECONDARY_PE_IDENTIFIER = "secondary";
    private static final String NODE_ID_IDENTIFIER = "SP_NODE_ID";
    private static final String SLASH = "/";

    private static final String NODE_CONTROLLER_LOCATION = "SP_NODE_ID";
    private static final String NODE_CONTROLLER_REGISTER_URL = "node/pe/container/register";

    // dummy class to route consul registration request to node controller instead of consul

    public static void registerPeService(String serviceID, String url, int port) {
        String serviceLocationTag = System.getenv(NODE_ID_IDENTIFIER) == null ? PRIMARY_PE_IDENTIFIER : SECONDARY_PE_IDENTIFIER;
        String uniquePEServiceId = url + SLASH + serviceID;
        registerService(PE_SERVICE_NAME, uniquePEServiceId, url, port, Arrays.asList("pe", serviceLocationTag));
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

    private static void registerServiceHttpClient(String body) throws IOException {
//        return Request.Post("http://localhost:7077/node/pe/container/register")
//                .addHeader("accept", "application/json")
//                .body(new StringEntity(body))
//                .execute()
//                .returnResponse()
//                .getStatusLine().getStatusCode();
        HttpClient client = HttpClients.custom().build();
        HttpUriRequest request = RequestBuilder.post()
                .setUri(nodeControllerURL().toString() + SLASH + NODE_CONTROLLER_REGISTER_URL)
                .setEntity(new StringEntity(body))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
        client.execute(request);
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
    }

    private static URL nodeControllerURL() {
        Map<String, String> env = System.getenv();
        URL url = null;

        if (env.containsKey(NODE_CONTROLLER_LOCATION)) {
            try {
                url = new URL("http", env.get(NODE_CONTROLLER_LOCATION), 7077, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                url = new URL("http", "localhost", 7077, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }
}
