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
import org.apache.streampipes.node.controller.management.offloading.strategies.policies.Comparator;
import org.apache.streampipes.node.controller.management.offloading.strategies.policies.ThresholdViolationOffloadingPolicy;
import org.apache.streampipes.node.controller.management.offloading.strategies.property.CPULoadResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.strategies.property.FreeDiskSpaceResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.strategies.property.FreeMemoryResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.strategies.selection.PrioritySelectionStrategy;
import org.apache.streampipes.node.controller.management.offloading.strategies.selection.RandomSelectionStrategy;

public class OffloadingStrategyFactory {

    public OffloadingStrategy select(){

        switch (NodeConfiguration.getAutoOffloadingStrategy()) {
            case CPU:
                return new OffloadingStrategy<Float>(
                        new ThresholdViolationOffloadingPolicy<>(5, Comparator.GREATER, 90f, 5),
                        new CPULoadResourceProperty(), new RandomSelectionStrategy());
            case MEM:
                return new OffloadingStrategy<Long>(
                        new ThresholdViolationOffloadingPolicy<>(5, Comparator.SMALLER,
                                549755813888L, 5),
                        new FreeMemoryResourceProperty(), new RandomSelectionStrategy());
            case DISK:
                return new OffloadingStrategy<Long>(
                        new ThresholdViolationOffloadingPolicy<>(5, Comparator.SMALLER,
                                549755813888L, 5),
                        new FreeDiskSpaceResourceProperty(), new RandomSelectionStrategy());
            case DEBUG:
                return new OffloadingStrategy<Float>(
                        new ThresholdViolationOffloadingPolicy<>(5,
                                Comparator.GREATER, 0.5f, 1),
                        new CPULoadResourceProperty(), new PrioritySelectionStrategy());
            default:
                return new OffloadingStrategy<Float>(
                        new ThresholdViolationOffloadingPolicy<>(5, Comparator.GREATER, 90f, 4),
                        new CPULoadResourceProperty(), new RandomSelectionStrategy());
        }
    }

}
