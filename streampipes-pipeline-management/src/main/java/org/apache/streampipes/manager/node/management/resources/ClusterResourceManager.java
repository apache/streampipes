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
package org.apache.streampipes.manager.node.management.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.resource.ResourceMetrics;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.INodeInfoStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ClusterResourceManager {

    private static final int SOCKET_TIMEOUT_MS = 1000;
    private static ClusterResourceManager instance = null;
    private static Map<String, Queue<ResourceMetrics>> resourceMetricsMap = new HashMap<>();

    private ClusterResourceManager() {}

    public static ClusterResourceManager getInstance() {
        if (instance == null) {
            synchronized (ClusterResourceManager.class) {
                if (instance == null)
                    instance = new ClusterResourceManager();
            }
        }
        return instance;
    }

    public static Map<String, Queue<ResourceMetrics>> getResourceMetricsMap(){
        return resourceMetricsMap;
    }

    public void checkResources(){
        List<NodeInfoDescription> nodes =  getNodeStorageApi().getAllActiveNodes();
        if (nodes.size() > 0) {
            nodes.forEach(node -> {
                try {
                    URL nodeUrl = generateNodeUrl(node);
                    Response resp = Request.Get(nodeUrl.toURI()).socketTimeout(SOCKET_TIMEOUT_MS).execute();
                    addResourceMetrics(node, extractResourceMetrics(resp.returnContent().asString()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private URL generateNodeUrl(NodeInfoDescription desc) throws MalformedURLException {
        return new URL("http", desc.getHostname(), desc.getPort(), "/api/v2/node/info/resources");
    }

    // Helpers

    private static INodeInfoStorage getNodeStorageApi() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage();
    }

    private ResourceMetrics extractResourceMetrics(String rm){
        try {
            return JacksonSerializer.getObjectMapper().readValue(rm, ResourceMetrics.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addResourceMetrics(NodeInfoDescription node, ResourceMetrics rm){
        if(!resourceMetricsMap.containsKey(node.getNodeControllerId()))
            resourceMetricsMap.put(node.getNodeControllerId(), new ArrayBlockingQueue<ResourceMetrics>(10));
        if(!resourceMetricsMap.get(node.getNodeControllerId()).offer(rm)){
            resourceMetricsMap.get(node.getNodeControllerId()).poll();
            resourceMetricsMap.get(node.getNodeControllerId()).offer(rm);
        }
    }

}
