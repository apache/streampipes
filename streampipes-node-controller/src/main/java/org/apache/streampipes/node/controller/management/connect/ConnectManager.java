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
package org.apache.streampipes.node.controller.management.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.node.controller.config.NodeControllerConfig;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConnectManager {
    private static final Logger LOG =
            LoggerFactory.getLogger(ConnectManager.class.getCanonicalName());

    private static final String HTTP_PROTOCOL = "http://";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String BACKEND_HOST = NodeControllerConfig.INSTANCE.backendLocation();
    private static final int BACKEND_PORT = NodeControllerConfig.INSTANCE.backendPort();
    private static final String BACKEND_ADMINISTRATION_ROUTE = "/streampipes-backend/api/v2/connect/{username}/master" +
            "/administration";

    // Connect adapter base route
    // TODO: get from registered extensions or connect adapater config
    private static final String CONNECT_WORKER_HOST = "streampipes-extensions";
    private static final int CONNECT_WORKER_PORT = 8090;
    private static final String CONNECT_WORKER_BASE_ROUTE = "/api/v1/{username}/worker";
    private static final String STREAM_ROUTE = "/stream";
    private static final String SET_ROUTE = "/set";
    private static final String INVOKE_ROUTE = "/invoke";
    private static final String STOP_ROUTE ="/stop";
    private static final String GUESS_ROUTE = "/guess/schema";
    private static final String RESOLVABLE_ROUTE = "/resolvable/{id}/configurations";
    private static final String ADAPTER_ROUTE = "/adapters/{id}/assets";
    private static final String PROTOCOL_ROUTE = "/protocols/{id}/assets";

    private static final Integer CONNECT_TIMEOUT = 10000;
    private static final Integer SOCKET_TIMEOUT = 100000;
    private static ConnectManager instance = null;

    private ConnectManager() {}

    public static ConnectManager getInstance() {
        if (instance == null) {
            synchronized (ConnectManager.class) {
                if (instance == null)
                    instance = new ConnectManager();
            }
        }
        return instance;
    }

    // adapter -> backend communication: registration

    // MasterRestClient
    public String register(String username, ConnectWorkerContainer wc) {
        String endpoint = (backendUrl() + BACKEND_ADMINISTRATION_ROUTE.replace("{username}", username));
        LOG.info("Trying to register connect worker at backend: " + endpoint);
        return post(endpoint , jackson(wc)).toString();
    }

    // backend -> adapter communication

    // WorkerResource
    public <T extends AdapterDescription> String invoke(String username, T ad) {
        LOG.info("Invoke adapter: appId=" + ad.getAppId() + ", name=" + ad.getName());
        if (ad instanceof AdapterStreamDescription) {
            return post(endpointFromDescription(username, ad, STREAM_ROUTE + INVOKE_ROUTE), jackson(ad)).toString();
        } else if (ad instanceof AdapterSetDescription) {
            return post(endpointFromDescription(username, ad, SET_ROUTE + INVOKE_ROUTE), jackson(ad)).toString();
        }
        throw new SpRuntimeException("Could not invoke adapter: " + ad.getAppId());
    }

    public <T extends AdapterDescription> String stop(String username, T ad) {
        LOG.info("Stop adapter: appId=" + ad.getAppId() + ", name=" + ad.getName());
        if (ad instanceof AdapterStreamDescription) {
            return post(endpointFromDescription(username, ad, STREAM_ROUTE + STOP_ROUTE), jackson(ad)).toString();
        } else if (ad instanceof AdapterSetDescription) {
            return post(endpointFromDescription(username, ad, SET_ROUTE + STOP_ROUTE), jackson(ad)).toString();
        }
        throw new SpRuntimeException("Could not stop adapter: " + ad.getAppId());
    }

    // GuessResource
    public GuessSchema guess(String username, AdapterDescription ad) {
        try {
            LOG.info("Trying to guess schema: " + ad.getAppId());

            Response resp = post(endpointFromDescription(username, ad, GUESS_ROUTE), jackson(ad));
            HttpResponse httpResponse = resp.returnResponse();
            String responseString = EntityUtils.toString(httpResponse.getEntity());
            return JacksonSerializer.getObjectMapper().readValue(responseString, GuessSchema.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new SpRuntimeException("Could not deserialize object");
    }

    // RuntimeResolvableResource
    public String fetchConfigurations(String username, String appId, RuntimeOptionsRequest runtimeOptions) {
        String endpoint = endpointFromStringRoute(username, RESOLVABLE_ROUTE.replace("{id}", appId));
        LOG.info("Trying to fetch configurations at: " + endpoint);
        return post(endpoint, jackson(runtimeOptions)).toString();
    }

    // AdapterResource
    public byte[] assets(String username, String appId, String assetType, String subroute) {
        String endpoint = "";
        if ("adapter".equals(assetType)) {
            if (subroute.isEmpty()) {
                endpoint = endpointFromStringRoute(username, ADAPTER_ROUTE.replace("{id}", appId));
            } else {
                endpoint = endpointFromStringRoute(username, (ADAPTER_ROUTE.replace("{id}", appId) + subroute));
            }
        } else if ("protocol".equals(assetType)) {
            if (subroute.isEmpty()) {
                endpoint = endpointFromStringRoute(username, PROTOCOL_ROUTE.replace("{id}", appId));
            } else {
                endpoint = endpointFromStringRoute(username, (PROTOCOL_ROUTE.replace("{id}", appId) + subroute));
            }
        }
        return get(endpoint);
    }

    // Helper methods
    private Response post(String endpoint, String payload) {
        try {
            return Request.Post(endpoint)
                    .bodyString(payload, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .socketTimeout(SOCKET_TIMEOUT)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new SpRuntimeException("Post request was not successful");
    }

    private byte[] get(String endpoint) {
        try {
            return Request.Get(endpoint)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .socketTimeout(SOCKET_TIMEOUT)
                    .execute().returnContent().asBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new SpRuntimeException("Get request was not successful");
    }

    private <T extends AdapterDescription> String jackson(T ad) {
        try {
            return JacksonSerializer.getObjectMapper().writeValueAsString(ad);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new SpRuntimeException("Could not serialize object");
    }

    private <T extends UnnamedStreamPipesEntity> String jackson(T ad) {
        try {
            return JacksonSerializer.getObjectMapper().writeValueAsString(ad);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new SpRuntimeException("Could not serialize object");
    }

    private <T extends AdapterDescription> String endpointFromDescription(String username, T ad, String subroute) {
        return workerUrl(ad) + addUserToBaseRoute(username) + subroute;
    }

    private String endpointFromStringRoute(String username, String subroute) {
        return workerUrl() + addUserToBaseRoute(username) + subroute;
    }

    private String addUserToBaseRoute(String username) {
        return CONNECT_WORKER_BASE_ROUTE.replace("{username}", username);
    }


    private String backendUrl() {
        return HTTP_PROTOCOL + BACKEND_HOST + COLON + BACKEND_PORT;
    }

    private String workerUrl() {
        if("true".equals(System.getenv("SP_DEBUG"))) {
            return HTTP_PROTOCOL + "localhost" + COLON + "7024";
        }
        return HTTP_PROTOCOL + CONNECT_WORKER_HOST + COLON + CONNECT_WORKER_PORT;
    }

    private <T extends AdapterDescription> String workerUrl(T ad) {
        return HTTP_PROTOCOL + ad.getElementEndpointHostname() + COLON + ad.getElementEndpointPort();
    }
}
