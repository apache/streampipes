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
import org.apache.streampipes.node.controller.management.offloading.strategies.selection.SelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OffloadingStrategyFactory {

    private static final int HISTORY_QUEUE_SIZE_FACTOR = 1;

    public List<OffloadingStrategy<?>> select(){

        switch (NodeConfiguration.getAutoOffloadingStrategy()) {
            case CPU:
                return Collections.singletonList(getDefaultCPUOffloadingPolicy());
            case MEM:
                return Collections.singletonList(getDefaultMemoryOffloadingPolicy());
            case DISK:
                return Collections.singletonList(getDefaultDiskSpaceOffloadingPolicy());
            case DEBUG:
                return Collections.singletonList(getDebugOffloadingPolicy());
            default:
                return getDefaultStrategy();
        }
    }

    private SelectionStrategy getAutoOffloadingSelectionStrategy() {
        switch (NodeConfiguration.getAutoOffloadingSelectionStrategy()) {
            case RANDOM:
                return new RandomSelectionStrategy();
            case PRIO:
                return new PrioritySelectionStrategy();
            default:
                return new RandomSelectionStrategy();
        }
    }

    private OffloadingStrategy<Float> getDebugOffloadingPolicy() {
        return new OffloadingStrategy<Float>(
                new ThresholdViolationOffloadingPolicy<>(
                        5,
                        Comparator.GREATER,
                        0.5f,
                        1),
                new CPULoadResourceProperty(),
                getAutoOffloadingSelectionStrategy());
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
                new ThresholdViolationOffloadingPolicy<>(
                        NodeConfiguration.getAutoOffloadingMaxNumViolations() + HISTORY_QUEUE_SIZE_FACTOR,
                        Comparator.GREATER,
                        NodeConfiguration.getAutoOffloadingThresholdInPercent(),
                        NodeConfiguration.getAutoOffloadingMaxNumViolations()),
                new CPULoadResourceProperty(),
                getAutoOffloadingSelectionStrategy());
    }

    private OffloadingStrategy<Long> getDefaultMemoryOffloadingPolicy(){
        long totalMemory = NodeManager.getInstance().getNodeInfoDescription()
                .getNodeResources().getHardwareResource().getMemory().getMemTotal();
        long threshold = (long) (totalMemory * 0.15);
        return new OffloadingStrategy<>(
                new ThresholdViolationOffloadingPolicy<>(
                        NodeConfiguration.getAutoOffloadingMaxNumViolations() + HISTORY_QUEUE_SIZE_FACTOR,
                        Comparator.SMALLER,
                        threshold,
                        NodeConfiguration.getAutoOffloadingMaxNumViolations()),
                new FreeMemoryResourceProperty(),
                getAutoOffloadingSelectionStrategy());
    }

    private OffloadingStrategy<Long> getDefaultDiskSpaceOffloadingPolicy(){
        long totalDisk = NodeManager.getInstance().getNodeInfoDescription()
                .getNodeResources().getHardwareResource().getDisk().getDiskTotal();
        long threshold = (long) (totalDisk * 0.1);
        return new OffloadingStrategy<>(
                new ThresholdViolationOffloadingPolicy<>(
                        NodeConfiguration.getAutoOffloadingMaxNumViolations() + HISTORY_QUEUE_SIZE_FACTOR,
                        Comparator.SMALLER,
                        threshold,
                        NodeConfiguration.getAutoOffloadingMaxNumViolations()),
                new FreeDiskSpaceResourceProperty(),
                getAutoOffloadingSelectionStrategy());
    }
}
