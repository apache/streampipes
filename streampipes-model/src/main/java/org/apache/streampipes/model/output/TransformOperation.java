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
package org.apache.streampipes.model.output;

public class TransformOperation {

  /**
   * the internal name of the mapping property that linkes to an event property related to this transform operation
   */
  private String mappingPropertyInternalName;

  /**
   * the static property which value is used to replace the schema
   */
  private String sourceStaticProperty;

  /**
   * the URI of the affected schema part
   */
  private String transformationScope;

  /**
   * the static target value if no static property is given
   */
  private String targetValue;

  public TransformOperation() {
    super();
  }

  public TransformOperation(String transformationScope, String mappingPropertyInternalName) {
    this.transformationScope = transformationScope;
    this.mappingPropertyInternalName = mappingPropertyInternalName;
  }

  public TransformOperation(TransformOperation other) {
    this.mappingPropertyInternalName = other.getMappingPropertyInternalName();
    this.sourceStaticProperty = other.getSourceStaticProperty();
    this.transformationScope = other.getTransformationScope();
    this.targetValue = other.getTargetValue();
  }

  public String getMappingPropertyInternalName() {
    return mappingPropertyInternalName;
  }

  public void setMappingPropertyInternalName(String mappingPropertyInternalName) {
    this.mappingPropertyInternalName = mappingPropertyInternalName;
  }

  public String getSourceStaticProperty() {
    return sourceStaticProperty;
  }

  public void setSourceStaticProperty(String sourceStaticProperty) {
    this.sourceStaticProperty = sourceStaticProperty;
  }

  public String getTransformationScope() {
    return transformationScope;
  }

  public void setTransformationScope(String transformationScope) {
    this.transformationScope = transformationScope;
  }

  public String getTargetValue() {
    return targetValue;
  }

  public void setTargetValue(String targetValue) {
    this.targetValue = targetValue;
  }
}
