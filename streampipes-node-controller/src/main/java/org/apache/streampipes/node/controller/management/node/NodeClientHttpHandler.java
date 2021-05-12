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

import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.utils.HttpRequest;
import org.apache.streampipes.node.controller.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeClientHttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NodeClientHttpHandler.class.getCanonicalName());

    private static final long RETRY_INTERVAL_MS = 5000;
    private final NodeInfoDescription node;
    private final String route;
    private final HttpRequest httpRequest;

    public NodeClientHttpHandler(NodeInfoDescription node, String route, HttpRequest httpRequest) {
        this.node = node;
        this.route = route;
        this.httpRequest = httpRequest;
    }

    public boolean execute() {

        String host = NodeConfiguration.getBackendHost();
        int port = NodeConfiguration.getBackendPort();

        String url = HttpUtils.generateEndpoint(host, port, route);
        LOG.info("Trying to sync with StreamPipes node management=" + url);

        boolean connected = false;
        while (!connected) {
            // call REST endpoints of StreamPipes node management
            if (httpRequest == HttpRequest.POST) {
                connected = HttpUtils.post(url, node, Boolean.class);
            }

            if (!connected) {
                LOG.debug("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info("Successfully synced with StreamPipes node management=" + url);
        return connected;
    }
}
