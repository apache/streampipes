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

package org.apache.streampipes.manager.pipeline.update;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventSchemaChangeDetectorTest {

  @ParameterizedTest
  @MethodSource("schemaChanges")
  void detectsFieldChanges(EventSchema previous, EventSchema updated) {
    assertTrue(EventSchemaChangeDetector.hasChanged(previous, updated));
    assertTrue(EventSchemaChangeDetector.hasChanged(updated, previous));
  }

  private static Stream<Arguments> schemaChanges() {
    var original = schema(field("value", "xs:string", "measurement"));
    return Stream.of(
        Arguments.of(original, schema()),
        Arguments.of(original, schema(field("value", "xs:string", "measurement"), field("extra"))),
        Arguments.of(original, schema(field("renamed", "xs:string", "measurement"))),
        Arguments.of(original, schema(field("value", "xs:double", "measurement"))),
        Arguments.of(original, schema(field("value", "xs:string", "dimension"))),
        Arguments.of(original, schema(field("value", "xs:string", null))),
        Arguments.of(schema(new EventPropertyNested("object", List.of(field("child")))),
            schema(new EventPropertyNested("object", List.of(field("renamed"))))),
        Arguments.of(schema(new EventPropertyNested("object", List.of(field("child")))),
            schema(new EventPropertyNested("object", List.of()))),
        Arguments.of(schema(new EventPropertyList("values", field("item"))),
            schema(new EventPropertyList("values", field("item", "xs:double", null)))),
        Arguments.of(schema(new EventPropertyList("values", new EventPropertyNested("item", List.of(field("child"))))),
            schema(new EventPropertyList("values", new EventPropertyNested("item", List.of(field("new")))))),
        Arguments.of(schema(field("value")), schema(new EventPropertyNested("value", List.of()))),
        Arguments.of(schema(field("value")), schema(new EventPropertyList("value", field("item")))),
        Arguments.of(null, schema(field("value")))
    );
  }

  @Test
  void ignoresFieldOrderAtEveryLevel() {
    var previous = schema(field("a"), new EventPropertyNested("object", List.of(field("b"), field("c"))),
        new EventPropertyList("list", new EventPropertyNested("item", List.of(field("d"), field("e")))));
    var updated = schema(
        new EventPropertyList("list", new EventPropertyNested("item", List.of(field("e"), field("d")))),
        new EventPropertyNested("object", List.of(field("c"), field("b"))), field("a"));

    assertFalse(EventSchemaChangeDetector.hasChanged(previous, updated));
  }

  @Test
  void ignoresDescriptiveAndGeneratedMetadata() {
    var previous = field("value");
    var updated = field("value");
    updated.setLabel("New label");
    updated.setDescription("New description");
    updated.setElementId("new-id");
    updated.setRuntimeId("new-runtime-id");
    updated.setSemanticType("new-semantic-type");
    updated.setMeasurementUnit(URI.create("urn:unit:new"));

    assertFalse(EventSchemaChangeDetector.hasChanged(schema(previous), schema(updated)));
  }

  @Test
  void treatsMissingAndEmptySchemasAsHavingNoFields() {
    assertFalse(EventSchemaChangeDetector.hasChanged(null, schema()));
    var missingFields = schema();
    missingFields.setEventProperties(null);
    assertFalse(EventSchemaChangeDetector.hasChanged(missingFields, schema()));
  }

  @Test
  void detectsDuplicateFieldRemoval() {
    assertTrue(EventSchemaChangeDetector.hasChanged(schema(field("value"), field("value")), schema(field("value"))));
  }

  private static EventSchema schema(EventProperty... properties) {
    return new EventSchema(List.of(properties));
  }

  private static EventPropertyPrimitive field(String name) {
    return field(name, "xs:string", null);
  }

  private static EventPropertyPrimitive field(String name, String type, String scope) {
    var property = new EventPropertyPrimitive(type, name, null, null);
    property.setPropertyScope(scope);
    return property;
  }
}
