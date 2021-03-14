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
import org.apache.streampipes.container.model.EdgeExtensionsConfig;
import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.container.model.consul.ConsulServiceRegistrationBody;
import org.apache.streampipes.container.model.consul.HealthCheckConfiguration;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class NodeControllerUtil {
    static Logger LOG = LoggerFactory.getLogger(NodeControllerUtil.class);

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String HEALTH_CHECK_INTERVAL = "10s";
    private static final String PE_TAG = "pe";
    private static final String SECONDARY_PE_IDENTIFIER_TAG = "secondary";
    private static final String NODE_CONTROLLER_REGISTER_SVC_URL = "api/v2/node/element/register";

    // StandaloneModelSubmitter for pipeline-elements-all-jvm
    public static void register(String id, String host, int port, List<String> supportedAppIds) {
        String body = createSvcBody(PE_TAG, makeSvcId(host, id), host,
                port, Arrays.asList(PE_TAG, SECONDARY_PE_IDENTIFIER_TAG), supportedAppIds);
        String endpoint = generateNodeControllerEndpointFromEnv();

        registerService(endpoint, body);
    }

    // ExtensionsModelSubmitter for extensions-all
    public static void register(EdgeExtensionsConfig conf, List<String> supportedAppIds) {
        String body = createSvcBody(PE_TAG, makeSvcId(conf.getHost(), conf.getId()), conf.getHost(),
                conf.getPort(), Arrays.asList(PE_TAG, SECONDARY_PE_IDENTIFIER_TAG), supportedAppIds);
        String endpoint = generateNodeControllerEndpointFromConfig(conf);

        registerService(endpoint, body);
    }


    public static void registerService(String endpoint, String body) {
        boolean connected = false;

        while (!connected) {

            connected = registerSvcHttpClient(endpoint, body);

            if (!connected) {
                LOG.info("Retrying in 5 seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info("Successfully registered pipeline element container at: {}", endpoint);
    }

    private static boolean registerSvcHttpClient(String endpoint, String body) {
        LOG.info("Trying to register pipeline element container at node controller: {}", endpoint);
        try {
            Request.Post(endpoint)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute();
            return true;
        } catch (IOException e) {
            LOG.error("Could not register at {}", endpoint);
        }
        return false;
    }

    private static String createSvcBody(String name, String id, String host, int port, List<String> tags,
                                        List<String> supportedAppIds) {
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
            svcBody.setSupportedPipelineElementAppIds(supportedAppIds);

            return JacksonSerializer.getObjectMapper().writeValueAsString(svcBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Failure");
    }

    private static String generateNodeControllerEndpointFromConfig(EdgeExtensionsConfig conf) {
        return HTTP_PROTOCOL
                + conf.getNodeControllerHost()
                + COLON
                + conf.getNodeControllerPort()
                + SLASH
                + NODE_CONTROLLER_REGISTER_SVC_URL;
    }

    private static String generateNodeControllerEndpointFromEnv() {
        String host = "";
        if ("true".equals(System.getenv("SP_DEBUG"))) {
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException("Could not retrieve host IP", e);
            }
        } else {
            host = "streampipes-node-controller";
        }
        return HTTP_PROTOCOL + host + COLON + 7077 + SLASH + NODE_CONTROLLER_REGISTER_SVC_URL;
    }

    private static String makeSvcId(String host, String serviceID) {
        return host + SLASH + serviceID;
    }
}
