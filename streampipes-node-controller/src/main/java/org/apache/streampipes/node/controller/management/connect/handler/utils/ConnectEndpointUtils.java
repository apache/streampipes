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
package org.apache.streampipes.node.controller.management.connect.handler.utils;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.connect.handler.MasterRegistrationHandler;
import org.apache.streampipes.node.controller.management.connect.handler.WorkerFetchAssetsHandler;
import org.apache.streampipes.node.controller.management.connect.handler.WorkerGuessSchemaHandler;
import org.apache.streampipes.node.controller.management.connect.handler.WorkerInteractionHandler;

public class ConnectEndpointUtils {

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String CONNECT_WORKER_HOST = "streampipes-extensions";
    private static final int CONNECT_WORKER_PORT = 8090;
    private static final String BASE_ROUTE = "/streampipes-backend/api/v2";
    private static final String BACKEND_ADMINISTRATION_ROUTE = "/connect/{username}/master/administration";
    private static final String CONNECT_WORKER_BASE_ROUTE = "/api/v1/{username}/worker";
    private static final String ADAPTER_ROUTE = "/adapters/{id}/assets";
    private static final String PROTOCOL_ROUTE = "/protocols/{id}/assets";
    private static final String GUESS_ROUTE = "/guess/schema";
    private static final String STREAM_ROUTE = "/stream";
    private static final String SET_ROUTE = "/set";

    /**
     * Endpoint for {@link MasterRegistrationHandler}
     *
     * @param username
     * @return
     */
    public static String generateMasterAdministrationEndpoint(String username) {
        return NodeConfiguration.getBackendUrl()
                + BASE_ROUTE
                + BACKEND_ADMINISTRATION_ROUTE.replace("{username}", username);
    }

    /**
     * Endpoint for {@link WorkerFetchAssetsHandler}
     *
     * @param username
     * @param appId
     * @param subroute
     * @return
     */
    public static String generateFetchAdapterAssetsEndpoint(String username, String appId, String subroute) {
        if (subroute.isEmpty()) {
            return workerUrl()
                    + CONNECT_WORKER_BASE_ROUTE.replace("{username}", username)
                    + ADAPTER_ROUTE.replace("{id}", appId);
        } else {
            return workerUrl()
                    + CONNECT_WORKER_BASE_ROUTE.replace("{username}", username)
                    + ADAPTER_ROUTE.replace("{id}", appId)
                    + subroute;
        }
    }

    /**
     * Endpoint for {@link WorkerFetchAssetsHandler}
     *
     * @param username
     * @param appId
     * @param subroute
     * @return
     */
    public static String generateFetchProtocolAssetsEndpoint(String username, String appId, String subroute) {
        if (subroute.isEmpty()) {
            return workerUrl()
                    + CONNECT_WORKER_BASE_ROUTE.replace("{username}", username)
                    + PROTOCOL_ROUTE.replace("{id}", appId);
        } else {
            return workerUrl()
                    + CONNECT_WORKER_BASE_ROUTE.replace("{username}", username)
                    + PROTOCOL_ROUTE.replace("{id}", appId)
                    + subroute;
        }
    }

    /**
     * Endpoint for {@link WorkerGuessSchemaHandler}
     *
     * @param username
     * @param adapterDescription
     * @return
     */
    public static String generateGuessSchemaEndpoint(String username, AdapterDescription adapterDescription) {
        return workerUrl(adapterDescription)
                + CONNECT_WORKER_BASE_ROUTE.replace("{username}", username)
                + GUESS_ROUTE;
    }


    /**
     * Endpoint for {@link WorkerInteractionHandler}
     *
     * @param username
     * @param adapterDescription
     * @param subroute
     * @return
     */
    public static String generateAdapterStreamEndpoint(String username, AdapterDescription adapterDescription,
                                                       String subroute) {
        return workerUrl(adapterDescription)
                + CONNECT_WORKER_BASE_ROUTE.replace("{username}", username)
                + STREAM_ROUTE
                + subroute;
    }

    /**
     * Endpoint for {@link WorkerInteractionHandler}
     *
     * @param username
     * @param adapterDescription
     * @param subroute
     * @return
     */
    public static String generateAdapterSetEndpoint(String username, AdapterDescription adapterDescription,
                                                    String subroute) {
        return workerUrl()
                + CONNECT_WORKER_BASE_ROUTE.replace("{username}", username)
                + SET_ROUTE
                + subroute;
    }

    // Helpers

    private static String workerUrl(AdapterDescription adapterDescription) {
        return HTTP_PROTOCOL
                + adapterDescription.getElementEndpointHostname()
                + COLON
                + adapterDescription.getElementEndpointPort();
    }


    private static String workerUrl() {
        if("true".equals(System.getenv("SP_DEBUG"))) {
            return HTTP_PROTOCOL + "localhost" + COLON + "7024";
        }
        return HTTP_PROTOCOL + CONNECT_WORKER_HOST + COLON + CONNECT_WORKER_PORT;
    }

}
