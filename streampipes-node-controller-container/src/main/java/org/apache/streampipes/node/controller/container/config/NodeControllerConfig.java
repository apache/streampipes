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
import org.apache.streampipes.model.node.resources.interfaces.AccessibleSensorActuatorResource;

import java.util.List;
import java.util.stream.Collectors;

public enum NodeControllerConfig {
    INSTANCE;

    private SpConfig config;

    private static final String SLASH = "/";
    private static final String NODE_SERVICE_ID = "node/org.apache.streampipes.node.controller";

    private static final String DEFAULT_NODE_CONTROLLER_ID = "node-controller";
    private static final int DEFAULT_NODE_CONTROLLER_PORT = 7077;
    private static final String DEFAULT_NODE_TYPE = "edge";
    private static final String DEFAULT_NODE_BROKER_HOST = "node-broker";
    private static final int DEFAULT_NODE_BROKER_PORT = 1883;
    private static final String DEFAULT_NODE_HOST_NAME = "host.docker.internal";

    // Node controller configs
    private static final int DEFAULT_DOCKER_PRUNING_FREQ_SECS = 3600;
    private static final int DEFAULT_NODE_RESOURCE_UPDATE_FREQ_SECS = 30;
    private static final int DEFAULT_EVENT_BUFFER_SIZE = 1000;

    NodeControllerConfig() {
        config = SpConfig.getSpConfig(NODE_SERVICE_ID + SLASH + getNodeHostName());

        config.register(ConfigKeys.NODE_CONTROLLER_ID_KEY, DEFAULT_NODE_CONTROLLER_ID, "node controller id");
        config.register(ConfigKeys.NODE_CONTROLLER_PORT_KEY, DEFAULT_NODE_CONTROLLER_PORT, "node controller port");
        config.register(ConfigKeys.NODE_HOST_NAME_KEY, "host.docker.internal", "node host name");
        config.register(ConfigKeys.NODE_TYPE, "edge", "node type");
        config.register(ConfigKeys.NODE_BROKER_HOST_KEY, DEFAULT_NODE_BROKER_HOST, "node broker host");
        config.register(ConfigKeys.NODE_BROKER_PORT_KEY, DEFAULT_NODE_BROKER_PORT, "node broker port");
    }

    public String getNodeServiceId() {
        return NODE_SERVICE_ID;
    }

    public String getNodeHostName(){
        return getEnvOrDefault(ConfigKeys.NODE_HOST_NAME_KEY,
                DEFAULT_NODE_HOST_NAME,
                String.class);
    }

    public String getNodeControllerId() {
        return getEnvOrDefault(ConfigKeys.NODE_CONTROLLER_ID_KEY,
                DEFAULT_NODE_CONTROLLER_ID,
                String.class);
    }

    public int getNodeControllerPort(){
        return getEnvOrDefault(
                ConfigKeys.NODE_CONTROLLER_PORT_KEY,
                DEFAULT_NODE_CONTROLLER_PORT,
                Integer.class);
    }

    public String getNodeBrokerHost() {
        return getEnvOrDefault(
                ConfigKeys.NODE_BROKER_HOST_KEY,
                DEFAULT_NODE_BROKER_HOST,
                String.class);
    }

    // TODO: should be flexibly set due to node broker technology used
    public int getNodeBrokerPort() {
        return getEnvOrDefault(
                ConfigKeys.NODE_BROKER_PORT_KEY,
                DEFAULT_NODE_BROKER_PORT,
                Integer.class);
    }

    public List<String> getNodeLocations() {
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> (e.getKey().contains(ConfigKeys.NODE_LOCATION_KEY)))
                .map(x ->  x.getKey().replace(ConfigKeys.NODE_LOCATION_KEY + "_", "").toLowerCase() + "=" + x.getValue())
                .collect(Collectors.toList());
    }

    // TODO: get supported PE programmatically instead of environment variables
    public List<String> getSupportedPipelineElements() {
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> (e.getKey().contains(ConfigKeys.NODE_SUPPORTED_PE_APP_ID_KEY)))
                .map(x -> x.getValue())
                .collect(Collectors.toList());
    }

    public List<AccessibleSensorActuatorResource> getAccessibleSensorActuator(){
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> (e.getKey().contains(ConfigKeys.NODE_ACCESSIBLE_SENSOR_ACTUATOR_KEY)))
                .map(x -> {
                    AccessibleSensorActuatorResource a = new AccessibleSensorActuatorResource();
                    a.setName(x.getValue().split(";")[0]);
                    a.setType(x.getValue().split(";")[1]);
                    a.setConnectionInfo(x.getValue().split(";")[2]);
                    a.setConnectionType(x.getValue().split(";")[3]);
                    return a;
                })
                .collect(Collectors.toList());
    }

    public boolean hasNodeGpu(){
        return getEnvOrDefault(
                ConfigKeys.NODE_HAS_GPU_KEY,
                false,
                Boolean.class);
    }

    public int getGpuCores() {
        return getEnvOrDefault(
                ConfigKeys.NODE_GPU_CUDA_CORES_KEY,
                0,
                Integer.class);
    }

    public String getGpuType() {
        return getEnvOrDefault(
                ConfigKeys.NODE_GPU_TYPE_KEY,
                "n/a",
                String.class);
    }

    public int getPruningFreq() {
        return getEnvOrDefault(
                ConfigKeys.DOCKER_PRUNING_FREQ_SECS_KEY,
                DEFAULT_DOCKER_PRUNING_FREQ_SECS,
                Integer.class);
    }

    public int getNodeResourceUpdateFreqSecs() {
        return getEnvOrDefault(
                ConfigKeys.NODE_RESOURCE_UPDATE_FREQ_SECS_KEY,
                DEFAULT_NODE_RESOURCE_UPDATE_FREQ_SECS,
                Integer.class);
    }

    public int getEventBufferSize() {
        return getEnvOrDefault(
                ConfigKeys.NODE_EVENT_BUFFER_SIZE,
                DEFAULT_EVENT_BUFFER_SIZE,
                Integer.class);
    }

    public String getNodeType() {
        return getEnvOrDefault(
                ConfigKeys.NODE_TYPE,
                DEFAULT_NODE_TYPE,
                String.class);
    }

    private <T> T getEnvOrDefault(String k, T defaultValue, Class<T> type) {
        if(type.equals(Integer.class)) {
            return System.getenv(k) != null ? (T) Integer.valueOf(System.getenv(k)) : defaultValue;
        } else if(type.equals(Boolean.class)) {
            return System.getenv(k) != null ? (T) Boolean.valueOf(System.getenv(k)) : defaultValue;
        } else {
            return System.getenv(k) != null ? type.cast(System.getenv(k)) : defaultValue;
        }
    }
}
