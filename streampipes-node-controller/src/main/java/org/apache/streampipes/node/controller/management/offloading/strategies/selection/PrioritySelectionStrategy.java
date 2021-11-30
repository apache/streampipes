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

package org.apache.streampipes.node.controller.management.offloading.strategies.selection;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PrioritySelectionStrategy implements SelectionStrategy{

    @Override
    public InvocableStreamPipesEntity select(List<InvocableStreamPipesEntity> blacklistInvocables) {
        // Sinks cannot be migrated
        List<InvocableStreamPipesEntity> candidateInvocables = RunningInvocableInstances.INSTANCE.getAll().stream()
                .filter(e -> e instanceof DataProcessorInvocation)
                .filter(entity -> isNotBlacklisted(entity, blacklistInvocables))
                .filter(InvocableStreamPipesEntity::isPreemption)
                .collect(Collectors.toList());
        if(candidateInvocables.size() == 0)
            return null;
        return selectWithLowestPrioScore(candidateInvocables);
    }

    private InvocableStreamPipesEntity selectWithLowestPrioScore(List<InvocableStreamPipesEntity> candidateInvocables){
        // sort pipeline element priorityScores ascending and return element with lowest score
        return candidateInvocables.stream()
                .sorted(Comparator.comparingInt(InvocableStreamPipesEntity::getPriorityScore))
                .collect(Collectors.toList()).get(0);
    }

    private boolean isNotBlacklisted(InvocableStreamPipesEntity entity,
                                     List<InvocableStreamPipesEntity> blacklistInvocables){
        return blacklistInvocables.stream()
                .noneMatch(blacklistEntity -> blacklistEntity
                        .getDeploymentRunningInstanceId().equals(entity.getDeploymentRunningInstanceId()));
    }
}
