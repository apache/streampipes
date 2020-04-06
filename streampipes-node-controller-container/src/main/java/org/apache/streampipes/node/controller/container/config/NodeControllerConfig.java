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
    private static final String DOT = ".";
    private static final String DEFAULT_NODE_BROKER_NAME_SUFFIX = "broker";
    private static final String node_service_id = "node/org.apache.streampipes.node.controller";

    private static final String DEFAULT_NODE_CONTROLLER_ID = "node-controller";
    private static final int DEFAULT_NODE_CONTROLLER_PORT = 7077;
    private static final String DEFAULT_NODE_BROKER_HOST = "node-broker";
    private static final int DEFAULT_NODE_BROKER_PORT = 1883;
    private static final String DEFAULT_NODE_HOST_NAME = "host.docker.internal";

    // Node controller configs
    private static final int DEFAULT_DOCKER_PRUNING_FREQ_SECS = 60;
    private static final int DEFAULT_NODE_RESOURCE_UPDATE_FREQ_SECS = 30;

    NodeControllerConfig() {
        config = SpConfig.getSpConfig(node_service_id + SLASH + getNodeHostName());

        config.register(ConfigKeys.NODE_CONTROLLER_ID_KEY, DEFAULT_NODE_CONTROLLER_ID, "node controller id");
        config.register(ConfigKeys.NODE_CONTROLLER_PORT_KEY,DEFAULT_NODE_CONTROLLER_PORT, "node controller port");
        config.register(ConfigKeys.NODE_HOST_KEY, "host.docker.internal", "node host name");
        config.register(ConfigKeys.NODE_LOCATION_KEY, "", "node location");
        config.register(ConfigKeys.NODE_BROKER_HOST_KEY, DEFAULT_NODE_BROKER_HOST, "node broker host");
        config.register(ConfigKeys.NODE_BROKER_PORT_KEY, DEFAULT_NODE_BROKER_PORT, "node broker port");

    }


    public String getNodeServiceId() {
        return node_service_id;
    }


    /**
     *
     * @return node host name (physical DNS name or IP)
     */
    public String getNodeHostName(){
        return envExist(ConfigKeys.NODE_HOST_KEY) ? getEnvAsString(ConfigKeys.NODE_HOST_KEY) :
                DEFAULT_NODE_HOST_NAME;
    }

    /**
     *
     * @return node-controller id
     */
    public String getNodeControllerId() {
        return envExist(ConfigKeys.NODE_CONTROLLER_ID_KEY) ? getEnvAsString(ConfigKeys.NODE_CONTROLLER_ID_KEY) :
                DEFAULT_NODE_BROKER_HOST;
    }

    /**
     *
     * @return node-controller port
     */
    public int getNodeControllerPort(){
        return envExist(ConfigKeys.NODE_CONTROLLER_PORT_KEY) ? getEnvAsInteger(ConfigKeys.NODE_CONTROLLER_PORT_KEY) :
                DEFAULT_NODE_CONTROLLER_PORT;
    }

    /**
     *
     * @return node broker host
     */
    public String getNodeBrokerHost() {
        return envExist(ConfigKeys.NODE_BROKER_HOST_KEY) ? getEnvAsString(ConfigKeys.NODE_BROKER_HOST_KEY) :
                DEFAULT_NODE_BROKER_HOST;
    }

    /**
     *
     * @return node broker port
     */
    // TODO: should be flexibly set due to node broker technology used
    public int getNodeBrokerPort() {
        return envExist(ConfigKeys.NODE_BROKER_PORT_KEY) ? getEnvAsInteger(ConfigKeys.NODE_BROKER_PORT_KEY) :
                DEFAULT_NODE_BROKER_PORT;
    }

    /**
     *
     * @return node location tags
     */
    public List<String> getNodeLocations() {
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> (e.getKey().contains(ConfigKeys.NODE_LOCATION_KEY)))
                .map(x ->  x.getKey().replace(ConfigKeys.NODE_LOCATION_KEY + "_", "").toLowerCase() + "=" + x.getValue())
                .collect(Collectors.toList());
    }

    /**
     *
     * @return supported pipeline elements that can run on this node
     */
    // TODO: get supported PE programmatically instead of environment variables
    public List<String> getSupportedPipelineElements() {
        return System.getenv()
                .entrySet()
                .stream()
                .filter(e -> (e.getKey().contains(ConfigKeys.NODE_SUPPORTED_PE_APP_ID_KEY)))
                .map(x -> x.getValue())
                .collect(Collectors.toList());
    }

    /**
     *
     * @return return sensors/actuators that are accessible
     */
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

    /**
     *
     * @return true if node has cuda capable GPU
     */
    public boolean hasNodeGpu(){
        return envExist(ConfigKeys.NODE_HAS_GPU_KEY) ? getEnvAsBoolean(ConfigKeys.NODE_HAS_GPU_KEY) : false;
    }

    /**
     *
     * @return number of cuda cores
     */
    public int getGpuCores() {
        return envExist(ConfigKeys.NODE_GPU_CUDA_CORES_KEY) ? getEnvAsInteger(ConfigKeys.NODE_GPU_CUDA_CORES_KEY) : 0;
    }

    /**
     *
     * @return specific node GPU type
     */
    public String getGpuType() {
        return envExist(ConfigKeys.NODE_GPU_TYPE_KEY) ? getEnvAsString(ConfigKeys.NODE_GPU_TYPE_KEY) : "n/a";
    }

    /**
     *
     * @return docker pruning frequency
     */
    public int getPruningFreq() {
        return envExist(ConfigKeys.DOCKER_PRUNING_FREQ_SECS_KEY) ? getEnvAsInteger(ConfigKeys.DOCKER_PRUNING_FREQ_SECS_KEY) :
                DEFAULT_DOCKER_PRUNING_FREQ_SECS;
    }

    /**
     *
     * @return node resource update frequency
     */
    public int getNodeResourceUpdateFreqSecs() {
        return envExist(ConfigKeys.NODE_RESOURCE_UPDATE_FREQ_SECS_KEY) ? getEnvAsInteger(ConfigKeys.NODE_RESOURCE_UPDATE_FREQ_SECS_KEY) :
                DEFAULT_NODE_RESOURCE_UPDATE_FREQ_SECS;
    }

    private boolean envExist(String key) {
        return System.getenv(key) != null;
    }

    private String getEnv(String key) {
        return System.getenv(key);
    }

    private String getEnvAsString(String key) {
        return String.valueOf(getEnv(key));
    }

    private int getEnvAsInteger(String key) {
        return Integer.parseInt(getEnv(key));
    }

    private boolean getEnvAsBoolean(String key) {
        return Boolean.parseBoolean(getEnv(key));
    }

}
