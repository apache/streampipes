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
package org.apache.streampipes.manager.info;

import org.apache.streampipes.model.client.version.SystemInfo;

public class SystemInfoProvider {

  public SystemInfo getSystemInfo() {
    SystemInfo systemInfo = new SystemInfo();

    systemInfo.setJavaVmName(getProperty("java.vm.name"));
    systemInfo.setJavaVmVendor(getProperty("java.vm.vendor"));
    systemInfo.setJavaVmVersion(getProperty("java.vm.version"));
    systemInfo.setJavaRuntimeName(getProperty("java.runtime.name"));
    systemInfo.setJavaRuntimeVersion(getProperty("java.runtime.version"));
    systemInfo.setOsName(getProperty("os.name"));
    systemInfo.setOsVersion(getProperty("os.version"));
    systemInfo.setCpu(getProperty("sun.cpu.isalist"));

    Runtime runtime = Runtime.getRuntime();
    systemInfo.setTotalMemory(runtime.totalMemory());
    systemInfo.setFreeMemory(runtime.freeMemory());
    systemInfo.setTotalMemoryKB(runtime.totalMemory() / 1024);
    systemInfo.setFreeMemoryKB(runtime.freeMemory() / 1024);

    return systemInfo;
  }

  private String getProperty(String key) {
    String propValue = null;
    try {
      propValue = System.getProperty(key, "");
    } catch (Exception ex) {
      propValue = "unknown";
    }
    return propValue;
  }

}
