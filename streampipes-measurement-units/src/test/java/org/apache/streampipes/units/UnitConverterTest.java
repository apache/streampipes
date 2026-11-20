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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Checks conversions against values that are exact by definition or given in NIST SP 811.
 */
public class UnitConverterTest {

  private static final double RELATIVE_TOLERANCE = 1e-12;

  private final UnitCatalog catalog = UnitCatalog.builtIn();

  @Test
  public void temperature() {
    assertConversion(100, "DegreeCelsius", 212, "DegreeFahrenheit");
    assertConversion(100, "DegreeCelsius", 373.15, "Kelvin");
    assertConversion(32, "DegreeFahrenheit", 273.15, "Kelvin");
    assertConversion(-40, "DegreeFahrenheit", -40, "DegreeCelsius");
    assertConversion(491.67, "DegreeRankine", 273.15, "Kelvin");
    assertConversion(0, "DegreeCelsius", 273.15, "Kelvin");
  }

  @Test
  public void temperatureRate() {
    assertConversion(60, "KelvinPerMinute", 1, "KelvinPerSecond");
    assertConversion(1, "DegreeCelsiusPerHour", 1, "KelvinPerHour");
  }

  @Test
  public void pressure() {
    assertConversion(1, "Bar", 100_000, "Pascal");
    assertConversion(1, "Bar", 1000, "Millibar");
    assertConversion(1, "AtmosphereStandard", 760, "Torr");
    assertConversion(1, "AtmosphereStandard", 1.01325, "Bar");
    assertConversion(1, "PoundForcePerSquareInch", 6894.757293168361, "Pascal");
    assertConversion(1, "MegaPA", 10, "Bar");
    assertConversion(1013.25, "HectoPA", 101.325, "KiloPascal");
  }

  @Test
  public void lengthAreaVolume() {
    assertConversion(1, "Inch", 25.4, "Millimeter");
    assertConversion(1, "MileInternational", 5280, "Foot");
    assertConversion(1, "NauticalMile", 1.852, "Kilometer");
    assertConversion(1, "Acre", 43_560, "SquareFoot");
    assertConversion(1, "Hectare", 10_000, "SquareMeter");
    assertConversion(1, "GallonUS", 3.785411784, "Liter");
    assertConversion(1, "CubicFoot", 1728, "CubicInch");
    assertConversion(1, "Barrel", 42, "GallonUS");
  }

  @Test
  public void massTimeAndRates() {
    assertConversion(1, "PoundMass", 16, "OunceMass");
    assertConversion(1, "TonShort", 2000, "PoundMass");
    assertConversion(1, "MetricTon", 1_000_000, "Gram");
    assertConversion(1, "Day", 86_400_000, "MilliSecond");
    assertConversion(36, "KilometerPerHour", 10, "MeterPerSecond");
    assertConversion(1, "Knot", 1.852, "KilometerPerHour");
    assertConversion(60, "L-PER-MIN", 3.6, "CubicMeterPerHour");
    assertConversion(3.6, "TONNE-PER-HR", 1, "KilogramPerSecond");
  }

  @Test
  public void mechanics() {
    assertConversion(60, "RevolutionPerMinute", 1, "RevolutionPerSecond");
    assertConversion(1, "RevolutionPerSecond", 2 * Math.PI, "RadianPerSecond");
    assertConversion(180, "DegreeAngle", Math.PI, "Radian");
    assertConversion(1, "DegreeAngle", 60, "ArcMinute");
    assertConversion(1, "PoundForce", 0.45359237 * 9.80665, "Newton");
    assertConversion(1, "PoundForceFoot", 12, "PoundForceInch");
    assertConversion(1, "Horsepower", 550 * 0.3048 * 0.45359237 * 9.80665, "Watt");
    assertConversion(1, "HorsepowerMetric", 735.49875, "Watt");
    assertConversion(1, "Gravity", 9.80665, "MeterPerSecondSquared");
  }

  @Test
  public void energyAndElectricity() {
    assertConversion(1, "Kilowatthour", 3.6, "MegaJ");
    assertConversion(1, "Kilocalorie", 4184, "Joule");
    assertConversion(1, "KiloCAL_IT", 4186.8, "Joule");
    assertConversion(1, "AmpereHour", 1000, "MilliA-HR");
    assertConversion(1, "KiloV", 1_000_000, "MilliV");
    assertConversion(1, "MegaOHM", 1000, "KiloOHM");
    assertConversion(1, "MicroFarad", 1_000_000, "PicoFarad");
    assertConversion(1, "Centipoise", 0.001, "PascalSecond");
  }

  @Test
  public void ratiosAndInformation() {
    assertConversion(1, "Percent", 10_000, "PPM");
    assertConversion(1, "PPM", 1000, "PPB");
    assertConversion(1, "MegaBYTE", 8_000_000, "Bit");
    assertConversion(1, "MegabitsPerSecond", 1000, "KilobitsPerSecond");
  }

  @Test
  public void electricalPower() {
    assertConversion(1, "KiloV-A", 1000, "V-A");
    assertConversion(2.5, "MegaV-A_Reactive", 2500, "KiloV-A_Reactive");
    assertConversion(1, "KiloV-A-HR", 1000, "V-A-HR");
    assertConversion(1, "MegaV-A_Reactive-HR", 1000, "KiloV-A_Reactive-HR");
    assertConversion(1, "GigaW-HR", 1000, "MegaW-HR");
    assertConversion(1, "BtuPerHour", 1055.05585262 / 3600, "Watt");
    assertConversion(1, "TonOfRefrigeration", 12_000, "BtuPerHour");
    assertConversion(1, "ThermUS", 105.4804, "MegaJ");
  }

  @Test
  public void vibrationAndMachineMotion() {
    assertConversion(1, "IN-PER-SEC", 25.4, "MilliM-PER-SEC");
    assertConversion(1, "MilliG", 9.80665, "MilliM-PER-SEC2");
    assertConversion(1, "IN-PER-MIN", 25.4, "MilliM-PER-MIN");
    assertConversion(1, "FootPerMinute", 0.3048, "MeterPerMinute");
    assertConversion(1, "RadianPerSecond", 60, "RadianPerMinute");
    assertConversion(1, "RevolutionPerHour", 1.0 / 60, "RevolutionPerMinute");
    assertConversion(180, "DEG-PER-SEC2", Math.PI, "RAD-PER-SEC2");
  }

  @Test
  public void processAndWaterQuality() {
    assertConversion(1, "MilliS-PER-CentiM", 1000, "MicroS-PER-CentiM");
    assertConversion(1, "MicroS-PER-CentiM", 0.1, "MilliS-PER-M");
    assertConversion(1, "M_H2O", 9.80665, "KiloPascal");
    assertConversion(1, "KiloGM_F-PER-CentiM2", 0.980665, "Bar");
    assertConversion(1, "N-PER-MilliM2", 1, "MegaPA");
    assertConversion(1, "MilliGM-PER-L", 1, "GM-PER-M3");
    assertConversion(1, "KiloGM-PER-L", 1, "GM-PER-CentiM3");
    assertConversion(1, "PPTH", 1000, "PPM");
    assertConversion(1, "MilliMOL-PER-L", 1, "MOL-PER-M3");
    assertConversion(1, "CentiST", 1, "MilliM2-PER-SEC");
  }

  @Test
  public void flowAndCounting() {
    assertConversion(1, "M3-PER-MIN", 60, "CubicMeterPerHour");
    assertConversion(1, "TONNE-PER-DAY", 1000.0 / 24, "KilogramPerHour");
    assertConversion(1, "NM3-PER-MIN", 60, "NM3-PER-HR");
    assertConversion(1000, "NL-PER-MIN", 60, "NM3-PER-HR");
    assertConversion(1, "SCF-PER-MIN", 60, "SCF-PER-HR");
    assertConversion(1, "NUM-PER-MIN", 60, "NUM-PER-HR");
  }

  @Test
  public void timeDataAndOthers() {
    assertConversion(1, "YR_Common", 365, "Day");
    assertConversion(1, "WK", 7, "Day");
    assertConversion(1, "GibiBYTE", 1024, "MebiBYTE");
    assertConversion(1, "KibiBYTE", 1024, "Byte");
    assertConversion(1, "TeraBYTE", 1000, "GigaBYTE");
    assertConversion(1, "GAUSS", 0.1, "MilliT");
    assertConversion(1, "DecaN", 10, "Newton");
    assertConversion(1, "KiloN-M", 100_000, "N-CentiM");
    assertConversion(1, "MilliHenry", 1000, "MicroHenry");
    assertConversion(1, "MilliS", 1000, "MicroS");
  }

  @Test
  public void unitsOfSameDimensionButDifferentMeaningAreNotConvertible() {
    assertNotConvertible("V-A", "Watt");
    assertNotConvertible("V-A_Reactive", "V-A");
    assertNotConvertible("KiloV-A-HR", "Kilowatthour");
    assertNotConvertible("NTU", "FNU");
    assertNotConvertible("DeciB_A", "Decibel");
    assertNotConvertible("NM3", "CubicMeter");
    assertNotConvertible("NM3-PER-HR", "CubicMeterPerHour");
    assertNotConvertible("SCF", "NM3");
    assertNotConvertible("PERCENT_RH", "Percent");
    assertNotConvertible("NUM-PER-MIN", "Hertz");
  }

  @Test
  public void reusableConversionChecksUnitsOnCreation() {
    var conversion = UnitConverter.between(StandardUnits.DEGREE_CELSIUS, StandardUnits.KELVIN);
    assertEquals(273.15, conversion.applyAsDouble(0));
    assertEquals(283.15, conversion.applyAsDouble(10));
    assertEquals(StandardUnits.DEGREE_CELSIUS, conversion.from());

    assertThrows(IllegalArgumentException.class,
        () -> UnitConverter.between(StandardUnits.DEGREE_CELSIUS, StandardUnits.BAR));
  }

  @Test
  public void sameUnitReturnsValueUnchanged() {
    var celsius = catalog.get("DegreeCelsius");
    assertEquals(21.5, UnitConverter.convert(21.5, celsius, celsius));
  }

  @Test
  public void differentKindsCannotBeConverted() {
    var celsius = catalog.get("DegreeCelsius");
    var bar = catalog.get("Bar");
    assertThrows(IllegalArgumentException.class, () -> UnitConverter.convert(1, celsius, bar));
  }

  private void assertNotConvertible(String from, String to) {
    var fromUnit = catalog.get(from);
    var toUnit = catalog.get(to);
    assertFalse(UnitConverter.isConvertible(fromUnit, toUnit), from + " -> " + to);
    assertThrows(IllegalArgumentException.class, () -> UnitConverter.convert(1, fromUnit, toUnit));
  }

  private void assertConversion(double value, String from, double expected, String to) {
    MeasurementUnit fromUnit = catalog.get(from);
    MeasurementUnit toUnit = catalog.get(to);
    double actual = UnitConverter.convert(value, fromUnit, toUnit);
    assertEquals(expected, actual, Math.max(Math.abs(expected), 1) * RELATIVE_TOLERANCE,
        "%s %s in %s".formatted(value, from, to));
  }
}
