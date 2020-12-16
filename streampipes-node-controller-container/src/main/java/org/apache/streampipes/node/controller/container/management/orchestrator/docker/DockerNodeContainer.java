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
package org.apache.streampipes.node.controller.container.management.orchestrator.docker;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.node.Node;
import org.apache.streampipes.model.node.PipelineElementDockerContainer;
import org.apache.streampipes.model.node.PipelineElementDockerContainerBuilder;
import org.apache.streampipes.node.controller.container.NodeControllerContainerInit;
import org.apache.streampipes.node.controller.container.config.ConfigKeys;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;

import java.util.*;

public enum DockerNodeContainer {
    INSTANCE;

    private static final String LABEL_CONTAINER_TYPE_KEY = "org.apache.streampipes.container.type";
    private static final String LABEL_CONTAINER_TYPE_EXTENSIONS_VALUE = "pipeline-element";
    private static final String LABEL_CONTAINER_TYPE_BROKER_VALUE = "broker";
    private static final String LABEL_NODE_TYPE_KEY = "org.apache.streampipes.node.type";
    private static final String LABEL_NODE_TYPE_VALUE = NodeControllerConfig.INSTANCE.getNodeType();

    private static final String SP_DOCKER_REPOSITORY = "apachestreampipes";
    // TODO: get version from backend when registering node controller
    private static final String SP_VERSION = NodeControllerConfig.INSTANCE.getSpVersion();
    private static final String SP_CONTAINER_NAME_PREFIX = "streampipes_";

    public List<PipelineElementDockerContainer> get() {
        //NodeInfoStorage.getInstance().retrieveNodeInfo().getNodeResources().getHardwareResource().getCpu().getArch();
        List<PipelineElementDockerContainer> nodeContainers = new ArrayList<>();

        // Extensions container
        PipelineElementDockerContainer extensions =
                PipelineElementDockerContainerBuilder.create("pe/org.apache.streampipes.extensions.all.jvm")
                        .withImage(SP_DOCKER_REPOSITORY + "/" + "extensions-all-jvm" + ":" + SP_VERSION)
                        .withName(SP_CONTAINER_NAME_PREFIX + "extensions-all-jvm")
                        .withExposedPorts(new String[]{"7023"})
                        .withEnvironmentVariables(makeContainerEnvVars())
                        .withLabels(makeLabels(ContainerType.EXTENSIONS))
                        .build();

        // Node broker container
        PipelineElementDockerContainer nodeBroker =
                PipelineElementDockerContainerBuilder.create("pe/org.apache.streampipes.node.broker")
                        .withImage("eclipse-mosquitto:1.6.12")
                        .withName(SP_CONTAINER_NAME_PREFIX + "node-broker")
                        .withExposedPorts(new String[]{"1883"})
                        .withEnvironmentVariables(makeContainerEnvVars())
                        .withLabels(makeLabels(ContainerType.BROKER))
                        .build();

        nodeContainers.add(extensions);
        nodeContainers.add(nodeBroker);

        return nodeContainers;
    }

    private Map<String, String> makeLabels(ContainerType containerType) {
        switch (containerType) {
            case EXTENSIONS:
                return new HashMap<String,String>(){{
                    put(LABEL_CONTAINER_TYPE_KEY, LABEL_CONTAINER_TYPE_EXTENSIONS_VALUE);
                    put(LABEL_NODE_TYPE_KEY, LABEL_NODE_TYPE_VALUE);}};
            case BROKER:
                return new HashMap<String,String>(){{
                    put(LABEL_CONTAINER_TYPE_KEY, LABEL_CONTAINER_TYPE_BROKER_VALUE);
                    put(LABEL_NODE_TYPE_KEY, LABEL_NODE_TYPE_VALUE);}};
            default:
                break;
        }
        throw new SpRuntimeException("Unsupported container type: " + containerType);
    }

    private List<String> makeContainerEnvVars() {
        return Arrays.asList(
                ConfigKeys.NODE_CONTROLLER_ID + "=" + NodeControllerConfig.INSTANCE.getNodeControllerId(),
                ConfigKeys.NODE_CONTROLLER_CONTAINER_HOST +  "=" + NodeControllerConfig.INSTANCE.getNodeHostName(),
                ConfigKeys.NODE_CONTROLLER_CONTAINER_PORT + "=" + NodeControllerConfig.INSTANCE.getNodeControllerPort(),
                ConfigKeys.NODE_BROKER_CONTAINER_PORT + "=" + NodeControllerConfig.INSTANCE.getNodeControllerPort()
        );
    }
}
