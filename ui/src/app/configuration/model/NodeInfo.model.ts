
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
