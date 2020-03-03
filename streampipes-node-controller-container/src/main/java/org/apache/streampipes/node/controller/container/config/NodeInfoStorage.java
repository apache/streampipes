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

import org.apache.streampipes.model.node.*;
import org.apache.streampipes.model.node.capabilities.hardware.Hardware;
import org.apache.streampipes.model.node.capabilities.hardware.resources.CPU;
import org.apache.streampipes.model.node.capabilities.hardware.resources.DISK;
import org.apache.streampipes.model.node.capabilities.hardware.resources.GPU;
import org.apache.streampipes.model.node.capabilities.hardware.resources.MEM;
import org.apache.streampipes.model.node.capabilities.interfaces.Interfaces;
import org.apache.streampipes.model.node.capabilities.software.Software;
import org.apache.streampipes.model.node.capabilities.software.resources.Docker;
import org.apache.streampipes.node.controller.container.deployment.utils.DockerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class NodeInfoStorage {

    private static final Logger LOG =
            LoggerFactory.getLogger(NodeInfoStorage.class.getCanonicalName());

    private NodeInfo nodeInfo = new NodeInfo();
    private static final int DEFAULT_NODE_CONTROLLER_PORT = 7077;
    private static final int DEFAULT_NODE_BROKER_PORT = 616161;

    private static final Map<String, String> SystemInfo = DockerUtils.getDockerInfo();

    private static NodeInfoStorage instance = null;

    private NodeInfoStorage() {}

    public static NodeInfoStorage getInstance() {
        if (instance == null)
            instance = new NodeInfoStorage();
        return instance;
    }

    public void add(NodeInfo n) {
        nodeInfo = n;
    }

    public NodeInfo get() {
        return nodeInfo;
    }

    public static void init() {

        NodeInfo nodeInfo = NodeInfoBuilder.create(getNodeControllerId())
                .withNodeControllerPort(getNodeControllerPort())
                .withNodeHost(getNodeHost())
                .withNodeLocation(getNodeLocation())
                .withNodeDescription(getNodeDescription())
                .withNodeCapabilities(getNodeCapabilities())
                .withJmsTransportProtocol(getNodeBrokerHost(), getNodeBrokerPort())
                .withSupportedPipelineElements(getSupportedPipelineElements())
                .build();

        NodeInfoStorage.getInstance().add(nodeInfo);
    }

    private static String getNodeControllerId(){
        return envExists(ConfigKeys.NODE_CONTROLLER_ID_KEY);
    }

    private static int getNodeControllerPort(){
        return System.getenv(ConfigKeys.NODE_CONTROLLER_PORT_KEY) != null
                ? Integer.parseInt(System.getenv(ConfigKeys.NODE_CONTROLLER_PORT_KEY)) : DEFAULT_NODE_CONTROLLER_PORT;
    }

    private static String getNodeHost(){
        return envExists(ConfigKeys.NODE_HOST_KEY);
    }

    private static String getNodeLocation() {
        return envExists(ConfigKeys.NODE_LOCATION_KEY);
    }

    private static String getNodeDescription() {
        return envExists(ConfigKeys.NODE_DESCRIPTION_KEY);
    }

    private static NodeCapabilities getNodeCapabilities() {
        NodeCapabilities nodeCapabilities = new NodeCapabilities();
        nodeCapabilities.setHardware(getActualHardware());
        nodeCapabilities.setSoftware(getActualSoftware());
        nodeCapabilities.setInterfaces(getActualInterfaces());

        return nodeCapabilities;
    }

    private static String getNodeBrokerHost(){
        return envExists(ConfigKeys.NODE_BROKER_HOST_KEY);
    }

    private static int getNodeBrokerPort(){
        return System.getenv(ConfigKeys.NODE_BROKER_PORT_KEY) != null
                ? Integer.parseInt(System.getenv(ConfigKeys.NODE_BROKER_PORT_KEY)) : DEFAULT_NODE_BROKER_PORT;
    }

    // TODO: remove when not needed for anything
    private static List<String> getSupportedPipelineElements() {
        return Collections.emptyList();
    }

    private static Hardware getActualHardware(){
        Hardware hardware = new Hardware();
        hardware.setGpu(getActualGpu());
        hardware.setCpu(getActualCpu());
        hardware.setMem(getActualMem());
        hardware.setDisk(getActualDisk());

        return hardware;
    }

    private static Software getActualSoftware(){
        Software software = new Software();
        software.setOs(SystemInfo.get("os"));
        software.setKernelVersion(SystemInfo.get("kernelVersion"));
        software.setDocker(getActualDocker());

        return software;
    }

    private static List<Interfaces> getActualInterfaces(){
        String[] interfaces = envExists(ConfigKeys.NODE_INTERFACES_KEY)
                .trim()
                .replaceAll("\\[|\\]|\"", "")
                .split("\\s*,\\s*");

        List<Interfaces> interfacesList = new ArrayList<>();
        for(String s: interfaces) {
            String [] substr = s.split(";");
            Interfaces i = new Interfaces();
            i.setName(substr[0]);
            i.setType(substr[1]);
            i.setConnectionInfo(substr[2]);
            i.setConnectionType(substr[3]);

            interfacesList.add(i);
        }

        return interfacesList;
    }

    private static CPU getActualCpu(){
        CPU cpu = new CPU();
        cpu.setCores(Integer.parseInt(SystemInfo.get("cpus")));
        cpu.setArch(SystemInfo.get("arch"));
        return cpu;
    }

    private static MEM getActualMem() {
        MEM mem = new MEM();
        mem.setMemTotal(Long.parseLong(SystemInfo.get("memTotal")));
        return mem;
    }

    private static DISK getActualDisk() {
        // TODO: add disk info
        DISK disk = new DISK();
        disk.setDiskTotal(System.getenv(ConfigKeys.NODE_DISK_TOTAL_KEY) != null
                ? Integer.parseInt(System.getenv(ConfigKeys.NODE_DISK_TOTAL_KEY)) : 0);
        disk.setType(envExists(ConfigKeys.NODE_DISK_TYPE_KEY));

        return disk;
    }

    private static GPU getActualGpu(){
        boolean hasGpu = Boolean.parseBoolean(envExists(ConfigKeys.NODE_HAS_GPU_KEY));
        GPU gpu = new GPU();
        if (!hasGpu) {
            gpu.setHasGPU(hasGpu);
            gpu.setCudaCores(0);
            gpu.setType(null);
        } else {
            gpu.setHasGPU(hasGpu);
            gpu.setCudaCores(System.getenv(ConfigKeys.NODE_GPU_CUDA_CORES_KEY) != null
                    ? Integer.parseInt(System.getenv(ConfigKeys.NODE_GPU_CUDA_CORES_KEY)) : 0);
            gpu.setType(System.getenv(ConfigKeys.NODE_GPU_TYPE_KEY) != null
                    ? System.getenv(ConfigKeys.NODE_GPU_TYPE_KEY) : "not specified");
        }

        return gpu;
    }

    private static Docker getActualDocker() {

        Docker docker = new Docker();
        docker.setHasDocker(true);
        docker.setHasNvidiaRuntime(System.getenv(ConfigKeys.NODE_HAS_NVIDIA_DOCKER_RUNTIME_KEY) != null
                ? Boolean.parseBoolean(System.getenv(ConfigKeys.NODE_HAS_NVIDIA_DOCKER_RUNTIME_KEY)) : false);
        docker.setServerVersion(SystemInfo.get("serverVersion"));
        docker.setApiVersion(SystemInfo.get("apiVersion"));

        return docker;
    }

    private static String envExists(String key) {
        return System.getenv(key) != null ? System.getenv(key) : "";
    }
}
