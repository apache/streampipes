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

import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.model.node.container.DockerContainerBuilder;
import org.apache.streampipes.node.controller.container.config.ConfigKeys;
import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.container.management.node.NodeManager;

import java.util.*;

public class DockerConstants {

    public static final String SP_VERSION =
            !NodeControllerConfig.INSTANCE.getSpVersion().equals("") ?
                    NodeControllerConfig.INSTANCE.getSpVersion() :  NodeManager.getInstance().getStreamPipesVersion();
    public static final String SP_CONTAINER_PREFIX = "streampipes-";
    public static final String SP_CONTAINER_NETWORK = "spnet";

    // extensions container
    public static final String SP_EXTENSIONS_ID = "pe/org.apache.streampipes.extensions.all.jvm";
    public static final String SP_IMG_EXTENSIONS = "apachestreampipes/extensions-all-jvm:" + SP_VERSION;
    public static final String SP_CONTAINER_EXTENSIONS_NAME = SP_CONTAINER_PREFIX + "extensions";
    public static final String[] SP_CONTAINER_EXTENSIONS_PORT = new String[]{"8090"};

    //broker container
    public static final String SP_BROKER_ID = "pe/org.apache.streampipes.node.broker";
    public static final String SP_IMG_MOSQUITTO = "eclipse-mosquitto:1.6.12";
    public static final String SP_CONTAINER_BROKER_NAME = SP_CONTAINER_PREFIX + "node-broker";
    public static final String[] SP_CONTAINER_BROKER_PORT =
            System.getenv(ConfigKeys.NODE_BROKER_CONTAINER_PORT) != null ?
                    new String[]{System.getenv(ConfigKeys.NODE_BROKER_CONTAINER_PORT)} : new String[]{"1883"};

    // container env variables
    public static final List<String> SP_CONTAINER_ENV_VARIABLES = Arrays.asList(
            ConfigKeys.NODE_CONTROLLER_ID + "=" + NodeControllerConfig.INSTANCE.getNodeControllerId(),
            ConfigKeys.NODE_CONTROLLER_CONTAINER_HOST +  "=" + NodeControllerConfig.INSTANCE.getNodeHost(),
            ConfigKeys.NODE_CONTROLLER_CONTAINER_PORT + "=" + NodeControllerConfig.INSTANCE.getNodeControllerPort(),
            ConfigKeys.NODE_BROKER_CONTAINER_HOST + "=" + NodeControllerConfig.INSTANCE.getNodeBrokerHost(),
            ConfigKeys.NODE_BROKER_CONTAINER_PORT + "=" + NodeControllerConfig.INSTANCE.getNodeBrokerPort(),
            //TODO: remove once PE does not need it for KV registration
            "CONSUL_LOCATION=" + NodeControllerConfig.INSTANCE.consulLocation(),
            "SP_HOST=" + NodeControllerConfig.INSTANCE.getNodeHost(),
            "SP_PORT=8090"
    );

    // extension container labels
    public static final Map<String, String> SP_CONTAINER_EXTENSIONS_LABELS = new HashMap<String, String>() {{
        put("org.apache.streampipes.service.id", SP_EXTENSIONS_ID);
        put("org.apache.streampipes.node.type", NodeControllerConfig.INSTANCE.getNodeType());
        put("org.apache.streampipes.container.type", "extensions");
    }};

    // mosquitto container labels
    public static final Map<String, String> SP_CONTAINER_BROKER_LABELS = new HashMap<String, String>() {{
        put("org.apache.streampipes.service.id", SP_BROKER_ID);
        put("org.apache.streampipes.node.type", NodeControllerConfig.INSTANCE.getNodeType());
        put("org.apache.streampipes.container.type", "broker");
    }};


    public enum ContainerStatus {
        DEPLOYED, RUNNING, STOPPED, REMOVED, UNKNOWN, FAILED
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
                    .withImage(DockerConstants.SP_IMG_EXTENSIONS)
                    .withName(DockerConstants.SP_CONTAINER_EXTENSIONS_NAME)
                    .withExposedPorts(DockerConstants.SP_CONTAINER_EXTENSIONS_PORT)
                    .withEnvironmentVariables(SP_CONTAINER_ENV_VARIABLES)
                    .withLabels(DockerConstants.SP_CONTAINER_EXTENSIONS_LABELS)
                    .build();

            // Node broker container
            DockerContainer nodeBroker = DockerContainerBuilder.create(SP_BROKER_ID)
                    .withImage(DockerConstants.SP_IMG_MOSQUITTO)
                    .withName(DockerConstants.SP_CONTAINER_BROKER_NAME)
                    .withExposedPorts(DockerConstants.SP_CONTAINER_BROKER_PORT)
                    .withEnvironmentVariables(SP_CONTAINER_ENV_VARIABLES)
                    .withLabels(SP_CONTAINER_BROKER_LABELS)
                    .build();

            nodeContainers.add(extensions);
            nodeContainers.add(nodeBroker);

            return nodeContainers;
        }

    }

}
