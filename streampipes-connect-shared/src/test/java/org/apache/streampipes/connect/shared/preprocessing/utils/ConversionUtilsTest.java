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
package org.apache.streampipes.connect.shared.preprocessing.utils;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConversionUtils Tests")
class ConversionUtilsTest {

  private EventSchema schema;

  @BeforeEach
  void setUp() {
    schema = new EventSchema();
  }

  @Test
  @DisplayName("Find top-level primitive property")
  void testFindTopLevelProperty() {
    EventPropertyPrimitive prop = new EventPropertyPrimitive("temperature");
    schema.addEventProperty(prop);

    EventProperty found = ConversionUtils.findProperty(schema, "temperature");
    assertNotNull(found);
    assertEquals("temperature", found.getRuntimeName());
  }

  @Test
  @DisplayName("Find nested single-level property")
  void testFindSingleLevelNestedProperty() {
    EventPropertyNested nested = new EventPropertyNested("address");
    nested.getEventProperties().add(new EventPropertyPrimitive("street"));
    schema.addEventProperty(nested);

    EventProperty found = ConversionUtils.findProperty(schema, "address.street");
    assertNotNull(found);
    assertEquals("street", found.getRuntimeName());
  }

  @Test
  @DisplayName("Find deeply nested property (3 levels)")
  void testFindDeeplyNestedProperty() {
    EventPropertyNested user = new EventPropertyNested("user");
    EventPropertyNested address = new EventPropertyNested("address");
    address.getEventProperties().add(new EventPropertyPrimitive("city"));
    user.getEventProperties().add(address);
    schema.addEventProperty(user);

    EventProperty found = ConversionUtils.findProperty(schema, "user.address.city");
    assertNotNull(found);
    assertEquals("city", found.getRuntimeName());
  }

  @Test
  @DisplayName("Throw exception for null property path")
  void testFindPropertyWithNullPath() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    assertThrows(IllegalArgumentException.class, () -> {
      ConversionUtils.findProperty(schema, null);
    });
  }

  @Test
  @DisplayName("Throw exception for empty property path")
  void testFindPropertyWithEmptyPath() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    assertThrows(IllegalArgumentException.class, () -> {
      ConversionUtils.findProperty(schema, "");
    });
  }

  @Test
  @DisplayName("Throw exception when property not found")
  void testFindNonExistentProperty() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    assertThrows(IllegalArgumentException.class, () -> {
      ConversionUtils.findProperty(schema, "humidity");
    });
  }

  @Test
  @DisplayName("Throw exception when nested property path doesn't exist")
  void testFindNonExistentNestedProperty() {
    EventPropertyNested nested = new EventPropertyNested("address");
    nested.getEventProperties().add(new EventPropertyPrimitive("street"));
    schema.addEventProperty(nested);

    assertThrows(IllegalArgumentException.class, () -> {
      ConversionUtils.findProperty(schema, "address.city");
    });
  }

  @Test
  @DisplayName("Throw exception when intermediate property is not nested")
  void testFindPropertyThroughNonNestedProperty() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    assertThrows(IllegalArgumentException.class, () -> {
      ConversionUtils.findProperty(schema, "temperature.nested");
    });
  }

  @Test
  @DisplayName("Check property exists - true case")
  void testPropertyExistsTrue() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    assertTrue(ConversionUtils.propertyExists(schema, "temperature"));
  }

  @Test
  @DisplayName("Check nested property exists - true case")
  void testNestedPropertyExistsTrue() {
    EventPropertyNested nested = new EventPropertyNested("address");
    nested.getEventProperties().add(new EventPropertyPrimitive("street"));
    schema.addEventProperty(nested);

    assertTrue(ConversionUtils.propertyExists(schema, "address.street"));
  }

  @Test
  @DisplayName("Check property exists - false case")
  void testPropertyExistsFalse() {
    schema.addEventProperty(new EventPropertyPrimitive("temperature"));

    assertFalse(ConversionUtils.propertyExists(schema, "humidity"));
  }

  @Test
  @DisplayName("Handle deeply nested properties (4+ levels)")
  void testDeeplyNestedPropertiesMultipleLevels() {
    EventPropertyNested root = new EventPropertyNested("root");
    EventPropertyNested level2 = new EventPropertyNested("level2");
    EventPropertyNested level3 = new EventPropertyNested("level3");
    EventPropertyNested level4 = new EventPropertyNested("level4");
    level4.getEventProperties().add(new EventPropertyPrimitive("value"));
    level3.getEventProperties().add(level4);
    level2.getEventProperties().add(level3);
    root.getEventProperties().add(level2);
    schema.addEventProperty(root);

    EventProperty found = ConversionUtils.findProperty(schema, "root.level2.level3.level4.value");
    assertNotNull(found);
    assertEquals("value", found.getRuntimeName());
  }

  @Test
  @DisplayName("Handle schema with null event properties")
  void testSchemaWithNullEventProperties() {
    EventSchema nullSchema = new EventSchema();
    nullSchema.setEventProperties(null);

    assertThrows(IllegalArgumentException.class, () -> {
      ConversionUtils.findProperty(nullSchema, "property");
    });
  }

  @Test
  @DisplayName("Handle null schema")
  void testNullSchema() {
    assertThrows(IllegalArgumentException.class, () -> {
      ConversionUtils.findProperty(null, "property");
    });
  }

  @Test
  @DisplayName("Multiple nested properties at same level")
  void testMultipleNestedPropertiesAtSameLevel() {
    EventPropertyNested location = new EventPropertyNested("location");
    location.getEventProperties().add(new EventPropertyPrimitive("latitude"));
    location.getEventProperties().add(new EventPropertyPrimitive("longitude"));
    schema.addEventProperty(location);

    EventProperty latitude = ConversionUtils.findProperty(schema, "location.latitude");
    EventProperty longitude = ConversionUtils.findProperty(schema, "location.longitude");

    assertEquals("latitude", latitude.getRuntimeName());
    assertEquals("longitude", longitude.getRuntimeName());
  }

  @Test
  @DisplayName("Find property after moving nested structure")
  void testFindPropertyAfterMoving() {
    EventPropertyNested address = new EventPropertyNested("address");
    address.getEventProperties().add(new EventPropertyPrimitive("city"));
    schema.addEventProperty(address);

    // Initial find
    EventProperty city = ConversionUtils.findProperty(schema, "address.city");
    assertEquals("city", city.getRuntimeName());

    // Simulate moving by creating new structure
    schema.getEventProperties().clear();
    EventPropertyNested location = new EventPropertyNested("location");
    location.getEventProperties().add(new EventPropertyPrimitive("city"));
    schema.addEventProperty(location);

    // Should find in new location
    assertTrue(ConversionUtils.propertyExists(schema, "location.city"));
    assertFalse(ConversionUtils.propertyExists(schema, "address.city"));
  }

  @Test
  @DisplayName("Handle whitespace in property names")
  void testPropertyWithWhitespace() {
    schema.addEventProperty(new EventPropertyPrimitive("property with spaces"));

    EventProperty found = ConversionUtils.findProperty(schema, "property with spaces");
    assertNotNull(found);
    assertEquals("property with spaces", found.getRuntimeName());
  }

  @Test
  @DisplayName("Case sensitivity in property lookup")
  void testCaseSensitivity() {
    schema.addEventProperty(new EventPropertyPrimitive("Temperature"));

    assertThrows(IllegalArgumentException.class, () -> {
      ConversionUtils.findProperty(schema, "temperature");
    });

    // Correct case should work
    EventProperty found = ConversionUtils.findProperty(schema, "Temperature");
    assertNotNull(found);
  }
}
