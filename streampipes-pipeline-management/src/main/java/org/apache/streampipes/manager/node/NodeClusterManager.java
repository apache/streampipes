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
package org.apache.streampipes.manager.node;

import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.storage.api.INodeDataStreamRelay;
import org.apache.streampipes.storage.api.INodeInfoStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NodeClusterManager extends AbstractClusterManager {
    private static final Logger LOG = LoggerFactory.getLogger(NodeClusterManager.class.getCanonicalName());

    public static List<NodeInfoDescription> getAvailableNodes() {
        List<NodeInfoDescription> availableAndHealthyNodes = new ArrayList<>();
        getNodeStorageApi().getAllActiveNodes().forEach(node -> {
            if (syncWithNodeController(node, NodeSyncOptions.HEALTHY)) {
                availableAndHealthyNodes.add(node);
            }
        });
        return availableAndHealthyNodes;
    }

    public static List<NodeInfoDescription> getAllNodes() {
        return getNodeStorageApi().getAllNodes();
    }

    public static Message updateNode(NodeInfoDescription desc) {
        boolean successfullyUpdated = syncWithNodeController(desc, NodeSyncOptions.UPDATE_NODE);
        if (successfullyUpdated) {
            getNodeStorageApi().updateNode(desc);
            return Notifications.success("Node updated");
        }
        return Notifications.error("Could not update node");
    }

    public static Message syncRemoteNodeUpdateRequest(NodeInfoDescription desc) {
        getNodeStorageApi().updateNode(desc);
        return Notifications.success("Node updated");
    }

    public static boolean deactivateNode(String nodeControllerId) {
        Optional<NodeInfoDescription> storedNode = getNodeStorageApi().getNode(nodeControllerId);
        boolean status = false;
        if (storedNode.isPresent()) {
            getNodeStorageApi().deactivateNode(nodeControllerId);
            status = syncWithNodeController(storedNode.get(), NodeSyncOptions.DEACTIVATE_NODE);
        }
        return status;
    }

    public static boolean activateNode(String nodeControllerId) {
        Optional<NodeInfoDescription> storedNode = getNodeStorageApi().getNode(nodeControllerId);
        boolean status = false;
        if (storedNode.isPresent()) {
            getNodeStorageApi().activateNode(nodeControllerId);
            status = syncWithNodeController(storedNode.get(), NodeSyncOptions.ACTIVATE_NODE);
        }
        return status;
    }

    public static Message addOrRejoin(NodeInfoDescription desc) {
        Optional<NodeInfoDescription> latestDesc = getLatestNodeOrElseEmpty(desc.getNodeControllerId());

        boolean alreadyRegistered = false;
        if (latestDesc.isPresent()) {
            alreadyRegistered = true;
        }

        if (!alreadyRegistered) {
            LOG.info("New cluster node join request from http://{}:{}", desc.getHostname(), desc.getPort());
            return addNewNode(desc);
        } else {
            LOG.info("Re-joined cluster node from http://{}:{}", desc.getHostname(), desc.getPort());
            return rejoinAndSyncNode(latestDesc.get());
        }
    }

    private static Optional<NodeInfoDescription> getLatestNodeOrElseEmpty(String nodeControllerId) {
        return getNodeStorageApi().getAllNodes().stream()
                .filter(n -> n.getNodeControllerId().equals(nodeControllerId))
                .findAny();
    }

    private static Message addNewNode(NodeInfoDescription desc) throws RuntimeException {
        try {
            getNodeStorageApi().storeNode(desc);
            LOG.info("New cluster node successfully joined http://{}:{}", desc.getHostname(), desc.getPort());
            return Notifications.success(NotificationType.NODE_JOIN_SUCCESS);
        } catch (Exception e) {
            return Notifications.success(NotificationType.NODE_JOIN_ERROR);
        }
    }

    private static Message rejoinAndSyncNode(NodeInfoDescription desc) {
        LOG.info("Sync latest node description to http://{}:{}", desc.getHostname(), desc.getPort());
        boolean success = syncWithNodeController(desc, NodeSyncOptions.UPDATE_NODE);
        if (success) {
            return restartRelays(desc);
        }
        return Notifications.success(NotificationType.NODE_JOIN_ERROR);
    }

    private static Message restartRelays(NodeInfoDescription desc) {
        List<SpDataStreamRelayContainer> runningRelays = getDataStreamRelay(desc.getNodeControllerId());
        if (runningRelays.size() > 0) {
            runningRelays.forEach(relay -> {
                LOG.info("Sync active relays name={} to http://{}:{}", relay.getName(), desc.getHostname(),
                        desc.getPort());
                syncWithNodeController(relay, NodeSyncOptions.RESTART_RELAYS);
            });
        }
        return Notifications.success(NotificationType.NODE_JOIN_SUCCESS);
    }

    public static void deleteNode(String nodeControllerId) {
        getNodeStorageApi().deleteNode(nodeControllerId);
    }


    public static void persistDataStreamRelay(SpDataStreamRelayContainer relayContainer) {
        getNodeDataStreamRelayStorageApi().addRelayContainer(relayContainer);
    }

    public static List<SpDataStreamRelayContainer> getDataStreamRelay(String nodeControllerId) {
        return getNodeDataStreamRelayStorageApi().getAllByNodeControllerId(nodeControllerId);
    }

    public static void updateDataStreamRelay(SpDataStreamRelayContainer relayContainer) {
        getNodeDataStreamRelayStorageApi().updateRelayContainer(relayContainer);
    }

    public static void deleteDataStreamRelay(SpDataStreamRelayContainer relayContainer) {
        getNodeDataStreamRelayStorageApi().deleteRelayContainer(relayContainer);
    }

    // Helpers

    private static INodeInfoStorage getNodeStorageApi() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage();
    }

    private static INodeDataStreamRelay getNodeDataStreamRelayStorageApi(){
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeDataStreamRelayStorage();
    }

}
