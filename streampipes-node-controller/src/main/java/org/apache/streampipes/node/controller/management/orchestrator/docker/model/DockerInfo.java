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
package org.apache.streampipes.node.controller.management.orchestrator.docker.model;

public class DockerInfo {

    private String serverVersion;
    private String apiVersion;
    private Long memTotal;
    private int cpus;
    private String os;
    private String osType;
    private String kernelVersion;
    private String arch;
    private boolean hasNvidiaRuntime;

    public DockerInfo(){}

    public DockerInfo(String serverVersion, String apiVersion, Long memTotal, int cpus, String os, String osType, String kernelVersion, String arch, boolean hasNvidiaRuntime) {
        this.serverVersion = serverVersion;
        this.apiVersion = apiVersion;
        this.memTotal = memTotal;
        this.cpus = cpus;
        this.os = os;
        this.osType = osType;
        this.kernelVersion = kernelVersion;
        this.arch = arch;
        this.hasNvidiaRuntime = hasNvidiaRuntime;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(Long memTotal) {
        this.memTotal = memTotal;
    }

    public int getCpus() {
        return cpus;
    }

    public void setCpus(int cpus) {
        this.cpus = cpus;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public boolean isHasNvidiaRuntime() {
        return hasNvidiaRuntime;
    }

    public void setHasNvidiaRuntime(boolean hasNvidiaRuntime) {
        this.hasNvidiaRuntime = hasNvidiaRuntime;
    }
}
