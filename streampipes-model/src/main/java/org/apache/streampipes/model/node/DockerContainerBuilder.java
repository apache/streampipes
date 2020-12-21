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

public class DockerContainerBuilder {

    private DockerContainer dockerContainer;
    private String imageURI;
    private String containerName;
    private String serviceId;
    private final String [] containerPorts;
    private List<String> envVars;
    private Map<String, String> labels;

    public DockerContainerBuilder(String id) {
        this.dockerContainer = new DockerContainer();
        this.dockerContainer.setServiceId(id);
        this.imageURI = "";
        this.containerName = "";
        this.containerPorts = new String[]{};
        this.envVars = new ArrayList<>();
        this.labels = new HashMap<>();
    }

    public static DockerContainerBuilder create(String id) {
        return new DockerContainerBuilder(id);
    }

    public DockerContainerBuilder withImage(String imageUri) {
        this.dockerContainer.setImageURI(imageUri);
        return this;
    }

    public DockerContainerBuilder withName(String name) {
        this.dockerContainer.setContainerName(name);
        return this;
    }

    public DockerContainerBuilder withExposedPorts(String[] ports) {
        this.dockerContainer.setContainerPorts(ports);
        return this;
    }

    public DockerContainerBuilder withEnvironmentVariables(List<String> envs) {
        this.dockerContainer.setEnvVars(envs);
        return this;
    }

    public DockerContainerBuilder withLabels(Map<String, String> labels) {
        this.dockerContainer.setLabels(labels);
        return this;
    }

    public DockerContainer build() {
        return dockerContainer;
    }

}
