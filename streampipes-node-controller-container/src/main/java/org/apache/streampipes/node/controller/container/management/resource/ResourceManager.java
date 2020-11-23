package org.apache.streampipes.node.controller.container.management.resource;/*
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

import com.google.gson.Gson;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;
import oshi.util.Util;

import oshi.hardware.CentralProcessor;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class ResourceManager {

    private static final Logger LOG =
            LoggerFactory.getLogger(ResourceManager.class.getCanonicalName());

    // OSHI to retreive system information
    private SystemInfo si = new SystemInfo();
    private HardwareAbstractionLayer hal = si.getHardware();
    private OperatingSystem os = si.getOperatingSystem();

    private Calendar cal = Calendar.getInstance();

    private Map<String, Object> nodeResources = new HashMap<>();

    private static ResourceManager instance = null;

    private ResourceManager() {}

    public static ResourceManager getInstance() {
        if (instance == null) {
            synchronized (ResourceManager.class) {
                if (instance == null)
                    instance = new ResourceManager();
            }
        }
        return instance;
    }

    public void run() {
        new Thread(getCurrentResources, "rm").start();
    }

    private final Runnable getCurrentResources = () -> {

        while(true) {
            try {
                // get current node resource metrics
                Thread.sleep( NodeControllerConfig.INSTANCE.getNodeResourceUpdateFreqSecs() * 1000);

                cal.setTimeInMillis(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

                /**
                 * Monitors the node resources
                 */
                String booted = getBooted(this.os);
                String uptime = getUptime(this.os);
                float cpuLoad = getCpuLoad(this.hal.getProcessor());
                double cpuTemperature = getCpuTemperature(this.hal.getSensors());
                long totalMemory = this.hal.getMemory().getTotal();
                long freeMemory = getAvailableMemory(this.hal.getMemory());
                long usedMemory = this.hal.getMemory().getTotal() - freeMemory;
                Map<String, Map<String, Long>> diskUsage = getDiskUsage(this.os.getFileSystem());

                nodeResources.put("systemTime", dateFormat.format(cal.getTime()));
                nodeResources.put("booted", booted);
                nodeResources.put("uptime", uptime);
                nodeResources.put("cpuLoad", String.format("%.1f%%", cpuLoad));
                nodeResources.put("cpuLoadInPercent", cpuLoad);
                nodeResources.put("cpuTemperature", String.format("%.2f°C", cpuTemperature));
                nodeResources.put("cpuTemperatureCelcius", cpuTemperature);
                nodeResources.put("freeMemoryInBytes", freeMemory);
                nodeResources.put("usedMemoryInBytes", usedMemory);
                nodeResources.put("totalMemoryInBytes", totalMemory);

                for (Map.Entry<String, Map<String, Long>> k : diskUsage.entrySet()) {
                    nodeResources.put("freeDiskSpaceInBytes", k.getValue().get("usableDiskSpace"));
                }

            } catch (InterruptedException e) {
                LOG.error("Thread interrupted. {}", e.toString());
            }
        }
    };

    public String retrieveNodeResources() {
        LOG.debug("Retrieve node resource");
        return new Gson().toJson(this.nodeResources);
    }

    private double getCpuTemperature(Sensors s) {
        return s.getCpuTemperature();
    }

    private Map<String, Map<String,Long>>  getDiskUsage(FileSystem fs) {
        List<OSFileStore> fsArray = fs.getFileStores();
        Map<String, Map<String, Long>> m = new HashMap<>();
        for(OSFileStore f : fsArray) {
            Map<String, Long> i = new HashMap<>();
            // has SATA disk
            if (f.getVolume().contains("/dev/sda")){
                i.put("usableDiskSpace", f.getUsableSpace());
                i.put("totalDiskSpace", f.getTotalSpace());
                m.put(f.getVolume(), i);
            }
            else if (f.getVolume().contains("/dev/nvme")){
                i.put("usableDiskSpace", f.getUsableSpace());
                i.put("totalDiskSpace", f.getTotalSpace());
                m.put(f.getVolume(), i);
            }
            else if (f.getVolume().contains("/dev/disk")){
                i.put("usableDiskSpace", f.getUsableSpace());
                i.put("totalDiskSpace", f.getTotalSpace());
                m.put(f.getVolume(), i);
            }
            // Docker in RPi
            else if (f.getVolume().contains("/dev/root")){
                i.put("usableDiskSpace", f.getUsableSpace());
                i.put("totalDiskSpace", f.getTotalSpace());
                m.put(f.getVolume(), i);
            }
            // Docker in Jetson Nano
            else if (f.getVolume().contains("/dev/mmcblk0p1")){
                i.put("usableDiskSpace", f.getUsableSpace());
                i.put("totalDiskSpace", f.getTotalSpace());
                m.put(f.getVolume(), i);
            }
//            // has SATA disk
//            if (f.getVolume().contains("/dev/sda") && ( f.getMount().equals("/") || f.getMount().equals("/home"))){
//                i.put("usableDiskSpace", f.getUsableSpace());
//                i.put("totalDiskSpace", f.getTotalSpace());
//                m.put(f.getVolume(), i);
//            }
//            // has overlay disk (container setup)
//            else if (f.getVolume().equals("overlay") && ( f.getMount().equals("/") || f.getMount().equals("/home"))){
//                    i.put("usableDiskSpace", f.getUsableSpace());
//                    i.put("totalDiskSpace", f.getTotalSpace());
//                    m.put(f.getVolume(), i);
//            }
//            // has NVME disk
//            else if(f.getVolume().contains("/dev/nvme") && ( f.getMount().equals("/") || f.getMount().equals("/home"))) {
//                i.put("usableDiskSpace", f.getUsableSpace());
//                i.put("totalDiskSpace", f.getTotalSpace());
//                m.put(f.getVolume(), i);
//            }
//            // disk on macOS
//            else if (f.getVolume().contains("/dev/disk") && f.getMount().equals("/")) {
//                i.put("usableDiskSpace", f.getUsableSpace());
//                i.put("totalDiskSpace", f.getTotalSpace());
//                m.put(f.getVolume(), i);
//            }
        }
        if (m.isEmpty()) {
            Map<String, Long> i = new HashMap<>();
            i.put("usableDiskSpace", 0L);
            i.put("totalDiskSpace", 0L);
            m.put("n/a", i);
        }
        return m;
    }

    private long getAvailableMemory(GlobalMemory m) {
        return m.getAvailable();
    }

    private float getCpuLoad(CentralProcessor c) {
        long[] prevTicks = c.getSystemCpuLoadTicks();
        // need to wait a second
        Util.sleep(1000);
        return (float) (c.getSystemCpuLoadBetweenTicks(prevTicks) * 100);
    }

    private String getUptime(OperatingSystem os) {
        return String.valueOf(FormatUtil.formatElapsedSecs(os.getSystemUptime()));
    }

    private String getBooted(OperatingSystem os) {
        return String.valueOf(Instant.ofEpochSecond(os.getSystemBootTime()));
    }
}
