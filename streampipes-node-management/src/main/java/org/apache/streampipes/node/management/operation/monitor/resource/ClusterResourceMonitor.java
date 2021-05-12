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
package org.apache.streampipes.node.management.operation.monitor.resource;


import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.node.monitor.ResourceMetrics;
import org.apache.streampipes.node.management.operation.monitor.health.ClusterHealthCheckMonitor;
import org.apache.streampipes.node.management.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClusterResourceMonitor {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterResourceMonitor.class.getCanonicalName());

    private static final String RESOURCE_COLLECTOR_ROUTE = "/api/v2/node/info/resources";
    private static final int RESOURCE_COLLECTOR_INTERVAL_SECS = 60;
    private static final Map<String, Queue<ResourceMetrics>> resourceMetricsMap = new HashMap<>();

    private static ClusterResourceMonitor instance = null;

    private ClusterResourceMonitor() {}

    public static ClusterResourceMonitor getInstance() {
        if (instance == null) {
            synchronized (ClusterResourceMonitor.class) {
                if (instance == null)
                    instance = new ClusterResourceMonitor();
            }
        }
        return instance;
    }

    public void run() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(resourceCollector, 0, RESOURCE_COLLECTOR_INTERVAL_SECS, TimeUnit.SECONDS);
    }

    private final Runnable resourceCollector = () -> {
        // Only collect resources for online nodes, others might not be reachable
        ClusterHealthCheckMonitor.getInstance().getAllHealthyNodes().forEach(node -> {
            // collect resources from node
            String endpoint = HttpUtils.generateEndpoint(node, RESOURCE_COLLECTOR_ROUTE);
            ResourceMetrics resourceMetrics = new NodeResourceCollector(endpoint).execute();

            addResourceMetrics(node, resourceMetrics);
        });
    };

    private void addResourceMetrics(NodeInfoDescription node, ResourceMetrics rm){
        if(!resourceMetricsMap.containsKey(node.getNodeControllerId()))
            resourceMetricsMap.put(node.getNodeControllerId(), new ArrayBlockingQueue<ResourceMetrics>(10));
        if(!resourceMetricsMap.get(node.getNodeControllerId()).offer(rm)){
            resourceMetricsMap.get(node.getNodeControllerId()).poll();
            resourceMetricsMap.get(node.getNodeControllerId()).offer(rm);
        }
    }

    public static Queue<ResourceMetrics> getNodeResourceMetricsById(String nodeControllerId){
        return resourceMetricsMap.get(nodeControllerId);
    }
}
