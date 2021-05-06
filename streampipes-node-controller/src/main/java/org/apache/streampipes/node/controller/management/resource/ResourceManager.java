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
package org.apache.streampipes.node.controller.management.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.offloading.OffloadingPolicyManager;
import org.apache.streampipes.node.controller.management.offloading.model.OffloadingStrategyFactory;
import org.apache.streampipes.model.resource.ResourceMetrics;
import org.apache.streampipes.node.controller.management.resource.utils.DiskSpace;
import org.apache.streampipes.node.controller.management.resource.utils.ResourceUtils;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ResourceManager {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceManager.class.getCanonicalName());

    private static ResourceManager instance = null;
    // OSHI to retrieve system information
    private final SystemInfo si = new SystemInfo();
    private final HardwareAbstractionLayer hal = si.getHardware();
    private final OperatingSystem os = si.getOperatingSystem();
    private final Calendar cal = Calendar.getInstance();
    private final ResourceMetrics resourceMetrics = new ResourceMetrics();

    private ResourceManager() {
        //Offloading Policy
        OffloadingPolicyManager.getInstance().addOffloadingStrategy(new OffloadingStrategyFactory().getFromEnv());
    }

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
                Thread.sleep( NodeConfiguration.getResourceMonitorFreqSecs() * 1000);
                retrieveResources();
                checkOffloadingPolicy();
            } catch (InterruptedException e) {
                LOG.error("Thread interrupted. {}", e.toString());
            }
        }
    };

    private void checkOffloadingPolicy() {
        OffloadingPolicyManager.getInstance().checkPolicies(resourceMetrics);
    }

    private void retrieveResources() {
        cal.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

        // Monitor node resources
        resourceMetrics.setSystemTime(dateFormat.format(cal.getTime()));
        resourceMetrics.setBooted(ResourceUtils.getBooted(this.os));
        resourceMetrics.setUptime(ResourceUtils.getUptime(this.os));
        resourceMetrics.setCpuLoad(String.format("%.1f%%", ResourceUtils.getCpuLoad(this.hal.getProcessor())));
        resourceMetrics.setCpuLoadInPercent(ResourceUtils.getCpuLoad(this.hal.getProcessor()));
        resourceMetrics.setCpuTemperature(String.format("%.2f°C",
                ResourceUtils.getCpuTemperature(this.hal.getSensors())));
        resourceMetrics.setCpuTemperatureCelcius(ResourceUtils.getCpuTemperature(this.hal.getSensors()));
        resourceMetrics.setFreeMemoryInBytes(ResourceUtils.getAvailableMemory(this.hal.getMemory()));
        resourceMetrics.setUsedMemoryInBytes(this.hal.getMemory().getTotal() - ResourceUtils.getAvailableMemory(this.hal.getMemory()));
        resourceMetrics.setTotalMemoryInBytes(this.hal.getMemory().getTotal());

        ResourceUtils.getDiskUsage(this.os.getFileSystem()).forEach((key, value) -> {
            resourceMetrics.setFreeDiskSpaceInBytes(value.get(DiskSpace.USABLE.getName()));
        });
    }

    public String retrieveNodeResources() {
        try {
            return JacksonSerializer
                    .getObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(resourceMetrics);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException("Could not serialize node resources " + e.getMessage(), e);
        }
    }
}
