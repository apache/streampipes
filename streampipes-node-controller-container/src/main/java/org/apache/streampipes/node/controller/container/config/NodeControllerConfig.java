package org.apache.streampipes.node.controller.container.config;/*
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

import org.apache.streampipes.config.SpConfig;

public enum NodeControllerConfig {
    INSTANCE;

    private SpConfig config;

    private static final String SLASH = "/";
    private static final String DOT = ".";
    private static final String DEFAULT_NODE_BROKER_NAME_SUFFIX = "broker";
    private static final String node_service_id = "node/org.apache.streampipes.node.controller";

    // Node controller configs
    private static final int DOCKER_PRUNING_FREQ_SECS = 60;
    private static final int NODE_RESOURCE_UPDATE_FREQ_SECS = 30;

    NodeControllerConfig() {
        config = SpConfig.getSpConfig(node_service_id + SLASH + getNodeHostName());

        config.register(ConfigKeys.NODE_CONTROLLER_ID_KEY, "node-controller", "node controller id");
        config.register(ConfigKeys.NODE_CONTROLLER_PORT_KEY,7077, "node controller port");
        config.register(ConfigKeys.NODE_HOST_KEY, "host.docker.internal", "node host name");
        config.register(ConfigKeys.NODE_LOCATION_KEY, "", "node location");
        config.register(ConfigKeys.NODE_BROKER_HOST_KEY, getDefaultNodeBrokerHost(), "node broker host");
        config.register(ConfigKeys.NODE_BROKER_PORT_KEY, 616161, "node broker port");

    }

    public String getNodeServiceId() {
        return node_service_id;
    }

    private String getEnv(String key) {
        return System.getenv(key);
    }

    private String getDefaultNodeBrokerHost() {
        return getEnv(ConfigKeys.NODE_HOST_KEY) + DOT + DEFAULT_NODE_BROKER_NAME_SUFFIX;
    }

    public int getNodeControllerPort(){
        return Integer.parseInt(getEnv(ConfigKeys.NODE_CONTROLLER_PORT_KEY));
    }

    public String getNodeHostName(){
        return getEnv(ConfigKeys.NODE_HOST_KEY);
    }


    /**
     *
     * @return DOCKER_PRUNING_FREQ_SECS_KEY
     */
    public int getPruningFreq() {
        if (getEnv(ConfigKeys.DOCKER_PRUNING_FREQ_SECS_KEY) == null) {
            return DOCKER_PRUNING_FREQ_SECS;
        }
        return Integer.parseInt(getEnv(ConfigKeys.DOCKER_PRUNING_FREQ_SECS_KEY));
    }

    /**
     *
     * @return DOCKER_PRUNING_FREQ_SECS_KEY
     */
    public int getNodeResourceUpdateFreqSecs() {
        if (getEnv(ConfigKeys.NODE_RESOURCE_UPDATE_FREQ_SECS_KEY) == null) {
            return NODE_RESOURCE_UPDATE_FREQ_SECS;
        }
        return Integer.parseInt(getEnv(ConfigKeys.NODE_RESOURCE_UPDATE_FREQ_SECS_KEY));
    }
}
