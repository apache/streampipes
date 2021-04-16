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
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.model.pipeline.Pipeline;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class MigrationPipelineGenerator {

    public static Pipeline generateMigrationPipeline(InvocableStreamPipesEntity entityToMigrate, Pipeline correspondingPipeline){

        List<NodeInfoDescription> possibleTargetNodes = new ArrayList<>();
        List<NodeInfoDescription> nodeInfo = StreamPipesClusterManager.getAllActiveAndHealthyNodes();
        nodeInfo.forEach(desc ->{
            if(desc.getSupportedElements().stream().anyMatch(element -> element.equals(entityToMigrate.getAppId()))
                && !desc.getNodeControllerId().equals(entityToMigrate.getDeploymentTargetNodeId()))
                possibleTargetNodes.add(desc);
        });

        if(possibleTargetNodes.isEmpty())
            return null;

        //Choose random node; should be adjusted to seek for a proper node to migrate to (e.g. based on user e.g.
        // selected labels, locality, free resources,...)
        NodeInfoDescription targetNode = possibleTargetNodes.get(new Random().nextInt(possibleTargetNodes.size()));

        entityToMigrate.setDeploymentTargetNodeHostname(targetNode.getHostname());
        entityToMigrate.setDeploymentTargetNodeId(targetNode.getNodeControllerId());
        entityToMigrate.setDeploymentTargetNodePort(targetNode.getPort());
        entityToMigrate.setElementEndpointHostname(targetNode.getHostname());
        entityToMigrate.setElementEndpointPort(targetNode.getPort());

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

}
