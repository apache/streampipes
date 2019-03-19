/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.schema;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.quality.EventPropertyQualityRequirement;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.RDFS;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.EVENT_PROPERTY)
@MappedSuperclass
@Entity
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

  private String runtimeId;

  public EventProperty() {
    super(prefix + UUID.randomUUID().toString());
    this.requiresEventPropertyQualities = new ArrayList<>();
    this.eventPropertyQualities = new ArrayList<>();
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
}
