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
package org.apache.streampipes.node.management;


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.node.NodeCondition;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.management.operation.monitor.health.ClusterHealthCheckMonitor;
import org.apache.streampipes.node.management.operation.relay.RelayHandler;
import org.apache.streampipes.node.management.operation.sync.SynchronizationFactory;
import org.apache.streampipes.node.management.operation.sync.SynchronizationType;
import org.apache.streampipes.node.management.utils.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class NodeManagement {
    private static final Logger LOG = LoggerFactory.getLogger(NodeManagement.class.getCanonicalName());

    public static List<NodeInfoDescription> getOnlineNodes() {
        return ClusterHealthCheckMonitor.getInstance().getAllHealthyNodes();
    }

    public static List<NodeInfoDescription> getAllNodes() {
        return StorageUtils.getAllNodes();
    }

    public static boolean updateNodeCondition(String nodeControllerId, NodeCondition condition) {
        Optional<NodeInfoDescription> storedNode = StorageUtils.getNode(nodeControllerId);
        boolean status = false;

        switch (condition) {
            case ACTIVE:
                if (storedNode.isPresent()) {
                    StorageUtils.activateNode(nodeControllerId);
                    status = SynchronizationFactory.synchronize(storedNode.get(), SynchronizationType.ACTIVATE_NODE);
                }
                break;
            case INACTIVE:
                if (storedNode.isPresent()) {
                    StorageUtils.deactivateNode(nodeControllerId);
                    status = SynchronizationFactory.synchronize(storedNode.get(), SynchronizationType.DEACTIVATE_NODE);
                }
                break;
            default:
                throw new SpRuntimeException("Node condition not supported " + condition);
        }
        return status;
    }

    public static Message updateNode(NodeInfoDescription desc) {
        boolean successfullyUpdated = SynchronizationFactory.synchronize(desc, SynchronizationType.UPDATE_NODE);
        if (successfullyUpdated) {
            StorageUtils.updateNode(desc);
            return Notifications.success("Node updated");
        }
        return Notifications.error("Could not update node");
    }

    public static void deleteNode(String nodeControllerId) {
        StorageUtils.deleteNode(nodeControllerId);
    }

    public static Message syncRemoteNodeUpdateRequest(NodeInfoDescription desc) {
        StorageUtils.updateNode(desc);
        return Notifications.success("Node updated");
    }

    public static Message addOrRejoin(NodeInfoDescription desc) {
        Optional<NodeInfoDescription> latestDesc = StorageUtils.getLatestNodeOrElseEmpty(desc.getNodeControllerId());

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

    private static Message addNewNode(NodeInfoDescription desc) throws RuntimeException {
        try {
            StorageUtils.storeNode(desc);
            LOG.info("New cluster node successfully joined http://{}:{}", desc.getHostname(), desc.getPort());
            return Notifications.success(NotificationType.NODE_JOIN_SUCCESS);
        } catch (Exception e) {
            return Notifications.success(NotificationType.NODE_JOIN_ERROR);
        }
    }

    private static Message rejoinAndSyncNode(NodeInfoDescription desc) {
        LOG.info("Sync latest node description to http://{}:{}", desc.getHostname(), desc.getPort());
        boolean success = SynchronizationFactory.synchronize(desc, SynchronizationType.UPDATE_NODE);
        if (success) {
            return new RelayHandler(desc).restart();
        }
        return Notifications.success(NotificationType.NODE_JOIN_ERROR);
    }

}
