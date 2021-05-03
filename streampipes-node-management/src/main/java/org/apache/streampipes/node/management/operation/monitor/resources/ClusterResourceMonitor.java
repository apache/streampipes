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
package org.apache.streampipes.node.management.operation.monitor.resources;


import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.management.utils.StorageUtils;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.List;

public class ClusterResourceMonitor {

    private static final int RESOURCE_RETRIEVE_FREQUENCY_MS = 60000;
    private static final int SOCKET_TIMEOUT_MS = 500;
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
        new Thread(getNodes, "nodes").start();
    }

    private final Runnable getNodes = () -> {
        while (true) {
            try {
                List<NodeInfoDescription> nodes =  StorageUtils.persistentNodeAPI().getAllNodes();
                if (nodes.size() > 0) {
                    nodes.forEach(node -> {
                        try {
                            URL nodeUrl = generateNodeUrl(node);
                            // TODO: gather current resources from all active node controller endpoints

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    });
                }
                Thread.sleep(RESOURCE_RETRIEVE_FREQUENCY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private URL generateNodeUrl(NodeInfoDescription desc) throws MalformedURLException {
        return new URL("http", desc.getHostname(), desc.getPort(), "");
    }

    private boolean healthCheck(URL url) {
        boolean isAlive = true;
        try {
            InetSocketAddress sa = new InetSocketAddress(url.getHost(), url.getPort());
            Socket ss = new Socket();
            ss.connect(sa, SOCKET_TIMEOUT_MS);
            ss.close();
        } catch(Exception e) {
            isAlive = false;
        }
        return isAlive;
    }
}
