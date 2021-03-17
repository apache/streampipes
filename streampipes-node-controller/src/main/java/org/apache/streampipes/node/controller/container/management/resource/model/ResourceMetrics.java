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
package org.apache.streampipes.node.controller.container.management.resource.model;

public class ResourceMetrics {

    private String systemTime;
    private String booted;
    private String uptime;
    private String cpuLoad;
    private float cpuLoadInPercent;
    private String cpuTemperature;
    private double cpuTemperatureCelcius;
    private long freeMemoryInBytes;
    private long usedMemoryInBytes;
    private long totalMemoryInBytes;
    private long freeDiskSpaceInBytes;

    public ResourceMetrics() {
    }

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public String getBooted() {
        return booted;
    }

    public void setBooted(String booted) {
        this.booted = booted;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(String cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public float getCpuLoadInPercent() {
        return cpuLoadInPercent;
    }

    public void setCpuLoadInPercent(float cpuLoadInPercent) {
        this.cpuLoadInPercent = cpuLoadInPercent;
    }

    public String getCpuTemperature() {
        return cpuTemperature;
    }

    public void setCpuTemperature(String cpuTemperature) {
        this.cpuTemperature = cpuTemperature;
    }

    public double getCpuTemperatureCelcius() {
        return cpuTemperatureCelcius;
    }

    public void setCpuTemperatureCelcius(double cpuTemperatureCelcius) {
        this.cpuTemperatureCelcius = cpuTemperatureCelcius;
    }

    public long getFreeMemoryInBytes() {
        return freeMemoryInBytes;
    }

    public void setFreeMemoryInBytes(long freeMemoryInBytes) {
        this.freeMemoryInBytes = freeMemoryInBytes;
    }

    public long getUsedMemoryInBytes() {
        return usedMemoryInBytes;
    }

    public void setUsedMemoryInBytes(long usedMemoryInBytes) {
        this.usedMemoryInBytes = usedMemoryInBytes;
    }

    public long getTotalMemoryInBytes() {
        return totalMemoryInBytes;
    }

    public void setTotalMemoryInBytes(long totalMemoryInBytes) {
        this.totalMemoryInBytes = totalMemoryInBytes;
    }

    public long getFreeDiskSpaceInBytes() {
        return freeDiskSpaceInBytes;
    }

    public void setFreeDiskSpaceInBytes(long freeDiskSpaceInBytes) {
        this.freeDiskSpaceInBytes = freeDiskSpaceInBytes;
    }
}
