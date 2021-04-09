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
package org.apache.streampipes.manager.node.management.cluster;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class AbstractClusterManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractClusterManager.class.getCanonicalName());

    private static final String PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final long RETRY_INTERVAL_MS = 5000;
    private static final int CONNECT_TIMEOUT = 1000;

    public enum RequestOptions {
        GET,POST,PUT,DELETE
    }

    protected static <T> boolean syncWithNodeController(T element, NodeSyncOptions sync) {
        switch (sync) {
            case ACTIVATE_NODE:
                return sync(element, "/api/v2/node/info/activate", RequestOptions.POST, false);
            case DEACTIVATE_NODE:
                return sync(element, "/api/v2/node/info/deactivate", RequestOptions.POST, false);
            case UPDATE_NODE:
                return sync(element, "/api/v2/node/info", RequestOptions.PUT, true);
            case RESTART_RELAYS:
                return sync(element, "/api/v2/node/stream/relay/invoke", RequestOptions.POST, true);
            default:
                return false;
        }
    }

    private static <T> boolean sync(T element, String route, RequestOptions request, boolean withBody) {
        boolean synced = false;

        String body = "{}";
        if (withBody) {
            body = jackson(element);
        }

        String url = generateEndpoint(element, route);
        LOG.info("Trying to sync with node controller=" + url);

        boolean connected = false;
        while (!connected) {
            // call node controller REST endpoints
            switch (request) {
                case POST: connected = post(url, body);
                case PUT : connected = put(url, body);
            }

            if (!connected) {
                LOG.info("Retrying in {} seconds", (RETRY_INTERVAL_MS / 10000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synced = true;
        }
        LOG.info("Successfully synced with node controller=" + url);
        return synced;
    }

    // Helpers

    private static <T> String generateEndpoint(T desc, String route) {
        if (desc instanceof NodeInfoDescription) {
            NodeInfoDescription d = (NodeInfoDescription) desc;
            return PROTOCOL
                    + d.getHostname()
                    + COLON
                    + d.getPort()
                    + route;
        } else {
            SpDataStreamRelayContainer d = (SpDataStreamRelayContainer) desc;
            return PROTOCOL
                    + d.getDeploymentTargetNodeHostname()
                    + COLON
                    + d.getDeploymentTargetNodePort()
                    + route;
        }
    }

    private static <T> String jackson(T desc) {
        try {
            return JacksonSerializer.getObjectMapper().writeValueAsString(desc);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException("Could not serialize node controller description");
        }
    }

    private static String get(String url) {
        try {
            return Request.Get(url)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute().returnContent().toString();
        } catch (IOException e) {
            throw new SpRuntimeException("Something went wrong during GET request to node controller", e);
        }
    }

    private static boolean put(String url, String body) {
        try {
            Request.Put(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean post(String url, String body) {
        try {
            Request.Post(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
