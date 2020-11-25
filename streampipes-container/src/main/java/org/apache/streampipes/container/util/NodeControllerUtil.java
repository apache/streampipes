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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.container.model.consul.ConsulServiceRegistrationBody;
import org.apache.streampipes.container.model.consul.HealthCheckConfiguration;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class NodeControllerUtil {
    static Logger LOG = LoggerFactory.getLogger(NodeControllerUtil.class);

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String HEALTH_CHECK_INTERVAL = "10s";
    private static final String PE_TAG = "pe";
    private static final String SECONDARY_PE_IDENTIFIER_TAG = "secondary";
    private static final String NODE_CONTROLLER_REGISTER_SVC_URL = "api/v2/node/container/register";

    public static void register(String serviceID, String host, int port,
                                Map<String, SemanticEventProcessingAgentDeclarer> epaDeclarers) {
        register(PE_TAG, makeSvcId(host, serviceID), host, port,
                Arrays.asList(PE_TAG, SECONDARY_PE_IDENTIFIER_TAG), epaDeclarers);
    }

    public static void register(String svcName, String svcId, String host, int port, List<String> tag,
                                Map<String, SemanticEventProcessingAgentDeclarer> epaDeclarers) {
        boolean connected = false;

        while (!connected) {
            LOG.info("Trying to register pipeline element container at node controller: " + makeRegistrationEndpoint());
            String body = createSvcBody(svcName, svcId, host, port, tag, epaDeclarers);
            connected = registerSvcHttpClient(body);

            if (!connected) {
                LOG.info("Retrying in 5 seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info("Successfully registered pipeline element container: " + svcId);
    }

    private static boolean registerSvcHttpClient(String body) {
        String endpoint = makeRegistrationEndpoint();
        try {
            Request.Post(makeRegistrationEndpoint())
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute();
            return true;
        } catch (IOException e) {
            LOG.error("Could not register at " + endpoint);
        }
        return false;
    }

    private static String createSvcBody(String name, String id, String host, int port, List<String> tags,
                                        Map<String, SemanticEventProcessingAgentDeclarer> epaDeclarers) {
        try {
            ConsulServiceRegistrationBody body = new ConsulServiceRegistrationBody();
            String healthCheckURL = HTTP_PROTOCOL + host + COLON + port;
            body.setID(id);
            body.setName(name);
            body.setTags(tags);
            body.setAddress(HTTP_PROTOCOL + host);
            body.setPort(port);
            body.setEnableTagOverride(true);
            body.setCheck(new HealthCheckConfiguration("GET", healthCheckURL, HEALTH_CHECK_INTERVAL));

            InvocableRegistration svcBody = new InvocableRegistration();
            svcBody.setConsulServiceRegistrationBody(body);
            svcBody.setSupportedPipelineElementAppIds(new ArrayList<>(epaDeclarers.keySet()));

            return JacksonSerializer.getObjectMapper().writeValueAsString(svcBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Failure");
    }

    private static String makeRegistrationEndpoint() {
        if (System.getenv("SP_NODE_CONTROLLER_HOST") != null) {
            return HTTP_PROTOCOL
                    + System.getenv("SP_NODE_CONTROLLER_HOST")
                    + COLON
                    + System.getenv("SP_NODE_CONTROLLER_PORT")
                    + SLASH
                    + NODE_CONTROLLER_REGISTER_SVC_URL;
        } else {
            return HTTP_PROTOCOL
                    + "localhost"
                    + COLON
                    + "7077"
                    + SLASH
                    + NODE_CONTROLLER_REGISTER_SVC_URL;
        }
    }

    private static String makeSvcId(String host, String serviceID) {
        return host + SLASH + serviceID;
    }
}
