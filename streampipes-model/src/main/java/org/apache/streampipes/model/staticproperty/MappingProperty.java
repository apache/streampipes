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

import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.ArrayList;
import java.util.List;

@JsonSubTypes({
    @JsonSubTypes.Type(MappingPropertyNary.class),
    @JsonSubTypes.Type(MappingPropertyUnary.class)
})
public abstract class MappingProperty extends StaticProperty {

  private static final long serialVersionUID = -7849999126274124847L;

  /**
   * The assigned requirementSelector links to the runtime name of a property requirement. The
   * internal id of the mapping property is assigned to the runtime name of the property
   * requirement as follows: r(streamIndex)::runtimeName
   * <p>
   * Example: The mapping property internal Id is number-mapping, and a property requirement
   * exists that declares the requirement for a number datatype. The processor has one input node.
   * <p>
   * The value of the requirementSelector will be r0::number-mapping.
   */
  private String requirementSelector;

  private List<String> mapsFromOptions;

  private String propertyScope;

  public MappingProperty() {
    super();
    this.mapsFromOptions = new ArrayList<>();
    this.requirementSelector = "";
  }

  public MappingProperty(StaticPropertyType type) {
    super(type);
    this.mapsFromOptions = new ArrayList<>();
    this.requirementSelector = "";
  }

  public MappingProperty(MappingProperty other) {
    super(other);
    this.requirementSelector = other.getRequirementSelector();
    this.propertyScope = other.getPropertyScope();
    this.mapsFromOptions = other.getMapsFromOptions();
  }

  protected MappingProperty(StaticPropertyType type, String requirementSelector, String
      internalName, String label, String description) {
    this(type, internalName, label, description);
    this.requirementSelector = requirementSelector;
  }

  protected MappingProperty(StaticPropertyType type, String internalName, String label, String description) {
    super(type, internalName, label, description);
    this.requirementSelector = "";
  }

  public String getRequirementSelector() {
    return requirementSelector;
  }

  public void setRequirementSelector(String requirementSelector) {
    this.requirementSelector = requirementSelector;
  }

  public List<String> getMapsFromOptions() {
    return mapsFromOptions;
  }

  public void setMapsFromOptions(List<String> mapsFromOptions) {
    this.mapsFromOptions = mapsFromOptions;
  }

  public String getPropertyScope() {
    return propertyScope;
  }

  public void setPropertyScope(String propertyScope) {
    this.propertyScope = propertyScope;
  }
}
