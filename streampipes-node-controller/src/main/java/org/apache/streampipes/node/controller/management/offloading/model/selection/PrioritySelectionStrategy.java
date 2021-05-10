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

package org.apache.streampipes.node.controller.management.offloading.model.selection;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.controller.management.node.NodeManager;
import org.apache.streampipes.node.controller.management.offloading.OffloadingPolicyManager;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PrioritySelectionStrategy implements SelectionStrategy{

    @Override
    public InvocableStreamPipesEntity selectEntity() {
        List<InvocableStreamPipesEntity> instances = RunningInvocableInstances.INSTANCE.getAll();
        //List all other nodes that are online
        List<NodeInfoDescription> onlineNodes = getNodeInfos().stream().filter(desc ->
                !desc.getNodeControllerId().equals(NodeManager.getInstance().retrieveNodeInfoDescription().getNodeControllerId()))
                .collect(Collectors.toList());

        List<InvocableStreamPipesEntity> candidateEntities =
                instances.stream()
                        .filter(InvocableStreamPipesEntity::isPreemption)
                        .filter(entity -> checkIfSupported(entity, onlineNodes)).collect(Collectors.toList());
        if(candidateEntities.isEmpty()) return null;
        return lowestPriority(candidateEntities);
    }

    private List<NodeInfoDescription> getNodeInfos(){
        return OffloadingPolicyManager.getInstance().getOnlineNodes();
    }

    private InvocableStreamPipesEntity lowestPriority(List<InvocableStreamPipesEntity> entities){
        List<InvocableStreamPipesEntity> sortedEntities = entities.stream()
                .sorted(Comparator.comparingInt(InvocableStreamPipesEntity::getPriorityScore))
                .collect(Collectors.toList());
        return sortedEntities.get(0);
    }

    private boolean checkIfSupported(InvocableStreamPipesEntity entity, List<NodeInfoDescription> nodeInfos){
        List<NodeInfoDescription> candidateNodes = new ArrayList<>();
        for(NodeInfoDescription nodeInfo : nodeInfos){
            if (nodeInfo.getSupportedElements().contains(entity.getAppId()))
                candidateNodes.add(nodeInfo);
        }
        return !candidateNodes.isEmpty();
    }
}
