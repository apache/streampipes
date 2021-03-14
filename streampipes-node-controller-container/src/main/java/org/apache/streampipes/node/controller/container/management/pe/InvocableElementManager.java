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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.streampipes.messaging.kafka.SpKafkaProducer;
import org.apache.streampipes.messaging.mqtt.MqttPublisher;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.container.management.node.NodeManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InvocableElementManager implements PipelineElementLifeCycle {

    private static final Logger LOG =
            LoggerFactory.getLogger(InvocableElementManager.class.getCanonicalName());

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String SP_URL = "SP_URL";
    private static final String CONSUL_LOCATION = "CONSUL_LOCATION";
    private static final String CONSUL_REGISTRATION_ROUTE = "v1/agent/service/register";
    private static final int CONSUL_DEFAULT_PORT = 8500;
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
        registerAtConsul(registration);
        updateAndSyncNodeInfoDescription(registration);
        LOG.info("Successfully registered pipeline element container");
    }

    @Override
    public Response invoke(InvocableStreamPipesEntity graph) {
        String endpoint = graph.getBelongsTo();
        LOG.info("Invoke pipeline element: {}", endpoint);
        try {
            org.apache.http.client.fluent.Response httpResp = Request
                    .Post(endpoint)
                    .bodyString(toJson(graph), ContentType.APPLICATION_JSON)
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
        setSupportedPipelineElements(Collections.emptyList());
        try {
            String url = generateBackendEndpoint();
            String desc = toJson(getNodeInfoDescription());
            Request.Put(url)
                    .bodyString(desc, ContentType.APPLICATION_JSON)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response adapt(InvocableStreamPipesEntity graph, String reconfigurationEvent) {
        ObjectMapper mapper = new ObjectMapper();
        Response r = new Response();
        r.setElementId(graph.getElementId());
        r.setSuccess(false);
        try{
            TransportProtocol tp = mapper.readValue(mapper.writeValueAsString(graph.getInputStreams().get(0)
                    .getEventGrounding().getTransportProtocol()), graph.getInputStreams().get(0)
                    .getEventGrounding().getTransportProtocol().getClass());
            tp.getTopicDefinition().setActualTopicName("org.apache.streampipes.control.event.reconfigure."
                    + graph.getDeploymentRunningInstanceId());
            EventProducer pub;
            if(tp instanceof KafkaTransportProtocol){
                pub = new SpKafkaProducer();
                pub.connect(tp);
            }else if (tp instanceof JmsTransportProtocol){
                pub = new ActiveMQPublisher();
                pub.connect(tp);
            } else{
                pub = new MqttPublisher();
                pub.connect(tp);
            }
            pub.publish(reconfigurationEvent.getBytes(StandardCharsets.UTF_8));
            pub.disconnect();
            r.setSuccess(true);
        } catch (JsonProcessingException e) {
            r.setOptionalMessage(e.getMessage());
        }
        return r;
    }

    private void updateAndSyncNodeInfoDescription(InvocableRegistration registration) {
        setSupportedPipelineElements(registration.getSupportedPipelineElementAppIds());
        try {
            String url = generateBackendEndpoint();
            String desc = toJson(getNodeInfoDescription());
            Request.Put(url)
                    .bodyString(desc, ContentType.APPLICATION_JSON)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private NodeInfoDescription getNodeInfoDescription() {
        return NodeManager.getInstance().retrieveNodeInfoDescription();
    }

    private void setSupportedPipelineElements(List<String> supportedPipelineElements) {
        NodeManager.getInstance()
                .retrieveNodeInfoDescription()
                .setSupportedElements(supportedPipelineElements);
    }

    private String generateBackendEndpoint() {
        return HTTP_PROTOCOL
                + NodeControllerConfig.INSTANCE.backendLocation()
                + COLON
                + NodeControllerConfig.INSTANCE.backendPort()
                + SLASH
                + "streampipes-backend/api/v2/users/admin@streampipes.org/nodes"
                + SLASH
                + NodeControllerConfig.INSTANCE.getNodeControllerId();
    }

    private void registerAtConsul(InvocableRegistration registration) {
        try {
            String endpoint = consulURL().toString() + SLASH + CONSUL_REGISTRATION_ROUTE;
            Request.Put(endpoint)
                    .addHeader("accept", "application/json")
                    .body(new StringEntity(toJson(registration.getConsulServiceRegistrationBody())))
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

    private static URL consulURL() {
        Map<String, String> env = System.getenv();
        URL url = null;

        if (env.containsKey(SP_URL)) {
            try {
                URL coreServerUrl = new URL(env.get(SP_URL));
                url = new URL("http", coreServerUrl.getHost(), CONSUL_DEFAULT_PORT, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (env.containsKey(CONSUL_LOCATION)) {
            try {
                url = new URL("http", env.get(CONSUL_LOCATION), CONSUL_DEFAULT_PORT, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                url = new URL("http", "localhost", CONSUL_DEFAULT_PORT, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    private <T> String toJson(T element) {
        try {
            return JacksonSerializer.getObjectMapper().writeValueAsString(element);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException("Could not serialize object: " + element, e);
        }
    }

}
