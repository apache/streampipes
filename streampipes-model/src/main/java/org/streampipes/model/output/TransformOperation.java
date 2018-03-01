/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.TRANSFORM_OPERATION)
@Entity
public class TransformOperation extends UnnamedStreamPipesEntity{

  /**
   * the internal name of the mapping property that linkes to an event property related to this transform operation
   */
  @RdfProperty(StreamPipes.HAS_LINKED_MAPPING_PROPERTY_ID)
  private String mappingPropertyInternalName;

  /**
   * the static property which value is used to replace the schema
   */
  @RdfProperty(StreamPipes.HAS_SOURCE_PROPERTY_INTERNAL_NAME)
  private String sourceStaticProperty;

  /**
   * the URI of the affected schema part
   */
  @RdfProperty(StreamPipes.HAS_TRANSFORMATION_SCOPE)
  private String transformationScope;

  /**
   * the static target value if no static property is given
   */
  @RdfProperty(StreamPipes.HAS_TARGET_VALUE)
  private String targetValue;

  public TransformOperation() {
    super();
  }

  public TransformOperation(String transformationScope, String mappingPropertyInternalName) {
    this.transformationScope = transformationScope;
    this.mappingPropertyInternalName = mappingPropertyInternalName;
  }

  public TransformOperation(TransformOperation other) {
    super(other);
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
