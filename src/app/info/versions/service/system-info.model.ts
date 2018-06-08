export interface SystemInfo {
    javaVmName: string;
    javaVmVendor: string;
    javaVmVersion: string;
    javaRuntimeName: string;
    javaRuntimeVersion: string;
    osName: string;
    osVersion: string;
    cpu: string;

    totalMemory: number;
    freeMemory: number;
    totalMemoryKB: number;
    freeMemoryKB: number;
}