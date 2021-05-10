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

import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.resource.Hardware;

import java.util.Optional;

public class ResourceChecker {
    private final NodeInfoDescription nodeInfo;

    public ResourceChecker(NodeInfoDescription nodeInfo){
        this.nodeInfo = nodeInfo;
    }

    public boolean checkResources(ConsumableStreamPipesEntity entity){
        return (checkCpuRequirement(entity) && checkGpuRequirement(entity)
                && checkMemoryRequirement(entity) && checkDiskSpaceRequirement(entity));
    }

    private boolean checkGpuRequirement(ConsumableStreamPipesEntity spEntity){
        Optional<Hardware> hw = extractHardware(spEntity);
        boolean entityRequiresGpu = false;
        if(hw.isPresent())
            entityRequiresGpu = hw.get().isGpu();
        boolean nodeHasGPU = (nodeInfo.getNodeResources().getHardwareResource().getGpu().getCores()>0);
        return (!entityRequiresGpu || nodeHasGPU);
    }

    private boolean checkCpuRequirement(ConsumableStreamPipesEntity spEntity){
        Optional<Hardware> hw = extractHardware(spEntity);
        int requiredCPUCores = 0;
        if(hw.isPresent())
            requiredCPUCores = hw.get().getCpuCores();
        int nodeCPUCores = nodeInfo.getNodeResources().getHardwareResource().getCpu().getCores();
        return (requiredCPUCores <= nodeCPUCores);
    }

    private boolean checkMemoryRequirement(ConsumableStreamPipesEntity spEntity){
        Optional<Hardware> hw = extractHardware(spEntity);
        long requiredMemory = 0L;
        if(hw.isPresent())
            requiredMemory = hw.get().getMemory();
        //Looks at total memory (could be adjusted to consider currently available Memory)
        long actualNodeMemory = nodeInfo.getNodeResources().getHardwareResource().getMemory().getMemTotal();
        return (requiredMemory <= actualNodeMemory);
    }

    private boolean checkDiskSpaceRequirement(ConsumableStreamPipesEntity spEntity){
        Optional<Hardware> hw = extractHardware(spEntity);
        long requiredDiskSpace = 0L;
        if(hw.isPresent())
            requiredDiskSpace = hw.get().getDisk();
        //Looks at total diskspace (could be adjusted to consider currently available diskspace)
        long actualNodeDiskSpace = nodeInfo.getNodeResources().getHardwareResource().getDisk().getDiskTotal();
        return (requiredDiskSpace <= actualNodeDiskSpace);
    }

    private Optional<Hardware> extractHardware(ConsumableStreamPipesEntity spEntity){
        return spEntity.getResourceRequirements().stream()
                .filter(req -> req instanceof Hardware).map(req -> (Hardware) req).findFirst();
    }

}
