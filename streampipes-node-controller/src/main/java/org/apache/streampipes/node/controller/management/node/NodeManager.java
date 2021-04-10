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
package org.apache.streampipes.node.controller.management.node;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.model.client.version.VersionInfo;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.model.node.*;
import org.apache.streampipes.model.node.container.DeploymentContainer;
import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.model.node.meta.GeoLocation;
import org.apache.streampipes.node.controller.config.NodeControllerConfig;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class NodeManager {
    private static final Logger LOG = LoggerFactory.getLogger(NodeManager.class.getCanonicalName());

    private static final String PROTOCOL = "http://";
    private static final String SLASH = "/";
    private static final String COLON = ":";
    private static final String BACKEND_BASE_ROUTE = "/streampipes-backend";
    private static final String BACKEND_VERSION_ROUTE = "/api/v2/info/versions";
    private static final String NODE_REGISTRATION_ROUTE = "/api/v2/users/admin@streampipes.org/nodes";
    private static final String NODE_SYNC_UPDATE_ROUTE = "/api/v2/users/admin@streampipes.org/nodes/sync";
    private static final long RETRY_INTERVAL_MS = 5000;
    private static final int CONNECT_TIMEOUT_MS = 10000;

    private NodeInfoDescription nodeInfo = new NodeInfoDescription();

    private static NodeManager instance = null;

    private NodeManager() {}

    public static NodeManager getInstance() {
        if (instance == null)
            instance = new NodeManager();
        return instance;
    }

    public void add(NodeInfoDescription n) {
        nodeInfo = n;
    }

    public NodeInfoDescription retrieveNodeInfoDescription() {
        return nodeInfo;
    }

    public void init() {
        NodeInfoDescription nodeInfoDescription =
                NodeInfoDescriptionBuilder.create(NodeConstants.NODE_CONTROLLER_ID)
                        .withHostname(NodeConstants.NODE_HOSTNAME)
                        .withPort(NodeConstants.NODE_PORT)
                        .withNodeBroker(NodeConstants.NODE_BROKER_HOST, NodeConstants.NODE_BROKER_PORT)
                        .staticNodeMetadata(
                                NodeConstants.NODE_TYPE,
                                NodeConstants.NODE_MODEL,
                                new GeoLocation(),
                                NodeConstants.NODE_LOCATION_TAGS)
                        .withSupportedElements(NodeConstants.SUPPORTED_PIPELINE_ELEMENTS)
                        .withRegisteredContainers(NodeConstants.REGISTERED_DOCKER_CONTAINER)
                        .withNodeResources(NodeResourceBuilder.create()
                                .hardwareResource(
                                        NodeConstants.NODE_CPU,
                                        NodeConstants.NODE_MEMORY,
                                        NodeConstants.NODE_DISK,
                                        NodeConstants.NODE_GPU)
                                .softwareResource(
                                        NodeConstants.NODE_OPERATING_SYSTEM,
                                        NodeConstants.NODE_KERNEL_VERSION,
                                        NodeConstants.NODE_CONTAINER_RUNTIME)
                                .withFieldDeviceAccessResources(NodeConstants.FIELD_DEVICE_ACCESS_RESOURCE_LIST)
                                .build())
                        .build();

        add(nodeInfoDescription);
    }

    public Message updateNodeInfoDescription(NodeInfoDescription desc) {
        LOG.info("Update node description for node controller: {}", desc.getNodeControllerId());
        this.nodeInfo = desc;
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    public Message activate() {
        LOG.info("Activate node controller");
        this.nodeInfo.setActive(true);
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    public Message deactivate() {
        LOG.info("Deactivate node controller");
        this.nodeInfo.setActive(false);
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    public void addToRegisteredContainers(DockerContainer container) {
        if(!registered(container)) {
            this.nodeInfo.addRegisteredContainer(container);
            syncWithNodeClusterManager();
        }
    }

    public void removeFromRegisteredContainers(DockerContainer container) {
        if(registered(container)) {
            this.nodeInfo.removeRegisteredContainer(container);
            syncWithNodeClusterManager();
        }
    }

    public List<DeploymentContainer> getRegisteredContainer() {
        return this.nodeInfo.getRegisteredContainers();
    }

    // Interactions with core

    public boolean register() {
        boolean connected = false;
        try {
            String body = JacksonSerializer.getObjectMapper().writeValueAsString(this.nodeInfo);
            String endpoint = generateRegistrationEndpoint();

            LOG.info("Trying to register node controller at node management: " + endpoint);

            while (!connected) {
                connected = post(endpoint, body);
                if (!connected) {
                    LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                    try {
                        Thread.sleep(RETRY_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info("Successfully registered node controller at node management");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }


    private boolean syncWithNodeClusterManager() {
        boolean connected = false;
        try {
            String body = JacksonSerializer.getObjectMapper().writeValueAsString(this.nodeInfo);
            String endpoint = generateSyncronizationEndpoint();

            LOG.info("Trying to sync node updates with node management: " + endpoint);

            while (!connected) {
                connected = post(endpoint, body);
                if (!connected) {
                    LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                    try {
                        Thread.sleep(RETRY_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info("Successfully synced node controller with node management");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }

    public String getStreamPipesVersion() {
        boolean connected = false;
        VersionInfo versionInfo = new VersionInfo();
        try {
            String endpoint = generateVersionEndpoint();

            LOG.info("Trying to retrieve StreamPipes version from backend: " + endpoint);

            while (!connected) {
                Response response = get(endpoint);
                versionInfo = deserialize(response, VersionInfo.class);

                if (versionInfo.getBackendVersion() != null) {
                    connected = true;
                }

                if (!connected) {
                    LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                    try {
                        Thread.sleep(RETRY_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info("Successfully retrieved StreamPipes version from backend");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return versionInfo.getBackendVersion();
    }

    private Response get(String endpoint) throws IOException {
        return Request.Get(endpoint)
                .connectTimeout(CONNECT_TIMEOUT_MS)
                .addHeader("Authorization", "Bearer " + NodeControllerConfig.INSTANCE.getApiKey())
                .execute();
    }

    private boolean post(String endpoint, String body) throws IOException {
        Response response = Request.Post(endpoint)
                .bodyString(body, ContentType.APPLICATION_JSON)
                .connectTimeout(CONNECT_TIMEOUT_MS)
                //.addHeader("Authorization", "Bearer " + NodeControllerConfig.INSTANCE.getApiKey())
                .execute();
        return handleResponse(response);
    }

    // Helpers

    private boolean registered(DockerContainer container) {
        return this.nodeInfo.getRegisteredContainers().contains(container);
    }

    private boolean handleResponse(Response response) throws IOException {
        SuccessMessage message = deserialize(response, SuccessMessage.class);
        return message.isSuccess();
    }

    private <T> T deserialize(Response response, Class<T> clazz) throws IOException {
        String resp = response.returnContent().asString();
        return JacksonSerializer.getObjectMapper().readValue(resp, clazz);
    }

    private String generateRegistrationEndpoint() {
        return generateBaseEndpoint() + NODE_REGISTRATION_ROUTE;
    }

    private String generateSyncronizationEndpoint() {
        return generateBaseEndpoint() + NODE_SYNC_UPDATE_ROUTE;
    }

    private String generateVersionEndpoint() {
        return generateBaseEndpoint() + BACKEND_VERSION_ROUTE;
    }

    private String generateBaseEndpoint() {
        return  PROTOCOL
                + NodeControllerConfig.INSTANCE.backendLocation()
                + COLON
                + NodeControllerConfig.INSTANCE.backendPort()
                + BACKEND_BASE_ROUTE;
    }
}
