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

package org.apache.streampipes.node.controller.management.offloading.model;

import org.apache.streampipes.node.controller.management.offloading.model.policies.Comparator;
import org.apache.streampipes.node.controller.management.offloading.model.policies.OffloadingPolicy;
import org.apache.streampipes.node.controller.management.offloading.model.policies.ThresholdViolationOffloadingPolicy;
import org.apache.streampipes.node.controller.management.offloading.model.property.CPULoadResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.model.property.FreeDiskSpaceResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.model.property.FreeMemoryResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.model.selection.CPULoadSelectionStrategy;
import org.apache.streampipes.node.controller.management.offloading.model.selection.RandomSelectionStrategy;
import org.apache.streampipes.node.controller.management.offloading.model.selection.SelectionStrategy;

public class OffloadingStrategyFactory {

    public OffloadingStrategy getFromEnv(){
        if(System.getenv("SP_AUTO_OFFLOADING_POLICY") == null || System.getenv("SP_AUTO_OFFLOADING_POLICY").isEmpty()){
            return new OffloadingStrategy<Float>(new ThresholdViolationOffloadingPolicy<>(5, Comparator.GREATER,90f, 4),
                    new CPULoadResourceProperty(), new RandomSelectionStrategy());
        }else{
            switch (System.getenv("SP_AUTO_OFFLOADING_POLICY")){
                case "CPU":
                    return new OffloadingStrategy<Float>(new ThresholdViolationOffloadingPolicy<>(5, Comparator.GREATER,90f, 4),
                            new CPULoadResourceProperty(), new RandomSelectionStrategy());
                case "debug":
                    return new OffloadingStrategy<Float>(new ThresholdViolationOffloadingPolicy<>(5,
                            Comparator.GREATER,0.5f, 3),
                            new CPULoadResourceProperty(), new RandomSelectionStrategy());
                case "memory":
                    return new OffloadingStrategy<Long>(new ThresholdViolationOffloadingPolicy<>(5, Comparator.SMALLER,
                            549755813888l, 5),
                            new FreeMemoryResourceProperty(), new RandomSelectionStrategy());
                case "disk space":
                    return new OffloadingStrategy<Long>(new ThresholdViolationOffloadingPolicy<>(5, Comparator.SMALLER,
                            549755813888l, 5),
                            new FreeDiskSpaceResourceProperty(), new RandomSelectionStrategy());
            }
        }
        return null;
    }

}
