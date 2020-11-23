package org.apache.streampipes.node.controller.container.management.orchestrator.docker;/*
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

import org.apache.streampipes.model.node.PipelineElementDockerContainer;
import org.apache.streampipes.model.node.PipelineElementDockerContainerBuilder;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum DockerNodeContainer {
    INSTANCE;

    public List<PipelineElementDockerContainer> get() {
        //NodeInfoStorage.getInstance().retrieveNodeInfo().getNodeResources().getHardwareResource().getCpu().getArch();
        List<PipelineElementDockerContainer> nodeContainers = new ArrayList<>();

        // PE: pipeline element JVM all container
        PipelineElementDockerContainer processor =
                PipelineElementDockerContainerBuilder.create("pe/org.apache.streampipes.processors.all.jvm")
                        .withImage("apachestreampipes/pipeline-elements-all-jvm:0.68.0-SNAPSHOT")
                        .withName("streampipes_pipeline-elements-all-jvm")
                        .withExposedPorts(new String[]{"7023"})
                        .withEnvironmentVariables(Arrays.asList(
                                "SP_NODE_ID=" + NodeControllerConfig.INSTANCE.getNodeBrokerHost(),
                                "SP_NODE_CONTROLLER_HOST=" + NodeControllerConfig.INSTANCE.getNodeControllerId(),
                                "SP_NODE_CONTROLLER_PORT=" + NodeControllerConfig.INSTANCE.getNodeControllerPort()
                        ))
                        .withLabels(new HashMap<String,String>(){{
                            put("org.apache.streampipes.container.type", "pipeline-element");
                            put("org.apache.streampipes.container.node.type", "edge");}})
                        .build();

        // Node broker: Mosquitto
        PipelineElementDockerContainer nodeBroker =
                PipelineElementDockerContainerBuilder.create("pe/org.apache.streampipes.node.broker")
                        .withImage("eclipse-mosquitto:1.6.12")
                        .withName("streampipes_node-broker")
                        .withExposedPorts(new String[]{"1883"})
                        .withLabels(new HashMap<String,String>(){{
                            put("org.apache.streampipes.pe.container.type", "broker");
                            put("org.apache.streampipes.pe.container.location", "edge");}})
                        .build();

        nodeContainers.add(processor);
        nodeContainers.add(nodeBroker);

        return nodeContainers;
    }
}
