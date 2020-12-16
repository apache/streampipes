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

package org.apache.streampipes.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.quality.EventStreamQualityDefinition;
import org.apache.streampipes.model.quality.EventStreamQualityRequirement;
import org.apache.streampipes.model.quality.MeasurementCapability;
import org.apache.streampipes.model.quality.MeasurementObject;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RdfsClass(StreamPipes.DATA_STREAM)
@Entity
@JsonSubTypes({
        @JsonSubTypes.Type(SpDataStream.class),
        @JsonSubTypes.Type(SpDataSet.class)
})
public class SpDataStream extends NamedStreamPipesEntity {

  private static final long serialVersionUID = -5732549347563182863L;

  private static final String prefix = "urn:fzi.de:eventstream:";

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_EVENT_STREAM_QUALITY_DEFINITION)
  protected transient List<EventStreamQualityDefinition> hasEventStreamQualities;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_EVENT_STREAM_QUALITY_REQUIREMENT)
  protected transient List<EventStreamQualityRequirement> requiresEventStreamQualities;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_GROUNDING)
  protected EventGrounding eventGrounding;

  @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_SCHEMA)
  protected EventSchema eventSchema;

  @RdfProperty(StreamPipes.HAS_MEASUREMENT_CAPABILTIY)
  @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
  protected List<MeasurementCapability> measurementCapability;

  @RdfProperty(StreamPipes.HAS_MEASUREMENT_OBJECT)
  @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
  protected List<MeasurementObject> measurementObject;

  @RdfProperty(StreamPipes.INDEX)
  private int index;

  protected List<String> category;

  @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_ID)
  private String deploymentTargetNodeId;

  @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_HOSTNAME)
  private String deploymentTargetNodeHostname;

  @RdfProperty(StreamPipes.DEPLOYMENT_TARGET_NODE_PORT)
  private Integer deploymentTargetNodePort;

  public SpDataStream(String uri, String name, String description, String iconUrl, List<EventStreamQualityDefinition> hasEventStreamQualities,
                      EventGrounding eventGrounding,
                      EventSchema eventSchema) {
    super(uri, name, description, iconUrl);
    this.hasEventStreamQualities = hasEventStreamQualities;
    this.eventGrounding = eventGrounding;
    this.eventSchema = eventSchema;
  }

  public SpDataStream(String uri, String name, String description, EventSchema eventSchema) {
    super(uri, name, description);
    this.eventSchema = eventSchema;
  }

  public SpDataStream() {
    super(prefix + RandomStringUtils.randomAlphabetic(6));
  }


  public SpDataStream(SpDataStream other) {
    super(other);
    this.index = other.getIndex();
    if (other.getEventGrounding() != null) {
      this.eventGrounding = new EventGrounding(other.getEventGrounding());
    }
    if (other.getEventSchema() != null) {
      this.eventSchema = new EventSchema(other.getEventSchema());
    }
    if (other.getHasEventStreamQualities() != null) {
      this.hasEventStreamQualities = other.getHasEventStreamQualities().stream().map(EventStreamQualityDefinition::new).collect(Collectors.toCollection(ArrayList::new));
    }
    if (other.getRequiresEventStreamQualities() != null) {
      this.requiresEventStreamQualities = other.getRequiresEventStreamQualities().stream().map(EventStreamQualityRequirement::new).collect(Collectors.toCollection(ArrayList::new));
    }
    if (other.getMeasurementCapability() != null) {
      this.measurementCapability = new Cloner().mc(other.getMeasurementCapability());
    }
    if (other.getMeasurementObject() != null) {
      this.measurementObject = new Cloner().mo(other.getMeasurementObject());
    }
    this.deploymentTargetNodeId = other.getDeploymentTargetNodeId();
    this.deploymentTargetNodeHostname = other.getDeploymentTargetNodeHostname();
    this.deploymentTargetNodePort = other.getDeploymentTargetNodePort();
  }

  public List<EventStreamQualityDefinition> getHasEventStreamQualities() {
    return hasEventStreamQualities;
  }

  public void setHasEventStreamQualities(
          List<EventStreamQualityDefinition> hasEventStreamQualities) {
    this.hasEventStreamQualities = hasEventStreamQualities;
  }


  public List<EventStreamQualityRequirement> getRequiresEventStreamQualities() {
    return requiresEventStreamQualities;
  }

  public void setRequiresEventStreamQualities(
          List<EventStreamQualityRequirement> requiresEventStreamQualities) {
    this.requiresEventStreamQualities = requiresEventStreamQualities;
  }

  public EventSchema getEventSchema() {
    return eventSchema;
  }

  public void setEventSchema(EventSchema eventSchema) {
    this.eventSchema = eventSchema;
  }

  public EventGrounding getEventGrounding() {
    return eventGrounding;
  }

  public void setEventGrounding(EventGrounding eventGrounding) {
    this.eventGrounding = eventGrounding;
  }

  public List<MeasurementCapability> getMeasurementCapability() {
    return measurementCapability;
  }

  public void setMeasurementCapability(
          List<MeasurementCapability> measurementCapability) {
    this.measurementCapability = measurementCapability;
  }

  public List<MeasurementObject> getMeasurementObject() {
    return measurementObject;
  }

  public void setMeasurementObject(List<MeasurementObject> measurementObject) {
    this.measurementObject = measurementObject;
  }

  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
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
}
