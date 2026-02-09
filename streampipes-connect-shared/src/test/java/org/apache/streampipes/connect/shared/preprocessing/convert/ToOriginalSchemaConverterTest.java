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
package org.apache.streampipes.connect.shared.preprocessing.convert;

import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ToOriginalSchemaConverter Tests")
class ToOriginalSchemaConverterTest {

  private EventSchema schema;
  private ToOriginalSchemaConverter converter;

  @BeforeEach
  void setUp() {
    schema = new EventSchema();
    converter = new ToOriginalSchemaConverter(schema);
  }

  @Test
  @DisplayName("Move top-level property to nested location")
  void testMoveTopLevelToNested() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    MoveRuleDescription rule = new MoveRuleDescription("temperature", "sensor.temperature");
    converter.visit(rule);

    // Check that property was moved
    assertFalse(propertyExistsAtRoot("temperature"));
    assertTrue(propertyExists("sensor.temperature"));
  }

  @Test
  @DisplayName("Move nested property to different nested location")
  void testMoveNestedProperty() {
    EventPropertyNested sensor = new EventPropertyNested("sensor");
    sensor.getEventProperties().add(new EventPropertyPrimitive("temperature"));
    schema.addEventProperty(sensor);

    MoveRuleDescription rule = new MoveRuleDescription("sensor.temperature", "readings.temperature");
    converter.visit(rule);

    assertTrue(propertyExists("readings.temperature"));
    assertFalse(propertyExists("sensor.temperature"));
  }

  @Test
  @DisplayName("Move property to deeply nested location")
  void testMoveToDeepNestedLocation() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    MoveRuleDescription rule = new MoveRuleDescription("temperature", "building.floor.room.temperature");
    converter.visit(rule);

    assertTrue(propertyExists("building.floor.room.temperature"));
    assertFalse(propertyExistsAtRoot("temperature"));
  }

  @Test
  @DisplayName("Skip move rule with null source property")
  void testMoveRuleWithNullSourceProperty() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));
    int initialSize = schema.getEventProperties().size();

    MoveRuleDescription rule = new MoveRuleDescription();
    rule.setOldRuntimeKey(null);
    rule.setNewRuntimeKey("sensor.temperature");

    // Should not throw, just skip
    assertDoesNotThrow(() -> converter.visit(rule));
    assertEquals(initialSize, schema.getEventProperties().size());
  }

  @Test
  @DisplayName("Skip move rule with null target property")
  void testMoveRuleWithNullTargetProperty() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));
    int initialSize = schema.getEventProperties().size();

    MoveRuleDescription rule = new MoveRuleDescription("temperature", null);

    assertDoesNotThrow(() -> converter.visit(rule));
    assertEquals(initialSize, schema.getEventProperties().size());
  }

  @Test
  @DisplayName("Skip move rule when source property doesn't exist")
  void testMoveRuleSourcePropertyDoesNotExist() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));
    int initialSize = schema.getEventProperties().size();

    MoveRuleDescription rule = new MoveRuleDescription("humidity", "sensor.humidity");

    assertDoesNotThrow(() -> converter.visit(rule));
    assertEquals(initialSize, schema.getEventProperties().size());
  }

  @Test
  @DisplayName("Skip move rule when target property already exists")
  void testMoveRuleTargetPropertyExists() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));
    EventPropertyNested sensor = new EventPropertyNested("sensor");
    sensor.getEventProperties().add(new EventPropertyPrimitive("temperature"));
    schema.addEventProperty(sensor);
    int initialSize = schema.getEventProperties().size();

    MoveRuleDescription rule = new MoveRuleDescription("temperature", "sensor.temperature");

    assertDoesNotThrow(() -> converter.visit(rule));
    
    // Should not have removed the original since target exists
    assertTrue(propertyExistsAtRoot("temperature"));
  }

  @Test
  @DisplayName("Handle null move rule")
  void testNullMoveRule() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    assertDoesNotThrow(() -> converter.visit((MoveRuleDescription) null));
  }

  @Test
  @DisplayName("Move multiple properties sequentially")
  void testMoveMultipleProperties() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));
    schema.addEventProperty(new EventPropertyPrimitive("humidity"));

    MoveRuleDescription rule1 = new MoveRuleDescription("temperature", "sensor.temperature");
    MoveRuleDescription rule2 = new MoveRuleDescription("humidity", "sensor.humidity");

    converter.visit(rule1);
    converter.visit(rule2);

    assertTrue(propertyExists("sensor.temperature"));
    assertTrue(propertyExists("sensor.humidity"));
    assertFalse(propertyExistsAtRoot("temperature"));
    assertFalse(propertyExistsAtRoot("humidity"));
  }

  @Test
  @DisplayName("Move property from nested to different nested location")
  void testMoveFromNestedToNested() {
    EventPropertyNested oldLocation = new EventPropertyNested("old");
    oldLocation.getEventProperties().add(new EventPropertyPrimitive("value"));
    schema.addEventProperty(oldLocation);

    MoveRuleDescription rule = new MoveRuleDescription("old.value", "new.value");
    converter.visit(rule);

    assertTrue(propertyExists("new.value"));
    assertFalse(propertyExists("old.value"));
  }

  @Test
  @DisplayName("Preserve property metadata after move")
  void testPreservePropertyMetadataAfterMove() {
    EventPropertyPrimitive originalProp = new EventPropertyPrimitive("temperature");
    originalProp.setLabel("Temperature Sensor");
    originalProp.setDescription("Temperature reading in Celsius");
    originalProp.setSemanticType("http://example.com/temperature");
    schema.addEventProperty(originalProp);

    MoveRuleDescription rule = new MoveRuleDescription("temperature", "sensor.temperature");
    converter.visit(rule);

    // Find the moved property and verify metadata
    assertTrue(propertyExists("sensor.temperature"));
  }

  @Test
  @DisplayName("Move nested structure with multiple child properties")
  void testMoveNestedStructureWithMultipleChildren() {
    EventPropertyNested location = new EventPropertyNested("location");
    location.getEventProperties().add(new EventPropertyPrimitive("latitude"));
    location.getEventProperties().add(new EventPropertyPrimitive("longitude"));
    schema.addEventProperty(location);

    // Move the parent, should move both children
    MoveRuleDescription rule = new MoveRuleDescription("location", "gps.location");
    converter.visit(rule);

    assertTrue(propertyExists("gps.location"));
  }

  @Test
  @DisplayName("Handle consecutive moves of same property")
  void testConsecutiveMovesOfSameProperty() {
    schema.addEventProperty(new EventPropertyPrimitive("value"));

    MoveRuleDescription rule1 = new MoveRuleDescription("value", "sensor.value");
    converter.visit(rule1);
    
    assertTrue(propertyExists("sensor.value"));
    assertFalse(propertyExistsAtRoot("value"));

    // Second move on the same property
    MoveRuleDescription rule2 = new MoveRuleDescription("sensor.value", "readings.sensor.value");
    converter.visit(rule2);

    assertTrue(propertyExists("readings.sensor.value"));
    assertFalse(propertyExists("sensor.value"));
  }

  @Test
  @DisplayName("Move property with special characters in name")
  void testMovePropertyWithSpecialCharacters() {
    EventPropertyPrimitive prop = new EventPropertyPrimitive("temperature-sensor");
    schema.addEventProperty(prop);

    MoveRuleDescription rule = new MoveRuleDescription("temperature-sensor", "sensor.temperature-sensor");
    converter.visit(rule);

    assertTrue(propertyExists("sensor.temperature-sensor"));
  }

  @Test
  @DisplayName("Empty move rule with empty strings")
  void testMoveRuleWithEmptyStrings() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));
    int initialSize = schema.getEventProperties().size();

    MoveRuleDescription rule = new MoveRuleDescription("", "");

    assertDoesNotThrow(() -> converter.visit(rule));
    assertEquals(initialSize, schema.getEventProperties().size());
  }

  // Helper methods
  private boolean propertyExistsAtRoot(String propertyName) {
    return schema.getEventProperties().stream()
        .anyMatch(p -> p.getRuntimeName().equals(propertyName));
  }

  private boolean propertyExists(String propertyPath) {
    String[] parts = propertyPath.split("\\.");
    return navigatePropertyPath(schema.getEventProperties(), parts, 0);
  }

  private boolean navigatePropertyPath(java.util.List<org.apache.streampipes.model.schema.EventProperty> properties,
      String[] pathParts, int depth) {
    if (properties == null) {
      return false;
    }

    if (depth >= pathParts.length) {
      return true;
    }

    for (org.apache.streampipes.model.schema.EventProperty prop : properties) {
      if (prop.getRuntimeName().equals(pathParts[depth])) {
        if (depth == pathParts.length - 1) {
          return true;
        }
        if (prop instanceof EventPropertyNested) {
          return navigatePropertyPath(
              ((EventPropertyNested) prop).getEventProperties(),
              pathParts,
              depth + 1
          );
        }
      }
    }
    return false;
  }
}
