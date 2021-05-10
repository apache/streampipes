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

import org.apache.streampipes.model.client.version.VersionInfo;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.node.*;
import org.apache.streampipes.model.node.container.DeploymentContainer;
import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.model.node.meta.GeoLocation;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.utils.HttpUtils;
import org.apache.streampipes.node.controller.utils.SocketUtils;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class NodeManager {
    private static final Logger LOG = LoggerFactory.getLogger(NodeManager.class.getCanonicalName());

    private static final String BACKEND_BASE_ROUTE = "/streampipes-backend/api/v2";
    private static final String BACKEND_VERSION_ROUTE = BACKEND_BASE_ROUTE + "/info/versions";
    private static final String NODE_REGISTRATION_ROUTE = BACKEND_BASE_ROUTE + "/users/admin@streampipes.org/nodes";
    private static final String NODE_SYNC_UPDATE_ROUTE = BACKEND_BASE_ROUTE + "/users/admin@streampipes.org/nodes/sync";
    private static final long RETRY_INTERVAL_MS = 5000;

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

    public NodeInfoDescription getNode() {
        return nodeInfo;
    }

    public void init() {
        NodeInfoDescription nodeInfoDescription =
                NodeInfoDescriptionBuilder.create(NodeConstants.NODE_CONTROLLER_ID)
                        .withHostname(NodeConstants.NODE_HOSTNAME)
                        .withPort(NodeConstants.NODE_PORT)
                        .withNodeBroker(
                                NodeConstants.NODE_BROKER_HOST,
                                NodeConstants.NODE_BROKER_PORT,
                                NodeConstants.NODE_BROKER_PROTOCOL)
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

    public Message updateNode(NodeInfoDescription desc) {
        LOG.info("Update node description for node controller: {}", desc.getNodeControllerId());
        this.nodeInfo = desc;
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    public Message activateNode() {
        LOG.info("Activate node controller");
        this.nodeInfo.setActive(true);
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    public Message deactivateNode() {
        LOG.info("Deactivate node controller");
        this.nodeInfo.setActive(false);
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    public void registerContainer(DockerContainer container) {
        if(!registered(container)) {
            this.nodeInfo.addRegisteredContainer(container);
            syncWithNodeClusterManager();
        }
    }

    public void deregisterContainer(DockerContainer container) {
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
        boolean registered;
        String host = NodeConfiguration.getBackendHost();
        int port = NodeConfiguration.getBackendPort();
        String endpoint = HttpUtils.generateEndpoint(host, port, NODE_REGISTRATION_ROUTE);

        while (!connected) {
            LOG.info("Trying to register node controller at StreamPipes cluster management: " + endpoint);
            connected = SocketUtils.isReady(NodeConfiguration.getBackendHost(), NodeConfiguration.getBackendPort());
            if (!connected) {
                LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        registered = HttpUtils.post(endpoint, this.nodeInfo);
        if (registered) {
            LOG.info("Successfully registered node controller at StreamPipes cluster management");
        }

        return registered;
    }


    private boolean syncWithNodeClusterManager() {
        boolean connected = false;
        String host = NodeConfiguration.getBackendHost();
        int port = NodeConfiguration.getBackendPort();

        String endpoint = HttpUtils.generateEndpoint(host, port, NODE_SYNC_UPDATE_ROUTE);

        LOG.info("Trying to sync node updates with StreamPipes cluster management: " + endpoint);

        while (!connected) {
            connected = HttpUtils.post(endpoint, this.nodeInfo);
            if (!connected) {
                LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info("Successfully synced node controller with StreamPipes cluster management");
        return connected;
    }

    public String getStreamPipesVersion() {
        boolean connected = false;
        VersionInfo versionInfo = new VersionInfo();
        String host = NodeConfiguration.getBackendHost();
        int port = NodeConfiguration.getBackendPort();

        String endpoint = HttpUtils.generateEndpoint(host, port, BACKEND_VERSION_ROUTE);
        String bearerToken = NodeConfiguration.getNodeApiKey();

        LOG.info("Trying to retrieve StreamPipes version from backend: " + endpoint);

        while (!connected) {
            versionInfo = HttpUtils.get(endpoint, bearerToken, VersionInfo.class);

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
        return versionInfo.getBackendVersion();
    }

    // Helpers

    private boolean registered(DockerContainer container) {
        return this.nodeInfo.getRegisteredContainers().contains(container);
    }

}
