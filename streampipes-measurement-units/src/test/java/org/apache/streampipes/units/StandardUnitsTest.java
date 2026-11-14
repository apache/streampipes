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

package org.apache.streampipes.units;

import org.apache.streampipes.units.catalog.UnitCatalog;
import org.apache.streampipes.units.model.MeasurementUnit;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StandardUnitsTest {

  @Test
  public void everyConstantIsACatalogUnit() throws IllegalAccessException {
    var catalog = UnitCatalog.builtIn();
    for (var field : StandardUnits.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) && field.getType() == MeasurementUnit.class) {
        var unit = (MeasurementUnit) field.get(null);
        assertNotNull(unit, field.getName());
        assertEquals(unit, catalog.get(unit.resource()), field.getName());
      }
    }
  }

  @Test
  public void constantsUsedByBuiltInExtensionsKeepTheirStoredIdentifiers() {
    // These URIs are stored in schemas created by the built-in simulators and processors.
    assertEquals(UnitNamespace.QUDT1_UNIT + "DegreeCelsius", StandardUnits.DEGREE_CELSIUS.resource().toString());
    assertEquals(UnitNamespace.QUDT1_UNIT + "Bar", StandardUnits.BAR.resource().toString());
    assertEquals(UnitNamespace.QUDT1_UNIT + "SecondTime", StandardUnits.SECOND.resource().toString());
    assertEquals(UnitNamespace.QUDT1_UNIT + "Percent", StandardUnits.PERCENT.resource().toString());
    assertEquals(UnitNamespace.QUDT1_UNIT + "NewtonMeter", StandardUnits.NEWTON_METER.resource().toString());
    assertEquals(UnitNamespace.QUDT1_UNIT + "DegreeAngle", StandardUnits.DEGREE.resource().toString());
    assertEquals(UnitNamespace.QUDT1_UNIT + "RevolutionPerMinute",
        StandardUnits.REVOLUTION_PER_MINUTE.resource().toString());
    assertEquals(UnitNamespace.QUDT1_UNIT + "Kilometer", StandardUnits.KILOMETER.resource().toString());
    assertEquals(UnitNamespace.QUDT1_UNIT + "KilometerPerHour",
        StandardUnits.KILOMETER_PER_HOUR.resource().toString());
  }
}
