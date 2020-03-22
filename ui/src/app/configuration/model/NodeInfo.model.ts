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

export interface NodeInfo {
    nodeControllerId: string;
    nodeControllerPort: number;
    nodeMetadata: NodeMetadata;
    nodeBrokerInfo: NodeBrokerInfo;
    nodeResources: NodeResources;
    supportedPipelineElementAppIds: string[];
}

export interface NodeMetadata {
    nodeAddress: string;
    nodeModel: string;
    nodeLocationTags: string[];
}

export interface NodeBrokerInfo {
    host: string;
    port: number;
}

export interface NodeResources {
    hardwareResource: HardwareResource;
    softwareResource: SoftwareResource;
    accessibleSensorActuatorResource: AccessibleSensorActuatorResource[];
}

export interface HardwareResource {
    cpu: CpuResource;
    memory: MemResource;
    disk: DiskResource;
    gpu: GpuResource;
}

export interface CpuResource {
    cores: number;
    arch: string;
}

export interface MemResource {
    memTotal: number;
}

export interface DiskResource {
    diskTotal: number;
}

export interface GpuResource {
    hasGpu: boolean;
    cudaCores: number;
    type: string;
}

export interface SoftwareResource {
    os: string;
    kernelVersion: string;
    // cuda: CudaResource
    docker: DockerResource;
}

export interface CudaResource {
    cudaDriverVersion: string;
    cudaRuntimeVersion: string;
}

export interface DockerResource {
    hasDocker: boolean;
    hasNvidiaRuntime: boolean;
    clientVersion: string;
    serverVersion: string;
    apiVersion: string;
    apiMinVersion: string;
}

export interface AccessibleSensorActuatorResource {
    name: string;
    type: string;
    connectionInfo: string;
    connectionType: string;
}
