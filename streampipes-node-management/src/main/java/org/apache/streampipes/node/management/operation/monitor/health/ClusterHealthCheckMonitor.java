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
package org.apache.streampipes.node.management.operation.monitor.health;

import org.apache.streampipes.model.NodeHealthStatus;
import org.apache.streampipes.model.node.NodeCondition;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.management.NodeManagement;
import org.apache.streampipes.node.management.utils.HttpUtils;
import org.apache.streampipes.node.management.utils.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class ClusterHealthCheckMonitor {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterHealthCheckMonitor.class.getCanonicalName());

    private static final String HEALTH_CHECK_ROUTE = "/healthy";
    private static final int HEALTH_CHECK_INTERVAL_SECS = 10;
    private static final Map<String, NodeLiveness> inMemoryNodeLivenessStore = new HashMap<>();

    private static ClusterHealthCheckMonitor instance = null;

    private ClusterHealthCheckMonitor() {
    }

    public static ClusterHealthCheckMonitor getInstance() {
        if (instance == null) {
            synchronized (ClusterHealthCheckMonitor.class) {
                if (instance == null)
                    instance = new ClusterHealthCheckMonitor();
            }
        }
        return instance;
    }

    public void run() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(healthCheck, 0, HEALTH_CHECK_INTERVAL_SECS, TimeUnit.SECONDS);
    }

    private final Runnable healthCheck = () -> {
        StorageUtils.persistentNodeAPI().getAllNodes().forEach(node -> {
            String nodeControllerId = node.getNodeControllerId();

            // add to inMemoryNodeLivenessStore if not already existing
            addNodeToInMemoryStorage(node);

            NodeLiveness nodeLiveness = inMemoryNodeLivenessStore.get(nodeControllerId);

            int failedChecks = nodeLiveness.getNumFailedLivenessChecks();
            int maxFailedChecks = nodeLiveness.getMaxNumFailedLivenessChecks();
            NodeCondition lastNodeCondition = nodeLiveness.getCondition();

            // conduct actual health check
            String endpoint = HttpUtils.generateEndpoint(node, HEALTH_CHECK_ROUTE);
            NodeHealthStatus healthCheck = new NodeHealthCheck(endpoint).execute();

            switch(lastNodeCondition) {
                case ONLINE:
                    // node is still online with successful healthcheck
                    if (healthCheck.isSuccess()) {

                        // reset failed checks (if any)
                        if (failedChecks > 0) {
                            LOG.info(String.format("Healthcheck successful - %s is back online", endpoint));
                            nodeLiveness.resetNumFailedLivenessChecks();
                            persistNodeUpdate(node, true);
                        } else {
                            LOG.debug(String.format("Healthcheck successful - %s is still online", endpoint));
                            // set timestamp from healthcheck
                            node.setLastHeartBeatTime(healthCheck.getTimestamp());
                            // update node description in DB
                            persistNodeUpdate(node, false);
                        }

                    } else {
                        // node was online and had unsuccessful healthcheck
                        // check if failedChecks exceeds allowed limit
                        if (failedChecks < maxFailedChecks) {
                            // increase failed checks
                            nodeLiveness.increaseFailedChecks();
                            LOG.info(String.format("Healthcheck failed - (%s/%s) for %s ",
                                    ++failedChecks,
                                    maxFailedChecks,
                                    endpoint));

                        } else {
                            // node is considered offline
                            LOG.info(String.format("Too many failed healtchecks - %s is considered offline",
                                    nodeControllerId));

                            // set node offline
                            nodeLiveness.setOffline();
                            node.setCondition(NodeCondition.OFFLINE);

                            // update node description in DB
                            persistNodeUpdate(node, false);
                        }
                    }
                    break;

                case OFFLINE:
                    // node was considered online and is now online again
                    if (healthCheck.isSuccess()) {
                        LOG.info(String.format("Healthcheck successful - %s is online", endpoint));

                        // set node online
                        nodeLiveness.setOnline();
                        node.setCondition(NodeCondition.ONLINE);
                        // set timestamp from healthcheck
                        node.setLastHeartBeatTime(healthCheck.getTimestamp());
                        // update node description in DB
                        persistNodeUpdate(node, false);
                    } else {
                        // still offline ... await reconnection
                        break;
                    }
                    break;

                case CREATED:
                    // newly initialized nodes are in CREATED state
                    if (healthCheck.isSuccess()) {
                        // set online
                        LOG.info(String.format("Healthcheck successful - %s is online", endpoint));

                        // set node online
                        nodeLiveness.setOnline();
                        node.setCondition(NodeCondition.ONLINE);
                        // set timestamp from healthcheck
                        node.setLastHeartBeatTime(healthCheck.getTimestamp());
                        // update node description in DB and sync initial online status to node controller as feedback
                        persistNodeUpdate(node, true);
                    }

                    break;
                default:
                    throw new RuntimeException("Node condition unsupported " + lastNodeCondition);
            }

            // update in-memory node liveness storage
            inMemoryNodeLivenessStore.put(nodeControllerId, nodeLiveness);

        });
    };

    private void persistNodeUpdate(NodeInfoDescription node, boolean syncWithNodeController) {
        if (syncWithNodeController) {
            NodeManagement.getInstance().updateNode(node);
        } else {
            StorageUtils.updateNode(node);
        }
    }

    private void addNodeToInMemoryStorage(NodeInfoDescription node) {
        String nodeControllerId = node.getNodeControllerId();
        if (inMemoryNodeLivenessStore.get(nodeControllerId) == null) {
            inMemoryNodeLivenessStore.put(nodeControllerId, new NodeLiveness(nodeControllerId,
                    NodeCondition.CREATED, 3));
        }
    }

    public List<NodeInfoDescription> getAllHealthyNodes() {
        List<NodeInfoDescription> onlineNodes = new ArrayList<>();
        inMemoryNodeLivenessStore.forEach((id, value) -> {
            if (value.getCondition() == NodeCondition.ONLINE) {
                Optional<NodeInfoDescription> node = StorageUtils.getNode(id);
                node.ifPresent(onlineNodes::add);
            }
        });
        return onlineNodes;
    }

}
