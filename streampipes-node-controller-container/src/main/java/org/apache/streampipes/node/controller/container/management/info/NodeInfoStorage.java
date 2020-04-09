package org.apache.streampipes.node.controller.container.management.info;/*
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
import org.apache.streampipes.model.node.resources.hardware.HardwareResource;
import org.apache.streampipes.model.node.resources.hardware.CPU;
import org.apache.streampipes.model.node.resources.hardware.DISK;
import org.apache.streampipes.model.node.resources.hardware.GPU;
import org.apache.streampipes.model.node.resources.hardware.MEM;
import org.apache.streampipes.model.node.resources.interfaces.AccessibleSensorActuatorResource;
import org.apache.streampipes.model.node.resources.software.SoftwareResource;
import org.apache.streampipes.model.node.resources.software.Docker;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.container.management.container.DockerInfo;
import org.apache.streampipes.node.controller.container.management.container.DockerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.*;

public class NodeInfoStorage {

    private static final Logger LOG =
            LoggerFactory.getLogger(NodeInfoStorage.class.getCanonicalName());

    private NodeInfo nodeInfo = new NodeInfo();

    private static DockerInfo DockerInfo = DockerUtils.getInstance().getDockerInfo();
    // OSHI to retreive system information
    private static SystemInfo si = new SystemInfo();
    private static HardwareAbstractionLayer hal = si.getHardware();
    private static OperatingSystem os = si.getOperatingSystem();

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

    public NodeInfo retrieveNodeInfo() {
        return nodeInfo;
    }

    public static void init() {

        NodeInfo nodeInfo = NodeInfoBuilder.create(getNodeControllerId())
                .withNodeControllerPort(getNodeControllerPort())
                .withNodeHost(getNodeHost())
                .withNodeLocation(getNodeLocation())
                .withNodeModel(getNodeModel())
                .withNodeResources(getNodeResources())
                .withJmsTransportProtocol(getNodeBrokerHost(), getNodeBrokerPort())
                .withSupportedPipelineElements(getSupportedPipelineElements())
                .build();

        NodeInfoStorage.getInstance().add(nodeInfo);
    }

    private static String getNodeControllerId(){
        return NodeControllerConfig.INSTANCE.getNodeControllerId();
    }

    private static int getNodeControllerPort(){
        return NodeControllerConfig.INSTANCE.getNodeControllerPort();
    }

    private static String getNodeHost(){
        return NodeControllerConfig.INSTANCE.getNodeHostName();
    }

    private static List<String> getNodeLocation() {
        return NodeControllerConfig.INSTANCE.getNodeLocations();
    }

    private static String getNodeModel() {
        return !printComputerSystem(hal.getComputerSystem()).equals("") ? printComputerSystem(hal.getComputerSystem()) : "n/a";
    }

    private static NodeResources getNodeResources() {
        NodeResources nodeResources = new NodeResources();
        nodeResources.setHardwareResource(getNodeHardwareResource());
        nodeResources.setSoftwareResource(getNodeSoftwareResource());
        nodeResources.setAccessibleSensorActuatorResource(getAcessibleSensorActuatorResources());

        return nodeResources;
    }

    private static String getNodeBrokerHost(){
        return NodeControllerConfig.INSTANCE.getNodeBrokerHost();
    }

    private static int getNodeBrokerPort(){
        return NodeControllerConfig.INSTANCE.getNodeBrokerPort();
    }

    // TODO: get supported PE programmatically instead of environment variables
    private static List<String> getSupportedPipelineElements() {
        return NodeControllerConfig.INSTANCE.getSupportedPipelineElements();
    }

    private static HardwareResource getNodeHardwareResource(){
        HardwareResource hardwareResource = new HardwareResource();
        hardwareResource.setGpu(getNodeGpu());
        hardwareResource.setCpu(getNodeCpu());
        hardwareResource.setMemory(getNodeMemory());
        hardwareResource.setDisk(getNodeDisk());

        return hardwareResource;
    }

    private static SoftwareResource getNodeSoftwareResource(){
        SoftwareResource softwareResource = new SoftwareResource();
        softwareResource.setOs(DockerInfo.getOs());
        softwareResource.setKernelVersion(DockerInfo.getKernelVersion());
        softwareResource.setDocker(getDocker());

        return softwareResource;
    }

    private static List<AccessibleSensorActuatorResource> getAcessibleSensorActuatorResources(){
        return NodeControllerConfig.INSTANCE.getAccessibleSensorActuator();
    }

    private static CPU getNodeCpu(){
        CPU cpu = new CPU();
        if (DockerInfo.getOs().equals("Docker Desktop")) {
            cpu.setCores(DockerInfo.getCpus());
            cpu.setArch(DockerInfo.getArch());
        } else {
            cpu.setCores(hal.getProcessor().getLogicalProcessorCount());
            cpu.setArch(DockerInfo.getArch());
        }
        return cpu;
    }

    private static MEM getNodeMemory() {
        MEM mem = new MEM();
        if (DockerInfo.getOs().equals("Docker Desktop")) {
            mem.setMemTotal(DockerInfo.getMemTotal());
        } else {
            mem.setMemTotal(hal.getMemory().getTotal());
        }
        return mem;
    }

    private static DISK getNodeDisk() {
        DISK disk = new DISK();
        disk.setDiskTotal(getDiskUsage(os.getFileSystem()));
        return disk;
    }

    // TODO: get node GPU info programmatically
    private static GPU getNodeGpu(){
        boolean hasGpu = NodeControllerConfig.INSTANCE.hasNodeGpu();
        GPU gpu = new GPU();
        if (!hasGpu) {
            gpu.setHasGPU(hasGpu);
            gpu.setCudaCores(0);
            gpu.setType(null);
        } else {
            gpu.setHasGPU(hasGpu);
            gpu.setCudaCores(NodeControllerConfig.INSTANCE.getGpuCores());
            gpu.setType(NodeControllerConfig.INSTANCE.getGpuType());
        }
        return gpu;
    }

    private static Docker getDocker() {

        Docker docker = new Docker();
        docker.setHasDocker(true);
        docker.setHasNvidiaRuntime(DockerInfo.isHasNvidiaRuntime());
        docker.setServerVersion(DockerInfo.getServerVersion());
        docker.setApiVersion(DockerInfo.getApiVersion());

        return docker;
    }

    private static String printComputerSystem(ComputerSystem cs) {
        return cs.getModel().trim();
    }

    private static Long getDiskUsage(FileSystem fs) {
        OSFileStore[] fsArray = fs.getFileStores();
        long diskTotal = 0L;
        for(OSFileStore f : fsArray) {
            // has SATA disk
            if (f.getVolume().contains("/dev/sda")){
                diskTotal = f.getTotalSpace();
            }
            // has NVME
            else if (f.getVolume().contains("/dev/nvme")){
                diskTotal = f.getTotalSpace();
            }
            // Docker in RPi
            else if (f.getVolume().contains("/dev/root")){
                diskTotal = f.getTotalSpace();
            }
            // Docker in Jetson Nano
            else if (f.getVolume().contains("/dev/mmcblk0p1")){
                diskTotal = f.getTotalSpace();
            }
            // macos
            else if (f.getVolume().contains("/dev/mmcblk0p1") && f.getMount().equals("/")){
                diskTotal = f.getTotalSpace();
            }
        }

        return diskTotal;
    }
}
