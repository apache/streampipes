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
    nodeId: string;
    nodeMetadata: NodeMetadata;
    nodeBrokerInfo: NodeBrokerInfo;
    nodeCapabilities: NodeCapabilities;
    supportedPipelineElementAppIds: string[];
}

export interface NodeMetadata {
    nodeDescription: string;
    nodeLocation: string;
}

export interface NodeBrokerInfo {
    host: string;
    port: number;
}

export interface NodeCapabilities {
    hardware: HardwareCapability;
    software: SoftwareCapability;
    interfaces: HardwareInterface[];
}

export interface HardwareCapability {
    cpu: CpuCapability;
    mem: MemCapability;
    disk: DiskCapability;
    gpu: GpuCapability;
}

export interface CpuCapability {
    cores: number;
    arch: string;
}

export interface MemCapability {
    size: number;
}

export interface DiskCapability {
    size: string;
    type: string;
}

export interface GpuCapability {
    hasGpu: boolean;
    arch: string;
    cudaCores: number;
    type: string;
}

export interface SoftwareCapability {
    os: string;
    cuda: CudaCapability
    docker: DockerCapability;
}

export interface CudaCapability {
    cudaDriverVersion: string;
    cudaRuntimeVersion: string;
}

export interface DockerCapability {
    hasDocker: boolean;
    hasNvidiaRuntime: boolean;
    clientVersion: string;
    serverVersion: string;
    apiVersion: string;
    apiMinVersion: string;
}

export interface HardwareInterface {
    name: string;
    type: string;
    domainProperty: string;
}
