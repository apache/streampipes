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

import org.apache.streampipes.model.node.DockerContainer;
import org.apache.streampipes.model.node.DockerContainerBuilder;
import org.apache.streampipes.node.controller.container.config.ConfigKeys;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;

import java.util.*;

public class DockerConstants {

    public static final String SP_DOCKER_CONTAINER_REPOSITORY = "apachestreampipes";
    public static final String SP_VERSION = NodeControllerConfig.INSTANCE.getSpVersion();
    public static final String SP_DOCKER_CONTAINER_NAME_PREFIX = "streampipes_";
    public static final String SP_DOCKER_NETWORK_NAME = "spnet";

    // extensions container
    public static final String SP_EXTENSIONS_ID = "pe/org.apache.streampipes.extensions.all.jvm";
    public static final String SP_DOCKER_IMAGE_EXTENSIONS_NAME = "extensions-all-jvm";
    public static final String SP_DOCKER_CONTAINER_EXTENSIONS_IMG_TAG = SP_DOCKER_CONTAINER_REPOSITORY +
            "/" + SP_DOCKER_IMAGE_EXTENSIONS_NAME + ":" + SP_VERSION;
    public static final String SP_DOCKER_CONTAINER_EXTENSIONS_NAME = SP_DOCKER_CONTAINER_NAME_PREFIX + "extensions";
    public static final String[] SP_DOCKER_CONTAINER_EXTENSIONS_PORT = new String[]{"7025"};

    //broker container
    public static final String SP_BROKER_ID = "pe/org.apache.streampipes.node.broker";
    public static final String SP_DOCKER_IMAGE_BROKER_NAME = "eclipse-mosquitto";
    public static final String SP_DOCKER_IMAGE_BROKER_VERSION = "1.6.12";
    public static final String SP_DOCKER_CONTAINER_BROKER_IMG_TAG =
            SP_DOCKER_IMAGE_BROKER_NAME + ":" + SP_DOCKER_IMAGE_BROKER_VERSION;
    public static final String SP_DOCKER_CONTAINER_BROKER_NAME = SP_DOCKER_CONTAINER_NAME_PREFIX + "broker";
    public static final String[] SP_DOCKER_CONTAINER_BROKER_PORT =
            System.getenv(ConfigKeys.NODE_BROKER_CONTAINER_PORT) != null ?
                    new String[]{System.getenv(ConfigKeys.NODE_BROKER_CONTAINER_PORT)} : new String[]{"1883"};

    public static final List<String> SP_DOCKER_CONTAINER_ENV_VARIABLES = Arrays.asList(
            ConfigKeys.NODE_CONTROLLER_ID + "=" + NodeControllerConfig.INSTANCE.getNodeControllerId(),
            ConfigKeys.NODE_CONTROLLER_CONTAINER_HOST +  "=" + NodeControllerConfig.INSTANCE.getNodeHostName(),
            ConfigKeys.NODE_CONTROLLER_CONTAINER_PORT + "=" + NodeControllerConfig.INSTANCE.getNodeControllerPort(),
            ConfigKeys.NODE_BROKER_CONTAINER_HOST + "=" + NodeControllerConfig.INSTANCE.getNodeBrokerHost(),
            ConfigKeys.NODE_BROKER_CONTAINER_PORT + "=" + NodeControllerConfig.INSTANCE.getNodeBrokerPort()
    );

    public static final Map<String, String> SP_DOCKER_CONTAINER_EXTENSIONS_LABELS = new HashMap<String, String>() {{
        put("org.apache.streampipes.service.id", SP_EXTENSIONS_ID);
        put("org.apache.streampipes.node.type", NodeControllerConfig.INSTANCE.getNodeType());
        put("org.apache.streampipes.container.type", "extensions");
    }};

    public static final Map<String, String> SP_DOCKER_CONTAINER_BROKER_LABELS = new HashMap<String, String>() {{
        put("org.apache.streampipes.service.id", SP_BROKER_ID);
        put("org.apache.streampipes.node.type", NodeControllerConfig.INSTANCE.getNodeType());
        put("org.apache.streampipes.container.type", "broker");
    }};


    public enum ContainerStatus {
        DEPLOYED, RUNNING, STOPPED, REMOVED, UNKNOWN
    }

    public enum ContainerType {
        EXTENSIONS, BROKER
    }

    public enum NodeContainer {
        INSTANCE;

        public List<DockerContainer> getAllStreamPipesContainer() {
            List<DockerContainer> nodeContainers = new ArrayList<>();

            // Extensions container
            DockerContainer extensions = DockerContainerBuilder.create(SP_EXTENSIONS_ID)
                    .withImage(DockerConstants.SP_DOCKER_CONTAINER_EXTENSIONS_IMG_TAG)
                    .withName(DockerConstants.SP_DOCKER_CONTAINER_EXTENSIONS_NAME)
                    .withExposedPorts(DockerConstants.SP_DOCKER_CONTAINER_EXTENSIONS_PORT)
                    .withEnvironmentVariables(SP_DOCKER_CONTAINER_ENV_VARIABLES)
                    .withLabels(DockerConstants.SP_DOCKER_CONTAINER_EXTENSIONS_LABELS)
                    .build();

            // Node broker container
            DockerContainer nodeBroker = DockerContainerBuilder.create(SP_BROKER_ID)
                    .withImage(DockerConstants.SP_DOCKER_CONTAINER_BROKER_IMG_TAG)
                    .withName(DockerConstants.SP_DOCKER_CONTAINER_BROKER_NAME)
                    .withExposedPorts(DockerConstants.SP_DOCKER_CONTAINER_BROKER_PORT)
                    .withEnvironmentVariables(SP_DOCKER_CONTAINER_ENV_VARIABLES)
                    .withLabels(SP_DOCKER_CONTAINER_BROKER_LABELS)
                    .build();

            nodeContainers.add(extensions);
            nodeContainers.add(nodeBroker);

            return nodeContainers;
        }

    }

}
