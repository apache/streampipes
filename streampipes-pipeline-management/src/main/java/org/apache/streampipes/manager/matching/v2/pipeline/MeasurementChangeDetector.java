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

package org.apache.streampipes.manager.matching.v2.pipeline;

import org.apache.streampipes.model.datalake.CriticalMeasurementFieldChange;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MeasurementChangeDetector {

  private MeasurementChangeDetector() {
  }

  public static List<CriticalMeasurementFieldChange> findCriticalMeasurementFieldChanges(
      EventSchema existingEventSchema,
      EventSchema updatedEventSchema) {
    var existingMeasurementFields = existingEventSchema
        .getEventProperties()
        .stream()
        .filter(MeasurementChangeDetector::isMeasurementField)
        .collect(Collectors.toMap(
            EventProperty::getRuntimeName,
            Function.identity(),
            (existingProperty, duplicateProperty) -> existingProperty
        ));

    return updatedEventSchema
        .getEventProperties()
        .stream()
        .filter(MeasurementChangeDetector::isMeasurementField)
        .map(updatedProperty -> {
          var existingProperty = existingMeasurementFields.get(updatedProperty.getRuntimeName());
          if (existingProperty != null && hasCriticalFieldTypeChange(existingProperty, updatedProperty)) {
            return Optional.of(new CriticalMeasurementFieldChange(
                updatedProperty.getRuntimeName(),
                toDisplayType(existingProperty),
                toDisplayType(updatedProperty)
            ));
          } else {
            return Optional.<CriticalMeasurementFieldChange>empty();
          }
        })
        .flatMap(Optional::stream)
        .toList();
  }

  private static boolean isMeasurementField(EventProperty eventProperty) {
    return !PropertyScope.DIMENSION_PROPERTY.name().equals(eventProperty.getPropertyScope());
  }

  private static boolean hasCriticalFieldTypeChange(EventProperty existingProperty,
                                                    EventProperty updatedProperty) {
    return toStorageType(existingProperty) != toStorageType(updatedProperty);
  }

  private static StorageType toStorageType(EventProperty eventProperty) {
    if (eventProperty instanceof EventPropertyPrimitive primitiveProperty) {
      return toPrimitiveStorageType(primitiveProperty.getRuntimeType());
    } else {
      return StorageType.STRING;
    }
  }

  private static StorageType toPrimitiveStorageType(String runtimeType) {
    if (XSD.INTEGER.toString().equals(runtimeType)
        || XSD.LONG.toString().equals(runtimeType)) {
      return StorageType.INTEGER;
    } else if (XSD.FLOAT.toString().equals(runtimeType)
        || XSD.DOUBLE.toString().equals(runtimeType)
        || SO.NUMBER.equals(runtimeType)) {
      return StorageType.FLOAT;
    } else if (XSD.BOOLEAN.toString().equals(runtimeType)) {
      return StorageType.BOOLEAN;
    } else {
      return StorageType.STRING;
    }
  }

  private static String toDisplayType(EventProperty eventProperty) {
    if (eventProperty instanceof EventPropertyPrimitive primitiveProperty) {
      return primitiveProperty.getRuntimeType();
    } else {
      return eventProperty.getClass().getSimpleName();
    }
  }
}
