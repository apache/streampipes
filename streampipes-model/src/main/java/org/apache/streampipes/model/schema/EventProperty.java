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

import org.apache.streampipes.model.util.ElementIdGenerator;
import org.apache.streampipes.model.util.ListUtils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonSubTypes({
    @JsonSubTypes.Type(EventPropertyList.class),
    @JsonSubTypes.Type(EventPropertyNested.class),
    @JsonSubTypes.Type(EventPropertyPrimitive.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class EventProperty {

  protected static final String PREFIX = "urn:streampipes.org:spi:";
  private static final long serialVersionUID = 7079045979946059387L;

  private String elementId;
  private String label;

  private String description;

  private String runtimeName;

  private boolean required;

  private List<URI> domainProperties;

  private String propertyScope;

  private int index = 0;

  private String runtimeId;


  public EventProperty() {
    this.elementId = ElementIdGenerator.makeElementId(EventProperty.class);
    this.domainProperties = new ArrayList<>();
  }

  public EventProperty(EventProperty other) {
    this.elementId = other.getElementId();
    this.label = other.getLabel();
    this.description = other.getDescription();
    this.required = other.isRequired();
    this.runtimeName = other.getRuntimeName();
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

  public EventProperty(String propertyName) {
    this();
    this.runtimeName = propertyName;
  }

  public static String getPrefix() {
    return PREFIX;
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

  public String getPropertyScope() {
    return propertyScope;
  }

  public void setPropertyScope(String propertyScope) {
    this.propertyScope = propertyScope;
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

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(elementId, label, description, runtimeName, required, domainProperties, propertyScope, index,
        runtimeId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventProperty that = (EventProperty) o;

    return required == that.required
           && index == that.index
           && Objects.equals(label, that.label)
           && Objects.equals(description, that.description)
           && Objects.equals(runtimeName, that.runtimeName)
           && Objects.equals(propertyScope, that.propertyScope)
           && Objects.equals(runtimeId, that.runtimeId)
           && ListUtils.isEqualList(this.domainProperties, that.domainProperties);
  }

  @Override
  public String toString() {
    return "EventProperty{"
           + "elementId='" + elementId + '\''
           + ", label='" + label + '\''
           + ", description='" + description + '\''
           + ", runtimeName='" + runtimeName + '\''
           + ", required=" + required
           + ", domainProperties=" + domainProperties
           + ", propertyScope='" + propertyScope + '\''
           + ", index=" + index
           + ", runtimeId='" + runtimeId + '\''
           + '}';
  }
}
