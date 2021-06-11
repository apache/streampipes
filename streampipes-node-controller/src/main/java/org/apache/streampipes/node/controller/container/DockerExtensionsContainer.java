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
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.orchestrator.docker.AbstractStreamPipesDockerContainer;

public class DockerExtensionsContainer extends AbstractStreamPipesDockerContainer {

    public static String SP_SVC_EXTENSIONS_ID = "svc/org.apache.streampipes.pe.extensions";

    @Override
    public DockerContainer declareDockerContainer() {
        return DockerContainerBuilder.create(SP_SVC_EXTENSIONS_ID)
                .withImage("apachestreampipes/extensions-all-jvm:" + getStreamPipesVersion())
                .supportedArchitectures(
                        SupportedArchitectures.amd64(),
                        SupportedArchitectures.arm32(),
                        SupportedArchitectures.aarch64())
                .supportedOperatingSystemTypes(
                        SupportedOsType.linux(),
                        SupportedOsType.darwin())
                .withContainerName("streampipes-extensions")
                .withExposedPorts(Ports.withMapping("8090"))
                .withEnvironmentVariables(ContainerEnvBuilder.create()
                        .addNodeEnvs(generateStreamPipesNodeEnvs())
                        .add("SP_HOST", NodeConfiguration.getNodeHost())
                        .add("SP_PORT", "8090")
                        .add("SP_LOGGING_FILE_PATH", "/var/log/streampipes/eval")
                        .add("TZ", "Europe/Berlin")
                        .build())
                .withVolumes(ContainerVolumesBuilder.create()
                        .add("streampipes-eval", "/var/log/streampipes/eval", false)
                        .build())
                .withLabels(ContainerLabels.with(SP_SVC_EXTENSIONS_ID, retrieveNodeType(), ContainerType.EXTENSIONS))
                .build();
    }
}
