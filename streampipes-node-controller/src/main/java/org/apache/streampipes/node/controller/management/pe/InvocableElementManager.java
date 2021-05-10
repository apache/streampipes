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
package org.apache.streampipes.node.controller.management.pe;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;
import org.apache.streampipes.model.resource.Hardware;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.node.NodeManager;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;
import org.apache.streampipes.node.controller.management.resource.utils.ResourceChecker;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class InvocableElementManager implements IPipelineElementLifeCycle {

    private static final Logger LOG =
            LoggerFactory.getLogger(InvocableElementManager.class.getCanonicalName());

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String DOT = ".";
    private static final String SP_URL = "SP_URL";
    private static final String CONSUL_LOCATION = "CONSUL_LOCATION";
    private static final String CONSUL_REGISTRATION_ROUTE = "v1/agent/service/register";
    private static final String RECONFIGURATION_TOPIC = "org.apache.streampipes.control.event.reconfigure";
    private static final int CONSUL_DEFAULT_PORT = 8500;
    private static final Integer CONNECT_TIMEOUT_MS = 10000;
    private static final long RETRY_INTERVAL_MS = 5000;
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
        invokePipelineElementsOnSystemRebootOrRestart();
        LOG.info("Successfully registered pipeline element container");
    }

    @Override
    public Response invoke(InvocableStreamPipesEntity graph) {

        Response response = new Response();
        String endpoint = graph.getBelongsTo();
        String body = toJson(graph);
        LOG.info("Trying to invoke pipeline element: {}", endpoint);

        boolean connected = false;
        while (!connected) {

            connected = post(endpoint, body, response);

            if (!connected) {
                LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Failed to invoke pipeline element: " + endpoint);
                }
            }
        }
        LOG.info("Successfully invoked pipeline element {}", endpoint);
        return response;
    }

    @Override
    public Response detach(String endpoint) {
        LOG.info("Detach pipeline element: {}", endpoint);
        try {
            org.apache.http.client.fluent.Response httpResp = Request
                    .Delete(endpoint)
                    .connectTimeout(CONNECT_TIMEOUT_MS)
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
            String url = generateNodeManagementUpdateEndpoint();
            String desc = toJson(getNodeInfoDescription());
            Request.Put(url)
                    .bodyString(desc, ContentType.APPLICATION_JSON)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response adapt(InvocableStreamPipesEntity graph, PipelineElementReconfigurationEntity reconfigurationEntity) {

        Response response = new Response();
        response.setElementId(graph.getElementId());
        response.setSuccess(false);

        EventProducer pub = getReconfigurationEventProducerFromInvocable(graph);

        byte [] reconfigurationEvent = reconfigurationToByteArray(reconfigurationEntity);
        pub.publish(reconfigurationEvent);
        pub.disconnect();

        adaptPipelineDescription(graph, reconfigurationEntity);

        response.setSuccess(true);
        return response;
    }

    public Response postOffloadRequest(InvocableStreamPipesEntity instanceToOffload){
        Response resp = new Response();
        post(generatePipelineManagementOffloadEndpoint(), toJson(instanceToOffload), resp);
        return resp;
    }

    private void adaptPipelineDescription(InvocableStreamPipesEntity graph, PipelineElementReconfigurationEntity reconfigurationEntity){
        List<StaticProperty> staticProperties = new ArrayList<>();
        graph.getStaticProperties().forEach(sp -> {
            int ind = graph.getStaticProperties().indexOf(sp);
            staticProperties.add(ind, sp);
            reconfigurationEntity.getReconfiguredStaticProperties().forEach(rp -> {
                if(sp.getInternalName().equals(rp.getInternalName())){
                    staticProperties.remove(ind);
                    staticProperties.add(ind, rp);
                }
            });
        });
        graph.setStaticProperties(staticProperties);
        RunningInvocableInstances.INSTANCE.remove(reconfigurationEntity.getDeploymentRunningInstanceId());
        RunningInvocableInstances.INSTANCE.add(reconfigurationEntity.getDeploymentRunningInstanceId(), graph);
    }

    private boolean post(String endpoint, String body, Response response) {
        try {
            org.apache.http.client.fluent.Response httpResp = Request
                    .Post(endpoint)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT_MS)
                    .execute();
            Response resp = handleResponse(httpResp);

            response.setElementId(resp.getElementId());
            response.setSuccess(resp.isSuccess());
            response.setOptionalMessage(resp.getOptionalMessage());

            return true;
        } catch (IOException e) {
            LOG.error("Could not invoke pipeline element - is the pipeline element container ready?");
        }
        return false;
    }

    public void invokePipelineElementsOnSystemRebootOrRestart() {
        getAllInvocables()
                .forEach(graph -> {
                    Response status = InvocableElementManager.getInstance().invoke(graph);
                    if (status.isSuccess()) {
                        if (status.getOptionalMessage().isEmpty()) {
                            LOG.info("Pipeline element successfully restarted {}", status.getElementId());
                        } else {
                            LOG.info("Pipeline element already running {}", status.getElementId());
                        }
                    } else {
                        LOG.info("Pipeline element could not be restarted - are the pipeline element containers " +
                                "running? {}", status.getElementId());
                    }
                });
    }

    private EventProducer getReconfigurationEventProducerFromInvocable(InvocableStreamPipesEntity graph) {
        TransportProtocol tp = getReconfigurationTransportProtocol(graph);
        EventProducer pub;
        if(tp instanceof KafkaTransportProtocol){
            pub = new SpKafkaProducer();
            pub.connect(tp);
        } else if (tp instanceof JmsTransportProtocol){
            pub = new ActiveMQPublisher();
            pub.connect(tp);
        } else{
            pub = new MqttPublisher();
            pub.connect(tp);
        }
        return pub;
    }

    private byte[] reconfigurationToByteArray(PipelineElementReconfigurationEntity entity) {
        Map<String, String> reconfigurationEventMap = new HashMap<>();
        entity.getReconfiguredStaticProperties().forEach(staticProperty -> {
            if (staticProperty instanceof FreeTextStaticProperty) {
                reconfigurationEventMap.put(staticProperty.getInternalName(),
                        ((FreeTextStaticProperty) staticProperty).getValue());
            }
        });
        return toJson(reconfigurationEventMap).getBytes(StandardCharsets.UTF_8);
    }

    private void updateAndSyncNodeInfoDescription(InvocableRegistration registration) {
        setSupportedPipelineElements(getSupportedEntities(registration).stream()
                .map(NamedStreamPipesEntity::getAppId).collect(Collectors.toList()));
        try {
            String url = generateNodeManagementUpdateEndpoint();
            String desc = toJson(getNodeInfoDescription());
            Request.Put(url)
                    .bodyString(desc, ContentType.APPLICATION_JSON)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ConsumableStreamPipesEntity> getSupportedEntities(InvocableRegistration registration){
        //Check if the node supports the entity (atm only hardware requirements; could be expanded to include
        // software requirements)
        ResourceChecker resourceChecker = new ResourceChecker(getNodeInfoDescription());
        return registration.getSupportedPipelineElements().stream()
                .filter(resourceChecker::checkResources).collect(Collectors.toList());
    }

    private List<InvocableStreamPipesEntity> getAllInvocables() {
        return RunningInvocableInstances.INSTANCE.getAll();
    }

    private NodeInfoDescription getNodeInfoDescription() {
        return NodeManager.getInstance().getNode();
    }

    private void setSupportedPipelineElements(List<String> supportedPipelineElements) {
        NodeManager.getInstance()
                .getNode()
                .setSupportedElements(supportedPipelineElements);
    }

    private TransportProtocol getReconfigurationTransportProtocol(InvocableStreamPipesEntity graph) {
        TransportProtocol tp = graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol();
        tp.setTopicDefinition(makeReconfigurationTopic(graph.getDeploymentRunningInstanceId()));
        return tp;
    }

    private TopicDefinition makeReconfigurationTopic(String runningInstanceId) {
        return new SimpleTopicDefinition( RECONFIGURATION_TOPIC + DOT + runningInstanceId);
    }

    private String generateNodeManagementUpdateEndpoint() {
        return HTTP_PROTOCOL
                + NodeConfiguration.getBackendHost()
                + COLON
                + NodeConfiguration.getBackendPort()
                + SLASH
                + "streampipes-backend/api/v2/users/admin@streampipes.org/nodes"
                + SLASH
                + NodeConfiguration.getNodeControllerId();
    }

    private String generatePipelineManagementOffloadEndpoint() {
        return HTTP_PROTOCOL
                + NodeConfiguration.getBackendHost()
                + COLON
                + NodeConfiguration.getBackendPort()
                + SLASH
                + "streampipes-backend/api/v2/users/admin@streampipes.org/pipelines/offload";
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

    public static boolean isAvailable(String host, int port) {
        try {
            InetSocketAddress sa = new InetSocketAddress(host, port);
            Socket ss = new Socket();
            ss.connect(sa, 1000);
            ss.close();
        } catch(Exception e) {
            return false;
        }
        return true;
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
