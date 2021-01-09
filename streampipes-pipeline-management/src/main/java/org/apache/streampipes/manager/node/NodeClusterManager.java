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

import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class NodeClusterManager extends AbstractClusterManager {

    private static final Logger LOG = LoggerFactory.getLogger(NodeClusterManager.class.getCanonicalName());

    public static List<NodeInfoDescription> getAvailableNodes() {
        //return new AvailableNodesFetcher().fetchNodes();
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().getAllActiveNodes();
    }

    public static List<NodeInfoDescription> getAllNodes() {
        //return new AvailableNodesFetcher().fetchNodes();
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().getAllNodes();
    }

    public static Message updateNode(NodeInfoDescription desc) {
        boolean successfullyUpdated = syncWithRemoteNodeController(desc);
        if (successfullyUpdated) {
            StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().updateNode(desc);
            return Notifications.success("Node updated");
        }
        return Notifications.error("Could not update node");
    }

    public static boolean deactivateNode(String nodeControllerId) {
        Optional<NodeInfoDescription> storedNode =
                StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().getNode(nodeControllerId);
        boolean status = false;
        if (storedNode.isPresent()) {
            StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().deactivateNode(nodeControllerId);
            status = syncStateUpdateWithRemoteNodeController(storedNode.get(), false);
        }
        return status;
    }

    public static boolean activateNode(String nodeControllerId) {
        Optional<NodeInfoDescription> storedNode =
                StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().getNode(nodeControllerId);
        boolean status = false;
        if (storedNode.isPresent()) {
            StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().activateNode(nodeControllerId);
            status = syncStateUpdateWithRemoteNodeController(storedNode.get(), true);
        }
        return status;
    }

    public static void addNode(NodeInfoDescription desc) {
        List<NodeInfoDescription> allNodes =
                StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().getAllNodes();

        boolean alreadyRegistered = false;
        if (allNodes.size() > 0) {
            alreadyRegistered = allNodes.stream()
                    .anyMatch(n -> n.getNodeControllerId().equals(desc.getNodeControllerId()));
        }

        if (!alreadyRegistered) {
            LOG.info("New cluster node join registration request on from http://{}:{}", desc.getHostname(), desc.getPort());
            StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().storeNode(desc);
            LOG.info("New cluster node successfully joined http://{}:{}", desc.getHostname(), desc.getPort());
        } else {
            LOG.info("Re-joined cluster node from http://{}:{}", desc.getHostname(), desc.getPort());
        }
    }

    public static void deleteNode(String nodeControllerId) {
        StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage().deleteNode(nodeControllerId);
    }
}
