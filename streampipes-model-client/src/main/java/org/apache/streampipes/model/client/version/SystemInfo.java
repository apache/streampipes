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
package org.apache.streampipes.model.client.version;

public class SystemInfo {

  private String javaVmName;
  private String javaVmVendor;
  private String javaVmVersion;
  private String javaRuntimeName;
  private String javaRuntimeVersion;
  private String osName;
  private String osVersion;
  private String cpu;

  private long totalMemory = 0;
  private long freeMemory = 0;
  private long totalMemoryKB = 0;
  private long freeMemoryKB = 0;

  public SystemInfo() {

  }

  public String getJavaVmName() {
    return javaVmName;
  }

  public void setJavaVmName(String javaVmName) {
    this.javaVmName = javaVmName;
  }

  public String getJavaVmVendor() {
    return javaVmVendor;
  }

  public void setJavaVmVendor(String javaVmVendor) {
    this.javaVmVendor = javaVmVendor;
  }

  public String getJavaVmVersion() {
    return javaVmVersion;
  }

  public void setJavaVmVersion(String javaVmVersion) {
    this.javaVmVersion = javaVmVersion;
  }

  public String getJavaRuntimeName() {
    return javaRuntimeName;
  }

  public void setJavaRuntimeName(String javaRuntimeName) {
    this.javaRuntimeName = javaRuntimeName;
  }

  public String getJavaRuntimeVersion() {
    return javaRuntimeVersion;
  }

  public void setJavaRuntimeVersion(String javaRuntimeVersion) {
    this.javaRuntimeVersion = javaRuntimeVersion;
  }

  public String getOsName() {
    return osName;
  }

  public void setOsName(String osName) {
    this.osName = osName;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public long getTotalMemory() {
    return totalMemory;
  }

  public void setTotalMemory(long totalMemory) {
    this.totalMemory = totalMemory;
  }

  public long getFreeMemory() {
    return freeMemory;
  }

  public void setFreeMemory(long freeMemory) {
    this.freeMemory = freeMemory;
  }

  public long getTotalMemoryKB() {
    return totalMemoryKB;
  }

  public void setTotalMemoryKB(long totalMemoryKB) {
    this.totalMemoryKB = totalMemoryKB;
  }

  public long getFreeMemoryKB() {
    return freeMemoryKB;
  }

  public void setFreeMemoryKB(long freeMemoryKB) {
    this.freeMemoryKB = freeMemoryKB;
  }
}
