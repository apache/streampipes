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

package org.apache.streampipes.node.controller.management.pe.validator;

import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.node.resources.hardware.HardwareResource;
import org.apache.streampipes.model.resource.Hardware;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequirementsResourceValidator implements Validator {

    private final NodeInfoDescription node;
    private final List<ConsumableStreamPipesEntity> registeredPipelineElements;

    public RequirementsResourceValidator(NodeInfoDescription node, List<ConsumableStreamPipesEntity> registeredPipelineElements) {
        this.node = node;
        this.registeredPipelineElements = registeredPipelineElements;
    }

    @Override
    public List<ConsumableStreamPipesEntity> validate() {

        return registeredPipelineElements.stream()
                .filter(this::validateCpuRequirement)
                .filter(this::validateMemoryRequirement)
                .filter(this::validateDiskRequirement)
                .filter(this::validateGpuRequirement)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateGpuRequirement(ConsumableStreamPipesEntity entity) {
        Optional<Hardware> hardwareRequirements = extractHardwareRequirements(entity);
        HardwareResource nodeHardwareResource = getNodeHardwareResource();
        boolean entityRequiresGpu = false;
        if(hardwareRequirements.isPresent())
            entityRequiresGpu = hardwareRequirements.get().isGpu();
        boolean nodeHasGPU = (nodeHardwareResource.getGpu().getCores()>0);
        return (!entityRequiresGpu || nodeHasGPU);
    }

    @Override
    public boolean validateDiskRequirement(ConsumableStreamPipesEntity entity) {
        Optional<Hardware> hardwareRequirements = extractHardwareRequirements(entity);
        HardwareResource nodeHardwareResource = getNodeHardwareResource();
        long requiredDiskSpace = 0L;
        if(hardwareRequirements.isPresent())
            requiredDiskSpace = hardwareRequirements.get().getDisk();
        //Looks at total diskspace (could be adjusted to consider currently available diskspace)
        long actualNodeDiskSpace = nodeHardwareResource.getDisk().getDiskTotal();
        return (requiredDiskSpace <= actualNodeDiskSpace);
    }

    @Override
    public boolean validateCpuRequirement(ConsumableStreamPipesEntity entity) {
        Optional<Hardware> hardwareRequirements = extractHardwareRequirements(entity);
        HardwareResource nodeHardwareResource = getNodeHardwareResource();
        int requiredCPUCores = 0;
        if(hardwareRequirements.isPresent())
            requiredCPUCores = hardwareRequirements.get().getCpuCores();
        int nodeCPUCores = nodeHardwareResource.getCpu().getCores();
        return (requiredCPUCores <= nodeCPUCores);
    }

    @Override
    public boolean validateMemoryRequirement(ConsumableStreamPipesEntity entity) {
        Optional<Hardware> hardwareRequirements = extractHardwareRequirements(entity);
        HardwareResource nodeHardwareResource = getNodeHardwareResource();
        long requiredMemory = 0L;
        if(hardwareRequirements.isPresent())
            requiredMemory = hardwareRequirements.get().getMemory();
        //Looks at total memory (could be adjusted to consider currently available Memory)
        long actualNodeMemory = nodeHardwareResource.getMemory().getMemTotal();
        return (requiredMemory <= actualNodeMemory);
    }

    // Helpers

    private Optional<Hardware> extractHardwareRequirements(ConsumableStreamPipesEntity entity){
        return entity.getResourceRequirements().stream()
                .filter(req -> req instanceof Hardware)
                .map(req -> (Hardware) req)
                .findFirst();
    }

    private HardwareResource getNodeHardwareResource() {
        return node.getNodeResources().getHardwareResource();
    }

}
