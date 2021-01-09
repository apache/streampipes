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

package org.apache.streampipes.model.base;

import io.fogsy.empire.annotations.RdfProperty;
import org.apache.streampipes.logging.LoggerFactory;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.apache.streampipes.model.resource.NodeResourceRequirement;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

public abstract class InvocableStreamPipesEntity extends NamedStreamPipesEntity {

  private static final long serialVersionUID = 2727573914765473470L;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.RECEIVES_STREAM)
  protected List<SpDataStream> inputStreams;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
  protected List<StaticProperty> staticProperties;

  @RdfProperty(StreamPipes.BELONGS_TO)
  private String belongsTo;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.STATUS_INFO_SETTINGS)
  private ElementStatusInfoSettings statusInfoSettings;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.SUPPORTED_GROUNDING)
  private EventGrounding supportedGrounding;

  @RdfProperty(StreamPipes.CORRESPONDING_PIPELINE)
  private String correspondingPipeline;

  @RdfProperty(StreamPipes.CORRESPONDING_USER)
  private String correspondingUser;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.REQUIRES_STREAM)
  private List<SpDataStream> streamRequirements;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.REQUIRES_RESOURCES)
  protected List<NodeResourceRequirement> resourceRequirements;

  @RdfProperty(StreamPipes.ELEMENT_ENDPOINT_HOSTNAME)
  private String elementEndpointHostname;

  @RdfProperty(StreamPipes.ELEMENT_ENDPOINT_PORT)
  private Integer elementEndpointPort;

  @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_ID)
  private String deploymentTargetNodeId;

  @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_HOSTNAME)
  private String deploymentTargetNodeHostname;

  @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_PORT)
  private Integer deploymentTargetNodePort;

  @RdfProperty(StreamPipes.DEPLOYMENT_RUNNING_INSTANCE_ID)
  private String deploymentRunningInstanceId;

  @RdfProperty(StreamPipes.ELEMENT_ENDPOINT_SERVICE_NAME)
  private String elementEndpointServiceName;

  //@RdfProperty(StreamPipes.PE_CONFIGURED)
  private boolean configured;

  private boolean uncompleted;

  public InvocableStreamPipesEntity() {
    super();
  }

  public InvocableStreamPipesEntity(InvocableStreamPipesEntity other) {
    super(other);
    this.belongsTo = other.getBelongsTo();
    this.correspondingPipeline = other.getCorrespondingPipeline();
    this.inputStreams = new Cloner().streams(other.getInputStreams());
    this.configured = other.isConfigured();
    this.uncompleted = other.isUncompleted();
    this.elementEndpointHostname = other.getElementEndpointHostname();
    this.elementEndpointPort = other.getElementEndpointPort();
    this.deploymentTargetNodeHostname = other.getDeploymentTargetNodeHostname();
    this.deploymentTargetNodeId = other.getDeploymentTargetNodeId();
    this.deploymentTargetNodePort = other.getDeploymentTargetNodePort();
    this.deploymentRunningInstanceId = other.getDeploymentRunningInstanceId();
    this.elementEndpointServiceName = other.getElementEndpointServiceName();
    this.correspondingUser = other.getCorrespondingUser();
    if (other.getStreamRequirements() != null) {
      this.streamRequirements = new Cloner().streams(other.getStreamRequirements());
    }
    if (other.getStaticProperties() != null) {
      this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
    }
    this.DOM = other.getDOM();
    if (other.getSupportedGrounding() != null) {
      this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());
    }
    if (other.getResourceRequirements() != null) {
      this.resourceRequirements = new Cloner().resourceRequirements(other.getResourceRequirements());
    }
  }

  public InvocableStreamPipesEntity(String uri, String name, String description, String iconUrl) {
    super(uri, name, description, iconUrl);
    this.configured = false;
  }

  public boolean addStaticProperty(StaticProperty staticProperty) {
    return staticProperties.add(staticProperty);
  }

  public List<SpDataStream> getInputStreams() {
    return inputStreams;
  }

  public void setInputStreams(List<SpDataStream> inputStreams) {
    this.inputStreams = inputStreams;
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public String getBelongsTo() {
    return belongsTo;
  }

  public void setBelongsTo(String belongsTo) {
    this.belongsTo = belongsTo;
  }

  public EventGrounding getSupportedGrounding() {
    return supportedGrounding;
  }

  public void setSupportedGrounding(EventGrounding supportedGrounding) {
    this.supportedGrounding = supportedGrounding;
  }

  public String getCorrespondingPipeline() {
    return correspondingPipeline;
  }

  public void setCorrespondingPipeline(String correspondingPipeline) {
    this.correspondingPipeline = correspondingPipeline;
  }

  public List<SpDataStream> getStreamRequirements() {
    return streamRequirements;
  }

  public void setStreamRequirements(List<SpDataStream> streamRequirements) {
    this.streamRequirements = streamRequirements;
  }

  public boolean isConfigured() {
    return configured;
  }

  public void setConfigured(boolean configured) {
    this.configured = configured;
  }

  public ElementStatusInfoSettings getStatusInfoSettings() {
    return statusInfoSettings;
  }

  public void setStatusInfoSettings(ElementStatusInfoSettings statusInfoSettings) {
    this.statusInfoSettings = statusInfoSettings;
  }

  public String getDeploymentTargetNodeId() {
    return deploymentTargetNodeId;
  }

  public void setDeploymentTargetNodeId(String deploymentTargetNodeId) {
    this.deploymentTargetNodeId = deploymentTargetNodeId;
  }

  public String getDeploymentTargetNodeHostname() {
    return deploymentTargetNodeHostname;
  }

  public void setDeploymentTargetNodeHostname(String deploymentTargetNodeHostname) {
    this.deploymentTargetNodeHostname = deploymentTargetNodeHostname;
  }

  public Integer getDeploymentTargetNodePort() {
    return deploymentTargetNodePort;
  }

  public void setDeploymentTargetNodePort(Integer deploymentTargetNodePort) {
    this.deploymentTargetNodePort = deploymentTargetNodePort;
  }

  public String getDeploymentRunningInstanceId() {
    return deploymentRunningInstanceId;
  }

  public void setDeploymentRunningInstanceId(String deploymentRunningInstanceId) {
    this.deploymentRunningInstanceId = deploymentRunningInstanceId;
  }

  public String getElementEndpointHostname() {
    return elementEndpointHostname;
  }

  public void setElementEndpointHostname(String elementEndpointHostname) {
    this.elementEndpointHostname = elementEndpointHostname;
  }

  public Integer getElementEndpointPort() {
    return elementEndpointPort;
  }

  public void setElementEndpointPort(Integer elementEndpointPort) {
    this.elementEndpointPort = elementEndpointPort;
  }

  public String getCorrespondingUser() {
    return correspondingUser;
  }

  public void setCorrespondingUser(String correspondingUser) {
    this.correspondingUser = correspondingUser;
  }

  public boolean isUncompleted() {
    return uncompleted;
  }

  public void setUncompleted(boolean uncompleted) {
    this.uncompleted = uncompleted;
  }

  //public Logger getLogger(Class clazz, PeConfig peConfig) {
  public Logger getLogger(Class clazz) {
    //	return LoggerFactory.getPeLogger(clazz, getCorrespondingPipeline(), getUri(), peConfig);
    return LoggerFactory.getPeLogger(clazz, getCorrespondingPipeline(), getUri());
  }

  public String getElementEndpointServiceName() {
    return elementEndpointServiceName;
  }

  public void setElementEndpointServiceName(String elementEndpointServiceName) {
    this.elementEndpointServiceName = elementEndpointServiceName;
  }

  public List<NodeResourceRequirement> getResourceRequirements() {
    return resourceRequirements;
  }

  public void setResourceRequirements(List<NodeResourceRequirement> resourceRequirements) {
    this.resourceRequirements = resourceRequirements;
  }
}
