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

package org.apache.streampipes.sinks.internal.jvm.datalake;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SO;

import java.util.List;

public class DataLakeDimensionProvider {

  public void applyOptions(List<EventProperty> inputFields,
                           RuntimeResolvableAnyStaticProperty staticProperty) {
    var primitiveFields = getPrimitiveFields(inputFields);
    primitiveFields
        .forEach(field -> addFieldIfNotExists(field, staticProperty.getOptions()));
    staticProperty.getOptions().removeIf(o -> !existsInFields(o, primitiveFields));
  }

  private List<EventPropertyPrimitive> getPrimitiveFields(List<EventProperty> inputFields) {
    return inputFields
        .stream()
        .filter(field -> field instanceof EventPropertyPrimitive)
        .filter(field -> satisfiesFilter((EventPropertyPrimitive) field))
        .map(field -> (EventPropertyPrimitive) field)
        .toList();
  }

  private boolean satisfiesFilter(EventPropertyPrimitive field) {
    return !field.getRuntimeType().equals(Datatypes.Float.toString())
        &&  !(SO.DATE_TIME.equalsIgnoreCase(field.getSemanticType()));
  }

  private void addFieldIfNotExists(EventPropertyPrimitive field,
                                   List<Option> options) {
    if (options.stream().noneMatch(o -> o.getName().equals(field.getRuntimeName()))) {
      if (field.getPropertyScope() != null) {
        options.add(new Option(
                field.getRuntimeName(),
                PropertyScope.valueOf(field.getPropertyScope()) == PropertyScope.DIMENSION_PROPERTY
            )
        );
      }
    }
  }

  private boolean existsInFields(Option o,
                                 List<EventPropertyPrimitive> primitiveFields) {
    return primitiveFields.stream().anyMatch(field -> field.getRuntimeName().equals(o.getName()));
  }
}
