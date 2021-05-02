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

package org.apache.streampipes.node.controller.management.offloading;

import org.apache.streampipes.node.controller.management.offloading.model.OffloadingStrategy;
import org.apache.streampipes.node.controller.management.pe.InvocableElementManager;
import org.apache.streampipes.node.controller.management.resource.model.ResourceMetrics;

import java.util.ArrayList;
import java.util.List;

public class OffloadingPolicyManager {

    private final List<OffloadingStrategy<?>> offloadingStrategies = new ArrayList<>();
    private static OffloadingPolicyManager instance;

    public static OffloadingPolicyManager getInstance(){
        if(instance == null){
            instance = new OffloadingPolicyManager();
        }
        return instance;
    }

    public void checkPolicies(ResourceMetrics rm){
        for(OffloadingStrategy strategy:offloadingStrategies){
            strategy.getOffloadingPolicy().addValue(strategy.getResourceProperty().getProperty(rm));
            if(strategy.getOffloadingPolicy().isViolated()){
                InvocableElementManager.getInstance().postOffloadRequest(strategy.getSelectionStrategy().selectEntity());
            }
        }
    }

    public void addOffloadingStrategy(OffloadingStrategy<?> offloadingStrategy){
        this.offloadingStrategies.add(offloadingStrategy);
    }

}
