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
package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.model.output.TransformOperation;
import org.apache.streampipes.model.output.TransformOperationType;
import org.apache.streampipes.sdk.utils.Datatypes;

public class TransformOperations {

  public static TransformOperation staticDatatypeTransformation(String mappingPropertyInternalName,
                                                                Datatypes targetDatatype) {
    return staticTransformOperation(TransformOperationType.DATATYPE_TRANSFORMATION, mappingPropertyInternalName,
        targetDatatype.toString());
  }

  public static TransformOperation dynamicDatatypeTransformation(String mappingPropertyInternalName, String
      linkedStaticProperty) {
    return dynamicTransformOperation(TransformOperationType.DATATYPE_TRANSFORMATION, mappingPropertyInternalName,
        linkedStaticProperty);
  }

  public static TransformOperation staticDomainPropertyTransformation(String mappingPropertyInternalName, String
      targetValue) {
    return staticTransformOperation(TransformOperationType.DOMAIN_PROPERTY_TRANSFORMATION, mappingPropertyInternalName,
        targetValue);
  }

  public static TransformOperation dynamicDomainPropertyTransformation(String mappingPropertyInternalName, String
      linkedStaticProperty) {
    return dynamicTransformOperation(TransformOperationType.DOMAIN_PROPERTY_TRANSFORMATION, mappingPropertyInternalName,
        linkedStaticProperty);
  }

  public static TransformOperation staticRuntimeNameTransformation(String mappingPropertyInternalName, String
      targetValue) {
    return staticTransformOperation(TransformOperationType.RUNTIME_NAME_TRANSFORMATION, mappingPropertyInternalName,
        targetValue);
  }

  public static TransformOperation dynamicRuntimeNameTransformation(String mappingPropertyInternalName, String
      linkedStaticProperty) {
    return dynamicTransformOperation(TransformOperationType.RUNTIME_NAME_TRANSFORMATION, mappingPropertyInternalName,
        linkedStaticProperty);
  }

  public static TransformOperation staticMeasurementUnitTransformation(String mappingPropertyInternalName, String
      targetValue) {
    return staticTransformOperation(TransformOperationType.MEASUREMENT_UNIT_TRANSFORMATION
        , mappingPropertyInternalName, targetValue);
  }

  public static TransformOperation dynamicMeasurementUnitTransformation(String
                                                                            mappingPropertyInternalName,
                                                                        String sourceStaticPropertyInternalName) {
    return dynamicTransformOperation(TransformOperationType.MEASUREMENT_UNIT_TRANSFORMATION,
        mappingPropertyInternalName, sourceStaticPropertyInternalName);
  }


  private static TransformOperation staticTransformOperation(TransformOperationType transformationScope, String
      mappingPropertyInternalName, String targetValue) {
    TransformOperation to = prepareTransformOperation(transformationScope.name()
        , mappingPropertyInternalName);
    to.setTargetValue(targetValue);
    return to;
  }

  private static TransformOperation dynamicTransformOperation(TransformOperationType transformationScope, String
      mappingPropertyInternalName, String sourceStaticPropertyInternalName) {
    TransformOperation to = prepareTransformOperation(transformationScope.name()
        , mappingPropertyInternalName);
    to.setSourceStaticProperty(sourceStaticPropertyInternalName);
    return to;
  }

  private static TransformOperation prepareTransformOperation(String transformationScope, String
      mappingPropertyInternalName) {

    return new TransformOperation(transformationScope, mappingPropertyInternalName);

  }
}
