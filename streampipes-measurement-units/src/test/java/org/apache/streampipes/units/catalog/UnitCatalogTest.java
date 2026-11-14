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

package org.apache.streampipes.units.catalog;

import org.apache.streampipes.units.UnitConverter;
import org.apache.streampipes.units.UnknownMeasurementUnitException;
import org.apache.streampipes.units.model.MeasurementUnit;
import org.apache.streampipes.units.model.Multiplier;

import org.junit.jupiter.api.Test;

import java.lang.reflect.RecordComponent;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitCatalogTest {

  private static final String V1 = "http://qudt.org/vocab/unit#";

  /**
   * Unit URIs referenced by built-in extensions, tests and example data. They are stored in
   * existing installations and must keep resolving to themselves.
   */
  private static final List<String> STORED_UNIT_IDS = List.of(
      "DegreeCelsius", "DegreeFahrenheit", "Kelvin", "Percent", "SecondTime", "Kilometer",
      "KilometerPerHour", "RevolutionPerMinute", "NewtonMeter", "DegreeAngle", "Bar");

  private final UnitCatalog catalog = UnitCatalog.builtIn();

  @Test
  public void storedUnitUrisResolveToThemselves() {
    for (String id : STORED_UNIT_IDS) {
      var unit = catalog.find(V1 + id);
      assertTrue(unit.isPresent(), "missing " + id);
      assertEquals(V1 + id, unit.get().resource().toString());
    }
  }

  @Test
  public void qudt2AliasesResolveToCanonicalUnit() {
    assertEquals(V1 + "DegreeCelsius", catalog.get("http://qudt.org/vocab/unit/DEG_C").resource().toString());
    assertEquals(V1 + "DegreeFahrenheit", catalog.get("http://qudt.org/vocab/unit/DEG_F").resource().toString());
    assertEquals(V1 + "Bar", catalog.get("http://qudt.org/vocab/unit/BAR").resource().toString());
    assertEquals(V1 + "DegreeCelsius", catalog.get(V1 + "DegreeCentigrade").resource().toString());
  }

  @Test
  public void looksUpUnitsByUriOrString() {
    var unit = catalog.get(V1 + "KilometerPerHour");
    assertEquals("Kilometer per Hour", unit.label());
    assertEquals("km/h", unit.abbreviation());
    assertEquals(URI.create("http://qudt.org/schema/qudt#LinearVelocityUnit"), unit.type());
    assertEquals(unit, catalog.get(URI.create(V1 + "KilometerPerHour")));
    assertEquals(unit, catalog.get("KilometerPerHour"));
    assertEquals(unit, catalog.get(" " + V1 + "KilometerPerHour "));
  }

  @Test
  public void unknownUnitThrowsDedicatedException() {
    var exception = assertThrows(UnknownMeasurementUnitException.class, () -> catalog.get(V1 + "DoesNotExist"));
    assertInstanceOf(IllegalStateException.class, exception);
    assertTrue(catalog.find(V1 + "DoesNotExist").isEmpty());
    assertTrue(catalog.find((String) null).isEmpty());
    assertTrue(catalog.find((URI) null).isEmpty());
  }

  @Test
  public void canonicalizesAliases() {
    assertEquals(Optional.of(URI.create(V1 + "DegreeCelsius")), catalog.canonicalize("DEG_C"));
    assertEquals(Optional.of(URI.create(V1 + "DegreeCelsius")), catalog.canonicalize(BuiltInUnits.V2 + "DEG_C"));
    assertTrue(catalog.canonicalize("NoSuchUnit").isEmpty());

    var unknown = URI.create("urn:unknown-unit");
    assertEquals(unknown, catalog.canonicalizeOrKeep(unknown));
    assertEquals(URI.create(V1 + "Bar"), catalog.canonicalizeOrKeep(URI.create(BuiltInUnits.SP + "BAR")));
  }

  @Test
  public void unitsOfKindContainAllUnitsOfThatKind() {
    var bar = catalog.get(V1 + "Bar");
    var pressureUnits = catalog.getUnitsOfKind(bar.type()).stream()
        .map(u -> u.resource().toString())
        .toList();
    assertTrue(pressureUnits.containsAll(List.of(V1 + "Bar", V1 + "PoundForcePerSquareInch", V1 + "Pascal")));
    assertTrue(catalog.getUnitsOfKind(URI.create("urn:unknown-kind")).isEmpty());
  }

  @Test
  public void jsonFieldNamesMatchTheFormerUnitClass() {
    // The REST API serialises MeasurementUnit; the UI expects these property names.
    var components = Arrays.stream(MeasurementUnit.class.getRecordComponents())
        .map(RecordComponent::getName)
        .toList();
    assertEquals(List.of("resource", "label", "abbreviation", "symbol", "type", "multiplier"), components);
  }

  @Test
  public void canonicalIdentifierIsQudt1OrStreamPipes() {
    for (MeasurementUnit unit : catalog.getUnits()) {
      var resource = unit.resource().toString();
      assertTrue(resource.startsWith(BuiltInUnits.V1) || resource.startsWith(BuiltInUnits.SP), resource);
    }
  }

  @Test
  public void everyUnitHasAStreamPipesIdentifier() {
    var unitsWithStreamPipesId = new HashSet<MeasurementUnit>();
    for (String identifier : catalog.getIdentifiers()) {
      if (identifier.startsWith(BuiltInUnits.SP)) {
        unitsWithStreamPipesId.add(catalog.get(identifier));
      }
    }
    for (MeasurementUnit unit : catalog.getUnits()) {
      assertTrue(unitsWithStreamPipesId.contains(unit), "no StreamPipes identifier for " + unit);
    }
  }

  @Test
  public void streamPipesAndQudt2IdentifiersResolveToSameUnit() {
    assertEquals(catalog.get(V1 + "DegreeCelsius"), catalog.get(BuiltInUnits.SP + "DEG_C"));
    assertEquals(catalog.get(BuiltInUnits.SP + "KiloV-A"), catalog.get(BuiltInUnits.V2 + "KiloV-A"));
    assertEquals(BuiltInUnits.SP + "MegaPA", catalog.get(BuiltInUnits.V2 + "MegaPA").resource().toString());
  }

  @Test
  public void julianYearIsNotAnAliasOfTheCommonYear() {
    assertTrue(catalog.find(BuiltInUnits.V2 + "YR").isEmpty());
    assertTrue(catalog.find(BuiltInUnits.V2 + "YR_Common").isPresent());
  }

  @Test
  public void everyUnitIsComplete() {
    for (MeasurementUnit unit : catalog.getUnits()) {
      assertNotNull(unit.label(), unit.toString());
      assertNotNull(unit.abbreviation(), unit.toString());
      assertNotNull(unit.symbol(), unit.toString());
      assertNotNull(unit.type(), unit.toString());
      assertTrue(Double.isFinite(unit.multiplier().multiplier()), unit.toString());
      assertTrue(unit.multiplier().multiplier() > 0, unit.toString());
      assertTrue(Double.isFinite(unit.multiplier().offset()), unit.toString());
      assertEquals(unit, catalog.get(unit.resource().toString()));
    }
  }

  @Test
  public void everyKindHasUnitsAndOneBaseUnit() {
    var kinds = new HashSet<>();
    for (var kind : catalog.getQuantityKinds()) {
      assertTrue(kinds.add(kind.resource()), "duplicate kind " + kind.resource());
      var units = catalog.getUnitsOfKind(kind.resource());
      assertFalse(units.isEmpty(), kind.resource().toString());
      // the builder enforces exactly one base unit; other units may be equal to it (°C/s = K/s)
      assertTrue(units.stream().anyMatch(u -> u.multiplier().equals(Multiplier.IDENTITY)),
          kind.resource().toString());
      units.forEach(u -> assertEquals(kind.resource(), u.type()));
    }
    assertEquals(catalog.getUnits().size(),
        catalog.getQuantityKinds().stream().mapToInt(k -> catalog.getUnitsOfKind(k.resource()).size()).sum());
  }

  @Test
  public void conversionsRoundTripWithinEachKind() {
    double[] values = {-273.15, -1, 0, 0.5, 1, 42, 1e6};
    for (var kind : catalog.getQuantityKinds()) {
      var units = catalog.getUnitsOfKind(kind.resource());
      for (MeasurementUnit from : units) {
        for (MeasurementUnit to : units) {
          for (double value : values) {
            double back = UnitConverter.convert(UnitConverter.convert(value, from, to), to, from);
            assertEquals(value, back, Math.max(Math.abs(value), 1) * 1e-9,
                "%s -> %s -> %s".formatted(from, to, from));
          }
        }
      }
    }
  }

  @Test
  public void rejectsKindWithoutBaseUnit() {
    var builder = UnitCatalog.builder()
        .kind("urn:kind", "Kind")
        .unit("urn:a", "A", "a", "a").factor("2");
    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  public void rejectsDuplicateIdentifiers() {
    var builder = UnitCatalog.builder()
        .kind("urn:kind", "Kind")
        .base("urn:a", "A", "a", "a")
        .unit("urn:b", "B", "b", "b").factor("2").alias("urn:a");
    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  public void rejectsDuplicateLocalNames() {
    var builder = UnitCatalog.builder()
        .kind("urn:kind#k", "Kind")
        .base("http://example.org/one#Same", "A", "a", "a")
        .unit("http://example.org/two/Same", "B", "b", "b").factor("2");
    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  public void evaluatesFactorExpressions() {
    assertEquals(0.5, FactorExpression.evaluate("1/2"));
    assertEquals(5.0 / 9 * 459.67, FactorExpression.evaluate("459.67*5/9"), 1e-12);
    assertEquals(Math.PI / 30, FactorExpression.evaluate("2*pi/60"), 1e-15);
    assertEquals(1e-6, FactorExpression.evaluate("0.000001"));
    assertThrows(IllegalArgumentException.class, () -> FactorExpression.evaluate("2*"));
    assertThrows(IllegalArgumentException.class, () -> FactorExpression.evaluate("two"));
  }
}
