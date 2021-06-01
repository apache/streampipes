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
package org.apache.streampipes.model.node.container;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.RDFS;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@RdfsClass(StreamPipes.DEPLOYMENT_CONTAINER)
@Entity
@JsonSubTypes({
        @JsonSubTypes.Type(DockerContainer.class)
})
@TsModel
public abstract class DeploymentContainer extends UnnamedStreamPipesEntity {

    protected static final String prefix = "urn:streampipes.org:spi:";

    @RdfProperty(StreamPipes.HAS_IMAGE_TAG)
    private String imageTag;

    @RdfProperty(StreamPipes.HAS_CONTAINER_NAME)
    private String containerName;

    @RdfProperty(StreamPipes.HAS_CONTAINER_SERVICE_ID)
    private String serviceId;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_CONTAINER_PORTS)
    private List<String> containerPorts;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_CONTAINER_ENV_VARS)
    private List<ContainerEnvVar> envVars;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_CONTAINER_LABELS)
    private List<ContainerLabel> labels;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_CONTAINER_VOLUMES)
    private List<String> volumes;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_SUPPORTED_ARCHITECTURES)
    private List<String> supportedArchitectures;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_SUPPORTED_OS_TYPES)
    private List<String> supportedOperatingSystemTypes;


    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_CONTAINER_DEPENDENCIES)
    private List<String> dependsOnContainers;

    public DeploymentContainer() {
        this.envVars = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.volumes = new ArrayList<>();
        this.dependsOnContainers = new ArrayList<>();
        this.supportedArchitectures = new ArrayList<>();
        this.supportedOperatingSystemTypes = new ArrayList<>();
    }

    public DeploymentContainer(String elementId) {
        super(elementId);
        this.envVars = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.volumes = new ArrayList<>();
        this.dependsOnContainers = new ArrayList<>();
        this.supportedArchitectures = new ArrayList<>();
        this.supportedOperatingSystemTypes = new ArrayList<>();
    }

    public DeploymentContainer(DeploymentContainer other) {
        super(other);
        this.imageTag = other.getImageTag();
        this.containerName = other.getContainerName();
        this.serviceId = other.getServiceId();
        this.containerPorts = other.getContainerPorts();
        this.envVars = other.getEnvVars();
        this.labels = other.getLabels();
        this.volumes = other.getVolumes();
        this.supportedArchitectures = other.getSupportedArchitectures();
        this.supportedOperatingSystemTypes = other.getSupportedOperatingSystemTypes();
        this.dependsOnContainers = other.getDependsOnContainers();
    }

    public DeploymentContainer(String imageTag, String containerName, String serviceId, List<String> containerPorts,
                               List<ContainerEnvVar> envVars, List<ContainerLabel> labels, List<String> volumes,
                               List<String> supportedArchitectures, List<String> supportedOperatingSystemsTypes,
                               List<String> dependsOnContainers) {
        super(prefix + UUID.randomUUID().toString());
        this.imageTag = imageTag;
        this.containerName = containerName;
        this.serviceId = serviceId;
        this.containerPorts = containerPorts;
        this.envVars = envVars;
        this.labels = labels;
        this.volumes = volumes;
        this.supportedArchitectures = supportedArchitectures;
        this.supportedOperatingSystemTypes = supportedOperatingSystemsTypes;
        this.dependsOnContainers = dependsOnContainers;
    }

    public String getImageTag() {
        return imageTag;
    }
    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
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

    public List<String> getContainerPorts() {
        return containerPorts;
    }

    public void setContainerPorts(List<String> containerPorts) {
        this.containerPorts = containerPorts;
    }

    public List<ContainerEnvVar> getEnvVars() {
        return envVars;
    }

    public void setEnvVars(List<ContainerEnvVar> envVars) {
        this.envVars = envVars;
    }

    public List<ContainerLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<ContainerLabel> labels) {
        this.labels = labels;
    }

    public List<String> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<String> volumes) {
        this.volumes = volumes;
    }

    public List<String> getDependsOnContainers() {
        return dependsOnContainers;
    }

    public void setDependsOnContainers(List<String> dependsOnContainers) {
        this.dependsOnContainers = dependsOnContainers;
    }

    public List<String> getSupportedArchitectures() {
        return supportedArchitectures;
    }

    public void setSupportedArchitectures(List<String> supportedArchitectures) {
        this.supportedArchitectures = supportedArchitectures;
    }

    public List<String> getSupportedOperatingSystemTypes() {
        return supportedOperatingSystemTypes;
    }

    public void setSupportedOperatingSystemTypes(List<String> supportedOperatingSystemTypes) {
        this.supportedOperatingSystemTypes = supportedOperatingSystemTypes;
    }
}
