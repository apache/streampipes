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
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.resource.NodeResourceRequirement;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

public abstract class ConsumableStreamPipesEntity extends NamedStreamPipesEntity {

  private static final long serialVersionUID = -6617391345752016449L;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.REQUIRES_STREAM)
  protected List<SpDataStream> spDataStreams;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
  protected List<StaticProperty> staticProperties;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_NODE_RESOURCE_REQUIREMENT)
  protected List<NodeResourceRequirement> resourceRequirements;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.SUPPORTED_GROUNDING)
  private EventGrounding supportedGrounding;

  @RdfProperty(StreamPipes.ELEMENT_ENDPOINT_HOSTNAME)
  private String elementEndpointHostname;

  @RdfProperty(StreamPipes.ELEMENT_ENDPOINT_PORT)
  private Integer elementEndpointPort;

  @RdfProperty(StreamPipes.ELEMENT_ENDPOINT_SERVICE_NAME)
  private String elementEndpointServiceName;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.IS_STATEFUL)
  private boolean isStateful;


  public boolean isStateful() {
    return isStateful;
  }

  public void setStateful(boolean stateful) {
    isStateful = stateful;
  }

  public ConsumableStreamPipesEntity() {
    super();
    this.spDataStreams = new ArrayList<>();
    this.staticProperties = new ArrayList<>();
    this.resourceRequirements = new ArrayList<>();
    this.setStateful(false);
  }

  public ConsumableStreamPipesEntity(String uri, String name, String description, String iconUrl) {
    super(uri, name, description, iconUrl);
    this.spDataStreams = new ArrayList<>();
    this.staticProperties = new ArrayList<>();
    this.resourceRequirements = new ArrayList<>();
    this.setStateful(false);
  }

  public ConsumableStreamPipesEntity(ConsumableStreamPipesEntity other) {
    super(other);
    this.elementEndpointHostname = other.getElementEndpointHostname();
    this.elementEndpointPort = other.getElementEndpointPort();
    this.elementEndpointServiceName = other.getElementEndpointServiceName();
    if (other.getSpDataStreams() != null) {
      this.spDataStreams = new Cloner().streams(other.getSpDataStreams());
    }
    this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
    if (other.getSupportedGrounding() != null) {
      this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());
    }
    if (other.getResourceRequirements() != null) {
      this.resourceRequirements = new Cloner().resourceRequirements(other.getResourceRequirements());
    }
    this.isStateful = other.isStateful;
  }

  public List<SpDataStream> getSpDataStreams() {
    return spDataStreams;
  }

  public void setSpDataStreams(List<SpDataStream> spDataStreams) {
    this.spDataStreams = spDataStreams;
  }

  public List<StaticProperty> getStaticProperties() {
    return staticProperties;
  }

  public void setStaticProperties(List<StaticProperty> staticProperties) {
    this.staticProperties = staticProperties;
  }

  public boolean addEventStream(SpDataStream spDataStream) {
    return spDataStreams.add(spDataStream);
  }

  public EventGrounding getSupportedGrounding() {
    return supportedGrounding;
  }

  public void setSupportedGrounding(EventGrounding supportedGrounding) {
    this.supportedGrounding = supportedGrounding;
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
