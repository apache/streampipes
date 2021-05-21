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

package org.apache.streampipes.node.controller.management.offloading.strategies;

import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.node.NodeManager;
import org.apache.streampipes.node.controller.management.offloading.strategies.policies.Comparator;
import org.apache.streampipes.node.controller.management.offloading.strategies.policies.ThresholdViolationOffloadingPolicy;
import org.apache.streampipes.node.controller.management.offloading.strategies.property.CPULoadResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.strategies.property.FreeDiskSpaceResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.strategies.property.FreeMemoryResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.strategies.selection.PrioritySelectionStrategy;
import org.apache.streampipes.node.controller.management.offloading.strategies.selection.RandomSelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OffloadingStrategyFactory {

    public List<OffloadingStrategy<?>> select(){

        switch (NodeConfiguration.getAutoOffloadingStrategy()) {
            case CPU:
                return Collections.singletonList(getDefaultCPUOffloadingPolicy());
            case MEM:
                return Collections.singletonList(getDefaultMemoryOffloadingPolicy());
            case DISK:
                return Collections.singletonList(getDefaultDiskSpaceOffloadingPolicy());
            case DEBUG:
                return Collections.singletonList(new OffloadingStrategy<Float>(
                        new ThresholdViolationOffloadingPolicy<>(5,
                                Comparator.GREATER, 0.5f, 1),
                        new CPULoadResourceProperty(), new PrioritySelectionStrategy()));
            default:
                return getDefaultStrategy();
        }
    }

    private List<OffloadingStrategy<?>> getDefaultStrategy(){
        List<OffloadingStrategy<?>> offloadingStrategies = new ArrayList<>();
        offloadingStrategies.add(getDefaultCPUOffloadingPolicy());
        offloadingStrategies.add(getDefaultMemoryOffloadingPolicy());
        offloadingStrategies.add(getDefaultDiskSpaceOffloadingPolicy());
        return offloadingStrategies;
    }

    private OffloadingStrategy<Float> getDefaultCPUOffloadingPolicy(){
        return new OffloadingStrategy<>(
                new ThresholdViolationOffloadingPolicy<>(5, Comparator.GREATER, 90f, 4),
                new CPULoadResourceProperty(), new RandomSelectionStrategy());
    }

    private OffloadingStrategy<Long> getDefaultMemoryOffloadingPolicy(){
        long totalMemory = NodeManager.getInstance().getNodeInfoDescription()
                .getNodeResources().getHardwareResource().getMemory().getMemTotal();
        long threshold = (long) (totalMemory * 0.15);
        return new OffloadingStrategy<>(
                new ThresholdViolationOffloadingPolicy<>(5, Comparator.SMALLER,
                        threshold, 4),
                new FreeMemoryResourceProperty(), new RandomSelectionStrategy());
    }

    private OffloadingStrategy<Long> getDefaultDiskSpaceOffloadingPolicy(){
        long totalDisk = NodeManager.getInstance().getNodeInfoDescription()
                .getNodeResources().getHardwareResource().getDisk().getDiskTotal();
        long threshold = (long) (totalDisk * 0.1);
        return new OffloadingStrategy<>(
                new ThresholdViolationOffloadingPolicy<>(5, Comparator.SMALLER,
                        threshold, 4),
                new FreeDiskSpaceResourceProperty(), new RandomSelectionStrategy());
    }

}
