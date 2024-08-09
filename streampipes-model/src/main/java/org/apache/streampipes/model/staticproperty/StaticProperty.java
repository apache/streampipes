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

package org.apache.streampipes.model.staticproperty;

import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

@JsonSubTypes({
    @JsonSubTypes.Type(AnyStaticProperty.class),
    @JsonSubTypes.Type(CodeInputStaticProperty.class),
    @JsonSubTypes.Type(CollectionStaticProperty.class),
    @JsonSubTypes.Type(ColorPickerStaticProperty.class),
    @JsonSubTypes.Type(FileStaticProperty.class),
    @JsonSubTypes.Type(FreeTextStaticProperty.class),
    @JsonSubTypes.Type(MappingPropertyUnary.class),
    @JsonSubTypes.Type(MappingPropertyNary.class),
    @JsonSubTypes.Type(MatchingStaticProperty.class),
    @JsonSubTypes.Type(OneOfStaticProperty.class),
    @JsonSubTypes.Type(RuntimeResolvableAnyStaticProperty.class),
    @JsonSubTypes.Type(RuntimeResolvableOneOfStaticProperty.class),
    @JsonSubTypes.Type(RuntimeResolvableTreeInputStaticProperty.class),
    @JsonSubTypes.Type(SecretStaticProperty.class),
    @JsonSubTypes.Type(StaticPropertyAlternative.class),
    @JsonSubTypes.Type(StaticPropertyAlternatives.class),
    @JsonSubTypes.Type(StaticPropertyGroup.class),
    @JsonSubTypes.Type(SlideToggleStaticProperty.class),
    @JsonSubTypes.Type(RuntimeResolvableGroupStaticProperty.class)
})
@TsModel
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class StaticProperty {

  protected boolean optional;
  protected StaticPropertyType staticPropertyType;
  private String label;
  private String description;
  private String internalName;
  private boolean predefined;


  public StaticProperty() {
    super();
  }

  public StaticProperty(StaticPropertyType type) {
    super();
    this.staticPropertyType = type;
    this.predefined = false;
  }

  public StaticProperty(StaticProperty other) {
    this.description = other.getDescription();
    this.internalName = other.getInternalName();
    this.optional = other.isOptional();
    this.staticPropertyType = other.getStaticPropertyType();
    this.label = other.getLabel();
  }

  public StaticProperty(StaticPropertyType type, String internalName, String label,
                        String description) {
    super();
    this.staticPropertyType = type;
    this.internalName = internalName;
    this.label = label;
    this.description = description;
    this.predefined = false;
  }

  public String getInternalName() {
    return internalName;
  }

  public void setInternalName(String name) {
    this.internalName = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isOptional() {
    return optional;
  }

  public void setOptional(boolean optional) {
    this.optional = optional;
  }

  public StaticPropertyType getStaticPropertyType() {
    return staticPropertyType;
  }

  public void setStaticPropertyType(StaticPropertyType staticPropertyType) {
    this.staticPropertyType = staticPropertyType;
  }

  public <T extends StaticProperty> T as(Class<T> targetClass) {
    return targetClass.cast(this);
  }

  public abstract void accept(StaticPropertyVisitor visitor);

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StaticProperty that)) {
      return false;
    }

    if (optional != that.optional) {
      return false;
    }
    if (predefined != that.predefined) {
      return false;
    }
    if (staticPropertyType != that.staticPropertyType) {
      return false;
    }
    if (!Objects.equals(label, that.label)) {
      return false;
    }
    if (!Objects.equals(description, that.description)) {
      return false;
    }
    return Objects.equals(internalName, that.internalName);
  }

  @Override
  public int hashCode() {
    int result = (optional ? 1 : 0);
    result = 31 * result + (staticPropertyType != null ? staticPropertyType.hashCode() : 0);
    result = 31 * result + (label != null ? label.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (internalName != null ? internalName.hashCode() : 0);
    result = 31 * result + (predefined ? 1 : 0);
    return result;
  }
}
