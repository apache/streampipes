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

import org.apache.streampipes.commons.constants.InstanceIdExtractor;
import org.apache.streampipes.logging.LoggerFactory;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.api.EndpointSelectable;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public abstract class InvocableStreamPipesEntity extends NamedStreamPipesEntity implements EndpointSelectable {

  protected List<SpDataStream> inputStreams;

  protected List<StaticProperty> staticProperties;

  private String belongsTo;

  private ElementStatusInfoSettings statusInfoSettings;

  private EventGrounding supportedGrounding;

  private String correspondingPipeline;

  private String correspondingUser;

  private List<SpDataStream> streamRequirements;

  private boolean configured;

  private boolean uncompleted;

  private String selectedEndpointUrl;

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
    this.correspondingUser = other.getCorrespondingUser();
    this.selectedEndpointUrl = other.getSelectedEndpointUrl();
    if (other.getStreamRequirements() != null) {
      this.streamRequirements = new Cloner().streams(other.getStreamRequirements());
    }
    if (other.getStaticProperties() != null) {
      this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
    }
    this.dom = other.getDom();
    if (other.getSupportedGrounding() != null) {
      this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());
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

  @Override
  public String getCorrespondingPipeline() {
    return correspondingPipeline;
  }

  @Override
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

  @Override
  public String getSelectedEndpointUrl() {
    return selectedEndpointUrl;
  }

  @Override
  public void setSelectedEndpointUrl(String selectedEndpointUrl) {
    this.selectedEndpointUrl = selectedEndpointUrl;
  }

  @Override
  @JsonIgnore
  public String getDetachPath() {
    return "/" + InstanceIdExtractor.extractId(getElementId());
  }

  //public Logger getLogger(Class clazz, PeConfig peConfig) {
  public Logger getLogger(Class clazz) {
    //return LoggerFactory.getPeLogger(clazz, getCorrespondingPipeline(), getUri(), peConfig);
    return LoggerFactory.getPeLogger(clazz, getCorrespondingPipeline(), getUri());
  }
}
