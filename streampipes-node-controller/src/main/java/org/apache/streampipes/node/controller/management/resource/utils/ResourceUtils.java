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
package org.apache.streampipes.node.controller.management.resource.utils;

import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.node.NodeManager;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.Sensors;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceUtils {

    public static double getCpuTemperature(Sensors s) {
        return s.getCpuTemperature();
    }

    public static Map<String, Map<String,Long>> getDiskUsage(FileSystem fs) {
        List<OSFileStore> fsArray = fs.getFileStores();
        Map<String, Map<String, Long>> diskUsage = new HashMap<>();

        for(OSFileStore f : fsArray) {
            String volume = f.getVolume();
            String mount = f.getMount();

            if ("true".equals(System.getenv("SP_DEBUG"))) {
                // TODO: find better approach to retrieve host volume in debug setup
                findVolumeAndAdd(volume, diskUsage, f);
            } else {
                // check mount for node storage path (default: /var/lib/streampipes)
                if (mount.equals(NodeConfiguration.getNodeStoragePath())) {
                    findVolumeAndAdd(volume, diskUsage, f);
                }
            }

        }
        return diskUsage.isEmpty() ? defaultDiskUsage() : diskUsage;
    }

    private static void findVolumeAndAdd(String volume, Map<String, Map<String, Long>> diskUsage, OSFileStore f) {
        // has SATA disk
        if (volume.contains(FileSystemType.SDA.getName())){
            addDiskUsage(diskUsage, f);
            // Docker in Jetson Xavier with NVMe storage
        } else if (volume.contains(FileSystemType.NVME.getName())){
            addDiskUsage(diskUsage, f);
        } else if (volume.contains(FileSystemType.DISK.getName())){
            addDiskUsage(diskUsage, f);
        } else if (volume.contains(FileSystemType.ROOT.getName())){
            // Docker in RPi
            addDiskUsage(diskUsage, f);
        } else if (volume.contains(FileSystemType.MMCBLK.getName())){
            // Docker in Jetson Nano
            addDiskUsage(diskUsage, f);
        } else if (volume.contains(FileSystemType.SDB.getName())){
            addDiskUsage(diskUsage, f);
        } else if (volume.contains(FileSystemType.MAPPER.getName())) {
            // block devices
            addDiskUsage(diskUsage, f);
        }
    }

    public static void addDiskUsage(Map<String, Map<String, Long>> m, OSFileStore f) {
        Map<String, Long> i = new HashMap<>();
        i.put(DiskSpace.USABLE.getName(), f.getUsableSpace());
        i.put(DiskSpace.TOTAL.getName(), f.getTotalSpace());
        m.put(f.getVolume(), i);
    }

    public static Map<String, Map<String, Long>> defaultDiskUsage() {
        return new HashMap<String, Map<String, Long>>() {{
            put("n/a", new HashMap<String, Long>() {{
                put(DiskSpace.USABLE.getName(), 0L);
                put(DiskSpace.TOTAL.getName(), 0L);
            }});
        }};
    }

    public static long getAvailableMemory(GlobalMemory m) {
        return m.getAvailable();
    }

    public static float getCpuLoad(CentralProcessor c) {
        long[] prevTicks = c.getSystemCpuLoadTicks();
        // need to wait a second
        Util.sleep(1000);
        return (float) (c.getSystemCpuLoadBetweenTicks(prevTicks) * 100);
    }

    public static String getUptime(OperatingSystem os) {
        return String.valueOf(FormatUtil.formatElapsedSecs(os.getSystemUptime()));
    }

    public static String getBooted(OperatingSystem os) {
        return String.valueOf(Instant.ofEpochSecond(os.getSystemBootTime()));
    }
}
