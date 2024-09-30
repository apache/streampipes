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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyHandler {

  private static final Logger LOG = LoggerFactory.getLogger(PropertyHandler.class);

  private final RawFieldSerializer rawFieldSerializer;
  private final PropertyDuplicateFilter duplicateFilter;


  public PropertyHandler(PropertyDuplicateFilter duplicateFilter) {
    rawFieldSerializer = new RawFieldSerializer();
    this.duplicateFilter = duplicateFilter;
  }

  /**
   * Takes properties of type primitive and stores them as tags or fields to the given point
   */
  public void handlePrimitiveProperty(
      Point.Builder point,
      EventPropertyPrimitive eventPropertyPrimitive,
      PrimitiveField primitiveField,
      String sanitizedRuntimeName
  ) {
    if (primitiveField.getRawValue() != null) {
      // store property as tag when the field is a dimension property
      if (PropertyScope.DIMENSION_PROPERTY.name()
                                          .equals(eventPropertyPrimitive.getPropertyScope())) {
        handleDimensionProperty(
            point,
            primitiveField,
            sanitizedRuntimeName
        );
      } else {
        handleMeasurementProperty(
            point,
            eventPropertyPrimitive.getRuntimeType(),
            sanitizedRuntimeName,
            primitiveField
        );
      }
    }
  }


  /**
   * Takes non-primitive properties of and stores them as strings to the given point
   * Since InfluxDB can't store non-primitive types, store them as string
   * and deserialize later in downstream processes
   */
  public void handleNonPrimitiveProperty(
      Point.Builder p,
      Event event,
      String preparedRuntimeName
  ) {
    try {
      var json = rawFieldSerializer.serialize(event.getRaw()
                                                   .get(preparedRuntimeName));
      p.addField(preparedRuntimeName, json);
    } catch (SpRuntimeException e) {
      LOG.warn("Failed to serialize field {}, ignoring.", preparedRuntimeName);
    }
  }

  /**
   * Add dimension properties as tags to point
   */
  private void handleDimensionProperty(
      Point.Builder point,
      PrimitiveField primitiveField,
      String sanitizedRuntimeName
  ) {
    point.tag(sanitizedRuntimeName, primitiveField.getAsString());
  }

  /**
   * Transforms the given primitive property to the corresponding InfluxDB data type and adds it to the point
   */
  private void handleMeasurementProperty(
      Point.Builder p,
      String runtimeType,
      String sanitizedRuntimeName,
      PrimitiveField eventPropertyPrimitiveField
  ) {

    if (!duplicateFilter.shouldIgnoreField(sanitizedRuntimeName, eventPropertyPrimitiveField)) {
      // Store property according to property type
      if (XSD.INTEGER.toString()
          .equals(runtimeType)) {
        handleIntegerProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
      } else if (XSD.LONG.toString()
          .equals(runtimeType)) {
        handleLongProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
      } else if (XSD.FLOAT.toString()
          .equals(runtimeType)) {
        handleFloatProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
      } else if (XSD.DOUBLE.toString()
          .equals(runtimeType)) {
        handleDoubleProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
      } else if (XSD.BOOLEAN.toString()
          .equals(runtimeType)) {
        handleBooleanProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
      } else if (SO.NUMBER.equals(runtimeType)) {
        handleDoubleProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
      } else {
        handleStringProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
      }
    }
  }

  private void handleStringProperty(
      Point.Builder p,
      String sanitizedRuntimeName,
      PrimitiveField eventPropertyPrimitiveField
  ) {
    p.addField(sanitizedRuntimeName, eventPropertyPrimitiveField.getAsString());
  }

  private void handleBooleanProperty(
      Point.Builder p,
      String sanitizedRuntimeName,
      PrimitiveField eventPropertyPrimitiveField
  ) {
    p.addField(sanitizedRuntimeName, eventPropertyPrimitiveField.getAsBoolean());
  }

  private void handleDoubleProperty(
      Point.Builder p,
      String sanitizedRuntimeName,
      PrimitiveField eventPropertyPrimitiveField
  ) {
    p.addField(sanitizedRuntimeName, eventPropertyPrimitiveField.getAsDouble());
  }

  private void handleFloatProperty(
      Point.Builder p,
      String sanitizedRuntimeName,
      PrimitiveField eventPropertyPrimitiveField
  ) {
    p.addField(sanitizedRuntimeName, eventPropertyPrimitiveField.getAsFloat());
  }

  private void handleLongProperty(
      Point.Builder p,
      String sanitizedRuntimeName,
      PrimitiveField eventPropertyPrimitiveField
  ) {
    try {
      p.addField(sanitizedRuntimeName, eventPropertyPrimitiveField.getAsLong());
    } catch (NumberFormatException ef) {
      handleFloatProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
    }
  }

  private void handleIntegerProperty(
      Point.Builder p,
      String sanitizedRuntimeName,
      PrimitiveField eventPropertyPrimitiveField
  ) {
    try {
      p.addField(sanitizedRuntimeName, eventPropertyPrimitiveField.getAsInt());
    } catch (NumberFormatException ef) {
      handleFloatProperty(p, sanitizedRuntimeName, eventPropertyPrimitiveField);
    }
  }

}
