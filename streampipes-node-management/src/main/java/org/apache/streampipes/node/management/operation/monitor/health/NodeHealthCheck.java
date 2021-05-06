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
import org.apache.streampipes.node.management.operation.monitor.NodeMonitor;
import org.apache.streampipes.node.management.utils.HttpUtils;

import java.util.concurrent.*;

public class NodeHealthCheck implements NodeMonitor<NodeHealthStatus> {

    private static final int FUTURE_TIMEOUT_SECS = 3;
    private final String healthCheckEndpoint;

    public NodeHealthCheck(String endpoint) {
        this.healthCheckEndpoint = endpoint;
    }

    @Override
    public Callable<NodeHealthStatus> monitoringCallable() {
        return () -> {
            if (!Thread.currentThread().isInterrupted()) {
                return HttpUtils.get(healthCheckEndpoint, NodeHealthStatus.class);
            }
            return new NodeHealthStatus(false);
        };
    }

    @Override
    public NodeHealthStatus execute() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        final Future<NodeHealthStatus> future = executorService.submit(monitoringCallable());

        NodeHealthStatus nodeHealthStatus;
        try {
            // blocking call until timeout is reached
            nodeHealthStatus = future.get(FUTURE_TIMEOUT_SECS, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            nodeHealthStatus = new NodeHealthStatus(false);
        } catch (TimeoutException e) {
            future.cancel(true);
            nodeHealthStatus = new NodeHealthStatus(false);
        }
        executorService.shutdown();
        return nodeHealthStatus;
    }
}
