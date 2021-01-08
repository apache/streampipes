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
package org.apache.streampipes.model.node;

import org.apache.streampipes.model.node.resources.*;
import org.apache.streampipes.model.node.resources.fielddevice.FieldDeviceAccessResource;
import org.apache.streampipes.model.node.resources.hardware.*;
import org.apache.streampipes.model.node.resources.software.ContainerRuntime;
import org.apache.streampipes.model.node.resources.software.DockerContainerRuntime;
import org.apache.streampipes.model.node.resources.software.NvidiaContainerRuntime;
import org.apache.streampipes.model.node.resources.software.SoftwareResource;

import java.util.ArrayList;
import java.util.List;

public class NodeResourceBuilder {

    private final NodeResource nodeResource;
    private final HardwareResource hardwareResource;
    private final SoftwareResource softwareResource;
    private final List<FieldDeviceAccessResource> fieldDeviceAccessResources;

    public NodeResourceBuilder() {
        this.nodeResource = new NodeResource();
        this.hardwareResource = new HardwareResource();
        this.softwareResource = new SoftwareResource();
        this.fieldDeviceAccessResources = new ArrayList<>();
    }

    public static NodeResourceBuilder create() {
        return new NodeResourceBuilder();
    }

    public NodeResourceBuilder withFieldDeviceAccessResources(List<FieldDeviceAccessResource> fieldDeviceAccessResources) {
        this.fieldDeviceAccessResources.addAll(fieldDeviceAccessResources);
        return this;
    }

    public NodeResourceBuilder softwareResource(String os, String kernelVersion,
                                                ContainerRuntime containerRuntime) {
        this.softwareResource.setOs(os);
        this.softwareResource.setKernelVersion(kernelVersion);
        if (containerRuntime instanceof DockerContainerRuntime) {
            DockerContainerRuntime dcr = (DockerContainerRuntime) containerRuntime;
            this.softwareResource.setContainerRuntime(dcr);
        } else if (containerRuntime instanceof NvidiaContainerRuntime) {
            NvidiaContainerRuntime ncr = (NvidiaContainerRuntime) containerRuntime;
            this.softwareResource.setContainerRuntime(ncr);
        }

        return this;
    }

    public NodeResourceBuilder hardwareResource(CPU cpu, MEM mem, DISK disk, GPU gpu) {
        this.hardwareResource.setCpu(cpu);
        this.hardwareResource.setMemory(mem);
        this.hardwareResource.setDisk(disk);
        this.hardwareResource.setGpu(gpu);
        return this;
    }

    public NodeResource build() {
        nodeResource.setHardwareResource(hardwareResource);
        nodeResource.setSoftwareResource(softwareResource);
        nodeResource.setFieldDeviceAccessResourceList(fieldDeviceAccessResources);
        return nodeResource;
    }
}
