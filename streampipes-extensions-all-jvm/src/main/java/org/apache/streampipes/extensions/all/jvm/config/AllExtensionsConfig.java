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
package org.apache.streampipes.extensions.all.jvm.config;

import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.container.model.ExtensionsConfig;

public enum AllExtensionsConfig implements ExtensionsConfig {
    INSTANCE;

    private final SpConfig peConfig;
    private final SpConfig adapterConfig;
    private final static String PE_ID = "pe/org.apache.streampipes.extensions.all.jvm";
    private final static String SERVICE_NAME = "StreamPipes Extensions (JVM)";
    private final static String SERVICE_CONTAINER_NAME = "extensions-all-jvm";

    AllExtensionsConfig() {
        // TODO: harmonize config
        peConfig = SpConfig.getSpConfig(PE_ID);
        adapterConfig = SpConfig.getSpConfig("connect-worker-main");

        peConfig.register(ConfigKeys.HOST, SERVICE_CONTAINER_NAME, "Host for extensions");
        peConfig.register(ConfigKeys.PORT, 8090, "Port for extensions");
        peConfig.register(ConfigKeys.SERVICE_NAME_KEY, SERVICE_NAME, "Service name");

        adapterConfig.register(ConfigKeys.KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
        adapterConfig.register(ConfigKeys.KAFKA_PORT, 9092, "Port for backend service for kafka");
        adapterConfig.register(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT, 8090, "The port of the connect container");
        adapterConfig.register(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST, SERVICE_CONTAINER_NAME, "The hostname of the connect container");
        adapterConfig.register(ConfigKeys.BACKEND_HOST, "backend", "The host of the backend to register the worker");
        adapterConfig.register(ConfigKeys.BACKEND_PORT, 8030, "The port of the backend to register the worker");
    }

    @Override
    public String getHost() {
        return peConfig.getString(ConfigKeys.HOST);
    }

    @Override
    public int getPort() {
        return peConfig.getInteger(ConfigKeys.PORT);
    }

    @Override
    public String getId() {
        return PE_ID;
    }

    @Override
    public String getName() {
        return peConfig.getString(ConfigKeys.SERVICE_NAME_KEY);
    }

    @Override
    public String getBackendHost() {
        return adapterConfig.getString(ConfigKeys.BACKEND_HOST);
    }

    @Override
    public int getBackendPort() {
        return adapterConfig.getInteger(ConfigKeys.BACKEND_PORT);
    }
}
