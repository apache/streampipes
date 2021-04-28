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
package org.apache.streampipes.sdk.builder;


import org.apache.streampipes.model.resource.Hardware;
import org.apache.streampipes.model.resource.NodeResourceRequirement;
import org.apache.streampipes.sdk.helpers.CollectedResourceRequirements;
import org.apache.streampipes.sdk.helpers.CollectedStreamRequirements;

import java.util.ArrayList;
import java.util.List;

public class ResourceRequirementsBuilder {

    private final List<NodeResourceRequirement> resourceRequirements;
    private final Hardware hardware;

    public static ResourceRequirementsBuilder create() {
        return new ResourceRequirementsBuilder();
    }

    public ResourceRequirementsBuilder() {
        this.resourceRequirements = new ArrayList<>();
        this.hardware = new Hardware();
    }

    public static CollectedResourceRequirements any() {
        return ResourceRequirementsBuilder.create().build();
    }

    @Deprecated
    public ResourceRequirementsBuilder requiredProperty(NodeResourceRequirement nrp) {
        this.resourceRequirements.add(nrp);
        return this;
    }

    public ResourceRequirementsBuilder requiredGpu(boolean gpu) {
        this.hardware.setGpu(gpu);
        return this;
    }

    public ResourceRequirementsBuilder requiredCores(int cores) {
        this.hardware.setCpuCores(cores);
        return this;
    }

    public ResourceRequirementsBuilder requiredMemory(String memoryString) {
        try {
            long mem = parse(memoryString);
            if (mem > 0) {
                this.hardware.setMemory(mem);
            } else {
                throw new IllegalArgumentException("Memory cannot be negative");
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot cast memory of value: " + memoryString);
        }
        return this;
    }

    public ResourceRequirementsBuilder requiredStorage(String storageString) {
        try {
            long disk = parse(storageString);
            if (disk > 0) {
                this.hardware.setDisk(disk);
            } else {
                throw new IllegalArgumentException("Storage cannot be negative");
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot cast storage of value: " + storageString);
        }
        return this;
    }

    public ResourceRequirementsBuilder requiredHardware(boolean gpu, int cores) {
        return requiredHardware(gpu, cores, null, null);
    }

    public ResourceRequirementsBuilder requiredHardware(boolean gpu, int cores, String memory) {
        return requiredHardware(gpu, cores, memory, null);
    }

    public ResourceRequirementsBuilder requiredHardware(boolean gpu, int cores, String memory,
                                                        String disk) {
        Hardware hw = new Hardware();
        hw.setGpu(gpu);
        if (cores > 0) {
            hw.setCpuCores(cores);
        } else {
            throw new IllegalArgumentException("Number of cores cannot be negative");
        }

        if (memory != null) {
            try {
                long mem = parse(memory);
                if (mem > 0) {
                    hw.setMemory(mem);
                } else {
                    throw new IllegalArgumentException("Memory cannot be negative");
                }
            } catch (Exception e) {
                throw new RuntimeException("Cannot cast memory of value: " + memory);
            }
        }

        if (disk != null) {
            try {
                long d = parse(disk);
                if (d > 0) {
                    hw.setDisk(d);
                } else {
                    throw new IllegalArgumentException("Disk cannot be negative");
                }
            } catch (Exception e) {
                throw new RuntimeException("Cannot cast disk of value: " + disk);
            }
        }

        this.resourceRequirements.add(hw);
        return this;
    }

    public CollectedResourceRequirements build() {
        this.resourceRequirements.add(this.hardware);
        return new CollectedResourceRequirements(this.resourceRequirements);
    }

    // Helpers

    private long parse(String text) {
        double d = Double.parseDouble(text.replaceAll("[GMK]B$", ""));
        long l = Math.round(d * 1024 * 1024 * 1024L);
        switch (text.charAt(Math.max(0, text.length() - 2))) {
            default:  l /= 1024;
            case 'K': l /= 1024;
            case 'M': l /= 1024;
            case 'G': return l;
        }
    }
}
