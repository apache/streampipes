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
package org.apache.streampipes.model.connect.rules.value;

import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.TransformationRulePriority;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.XSD;

public class AddValueTransformationRuleDescription extends ValueTransformationRuleDescription {

  private String runtimeKey;

  private String staticValue;
  private String datatype = XSD.STRING.toString();
  private String semanticType;
  private String measurementUnit;
  private String label;
  private String description;
  private PropertyScope propertyScope = PropertyScope.MEASUREMENT_PROPERTY;

  public AddValueTransformationRuleDescription() {
    super();
  }

  public AddValueTransformationRuleDescription(AddValueTransformationRuleDescription other) {
    super(other);
    this.runtimeKey = other.getRuntimeKey();
    this.staticValue = other.getStaticValue();
  }

  public String getRuntimeKey() {
    return runtimeKey;
  }

  public void setRuntimeKey(String runtimeKey) {
    this.runtimeKey = runtimeKey;
  }

  public String getStaticValue() {
    return staticValue;
  }

  public void setStaticValue(String staticValue) {
    this.staticValue = staticValue;
  }

  public String getDatatype() {
    return datatype;
  }

  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  public String getSemanticType() {
    return semanticType;
  }

  public void setSemanticType(String semanticType) {
    this.semanticType = semanticType;
  }

  public String getMeasurementUnit() {
    return measurementUnit;
  }

  public void setMeasurementUnit(String measurementUnit) {
    this.measurementUnit = measurementUnit;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PropertyScope getPropertyScope() {
    return propertyScope;
  }

  public void setPropertyScope(PropertyScope propertyScope) {
    this.propertyScope = propertyScope;
  }

  @Override
  public void accept(ITransformationRuleVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int getRulePriority() {
    return TransformationRulePriority.ADD_VALUE.getCode();
  }
}
