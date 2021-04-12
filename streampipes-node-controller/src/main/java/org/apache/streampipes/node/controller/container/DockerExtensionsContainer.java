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
package org.apache.streampipes.node.controller.container;

import org.apache.streampipes.model.node.container.*;
import org.apache.streampipes.node.controller.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.management.orchestrator.docker.AbstractStreamPipesDockerContainer;

public class DockerExtensionsContainer extends AbstractStreamPipesDockerContainer {

    @Override
    public DockerContainer declareDockerContainer() {
        return DockerContainerBuilder.create(StreamPipesDockerServiceID.SP_SVC_EXTENSIONS_ID)
                .withImage("apachestreampipes/extensions-all-jvm:" + getStreamPipesVersion())
                .withContainerName("streampipes-extensions")
                .withExposedPorts(Ports.withMapping("8090"))
                .withEnvironmentVariables(ContainerEnvBuilder.create()
                        .addNodeEnvs(generateStreamPipesNodeEnvs())
                        .add("CONSUL_LOCATION", NodeControllerConfig.INSTANCE.consulLocation())
                        .add("SP_HOST", NodeControllerConfig.INSTANCE.getNodeHost())
                        .add("SP_PORT", "8090")
                        .build())
                .withLabels(ContainerLabels.with(StreamPipesDockerServiceID.SP_SVC_EXTENSIONS_ID, retrieveNodeType(),
                        ContainerType.EXTENSIONS))
                .build();
    }
}
