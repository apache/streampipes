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
import org.apache.streampipes.node.controller.config.EnvConfigParam;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.node.NodeManager;
import org.apache.streampipes.node.controller.utils.VersionUtils;

import java.util.*;

public abstract class AbstractStreamPipesDockerContainer {


    protected abstract DockerContainer declareDockerContainer();

    public static String getStreamPipesVersion() {
        return !NodeConfiguration.getStreampipesVersion().isEmpty() ?
                NodeConfiguration.getStreampipesVersion() :  VersionUtils.getStreamPipesVersion();
    }

    public List<String> generateStreamPipesNodeEnvs() {
        return new ArrayList<>(Arrays.asList(
                toEnv(EnvConfigParam.NODE_CONTROLLER_ID.getEnvironmentKey(),
                        NodeConfiguration.getNodeControllerId()),
                toEnv(EnvConfigParam.NODE_CONTROLLER_CONTAINER_HOST.getEnvironmentKey(),
                        NodeConfiguration.getNodeHost()),
                toEnv( EnvConfigParam.NODE_CONTROLLER_CONTAINER_PORT.getEnvironmentKey(),
                        NodeConfiguration.getNodeControllerPort()),
                toEnv(EnvConfigParam.NODE_BROKER_CONTAINER_HOST.getEnvironmentKey(),
                        NodeConfiguration.getNodeBrokerHost()),
                toEnv(EnvConfigParam.NODE_BROKER_CONTAINER_PORT.getEnvironmentKey(),
                        NodeConfiguration.getNodeBrokerPort()),
                toEnv(EnvConfigParam.CONSUL_LOCATION.getEnvironmentKey(),
                        NodeConfiguration.getBackendHost())
        ));
    }

    public String retrieveNodeType(){
        return NodeConfiguration.getNodeType();
    }

    // Helper

    private <T>String toEnv(String key, T value) {
        return String.format("%s=%s", key, value);
    }
}
