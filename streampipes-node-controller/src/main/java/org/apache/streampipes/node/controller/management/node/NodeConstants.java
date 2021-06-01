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
package org.apache.streampipes.node.controller.management.node;

import org.apache.streampipes.model.node.container.ContainerLabel;
import org.apache.streampipes.model.node.container.DeploymentContainer;
import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.model.node.resources.software.ContainerRuntime;
import org.apache.streampipes.model.node.resources.software.DockerContainerRuntime;
import org.apache.streampipes.model.node.resources.fielddevice.FieldDeviceAccessResource;
import org.apache.streampipes.model.node.resources.software.NvidiaContainerRuntime;
import org.apache.streampipes.model.node.resources.hardware.CPU;
import org.apache.streampipes.model.node.resources.hardware.DISK;
import org.apache.streampipes.model.node.resources.hardware.GPU;
import org.apache.streampipes.model.node.resources.hardware.MEM;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.orchestrator.docker.DockerContainerDeclarerSingleton;
import org.apache.streampipes.node.controller.management.orchestrator.docker.model.DockerInfo;
import org.apache.streampipes.node.controller.management.orchestrator.docker.utils.DockerUtils;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class NodeConstants {
    private static final DockerInfo docker = DockerUtils.getInstance().getDockerInfo();

    // OSHI to retreive system information
    private static final SystemInfo si = new SystemInfo();
    private static final HardwareAbstractionLayer hal = si.getHardware();
    private static final OperatingSystem os = si.getOperatingSystem();


    // accessible node constants
    public static final String NODE_CONTROLLER_ID = NodeConfiguration.getNodeControllerId();
    public static final String NODE_HOSTNAME = NodeConfiguration.getNodeHost();
    public static final int NODE_PORT = NodeConfiguration.getNodeControllerPort();
    public static final String NODE_BROKER_HOST = NodeConfiguration.getNodeBrokerHost();
    public static final int NODE_BROKER_PORT = NodeConfiguration.getNodeBrokerPort();
    public static final String NODE_BROKER_PROTOCOL = NodeConfiguration.getNodeBrokerProtocol();
    public static final String NODE_TYPE = NodeConfiguration.getNodeType();
    public static final List<String> NODE_LOCATION_TAGS = NodeConfiguration.getNodeTags();
    public static final List<String> SUPPORTED_PIPELINE_ELEMENTS = NodeConfiguration.getSupportedPipelineElements();
    public static final String NODE_MODEL = !printComputerSystem(hal.getComputerSystem()).equals("")  ?
            printComputerSystem(hal.getComputerSystem()) : "n/a";
    public static final List<DeploymentContainer> REGISTERED_DEPLOYMENT_CONTAINERS = getRegisteredDeploymentContainers();
    public static final List<DeploymentContainer> AUTO_DEPLOYMENT_CONTAINERS = getAutoDeploymentContainer();

    public static final String NODE_OPERATING_SYSTEM = docker.getOs();
    public static final String NODE_KERNEL_VERSION = docker.getKernelVersion();
    public static final ContainerRuntime NODE_CONTAINER_RUNTIME = getContainerRuntime();
    public static final List<FieldDeviceAccessResource> FIELD_DEVICE_ACCESS_RESOURCE_LIST =
            NodeConfiguration.getFieldDeviceAccessResources();
    public static final CPU NODE_CPU = getNodeCpu();
    public static final MEM NODE_MEMORY = getNodeMemory();
    public static final DISK NODE_DISK = getNodeDisk();
    public static final GPU NODE_GPU = getNodeGpu();

    private static List<DeploymentContainer> getRegisteredDeploymentContainers() {
        List<DeploymentContainer> containers = new ArrayList<>();
        DockerUtils.getInstance().getRunningStreamPipesContainer()
                .forEach(runningContainer -> {
                    DockerContainer container = new DockerContainer();
                    container.setContainerName(runningContainer.names().get(0).replace("/", ""));
                    container.setImageTag(runningContainer.image());

                    Optional<String> serviceId = runningContainer.labels().entrySet().stream()
                            .filter(l -> l.getKey().contains("org.apache.streampipes.service.id"))
                            .map(Map.Entry::getValue)
                            .findFirst();

                    serviceId.ifPresent(container::setServiceId);

                    container.setLabels(
                            runningContainer.labels().entrySet().stream()
                                    .filter(l -> l.getKey().contains("org.apache.streampipes"))
                                    .map(ContainerLabel::new)
                                    .collect(Collectors.toList()));
                    containers.add(container);
                });
        return containers;
    }

    private static List<DeploymentContainer> getAutoDeploymentContainer() {
        List<DeploymentContainer> deploymentContainers = new ArrayList<>();
        deploymentContainers.addAll(DockerContainerDeclarerSingleton.getInstance().getAllDockerContainerAsList());

        return deploymentContainers;
    }


    private static CPU getNodeCpu(){
        CPU cpu = new CPU();
        if (docker.getOs().equals("Docker Desktop")) {
            cpu.setCores(docker.getCpus());
            cpu.setArch(docker.getArch());
        } else {
            cpu.setCores(hal.getProcessor().getLogicalProcessorCount());
            cpu.setArch(docker.getArch());
        }
        return cpu;
    }
    private static MEM getNodeMemory() {
        MEM mem = new MEM();
        if (docker.getOs().equals("Docker Desktop")) {
            mem.setMemTotal(docker.getMemTotal());
        } else {
            mem.setMemTotal(hal.getMemory().getTotal());
        }
        return mem;
    }

    // TODO: get node GPU info programmatically
    private static GPU getNodeGpu(){
        GPU gpu = new GPU();
        if (!NodeConfiguration.isGpuAccelerated()) {
            gpu.setCores(0);
            gpu.setType(null);
        } else {
            gpu.setCores(NodeConfiguration.getGpuCores());
            gpu.setType(NodeConfiguration.getGpuType());
        }
        return gpu;
    }

    private static DISK getNodeDisk() {
        DISK disk = new DISK();
        disk.setDiskTotal(getDiskUsage(os.getFileSystem()));
        return disk;
    }

    private static ContainerRuntime getContainerRuntime() {
        ContainerRuntime containerRuntime;
        if (docker.isHasNvidiaRuntime()) {
            containerRuntime = new NvidiaContainerRuntime();
            // TODO: get CUDA info programmatically
//            ((NvidiaContainerRuntime) containerRuntime).setCudaDriverVersion();
//            ((NvidiaContainerRuntime) containerRuntime).setCudaRuntimeVersion();
        } else {
            containerRuntime = new DockerContainerRuntime();
        }
        containerRuntime.setServerVersion(docker.getServerVersion());
        containerRuntime.setApiVersion(docker.getApiVersion());

        return containerRuntime;
    }

    private static String printComputerSystem(ComputerSystem cs) {
        return cs.getModel().trim();
    }

    private static Long getDiskUsage(FileSystem fs) {
        List<OSFileStore> fsArray = fs.getFileStores();
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
            // macos
            else if (f.getVolume().contains("/dev/disk") && f.getMount().equals("/")){
                diskTotal = f.getTotalSpace();
            }
        }

        return diskTotal;
    }
}
