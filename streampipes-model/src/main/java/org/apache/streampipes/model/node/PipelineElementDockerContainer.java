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

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RdfsClass(StreamPipes.PE_DOCKER_CONTAINER)
@Entity
public class PipelineElementDockerContainer extends UnnamedStreamPipesEntity {

    @RdfProperty(StreamPipes.PE_DOCKER_CONTAINER_IMAGE_URI)
    private String imageURI;

    @RdfProperty(StreamPipes.PE_DOCKER_CONTAINER_NAME)
    private String containerName;

    @RdfProperty(StreamPipes.PE_DOCKER_CONTAINER_SERVICE_ID)
    private String serviceId;

    @RdfProperty(StreamPipes.PE_DOCKER_CONTAINER_PORTS)
    private String [] containerPorts;

    @RdfProperty(StreamPipes.PE_DOCKER_CONTAINER_ENV_VARS)
    private List<String> envVars;

    @RdfProperty(StreamPipes.PE_DOCKER_CONTAINER_LABELS)
    private Map<String, String> labels;

    public PipelineElementDockerContainer() {
        super();
    }

    public PipelineElementDockerContainer(String elementId) {
        super(elementId);
    }

    public PipelineElementDockerContainer(String imageURI, String containerName, String serviceId, String [] containerPorts,
                                          List<String> envVars, Map<String, String> labels) {
        this.imageURI = imageURI;
        this.containerName = containerName;
        this.serviceId = serviceId;
        this.containerPorts = containerPorts;
        this.envVars = envVars;
        this.labels = labels;
    }

    public PipelineElementDockerContainer(PipelineElementDockerContainer other) {
        super(other);
        this.imageURI = imageURI;
        this.containerName = containerName;
        this.serviceId = serviceId;
        this.containerPorts = containerPorts;
        this.envVars = envVars;
        this.labels = labels;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String[] getContainerPorts() {
        return containerPorts;
    }

    public void setContainerPorts(String[] containerPorts) {
        this.containerPorts = containerPorts;
    }

    public List<String> getEnvVars() {
        return envVars;
    }

    public void setEnvVars(List<String> envVars) {
        this.envVars = envVars;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "PipelineElementDockerContainer{" +
                "imageURI='" + imageURI + '\'' +
                ", containerName='" + containerName + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", containerPorts=" + Arrays.toString(containerPorts) +
                ", envVars=" + envVars +
                ", labels=" + labels +
                '}';
    }
}
