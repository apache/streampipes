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
package org.apache.streampipes.node.controller.config;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public enum EnvConfigParam {

    STREAMPIPES_VERSION("SP_VERSION", ""),
    API_KEY("SP_API_KEY", ""),
    NODE_HOST("SP_NODE_HOST", "localhost"),
    NODE_CONTROLLER_URL("SP_NODE_CONTROLLER_URL", "http://localhost:7077"),
    NODE_CONTROLLER_ID("SP_NODE_CONTROLLER_ID", "nodectlr"),
    NODE_TYPE("SP_NODE_TYPE", "edge"),
    NODE_TAGS("SP_NODE_TAGS", ""),
    NODE_CONTROLLER_CONTAINER_HOST("SP_NODE_CONTROLLER_CONTAINER_HOST","streampipes-node-controller"),
    NODE_CONTROLLER_CONTAINER_PORT("SP_NODE_CONTROLLER_CONTAINER_PORT", "7077"),
    NODE_BROKER_CONTAINER_HOST("SP_NODE_BROKER_CONTAINER_HOST", "streampipes-node-broker"),
    NODE_BROKER_CONTAINER_PORT("SP_NODE_BROKER_CONTAINER_PORT", "1883"),
    NODE_BROKER_PROTOCOL("SP_NODE_BROKER_PROTOCOL", "mqtt"),
    BACKEND_URL("SP_URL", "http://localhost:8030"),
    BACKEND_HOST("SP_BACKEND_HOST", "localhost"),
    BACKEND_PORT("SP_BACKEND_PORT", "8030"),
    DOCKER_PRUNING_FREQ("SP_DOCKER_PRUNING_FREQ_SECS", "3600"),
    DOCKER_STATS_COLLECT_FREQ("SP_DOCKER_STATS_COLLECT_FREQ_SECS", "1"),
    RESOURCE_UPDATE_FREQ("SP_NODE_RESOURCE_UPDATE_FREQ_SECS", "30"),
    EVENT_RELAY_BUFFER_SIZE("SP_NODE_EVENT_BUFFER_SIZE", "1000"),
    EVENT_RELAY_TARGET_BROKER_CHECK_ENABLED("SP_EVENT_RELAY_TARGET_BROKER_CHECK_ENABLED", "true"),
    NODE_GPU_ACCESS("SP_NODE_HAS_GPU", "false"),
    NODE_GPU_CORES("SP_NODE_GPU_CUDA_CORES", "0"),
    NODE_GPU_TYPE("SP_NODE_GPU_TYPE", "n/a"),
    NODE_ACCESSIBLE_FIELD_DEVICE("SP_NODE_ACCESSIBLE_FIELD_DEVICE", ""),
    CONSUL_LOCATION("CONSUL_LOCATION", "consul"),
    SUPPORTED_PIPELINE_ELEMENTS("SP_SUPPORTED_PIPELINE_ELEMENTS", ""),
    AUTO_OFFLOADING("SP_AUTO_OFFLOADING_ACTIVATED", "false"),
    AUTO_OFFLOADING_STRATEGY("SP_AUTO_OFFLOADING_STRATEGY", "default"),
    AUTO_OFFLOADING_THRESHOLD_IN_PERCENT("SP_AUTO_OFFLOADING_THRESHOLD_IN_PERCENT", "90"),
    AUTO_OFFLOADING_MAX_NUM_VIOLATIONS("SP_AUTO_OFFLOADING_MAX_NUM_VIOLATIONS", "4"),
    NODE_STORAGE_PATH("SP_NODE_STORAGE_PATH", "/var/lib/streampipes"),
    LOGGING_MQTT_URL("SP_LOGGING_MQTT_URL", "tcp://localhost:1883");


    private final String environmentKey;
    private final String defaultValue;

    EnvConfigParam(String environmentKey, String defaultValue) {
        this.environmentKey = environmentKey;
        this.defaultValue = defaultValue;
    }

    public String getEnvironmentKey() {
        return environmentKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public static List<String> getAllEnvironmentKeys() {
        return stream(EnvConfigParam.values())
                .map(EnvConfigParam::getEnvironmentKey)
                .collect(Collectors.toList());
    }

    public static List<EnvConfigParam> getAllEnvironmentConfigs(){
        return stream(EnvConfigParam.values()).collect(Collectors.toList());
    }

    public static Optional<EnvConfigParam> getEnvironmentKeyByName(String envKey) {
        return stream(EnvConfigParam.values())
                .filter(cmdParameter -> cmdParameter.getEnvironmentKey().equals(envKey))
                .findFirst();
    }
}
