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

import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementChangeDetectorTest {

  @Test
  void findCriticalMeasurementFieldChanges_ShouldReturnChangedNamesAndTypes() {
    var existingSchema = makeSchema(
        makeMeasurementProperty("temperature", XSD.INTEGER),
        makeMeasurementProperty("pressure", XSD.FLOAT)
    );
    var updatedSchema = makeSchema(
        makeMeasurementProperty("temperature", XSD.STRING),
        makeMeasurementProperty("pressure", XSD.BOOLEAN)
    );

    var result = MeasurementChangeDetector.findCriticalMeasurementFieldChanges(existingSchema, updatedSchema);

    assertEquals(2, result.size());
    assertEquals("temperature", result.get(0).runtimeName());
    assertEquals(XSD.INTEGER.toString(), result.get(0).existingType());
    assertEquals(XSD.STRING.toString(), result.get(0).updatedType());
    assertEquals("pressure", result.get(1).runtimeName());
    assertEquals(XSD.FLOAT.toString(), result.get(1).existingType());
    assertEquals(XSD.BOOLEAN.toString(), result.get(1).updatedType());
  }

  @Test
  void findCriticalMeasurementFieldChanges_ShouldIgnoreIntegerToLongChange() {
    var existingSchema = makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER));
    var updatedSchema = makeSchema(makeMeasurementProperty("temperature", XSD.LONG));

    var result = MeasurementChangeDetector.findCriticalMeasurementFieldChanges(existingSchema, updatedSchema);

    assertTrue(result.isEmpty());
  }

  @Test
  void findCriticalMeasurementFieldChanges_ShouldIgnoreFloatToDoubleChange() {
    var existingSchema = makeSchema(makeMeasurementProperty("temperature", XSD.FLOAT));
    var updatedSchema = makeSchema(makeMeasurementProperty("temperature", XSD.DOUBLE));

    var result = MeasurementChangeDetector.findCriticalMeasurementFieldChanges(existingSchema, updatedSchema);

    assertTrue(result.isEmpty());
  }

  @Test
  void findCriticalMeasurementFieldChanges_ShouldIgnoreAddedAndRemovedFields() {
    var existingSchema = makeSchema(
        makeMeasurementProperty("temperature", XSD.INTEGER),
        makeMeasurementProperty("pressure", XSD.DOUBLE)
    );
    var updatedSchema = makeSchema(
        makeMeasurementProperty("temperature", XSD.INTEGER),
        makeMeasurementProperty("humidity", XSD.DOUBLE)
    );

    var result = MeasurementChangeDetector.findCriticalMeasurementFieldChanges(existingSchema, updatedSchema);

    assertTrue(result.isEmpty());
  }

  @Test
  void findCriticalMeasurementFieldChanges_ShouldIgnoreDimensionFieldChanges() {
    var existingSchema = makeSchema(makeDimensionProperty("machineId", XSD.INTEGER));
    var updatedSchema = makeSchema(makeDimensionProperty("machineId", XSD.STRING));

    var result = MeasurementChangeDetector.findCriticalMeasurementFieldChanges(existingSchema, updatedSchema);

    assertTrue(result.isEmpty());
  }

  private EventSchema makeSchema(EventPropertyPrimitive... properties) {
    return new EventSchema(List.of(properties));
  }

  private EventPropertyPrimitive makeMeasurementProperty(String runtimeName,
                                                        URI runtimeType) {
    var property = new EventPropertyPrimitive(runtimeType.toString(), runtimeName, "", "");
    property.setPropertyScope(PropertyScope.MEASUREMENT_PROPERTY.name());
    return property;
  }

  private EventPropertyPrimitive makeDimensionProperty(String runtimeName,
                                                      URI runtimeType) {
    var property = new EventPropertyPrimitive(runtimeType.toString(), runtimeName, "", "");
    property.setPropertyScope(PropertyScope.DIMENSION_PROPERTY.name());
    return property;
  }
}
