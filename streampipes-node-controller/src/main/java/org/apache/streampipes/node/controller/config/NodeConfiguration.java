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

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.streampipes.model.node.resources.fielddevice.FieldDeviceAccessResource;
import org.apache.streampipes.node.controller.config.utils.ConfigUtils;
import org.apache.streampipes.node.controller.management.offloading.strategies.OffloadingStrategyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public final class NodeConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(NodeConfiguration.class.getCanonicalName());

    private static final String[] VALID_URL_SCHEMES = new String[]{"http","https", "tcp"};

    private static String nodeApiKey;
    private static String nodeHost;
    private static String nodeType;
    private static String nodeControllerId;
    private static String nodeControllerUrl;
    private static String nodeControllerHost;
    private static int nodeControllerPort;
    private static String nodeBrokerHost;
    private static int nodeBrokerPort;
    private static String nodeBrokerProtocol;
    private static String backendUrl;
    private static String backendHost;
    private static int backendPort;
    private static String streampipesVersion;
    private static List<String> nodeTags = new ArrayList<>();
    private static List<FieldDeviceAccessResource> fieldDeviceAccessResources = new ArrayList<>();
    private static List<String> supportedPipelineElements = new ArrayList<>();
    private static boolean gpuAccelerated;
    private static int gpuCores;
    private static String gpuType;
    private static int dockerPruningFreqSecs;
    private static int resourceMonitorFreqSecs;
    private static int relayEventBufferSize;
    private static String consulHost;
    private static boolean autoOffloadingActivated;
    private static OffloadingStrategyType autoOffloadingStrategy;
    private static String nodeStoragePath;
    private static String loggingMqttUrl;

    private static HashMap<String, String> configMap;

    public static String getNodeApiKey() {
        return nodeApiKey;
    }

    public static void setNodeApiKey(String nodeApiKey) {
        NodeConfiguration.nodeApiKey = nodeApiKey;
    }

    public static String getNodeControllerUrl() {
        return nodeControllerUrl;
    }

    public static void setNodeControllerUrl(String nodeControllerUrl) {
        NodeConfiguration.nodeControllerUrl = nodeControllerUrl;
    }

    public static String getNodeHost() {
        return nodeHost;
    }

    public static void setNodeHost(String nodeHost) {
        NodeConfiguration.nodeHost = nodeHost;
    }

    public static String getNodeType() {
        return nodeType;
    }

    public static void setNodeType(String nodeType) {
        NodeConfiguration.nodeType = nodeType;
    }

    public static String getNodeControllerId() {
        return nodeControllerId;
    }

    public static void setNodeControllerId(String nodeControllerId) {
        NodeConfiguration.nodeControllerId = nodeControllerId;
    }

    public static String getNodeControllerHost() {
        return nodeControllerHost;
    }

    public static void setNodeControllerHost(String nodeControllerHost) {
        NodeConfiguration.nodeControllerHost = nodeControllerHost;
    }

    public static int getNodeControllerPort() {
        return nodeControllerPort;
    }

    public static void setNodeControllerPort(int nodeControllerPort) {
        NodeConfiguration.nodeControllerPort = nodeControllerPort;
    }

    public static String getNodeBrokerHost() {
        return nodeBrokerHost;
    }

    public static void setNodeBrokerHost(String nodeBrokerHost) {
        NodeConfiguration.nodeBrokerHost = nodeBrokerHost;
    }

    public static int getNodeBrokerPort() {
        return nodeBrokerPort;
    }

    public static void setNodeBrokerPort(int nodeBrokerPort) {
        NodeConfiguration.nodeBrokerPort = nodeBrokerPort;
    }

    public static String getNodeBrokerProtocol() {
        return nodeBrokerProtocol;
    }

    public static void setNodeBrokerProtocol(String nodeBrokerProtocol) {
        NodeConfiguration.nodeBrokerProtocol = nodeBrokerProtocol;
    }

    public static String getBackendUrl() {
        return backendUrl;
    }

    public static void setBackendUrl(String backendUrl) {
        NodeConfiguration.backendUrl = backendUrl;
    }

    public static String getBackendHost() {
        return backendHost;
    }

    public static void setBackendHost(String backendHost) {
        NodeConfiguration.backendHost = backendHost;
    }

    public static int getBackendPort() {
        return backendPort;
    }

    public static void setBackendPort(int backendPort) {
        NodeConfiguration.backendPort = backendPort;
    }

    public static String getStreampipesVersion() {
        return streampipesVersion;
    }

    public static void setStreampipesVersion(String streampipesVersion) {
        NodeConfiguration.streampipesVersion = streampipesVersion;
    }

    public static List<String> getNodeTags() {
        return nodeTags;
    }

    public static void setNodeTags(List<String> nodeTags) {
        NodeConfiguration.nodeTags = nodeTags;
    }

    public static void addNodeTags(List<String> nodeTags) {
        NodeConfiguration.nodeTags.addAll(nodeTags);
    }

    public static void addNodeTag(String nodeTag) {
        NodeConfiguration.nodeTags.add(nodeTag);
    }

    public static List<FieldDeviceAccessResource> getFieldDeviceAccessResources() {
        return fieldDeviceAccessResources;
    }

    public static void setFieldDeviceAccessResources(List<FieldDeviceAccessResource> fieldDeviceAccessResources) {
        NodeConfiguration.fieldDeviceAccessResources = fieldDeviceAccessResources;
    }

    public static void addFieldDeviceAccessResources(List<FieldDeviceAccessResource> fieldDeviceAccessResources) {
        NodeConfiguration.fieldDeviceAccessResources.addAll(fieldDeviceAccessResources);
    }

    public static void addFieldDeviceAccessResource(FieldDeviceAccessResource fieldDeviceAccessResource) {
        NodeConfiguration.fieldDeviceAccessResources.add(fieldDeviceAccessResource);
    }

    public static boolean isGpuAccelerated() {
        return gpuAccelerated;
    }

    public static void setGpuAccelerated(boolean gpuAccelerated) {
        NodeConfiguration.gpuAccelerated = gpuAccelerated;
    }

    public static int getGpuCores() {
        return gpuCores;
    }

    public static void setGpuCores(int gpuCores) {
        NodeConfiguration.gpuCores = gpuCores;
    }

    public static String getGpuType() {
        return gpuType;
    }

    public static void setGpuType(String gpuType) {
        NodeConfiguration.gpuType = gpuType;
    }

    public static int getDockerPruningFreqSecs() {
        return dockerPruningFreqSecs;
    }

    public static void setDockerPruningFreqSecs(int dockerPruningFreqSecs) {
        NodeConfiguration.dockerPruningFreqSecs = dockerPruningFreqSecs;
    }

    public static int getResourceMonitorFreqSecs() {
        return resourceMonitorFreqSecs;
    }

    public static void setResourceMonitorFreqSecs(int resourceMonitorFreqSecs) {
        NodeConfiguration.resourceMonitorFreqSecs = resourceMonitorFreqSecs;
    }

    public static int getRelayEventBufferSize() {
        return relayEventBufferSize;
    }

    public static void setRelayEventBufferSize(int relayEventBufferSize) {
        NodeConfiguration.relayEventBufferSize = relayEventBufferSize;
    }

    public static String getConsulHost() {
        return consulHost;
    }

    public static void setConsulHost(String consulHost) {
        NodeConfiguration.consulHost = consulHost;
    }

    public static List<String> getSupportedPipelineElements() {
        return supportedPipelineElements;
    }

    public static void setSupportedPipelineElements(List<String> supportedPipelineElements) {
        NodeConfiguration.supportedPipelineElements = supportedPipelineElements;
    }

    public static void addSupportedPipelineElements(List<String> supportedPipelineElements){
        NodeConfiguration.supportedPipelineElements.addAll(supportedPipelineElements);
    }

    public static boolean isAutoOffloadingActivated() {
        return autoOffloadingActivated;
    }

    public static void setAutoOffloadingActivated(boolean autoOffloadingActivated) {
        NodeConfiguration.autoOffloadingActivated = autoOffloadingActivated;
    }

    public static OffloadingStrategyType getAutoOffloadingStrategy() {
        return autoOffloadingStrategy;
    }

    public static void setAutoOffloadingStrategy(OffloadingStrategyType autoOffloadingStrategy) {
        NodeConfiguration.autoOffloadingStrategy = autoOffloadingStrategy;
    }

    public static String getNodeStoragePath() {
        return nodeStoragePath;
    }

    public static void setNodeStoragePath(String nodeStoragePath) {
        NodeConfiguration.nodeStoragePath = nodeStoragePath;
    }

    public static String getLoggingMqttUrl() {
        return loggingMqttUrl;
    }

    public static void setLoggingMqttUrl(String loggingMqttUrl) {
        NodeConfiguration.loggingMqttUrl = loggingMqttUrl;
    }

    public static HashMap<String, String> getConfigMap() {
        return configMap;
    }

    /**
     * load configuration from environment variables or use default values from {@link EnvConfigParam}
     */
    public static void loadConfigFromEnvironment() {
        NodeConfiguration.configMap = new HashMap<>();

        EnvConfigParam.getAllEnvironmentConfigs().forEach(configParam -> {

            String envKey = configParam.getEnvironmentKey();
            String defaultValue = configParam.getDefaultValue();

            String value = ConfigUtils.envOrDefault(envKey, defaultValue, String.class);

            switch (configParam) {
                case STREAMPIPES_VERSION:
                    configMap.put(envKey, value);
                    setStreampipesVersion(value);
                    break;

                case API_KEY:
                    if (!"true".equals(System.getenv("SP_DEBUG"))) {
                        if (!value.isEmpty()) {
                            configMap.put(envKey, value);
                            setNodeApiKey(value);
                            break;
                        }
                        throw new RuntimeException("StreamPipes API key not provided");
                    }
                    break;

                case NODE_HOST:
                    if (!System.getenv().containsKey(EnvConfigParam.BACKEND_URL.getEnvironmentKey())) {
                        configMap.put(envKey, value);
                        setNodeHost(value);
                        break;
                    }
                    break;


                case NODE_CONTROLLER_URL:
                    if (isValidUrl(value)) {
                        configMap.put(envKey, value);
                        setNodeControllerUrl(value);
                        try {
                            URL url = new URL(value);
                            configMap.put(EnvConfigParam.NODE_HOST.getEnvironmentKey(), url.getHost());
                            setNodeHost(url.getHost());

                            configMap.put(EnvConfigParam.NODE_CONTROLLER_CONTAINER_HOST.getEnvironmentKey(), url.getHost());
                            setNodeControllerHost(url.getHost());

                            configMap.put(EnvConfigParam.NODE_CONTROLLER_CONTAINER_PORT.getEnvironmentKey(),
                                    String.valueOf(url.getPort()));
                            setNodeControllerPort(url.getPort());

                            configMap.put(EnvConfigParam.NODE_BROKER_CONTAINER_HOST.getEnvironmentKey(), url.getHost());
                            setNodeBrokerHost(url.getHost());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    throw new IllegalArgumentException(String.format("Url not valid %s=%s", envKey, value));

                case NODE_CONTROLLER_ID:
                    configMap.put(envKey, value);
                    setNodeControllerId(value);
                    break;
                case NODE_TYPE:
                    configMap.put(envKey, value);
                    setNodeType(value);
                    break;
                case NODE_TAGS:
                    configMap.put(envKey, value);
                    List<String> nodeTags = Arrays.asList(value.split(";").clone());
                    addNodeTags(nodeTags);
                    break;

                case NODE_CONTROLLER_CONTAINER_HOST:
                    if (!System.getenv().containsKey(EnvConfigParam.BACKEND_URL.getEnvironmentKey())) {
                        configMap.put(envKey, value);
                        setNodeControllerHost(value);
                        break;
                    }
                    break;

                case NODE_CONTROLLER_CONTAINER_PORT:
                    if (!System.getenv().containsKey(EnvConfigParam.BACKEND_URL.getEnvironmentKey())) {
                        configMap.put(envKey, value);
                        setNodeControllerPort(Integer.parseInt(value));
                        break;
                    }
                    break;

                case NODE_BROKER_CONTAINER_HOST:
                    if (!System.getenv().containsKey(EnvConfigParam.BACKEND_URL.getEnvironmentKey())) {
                        configMap.put(envKey, value);
                        setNodeBrokerHost(value);
                        break;
                    }
                    break;

                case NODE_BROKER_CONTAINER_PORT:
                    configMap.put(envKey, value);
                    setNodeBrokerPort(Integer.parseInt(value));
                    break;

                case NODE_BROKER_PROTOCOL:
                    configMap.put(envKey, value);
                    setNodeBrokerProtocol(value);
                    break;

                case BACKEND_URL:
                    if (isValidUrl(value)) {
                        configMap.put(envKey, value);
                        setBackendUrl(value);
                        try {
                            URL url = new URL(value);
                            configMap.put(EnvConfigParam.BACKEND_HOST.getEnvironmentKey(), url.getHost());
                            setBackendHost(url.getHost());

                            configMap.put(EnvConfigParam.BACKEND_PORT.getEnvironmentKey(),
                                    String.valueOf(url.getPort()));
                            setBackendPort(url.getPort());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    throw new IllegalArgumentException(String.format("Url not valid %s=%s", envKey, value));

                case BACKEND_HOST:
                    if (!System.getenv().containsKey(EnvConfigParam.BACKEND_URL.getEnvironmentKey())) {
                        configMap.put(envKey, value);
                        setBackendHost(value);
                        break;
                    }
                    break;

                case BACKEND_PORT:
                    if (!System.getenv().containsKey(EnvConfigParam.BACKEND_URL.getEnvironmentKey())) {
                        configMap.put(envKey, value);
                        setBackendPort(Integer.parseInt(value));
                        break;
                    }
                    break;

                case DOCKER_PRUNING_FREQ:
                    configMap.put(envKey, value);
                    setDockerPruningFreqSecs(Integer.parseInt(value));
                    break;
                case RESOURCE_UPDATE_FREQ:
                    configMap.put(envKey, value);
                    setResourceMonitorFreqSecs(Integer.parseInt(value));
                    break;
                case EVENT_RELAY_BUFFER_SIZE:
                    configMap.put(envKey, value);
                    setRelayEventBufferSize(Integer.parseInt(value));
                    break;
                case NODE_GPU_ACCESS:
                    configMap.put(envKey, value);
                    setGpuAccelerated(Boolean.parseBoolean(value));
                    break;
                case NODE_GPU_CORES:
                    configMap.put(envKey, value);
                    setGpuCores(Integer.parseInt(value));
                    break;
                case NODE_GPU_TYPE:
                    configMap.put(envKey, value);
                    setGpuType(value);
                    break;
                case NODE_ACCESSIBLE_FIELD_DEVICE:
                    List<FieldDeviceAccessResource> fieldDevicesInEnv = System.getenv().entrySet().stream()
                            .filter(entry -> {
                                if (entry.getKey().contains(envKey)) {
                                    configMap.put(entry.getKey(), entry.getValue());
                                    return true;
                                }
                                return false;
                            })
                            .map(x -> {
                                FieldDeviceAccessResource a = new FieldDeviceAccessResource();
                                a.setDeviceName(x.getValue().split(";")[0]);
                                a.setDeviceType(x.getValue().split(";")[1]);
                                a.setConnectionType(x.getValue().split(";")[2]);
                                a.setConnectionString(x.getValue().split(";")[3]);
                                return a;
                            })
                            .collect(Collectors.toList());

                    addFieldDeviceAccessResources(fieldDevicesInEnv);
                    break;

                case CONSUL_LOCATION:
                    configMap.put(envKey, value);
                    setConsulHost(value);
                    break;
                case SUPPORTED_PIPELINE_ELEMENTS:
                    configMap.put(envKey, value);
                    List<String> supportedPipelineElements = Arrays.asList(value.split(";").clone());
                    addSupportedPipelineElements(supportedPipelineElements);
                    break;
                case AUTO_OFFLOADING:
                    configMap.put(envKey, value);
                    setAutoOffloadingActivated(Boolean.parseBoolean(value));
                    break;
                case AUTO_OFFLOADING_STRATEGY:
                    configMap.put(envKey, value);
                    setAutoOffloadingStrategy(OffloadingStrategyType.fromString(value));
                    break;
                case NODE_STORAGE_PATH:
                    configMap.put(envKey, value);
                    setNodeStoragePath(value);
                    break;
                case LOGGING_MQTT_URL:
                    if (isValidUrl(value)) {
                        configMap.put(envKey, value);
                        setLoggingMqttUrl(value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid environment config param: " + configParam);
            }
        });
        printConfigMapToConsole();
    }

    private static boolean isValidUrl(String url) {
        UrlValidator urlValidator = new UrlValidator(VALID_URL_SCHEMES, UrlValidator.ALLOW_LOCAL_URLS);;
        return urlValidator.isValid(url);
    }

    private static void printConfigMapToConsole() {
        LOG.info("Configuration properties:");
        NodeConfiguration.getConfigMap().forEach((key, value) -> System.out.printf("%s=%s%n", key, value));
    }
}
