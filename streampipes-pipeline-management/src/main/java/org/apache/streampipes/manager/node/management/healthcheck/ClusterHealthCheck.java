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
package org.apache.streampipes.manager.node.management.healthcheck;

import org.apache.streampipes.model.node.NodeInfoDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClusterHealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterHealthCheck.class.getCanonicalName());

    private static final String PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final long RETRY_INTERVAL_MS = 5000;
    private static final int CONNECT_TIMEOUT = 1000;

    public static boolean check(NodeInfoDescription node) {
        return healthCheck(node);
    }

    private static boolean healthCheck(NodeInfoDescription desc) {
        String url = generateHealthCheckEndpoint(desc);
        // call node controller REST endpoints
        //return get(url).contains("PONG");

        String nodeCtlId = desc.getNodeControllerId();
        boolean isAlive = false;
        String host = desc.getHostname();
        int port = desc.getPort();

        int retries = 5;
        for (int i = 0 ; i < retries ; i++) {
            try {
                LOG.info("Trying to health check node controller={} ({})", nodeCtlId, i+1 + "/" + retries);
                InetSocketAddress sa = new InetSocketAddress(host, port);
                Socket ss = new Socket();
                ss.connect(sa, 500);
                ss.close();
                if (ss.isConnected()) {
                    isAlive = true;
                    break;
                }
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                continue;
            }
            isAlive = true;
        }
        LOG.info(isAlive ? "Successfully health check node controller=" + url :
                "Could not perform health check node with controller=" + url);
        return isAlive;
    }

    // Helpers

    private static String generateHealthCheckEndpoint(NodeInfoDescription desc) {
        return PROTOCOL
                + desc.getHostname()
                + COLON
                + desc.getPort()
                + "/healthy";
    }
}
