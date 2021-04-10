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
package org.apache.streampipes.node.controller.management.orchestrator.docker;

import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.node.controller.config.ConfigKeys;
import org.apache.streampipes.node.controller.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.management.node.NodeManager;

import java.util.*;

public abstract class AbstractStreamPipesDockerContainer {


    protected abstract DockerContainer declareDockerContainer();

    public static String getStreamPipesVersion() {
        return !NodeControllerConfig.INSTANCE.getSpVersion().equals("") ?
                NodeControllerConfig.INSTANCE.getSpVersion() :  NodeManager.getInstance().getStreamPipesVersion();
    }

    public List<String> generateStreamPipesNodeEnvs() {
        return new ArrayList<>(Arrays.asList(
                toEnv(ConfigKeys.NODE_CONTROLLER_ID, NodeControllerConfig.INSTANCE.getNodeControllerId()),
                toEnv(ConfigKeys.NODE_CONTROLLER_CONTAINER_HOST, NodeControllerConfig.INSTANCE.getNodeHost()),
                toEnv( ConfigKeys.NODE_CONTROLLER_CONTAINER_PORT, NodeControllerConfig.INSTANCE.getNodeControllerPort()),
                toEnv(ConfigKeys.NODE_BROKER_CONTAINER_HOST, NodeControllerConfig.INSTANCE.getNodeBrokerHost()),
                toEnv(ConfigKeys.NODE_BROKER_CONTAINER_PORT, NodeControllerConfig.INSTANCE.getNodeBrokerPort())
        ));
    }

    public String retrieveNodeType(){
        return NodeControllerConfig.INSTANCE.getNodeType();
    }

    // Helper

    private <T>String toEnv(String key, T value) {
        return String.format("%s=%s", key, value);
    }
}
