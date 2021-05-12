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
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.node.monitor.ResourceMetrics;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.resource.Hardware;
import org.apache.streampipes.node.management.NodeManagement;
import org.apache.streampipes.node.management.operation.monitor.resource.ClusterResourceMonitor;


import java.util.*;
import java.util.stream.Collectors;

public class MigrationPipelineGenerator {

    private static final float MEM_MULTIPLICATION_FACTOR = 0.9F;
    private static final float DISK_MULTIPLICATION_FACTOR = 0.9F;

    private final InvocableStreamPipesEntity entityToMigrate;
    private final Pipeline correspondingPipeline;

    public MigrationPipelineGenerator(InvocableStreamPipesEntity entityToMigrate, Pipeline correspondingPipeline){
        this.entityToMigrate = entityToMigrate;
        this.correspondingPipeline = correspondingPipeline;
    }

    public Pipeline generateMigrationPipeline(){

        List<NodeInfoDescription> eligibleTargetNodes = findEligibleTargetNodes();

        switch(correspondingPipeline.getExecutionPolicy()){
            case "custom": //TODO: Enum class
                eligibleTargetNodes = filterLocationTags(eligibleTargetNodes);
            case "locality-aware":
                //TODO: incorporate strategy for locality-aware deployment
            case "default":
                //TODO: incorporate strategy for default deployment
        }

        //Check current resource utilization on node
        eligibleTargetNodes = filterResourceUtilization(eligibleTargetNodes);

        //Different strategies possible (atm cancel offloading)
        if(eligibleTargetNodes.isEmpty())
            return null;

        //Random Selection of new node within the remaining eligible nodes
        randomSelectionAndUpdate(eligibleTargetNodes);

        return generateTargetPipeline();
    }

    private List<NodeInfoDescription> findEligibleTargetNodes(){

        List<NodeInfoDescription> onlineNodes = NodeManagement.getInstance().getOnlineNodes();

        return onlineNodes.stream()
                .filter(this::matchAndVerify)
                .collect(Collectors.toList());
    }

    private boolean matchAndVerify(NodeInfoDescription node) {
        boolean matchingAppIds = node.getSupportedElements().stream()
                .anyMatch(pipelineElement -> pipelineElement.equals(entityToMigrate.getAppId()));

        boolean dissimilarNodes = !node.getNodeControllerId().equals(entityToMigrate.getDeploymentTargetNodeId());

        return matchingAppIds && dissimilarNodes;
    }

    private List<NodeInfoDescription> filterLocationTags(List<NodeInfoDescription> possibleTargetNodes){
        return possibleTargetNodes.stream()
                .filter(desc -> nodeTagsContainElementTag(correspondingPipeline.getNodeTags(), desc))
                .collect(Collectors.toList());
    }

    private boolean nodeTagsContainElementTag(List<String> pipelineNodeTags,
                                              NodeInfoDescription node){
        return node.getStaticNodeMetadata().getLocationTags().stream().anyMatch(pipelineNodeTags::contains);
    }

    private List<NodeInfoDescription> filterResourceUtilization(List<NodeInfoDescription> possibleTargetNodes){
        //Currently only checking for free disk space and memory
        List<NodeInfoDescription> filteredTargetNodes = new ArrayList<>();

        for(NodeInfoDescription nodeInfo : possibleTargetNodes){

            String nodeControllerId = nodeInfo.getNodeControllerId();
            Queue<ResourceMetrics> rmHistory = ClusterResourceMonitor.getNodeResourceMetricsById(nodeControllerId);

            if(rmHistory == null){
                //If no RessourceMetrics history is available (e.g. shorly after Backend start), consider node as
                // possible target node
                filteredTargetNodes.add(nodeInfo);
                continue;
            }

            Hardware hardware = entityToMigrate.getResourceRequirements().stream()
                    .filter(nodeRR -> nodeRR instanceof Hardware)
                    .map(nodeRR -> (Hardware)nodeRR)
                    .findFirst()
                    .orElse(null);

            if(hardware != null){ //TODO: Map CPU load ()
                //Does produce empty list if no hardware requirements are defined
                if (rmHistory.peek() != null
                        && hardware.getDisk() <= DISK_MULTIPLICATION_FACTOR * rmHistory.peek().getFreeDiskSpaceInBytes()
                        && hardware.getMemory() <= MEM_MULTIPLICATION_FACTOR * rmHistory.peek().getFreeMemoryInBytes()) {
                    filteredTargetNodes.add(nodeInfo);
                }
            } else{
                filteredTargetNodes.add(nodeInfo);
            }
        }
        return filteredTargetNodes;
    }

    private Pipeline generateTargetPipeline(){
        Optional<DataProcessorInvocation> originalInvocation = correspondingPipeline.getSepas().stream()
                .filter(dp -> dp.getDeploymentRunningInstanceId().equals(entityToMigrate.getDeploymentRunningInstanceId()))
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

    private void randomSelectionAndUpdate(List<NodeInfoDescription> nodes){
        NodeInfoDescription targetNode = nodes.get(new Random().nextInt(nodes.size()));

        entityToMigrate.setDeploymentTargetNodeHostname(targetNode.getHostname());
        entityToMigrate.setDeploymentTargetNodeId(targetNode.getNodeControllerId());
        entityToMigrate.setDeploymentTargetNodePort(targetNode.getPort());
        entityToMigrate.setElementEndpointHostname(targetNode.getHostname());
        entityToMigrate.setElementEndpointPort(targetNode.getPort());
    }
}
