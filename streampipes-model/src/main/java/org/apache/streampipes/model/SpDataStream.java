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

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.quality.EventStreamQualityDefinition;
import org.apache.streampipes.model.quality.EventStreamQualityRequirement;
import org.apache.streampipes.model.quality.MeasurementCapability;
import org.apache.streampipes.model.quality.MeasurementObject;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.util.Cloner;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonSubTypes({
    @JsonSubTypes.Type(SpDataStream.class),
    @JsonSubTypes.Type(SpDataSet.class)
})
public class SpDataStream extends NamedStreamPipesEntity {

  private static final long serialVersionUID = -5732549347563182863L;

  private static final String prefix = "urn:streampipes.apache.org:eventstream:";

  protected transient List<EventStreamQualityDefinition> hasEventStreamQualities;

  protected transient List<EventStreamQualityRequirement> requiresEventStreamQualities;

  protected EventGrounding eventGrounding;

  protected EventSchema eventSchema;

  protected List<MeasurementCapability> measurementCapability;

  protected List<MeasurementObject> measurementObject;
  protected List<String> category;
  private int index;
  private String correspondingAdapterId;

  public SpDataStream(String uri, String name, String description, String iconUrl,
                      List<EventStreamQualityDefinition> hasEventStreamQualities,
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
    this.eventSchema = new EventSchema();
  }


  public SpDataStream(SpDataStream other) {
    super(other);
    this.index = other.getIndex();
    this.correspondingAdapterId = other.getCorrespondingAdapterId();
    if (other.getEventGrounding() != null) {
      this.eventGrounding = new EventGrounding(other.getEventGrounding());
    }
    if (other.getEventSchema() != null) {
      this.eventSchema = new EventSchema(other.getEventSchema());
    }
    if (other.getHasEventStreamQualities() != null) {
      this.hasEventStreamQualities = other.getHasEventStreamQualities().stream().map(EventStreamQualityDefinition::new)
          .collect(Collectors.toCollection(ArrayList::new));
    }
    if (other.getRequiresEventStreamQualities() != null) {
      this.requiresEventStreamQualities =
          other.getRequiresEventStreamQualities().stream().map(EventStreamQualityRequirement::new)
              .collect(Collectors.toCollection(ArrayList::new));
    }
    if (other.getMeasurementCapability() != null) {
      this.measurementCapability = new Cloner().mc(other.getMeasurementCapability());
    }
    if (other.getMeasurementObject() != null) {
      this.measurementObject = new Cloner().mo(other.getMeasurementObject());
    }
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

  public String getCorrespondingAdapterId() {
    return correspondingAdapterId;
  }

  public void setCorrespondingAdapterId(String correspondingAdapterId) {
    this.correspondingAdapterId = correspondingAdapterId;
  }
}
