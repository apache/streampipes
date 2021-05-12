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
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.offloading.OffloadingPolicyManager;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PrioritySelectionStrategy implements SelectionStrategy{

    @Override
    public InvocableStreamPipesEntity select() {
        List<InvocableStreamPipesEntity> runningPipelineElements = RunningInvocableInstances.INSTANCE.getAll();

        //List all other nodes that are online
        // TODO: this involves request to central backend
        List<NodeInfoDescription> onlineNodes = getNodeInfos().stream().filter(desc ->
                !desc.getNodeControllerId().equals(NodeConfiguration.getNodeControllerId()))
                .collect(Collectors.toList());

        List<InvocableStreamPipesEntity> candidatesForOffloading = runningPipelineElements.stream()
                        .filter(InvocableStreamPipesEntity::isPreemption)
                        .filter(entity -> verifyIfSupportedOnOtherNodes(entity, onlineNodes))
                        .collect(Collectors.toList());

        if (candidatesForOffloading.isEmpty()) {
            // TODO what to do when null?
            return null;
        } else {
            return selectPipelineElementWithLowestPriorityScore(candidatesForOffloading);
        }
    }

    private List<NodeInfoDescription> getNodeInfos(){
        return OffloadingPolicyManager.getInstance().getOnlineNodes();
    }

    private InvocableStreamPipesEntity selectPipelineElementWithLowestPriorityScore(List<InvocableStreamPipesEntity> entities){
        // sort pipeline element priorityScores ascending
        List<InvocableStreamPipesEntity> sortedPipelineElements = entities.stream()
                .sorted(Comparator.comparingInt(InvocableStreamPipesEntity::getPriorityScore))
                .collect(Collectors.toList());

        // return first element with lowest score
        return sortedPipelineElements.get(0);
    }

    private boolean verifyIfSupportedOnOtherNodes(InvocableStreamPipesEntity entity, List<NodeInfoDescription> nodeInfos){
        List<NodeInfoDescription> candidateNodes = new ArrayList<>();
        for(NodeInfoDescription nodeInfo : nodeInfos){
            if (nodeInfo.getSupportedElements().contains(entity.getAppId()))
                candidateNodes.add(nodeInfo);
        }
        return !candidateNodes.isEmpty();
    }
}
