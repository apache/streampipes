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
package org.apache.streampipes.node.controller.container.config;

import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.model.node.resources.interfaces.AccessibleSensorActuatorResource;
import sun.security.krb5.Config;

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
    private static final String DEFAULT_NODE_BROKER_HOST = "broker";
    private static final int DEFAULT_NODE_BROKER_PORT = 1883;
    private static final String DEFAULT_NODE_HOST_NAME = "host.docker.internal";
    private static final String DEFAULT_BACKEND_HOST = "host.docker.internal";
    private static final int DEFAULT_BACKEND_PORT = 8030;

    // Node controller configs
    private static final int DEFAULT_DOCKER_PRUNING_FREQ_SECS = 3600;
    private static final int DEFAULT_NODE_RESOURCE_UPDATE_FREQ_SECS = 30;
    private static final int DEFAULT_EVENT_BUFFER_SIZE = 1000;

    NodeControllerConfig() {
        config = SpConfig.getSpConfig(NODE_SERVICE_ID + SLASH + getNodeHostName());

        config.register(ConfigKeys.NODE_HOST, DEFAULT_NODE_HOST_NAME, "node host name");
        config.register(ConfigKeys.NODE_TYPE, DEFAULT_NODE_TYPE, "node type");
        config.register(ConfigKeys.NODE_CONTROLLER_ID, DEFAULT_NODE_CONTROLLER_ID, "node controller id");
        config.register(ConfigKeys.NODE_CONTROLLER_CONTAINER_HOST, DEFAULT_NODE_CONTROLLER_ID, "node controller container host");
        config.register(ConfigKeys.NODE_CONTROLLER_CONTAINER_PORT, DEFAULT_NODE_CONTROLLER_PORT, "node controller port");
        config.register(ConfigKeys.NODE_BROKER_CONTAINER_HOST, DEFAULT_NODE_BROKER_HOST, "node broker host");
        config.register(ConfigKeys.NODE_BROKER_CONTAINER_PORT, DEFAULT_NODE_BROKER_PORT, "node broker port");
        // currently used for connect adapter registration
        config.register(ConfigKeys.BACKEND_HOST, DEFAULT_BACKEND_HOST, "backend host");
        config.register(ConfigKeys.BACKEND_PORT, DEFAULT_BACKEND_PORT, "backend port");
    }

    public String getNodeServiceId() {
        return NODE_SERVICE_ID;
    }

    public String getSpVersion() {
        return getEnvOrDefault(ConfigKeys.SP_VERSION, "", String.class);
    }

    public String getNodeHostName(){
        return getEnvOrDefault(ConfigKeys.NODE_HOST,
                DEFAULT_NODE_HOST_NAME,
                String.class);
    }

    public String getNodeControllerId() {
        return getEnvOrDefault(ConfigKeys.NODE_CONTROLLER_ID,
                DEFAULT_NODE_CONTROLLER_ID,
                String.class);
    }

    public int getNodeControllerPort(){
        return getEnvOrDefault(
                ConfigKeys.NODE_CONTROLLER_CONTAINER_PORT,
                DEFAULT_NODE_CONTROLLER_PORT,
                Integer.class);
    }

    public String getNodeBrokerHost() {
        return getEnvOrDefault(
                ConfigKeys.NODE_BROKER_CONTAINER_HOST,
                DEFAULT_NODE_BROKER_HOST,
                String.class);
    }

    // TODO: should be flexibly set due to node broker technology used
    public int getNodeBrokerPort() {
        return getEnvOrDefault(
                ConfigKeys.NODE_BROKER_CONTAINER_PORT,
                DEFAULT_NODE_BROKER_PORT,
                Integer.class);
    }

    public List<String> getNodeLocations() {
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> (e.getKey().contains(ConfigKeys.NODE_LOCATION)))
                .map(x ->  x.getKey().replace(ConfigKeys.NODE_LOCATION + "_", "").toLowerCase() + "=" + x.getValue())
                .collect(Collectors.toList());
    }

    // TODO: get supported PE programmatically instead of environment variables
    public List<String> getSupportedPipelineElements() {
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> (e.getKey().contains(ConfigKeys.NODE_SUPPORTED_PE_APP_ID)))
                .map(x -> x.getValue())
                .collect(Collectors.toList());
    }

    public List<AccessibleSensorActuatorResource> getAccessibleSensorActuator(){
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> (e.getKey().contains(ConfigKeys.NODE_ACCESSIBLE_SENSOR_ACTUATOR)))
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
                ConfigKeys.NODE_HAS_GPU,
                false,
                Boolean.class);
    }

    public int getGpuCores() {
        return getEnvOrDefault(
                ConfigKeys.NODE_GPU_CUDA_CORES,
                0,
                Integer.class);
    }

    public String getGpuType() {
        return getEnvOrDefault(
                ConfigKeys.NODE_GPU_TYPE,
                "n/a",
                String.class);
    }

    public int getPruningFreq() {
        return getEnvOrDefault(
                ConfigKeys.DOCKER_PRUNING_FREQ_SECS,
                DEFAULT_DOCKER_PRUNING_FREQ_SECS,
                Integer.class);
    }

    public int getNodeResourceUpdateFreqSecs() {
        return getEnvOrDefault(
                ConfigKeys.RESOURCE_UPDATE_FREQ_SECS,
                DEFAULT_NODE_RESOURCE_UPDATE_FREQ_SECS,
                Integer.class);
    }

    public int getEventBufferSize() {
        return getEnvOrDefault(
                ConfigKeys.EVENT_BUFFER_SIZE,
                DEFAULT_EVENT_BUFFER_SIZE,
                Integer.class);
    }

    public String getNodeType() {
        return getEnvOrDefault(
                ConfigKeys.NODE_TYPE,
                DEFAULT_NODE_TYPE,
                String.class);
    }

    public String getBackendHost() {
        return getEnvOrDefault(
                ConfigKeys.BACKEND_HOST,
                DEFAULT_BACKEND_HOST,
                String.class);
    }

    public int getBackendPort(){
        return getEnvOrDefault(
                ConfigKeys.BACKEND_PORT,
                DEFAULT_BACKEND_PORT,
                Integer.class);
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
