package org.apache.streampipes.node.controller.container.management.pe;/*
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

public class PipelineElementManager {

    private static final Logger LOG =
            LoggerFactory.getLogger(PipelineElementManager.class.getCanonicalName());

    private static final String PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String ENV_CONSUL_LOCATION = "CONSUL_LOCATION";
    private static final Integer CONNECT_TIMEOUT = 10000;
    private static PipelineElementManager instance = null;

    private PipelineElementManager() {}

    public static PipelineElementManager getInstance() {
        if (instance == null) {
            synchronized (PipelineElementManager.class) {
                if (instance == null)
                    instance = new PipelineElementManager();
            }
        }
        return instance;
    }

    /**
     * Register pipeline element container
     *
     * @param invocableRegistration
     */
    public void registerPipelineElements(InvocableRegistration invocableRegistration) {
        try {
            Request.Put(makeConsulRegistrationEndpoint())
                    .addHeader("accept", "application/json")
                    .body(new StringEntity(JacksonSerializer
                            .getObjectMapper()
                            .writeValueAsString(invocableRegistration.getConsulServiceRegistrationBody())))
                    .execute();

            // TODO: persistent storage to survive failures
            NodeInfoStorage.getInstance()
                    .retrieveNodeInfo()
                    .setSupportedPipelineElementAppIds(invocableRegistration.getSupportedPipelineElementAppIds());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoke pipeline elements when pipeline is started
     *
     * @param pipelineElementEndpoint
     * @param payload
     * @return
     */
    public String invokePipelineElement(String pipelineElementEndpoint, String payload) {
        LOG.info("Invoking element: {}", pipelineElementEndpoint);
        try {
            Response httpResp = Request
                    .Post(pipelineElementEndpoint)
                    .bodyString(payload, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return httpResp.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return "";
    }

    /**
     * detaches pipeline elements when pipeline is stopped
     */
    // TODO: implement detach pe logic
    public void detachPipelineElement() {

    }

    public void unregisterPipelineElements(){
        NodeInfoStorage.getInstance()
                .retrieveNodeInfo()
                .setSupportedPipelineElementAppIds(Collections.emptyList());
    }

    private String makeConsulRegistrationEndpoint() {
        if (System.getenv(ENV_CONSUL_LOCATION) != null) {
            return PROTOCOL
                    + System.getenv(ENV_CONSUL_LOCATION)
                    + COLON
                    + "8500"
                    + SLASH
                    + "v1/agent/service/register";
        } else {
            return PROTOCOL
                    + "localhost"
                    + COLON
                    + "8500"
                    + SLASH
                    + "v1/agent/service/register";
        }
    }

}
