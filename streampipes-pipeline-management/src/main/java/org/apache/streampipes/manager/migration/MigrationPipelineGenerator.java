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
package org.apache.streampipes.manager.migration;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.streampipes.manager.node.StreamPipesClusterManager;
import org.apache.streampipes.manager.node.management.resources.ClusterResourceManager;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.resource.Hardware;
import org.apache.streampipes.model.resource.ResourceMetrics;
import org.apache.streampipes.node.management.NodeManagement;


import java.util.*;
import java.util.stream.Collectors;

public class MigrationPipelineGenerator {

    private InvocableStreamPipesEntity entityToMigrate;
    private Pipeline correspondingPipeline;

    public MigrationPipelineGenerator(InvocableStreamPipesEntity entityToMigrate, Pipeline correspondingPipeline){
        this.entityToMigrate = entityToMigrate;
        this.correspondingPipeline = correspondingPipeline;
    }


    public Pipeline generateMigrationPipeline(){

        List<NodeInfoDescription> possibleTargetNodes = getNodeInfos();

        switch(correspondingPipeline.getExecutionPolicy()){
            case "custom":
                possibleTargetNodes = filterLocationTags(possibleTargetNodes);
            case "locality-aware":
                //TODO: incorporate strategy for locality-aware deployment
            case "default":
                //TODO: incorporate strategy for default deployment
        }

        //Check current resource utilization on node
        possibleTargetNodes = filterResourceUtilization(possibleTargetNodes);


        //Different strategies possible (atm cancel offloading)
        if(possibleTargetNodes == null || possibleTargetNodes.isEmpty())
            return null;

        //Random Selection of new Node within the remaining possible nodes
        changeEntityDescriptionToMatchRandomNode(possibleTargetNodes);

        return generateTargetPipeline();
    }

    private List<NodeInfoDescription> getNodeInfos(){
        List<NodeInfoDescription> possibleTargetNodes = new ArrayList<>();
        List<NodeInfoDescription> nodeInfo = NodeManagement.getInstance().getOnlineNodes();
        nodeInfo.forEach(desc ->{
            if(desc.getSupportedElements().stream().anyMatch(element -> element.equals(entityToMigrate.getAppId()))
                    && !desc.getNodeControllerId().equals(entityToMigrate.getDeploymentTargetNodeId()))
                possibleTargetNodes.add(desc);
        });
        return possibleTargetNodes;
    }

    private List<NodeInfoDescription> filterLocationTags(List<NodeInfoDescription> possibleTargetNodes){
        return possibleTargetNodes.stream()
                .filter(desc -> nodeTagsContainElementTag(correspondingPipeline.getNodeTags(), desc))
                .collect(Collectors.toList());
    }

    private boolean nodeTagsContainElementTag(List<String> pipelineNodeTags,
                                              NodeInfoDescription desc){
        return desc.getStaticNodeMetadata().getLocationTags().stream().anyMatch(pipelineNodeTags::contains);
    }

    private List<NodeInfoDescription> filterResourceUtilization(List<NodeInfoDescription> possibleTargetNodes){
        //Currently only checking for free disk space and memory
        List<NodeInfoDescription> filteredTargetNodes = new ArrayList<>();
        for(NodeInfoDescription nodeInfo : possibleTargetNodes){
            Queue<ResourceMetrics> rmHistory = ClusterResourceManager.getResourceMetricsMap()
                    .get(nodeInfo.getNodeControllerId());
            if(rmHistory == null) return null;
            Hardware hardware = entityToMigrate.getResourceRequirements().stream()
                            .filter(nodeRR -> nodeRR instanceof Hardware).map(nodeRR -> (Hardware)nodeRR).findFirst().
                            orElse(null);
            if(hardware != null){
                if (rmHistory.peek() != null
                        && hardware.getDisk() <= rmHistory.peek().getFreeDiskSpaceInBytes()
                        && hardware.getMemory() <= rmHistory.peek().getFreeMemoryInBytes()) {
                    filteredTargetNodes.add(nodeInfo);
                }
            }
        }
        return filteredTargetNodes;
    }

    private Pipeline generateTargetPipeline(){
        Optional<DataProcessorInvocation> originalInvocation =
                correspondingPipeline.getSepas().stream().filter(dp ->
                        dp.getDeploymentRunningInstanceId().equals(entityToMigrate.getDeploymentRunningInstanceId()))
                        .findFirst();
        int index = correspondingPipeline.getSepas().indexOf(originalInvocation.get());

        Pipeline targetPipeline;
        try {
            targetPipeline = PipelineElementMigrationHandler.deepCopyPipeline(correspondingPipeline);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        targetPipeline.getSepas().remove(index);
        targetPipeline.getSepas().add(index, (DataProcessorInvocation) entityToMigrate);

        return targetPipeline;
    }

    private void changeEntityDescriptionToMatchRandomNode(List<NodeInfoDescription> nodes){
        NodeInfoDescription targetNode = nodes.get(new Random().nextInt(nodes.size()));

        entityToMigrate.setDeploymentTargetNodeHostname(targetNode.getHostname());
        entityToMigrate.setDeploymentTargetNodeId(targetNode.getNodeControllerId());
        entityToMigrate.setDeploymentTargetNodePort(targetNode.getPort());
        entityToMigrate.setElementEndpointHostname(targetNode.getHostname());
        entityToMigrate.setElementEndpointPort(targetNode.getPort());
    }
}
