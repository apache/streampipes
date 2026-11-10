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

package org.apache.streampipes.connect.management.compact;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.model.connect.adapter.compact.CompactEventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchemaMetadataEnricherTest {

  private static final String CELSIUS = "http://qudt.org/vocab/unit#DegreeCelsius";
  private static final String FAHRENHEIT = "http://qudt.org/vocab/unit#DegreeFahrenheit";

  private final SchemaMetadataEnricher enricher = new SchemaMetadataEnricher();

  @Test
  public void storesCanonicalUnitIdentifiers() throws AdapterException {
    var property = temperatureProperty();

    enricher.enrich(property, unitConversion("DEG_F", "http://qudt.org/vocab/unit/DEG_C"));

    assertEquals(URI.create(CELSIUS), property.getMeasurementUnit());
    assertEquals(FAHRENHEIT, property.getAdditionalMetadata().get("fromMeasurementUnit"));
    assertEquals(CELSIUS, property.getAdditionalMetadata().get("toMeasurementUnit"));
  }

  @Test
  public void rejectsUnknownUnit() {
    var property = temperatureProperty();

    var exception = assertThrows(
        AdapterException.class,
        () -> enricher.enrich(property, unitConversion("DEG_F", "DegreeCelcius")));
    assertTrue(exception.getMessage().contains("DegreeCelcius"));
    assertTrue(exception.getMessage().contains("temperature"));
  }

  @Test
  public void rejectsConversionBetweenDifferentQuantities() {
    var property = temperatureProperty();

    assertThrows(
        AdapterException.class,
        () -> enricher.enrich(property, unitConversion("DEG_C", "BAR")));
  }

  private EventPropertyPrimitive temperatureProperty() {
    var property = new EventPropertyPrimitive();
    property.setRuntimeName("temperature");
    return property;
  }

  private CompactEventProperty unitConversion(String from, String to) {
    return new CompactEventProperty(
        null,
        null,
        null,
        null,
        Map.of("fromMeasurementUnit", from, "toMeasurementUnit", to)
    );
  }
}
