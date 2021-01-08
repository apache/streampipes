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
package org.apache.streampipes.node.controller.container.management.pe;

import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.container.management.node.NodeManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public class InvocableElementManager implements InvocableLifeCycle {

    private static final Logger LOG =
            LoggerFactory.getLogger(InvocableElementManager.class.getCanonicalName());

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String ENV_CONSUL_LOCATION = "CONSUL_LOCATION";
    private static final Integer CONNECT_TIMEOUT = 10000;
    private static InvocableElementManager instance = null;

    private InvocableElementManager() {}

    public static InvocableElementManager getInstance() {
        if (instance == null) {
            synchronized (InvocableElementManager.class) {
                if (instance == null)
                    instance = new InvocableElementManager();
            }
        }
        return instance;
    }

    @Override
    public void register(InvocableRegistration registration) {
        try {
            Request.Put(makeConsulRegistrationEndpoint())
                    .addHeader("accept", "application/json")
                    .body(new StringEntity(JacksonSerializer
                            .getObjectMapper()
                            .writeValueAsString(registration.getConsulServiceRegistrationBody())))
                    .execute();

            // TODO: persistent storage to survive failures
            NodeManager.getInstance()
                    .retrieveNodeInfoDescription()
                    .setSupportedElements(registration.getSupportedPipelineElementAppIds());

            String url = "http://"
                            + NodeControllerConfig.INSTANCE.getBackendHost()
                            + ":"
                            + NodeControllerConfig.INSTANCE.getBackendPort()
                            + "/"
                            + "streampipes-backend/api/v2/users/admin@streampipes.org/nodes"
                            + "/"
                            + NodeControllerConfig.INSTANCE.getNodeControllerId();

            String desc = JacksonSerializer.getObjectMapper()
                    .writeValueAsString(NodeManager.getInstance().retrieveNodeInfoDescription());

            Request.Put(url)
                    .bodyString(desc, ContentType.APPLICATION_JSON)
//                    .connectTimeout(1000)
//                    .socketTimeout(100000)
                    .execute();

            LOG.info("Successfully registered pipeline element container");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response invoke(String endpoint, String payload) {
        LOG.info("Invoke pipeline element: {}", endpoint);
        try {
            org.apache.http.client.fluent.Response httpResp = Request
                    .Post(endpoint)
                    .bodyString(payload, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return handleResponse(httpResp);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        throw new RuntimeException("Failed to invoke pipeline element: " + endpoint);
    }

    @Override
    public Response detach(String endpoint) {
        LOG.info("Detach pipeline element: {}", endpoint);
        try {
            org.apache.http.client.fluent.Response httpResp = Request
                    .Delete(endpoint)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return handleResponse(httpResp);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        throw new IllegalArgumentException("Failed to detach pipeline element: " + endpoint);
    }

    @Override
    public void unregister(){
        // TODO: unregister element from Consul and
        NodeManager.getInstance()
                .retrieveNodeInfoDescription()
                .setSupportedElements(Collections.emptyList());

        String url = "http://"
                + NodeControllerConfig.INSTANCE.getBackendHost()
                + ":"
                + NodeControllerConfig.INSTANCE.getBackendPort()
                + "/"
                + "streampipes-backend/api/v2/users/admin@streampipes.org/nodes"
                + "/"
                + NodeControllerConfig.INSTANCE.getNodeControllerId();

        try {
            String desc = JacksonSerializer.getObjectMapper()
                    .writeValueAsString(NodeManager.getInstance().retrieveNodeInfoDescription());

            Request.Put(url)
                    .bodyString(desc, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response handleResponse(org.apache.http.client.fluent.Response httpResp) throws JsonSyntaxException,
            IOException {
        String resp = httpResp.returnContent().asString();
        return JacksonSerializer
                .getObjectMapper()
                .readValue(resp, Response.class);
    }

    private String makeConsulRegistrationEndpoint() {
        if (System.getenv(ENV_CONSUL_LOCATION) != null) {
            return HTTP_PROTOCOL
                    + System.getenv(ENV_CONSUL_LOCATION)
                    + COLON
                    + "8500"
                    + SLASH
                    + "v1/agent/service/register";
        } else {
            return HTTP_PROTOCOL
                    + "localhost"
                    + COLON
                    + "8500"
                    + SLASH
                    + "v1/agent/service/register";
        }
    }

}
