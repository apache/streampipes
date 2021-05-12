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

import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.node.*;
import org.apache.streampipes.model.node.container.DeploymentContainer;
import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.model.node.meta.GeoLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NodeManager implements INodeManager {
    private static final Logger LOG = LoggerFactory.getLogger(NodeManager.class.getCanonicalName());

    private NodeInfoDescription nodeInfo = new NodeInfoDescription();
    private static NodeManager instance = null;

    private NodeManager() {}

    public static NodeManager getInstance() {
        if (instance == null)
            instance = new NodeManager();
        return instance;
    }

    @Override
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
                                // TODO: get actual geolocation in case of mobile edge node
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

        addNode(nodeInfoDescription);
    }

    @Override
    public void addNode(NodeInfoDescription node) {
        nodeInfo = node;
    }

    @Override
    public NodeInfoDescription getNodeInfoDescription() {
        return nodeInfo;
    }

    @Override
    public Message updateNode(NodeInfoDescription desc) {
        LOG.info("Update node description for node controller: {}", desc.getNodeControllerId());
        this.nodeInfo = desc;
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    @Override
    public Message activateNode() {
        LOG.info("Activate node controller");
        this.nodeInfo.setActive(true);
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    @Override
    public Message deactivateNode() {
        LOG.info("Deactivate node controller");
        this.nodeInfo.setActive(false);
        return Notifications.success(NotificationType.OPERATION_SUCCESS);
    }

    @Override
    public void registerContainerWhenDeployed(DockerContainer container) {
        if(!registered(container)) {
            this.nodeInfo.addRegisteredContainer(container);
            synchronizeNodeWithNodeManagement();
        }
    }

    @Override
    public void unregisterContainerWhenRemoved(DockerContainer container) {
        if(registered(container)) {
            this.nodeInfo.removeRegisteredContainer(container);
            synchronizeNodeWithNodeManagement();
        }
    }

    // Interactions with core

    @Override
    public boolean registerNodeAtNodeManagement() {
        return NodeClientHttpFactory.execute(this.nodeInfo, NodeLifeCycleType.REGISTER);
    }

    @Override
    public boolean synchronizeNodeWithNodeManagement() {
        return NodeClientHttpFactory.execute(this.nodeInfo, NodeLifeCycleType.UPDATE);
    }

    // Helpers

    private boolean registered(DockerContainer container) {
        return this.nodeInfo.getRegisteredContainers().contains(container);
    }

    public List<DeploymentContainer> getRegisteredContainer() {
        return this.nodeInfo.getRegisteredContainers();
    }

}
