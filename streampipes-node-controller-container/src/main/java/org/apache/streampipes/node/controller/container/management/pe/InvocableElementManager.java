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

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.node.controller.container.management.info.NodeInfoStorage;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public class InvocableElementManager implements ElementLifeCyle {

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
            NodeInfoStorage.getInstance()
                    .retrieveNodeInfo()
                    .setSupportedPipelineElementAppIds(registration.getSupportedPipelineElementAppIds());

            LOG.info("Successfully registered pipeline element container");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public org.apache.streampipes.model.Response invoke(String endpoint, String payload) {
        LOG.info("Invoke pipeline element: {}", endpoint);
        try {
            Response httpResp = Request
                    .Post(endpoint)
                    .bodyString(payload, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();

            return new Gson().fromJson(httpResp.returnContent().asString(),
                    org.apache.streampipes.model.Response.class);

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        throw new RuntimeException("Failed to invoke pipeline element: " + endpoint);
    }

    @Override
    public String detach(String endpoint) {
        LOG.info("Detach pipeline element: {}", endpoint);
        try {
            Response httpResp = Request
                    .Delete(endpoint)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();

            String resp = httpResp.returnContent().asString();
            org.apache.streampipes.model.Response streamPipesResp = new Gson().fromJson(resp,
                    org.apache.streampipes.model.Response.class);

            return streamPipesResp.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        throw new IllegalArgumentException("Failed to detach pipeline element: " + endpoint);
    }

    @Override
    public void unregister(){
        // TODO: unregister element from Consul and
        NodeInfoStorage.getInstance()
                .retrieveNodeInfo()
                .setSupportedPipelineElementAppIds(Collections.emptyList());
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
