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
import org.apache.streampipes.node.controller.management.orchestrator.docker.AbstractStreamPipesDockerContainer;

public class DockerMosquittoContainer extends AbstractStreamPipesDockerContainer {

    @Override
    public DockerContainer declareDockerContainer() {
        return DockerContainerBuilder.create(StreamPipesDockerServiceID.SP_SVC_MOSQUITTO_ID)
                .withImage("eclipse-mosquitto:1.6.12")
                .supportedArchitectures(
                        SupportedArchitectures.amd(),
                        SupportedArchitectures.arm32v7(),
                        SupportedArchitectures.arm64v8())
                .supportedOperatingSystemTypes(
                        SupportedOsType.linux(),
                        SupportedOsType.darwin())
                .withContainerName("streampipes-node-broker")
                .withExposedPorts(Ports.withMapping("1883"))
                .withEnvironmentVariables(ContainerEnvBuilder.create()
                        .addNodeEnvs(generateStreamPipesNodeEnvs())
                        .build())
                .withLabels(ContainerLabels.with(StreamPipesDockerServiceID.SP_SVC_MOSQUITTO_ID, retrieveNodeType(),
                        ContainerType.BROKER))
                .build();
    }

}


