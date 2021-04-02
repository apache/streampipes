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

package org.apache.streampipes.model.schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.quality.EventPropertyQualityDefinition;
import org.apache.streampipes.model.quality.EventPropertyQualityRequirement;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.RDFS;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RdfsClass(StreamPipes.EVENT_PROPERTY)
@MappedSuperclass
@Entity
@JsonSubTypes({
        @JsonSubTypes.Type(EventPropertyList.class),
        @JsonSubTypes.Type(EventPropertyNested.class),
        @JsonSubTypes.Type(EventPropertyPrimitive.class)
})
public abstract class EventProperty extends UnnamedStreamPipesEntity {

  private static final long serialVersionUID = 7079045979946059387L;

  protected static final String prefix = "urn:streampipes.org:spi:";

  @RdfProperty(RDFS.LABEL)
  private String label;

  @RdfProperty(RDFS.DESCRIPTION)
  private String description;

  @RdfProperty(StreamPipes.HAS_RUNTIME_NAME)
  private String runtimeName;

  @RdfProperty(StreamPipes.REQUIRED)
  private boolean required;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.DOMAIN_PROPERTY)
  private List<URI> domainProperties;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_EVENT_PROPERTY_QUALITY_DEFINITION)
  private List<EventPropertyQualityDefinition> eventPropertyQualities;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_EVENT_PROPERTY_QUALITY_REQUIREMENT)
  private List<EventPropertyQualityRequirement> requiresEventPropertyQualities;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_PROPERTY_SCOPE)
  private String propertyScope;

  @RdfProperty(StreamPipes.INDEX)
  private int index = 0;

  private String runtimeId;

  private boolean equateLists(List<?> list1, List<?> list2) {
    list1.forEach(listProperty1 -> {
      list2.forEach(listProperty2 -> {
        if (listProperty1.equals(listProperty2)) {
          list1.remove(listProperty1);
        }
      });
    });
    return list1.size() == 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EventProperty that = (EventProperty) o;
    if (domainProperties.size() != that.domainProperties.size())
      return false;
    if (eventPropertyQualities.size() != that.eventPropertyQualities.size())
      return false;
    if (requiresEventPropertyQualities.size() != that.requiresEventPropertyQualities.size())
      return false;


    List<URI> thatCopyDomainProperties = new ArrayList<>(that.domainProperties);
    List<EventPropertyQualityDefinition> thatCopyEventPropertyQualities = new ArrayList<>(that.eventPropertyQualities);
    List<EventPropertyQualityRequirement> thatCopyRequiresEventPropertyQualities = new ArrayList<>(that.requiresEventPropertyQualities);


    return required == that.required &&
            index == that.index &&
            Objects.equals(label, that.label) &&
            Objects.equals(description, that.description) &&
            Objects.equals(runtimeName, that.runtimeName) &&
            Objects.equals(propertyScope, that.propertyScope) &&
            Objects.equals(runtimeId, that.runtimeId) &&
            equateLists(thatCopyDomainProperties, this.domainProperties) &&
            equateLists(thatCopyEventPropertyQualities, this.eventPropertyQualities) &&
            equateLists(thatCopyRequiresEventPropertyQualities, this.requiresEventPropertyQualities);

  }

  public EventProperty() {
    super(prefix + UUID.randomUUID().toString());
    this.requiresEventPropertyQualities = new ArrayList<>();
    this.eventPropertyQualities = new ArrayList<>();
    this.domainProperties = new ArrayList<>();
  }

  public EventProperty(EventProperty other) {
    super(other);
    this.label = other.getLabel();
    this.description = other.getDescription();
    this.required = other.isRequired();
    if (other.getRequiresEventPropertyQualities() != null) {
      this.requiresEventPropertyQualities = new Cloner()
              .reqEpQualitities(other
                      .getRequiresEventPropertyQualities());
    }
    this.runtimeName = other.getRuntimeName();
    if (other.getEventPropertyQualities() != null) {
      this.eventPropertyQualities = new Cloner().provEpQualities(other
              .getEventPropertyQualities());
    }
    this.domainProperties = other.getDomainProperties();
    this.propertyScope = other.getPropertyScope();
    this.runtimeId = other.getRuntimeId();
    this.index = other.getIndex();
  }

  public EventProperty(List<URI> subClassOf) {
    this();
    this.domainProperties = subClassOf;
  }

  public EventProperty(String propertyName, List<URI> subClassOf) {
    this();
    this.runtimeName = propertyName;
    this.domainProperties = subClassOf;
  }

  public EventProperty(String propertyName, List<URI> subClassOf, List<EventPropertyQualityDefinition> eventPropertyQualities) {
    this();
    this.runtimeName = propertyName;
    this.domainProperties = subClassOf;
    this.eventPropertyQualities = eventPropertyQualities;
  }

  public EventProperty(String propertyName) {
    this();
    this.runtimeName = propertyName;
  }


  public List<EventPropertyQualityRequirement> getRequiresEventPropertyQualities() {
    return requiresEventPropertyQualities;
  }

  public void setRequiresEventPropertyQualities(
          List<EventPropertyQualityRequirement> requiresEventPropertyQualities) {
    this.requiresEventPropertyQualities = requiresEventPropertyQualities;
  }

  public String getRuntimeName() {
    return runtimeName;
  }

  public void setRuntimeName(String propertyName) {
    this.runtimeName = propertyName;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public List<URI> getDomainProperties() {
    return domainProperties;
  }

  public void setDomainProperties(List<URI> subClassOf) {
    this.domainProperties = subClassOf;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String humanReadableTitle) {
    this.label = humanReadableTitle;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String humanReadableDescription) {
    this.description = humanReadableDescription;
  }

  public List<EventPropertyQualityDefinition> getEventPropertyQualities() {
    return eventPropertyQualities;
  }

  public void setEventPropertyQualities(
          List<EventPropertyQualityDefinition> eventPropertyQualities) {
    this.eventPropertyQualities = eventPropertyQualities;
  }

  public String getPropertyScope() {
    return propertyScope;
  }

  public void setPropertyScope(String propertyScope) {
    this.propertyScope = propertyScope;
  }

  public static String getPrefix() {
    return prefix;
  }

  public String getRuntimeId() {
    return runtimeId;
  }

  public void setRuntimeId(String runtimeId) {
    this.runtimeId = runtimeId;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
}
