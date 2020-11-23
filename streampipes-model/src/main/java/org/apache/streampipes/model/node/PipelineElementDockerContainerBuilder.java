package org.apache.streampipes.model.node;/*
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
import java.util.*;

public class PipelineElementDockerContainerBuilder {

    private PipelineElementDockerContainer pipelineElementDockerContainer;
    private String imageURI;
    private String containerName;
    private String serviceId;
    private final String [] containerPorts;
    private List<String> envVars;
    private Map<String, String> labels;

    public PipelineElementDockerContainerBuilder(String id) {
        this.pipelineElementDockerContainer = new PipelineElementDockerContainer();
        this.pipelineElementDockerContainer.setServiceId(id);
        this.imageURI = "";
        this.containerName = "";
        this.containerPorts = new String[]{};
        this.envVars = new ArrayList<>();
        this.labels = new HashMap<>();
    }

    public static PipelineElementDockerContainerBuilder create(String id) {
        return new PipelineElementDockerContainerBuilder(id);
    }

    public PipelineElementDockerContainerBuilder withImage(String imageUri) {
        this.pipelineElementDockerContainer.setImageURI(imageUri);
        return this;
    }

    public PipelineElementDockerContainerBuilder withName(String name) {
        this.pipelineElementDockerContainer.setContainerName(name);
        return this;
    }

    public PipelineElementDockerContainerBuilder withExposedPorts(String[] ports) {
        this.pipelineElementDockerContainer.setContainerPorts(ports);
        return this;
    }

    public PipelineElementDockerContainerBuilder withEnvironmentVariables(List<String> envs) {
        this.pipelineElementDockerContainer.setEnvVars(envs);
        return this;
    }

    public PipelineElementDockerContainerBuilder withLabels(Map<String, String> labels) {
        this.pipelineElementDockerContainer.setLabels(labels);
        return this;
    }

    public PipelineElementDockerContainer build() {
        return pipelineElementDockerContainer;
    }

}
