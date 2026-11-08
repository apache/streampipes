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

import org.apache.streampipes.units.UnitNamespace;

/**
 * The units that ship with StreamPipes.
 *
 * <p><b>Identifiers.</b></p>
 *
 * <p>Every unit has a StreamPipes identifier, {@value #SP} followed by a unit code. Where QUDT 2.x
 * defines the same unit, the code is the QUDT 2.x code (e.g. {@code DEG_C}), and the QUDT 2.x URI
 * is an alias.</p>
 *
 * <p>The canonical identifier (the one returned in {@code resource}) is:</p>
 * <ul>
 *   <li>the QUDT 1.1 URI ({@value #V1}...) for units that QUDT 1.1 defines, because existing
 *   adapters, pipelines and event schemas store that URI;</li>
 *   <li>the StreamPipes URI for all other units.</li>
 * </ul>
 * <p>Moving the remaining units to StreamPipes identifiers later needs a data migration of the
 * stored URIs and a change of the canonical identifier here; the aliases already resolve.</p>
 *
 * <p>Quantity kinds that QUDT 1.1 defines keep their QUDT 1.1 class URI; new kinds use
 * {@value #SP_KIND}. Units that cannot be converted into each other although they have the same
 * dimension (W and VA, NTU and FNU, dB and dB(A), m³ and Nm³) are in separate kinds.</p>
 *
 * <p>Only identifiers are referenced; labels, symbols and numbers are not taken from QUDT.</p>
 *
 * <p><b>Conversion factors.</b></p>
 *
 * <p>SI definitions and prefixes follow the SI Brochure (BIPM, 9th edition, 2019). Other factors
 * follow NIST Special Publication 811 (2008), Appendix B; the comment on an entry names the
 * section ({@code B.8}, {@code B.9}). International foot, pound and pound-force are exact by
 * definition (1 ft = 0.3048 m, 1 lb = 0.45359237 kg, 1 lbf = 0.45359237 kg × 9.80665 m/s²).</p>
 */
final class BuiltInUnits {

  static final UnitCatalog CATALOG = define();

  static final String SP = UnitNamespace.STREAMPIPES_UNIT;
  static final String SP_KIND = UnitNamespace.STREAMPIPES_QUANTITY_KIND;
  static final String V1 = UnitNamespace.QUDT1_UNIT;
  static final String V2 = UnitNamespace.QUDT2_UNIT;
  private static final String V1_KIND = UnitNamespace.QUDT1_QUANTITY_KIND;

  // Exact values reused below
  private static final String LBF = "4.4482216152605";
  private static final String LB = "0.45359237";
  private static final String FT3 = "0.028316846592";
  private static final String GAL_US = "0.003785411784";
  private static final String STANDARD_GRAVITY = "9.80665";
  private static final String BTU_IT = "1055.05585262";

  private BuiltInUnits() {
  }

  private static String v1(String name) {
    return V1 + name;
  }

  private static String v2(String code) {
    return V2 + code;
  }

  private static String sp(String code) {
    return SP + code;
  }

  /**
   * The StreamPipes and QUDT 2.x identifiers of a unit with the given code.
   */
  private static String[] codes(String code) {
    return new String[]{SP + code, V2 + code};
  }

  private static UnitCatalog define() {
    // One statement per quantity kind: a single fluent chain over the whole catalog nests
    // hundreds of calls and overflows the default javac stack.
    var catalog = UnitCatalog.builder();

    // ---------------------------------------------------------------- thermal

    // Temperature, base kelvin (SI). Fahrenheit and Rankine: B.9
    catalog.kind(V1_KIND + "TemperatureUnit", "Temperature")
        .base(v1("Kelvin"), "Kelvin", "K", "K").alias(codes("K"))
        .unit(v1("DegreeCelsius"), "Degree Celsius", "degC", "°C")
          .offset("273.15").alias(codes("DEG_C")).alias(v1("DegreeCentigrade"))
        .unit(v1("DegreeFahrenheit"), "Degree Fahrenheit", "degF", "°F")
          .factor("5/9").offset("459.67*5/9").alias(codes("DEG_F"))
        .unit(v1("DegreeRankine"), "Degree Rankine", "degR", "°R")
          .factor("5/9").alias(codes("DEG_R"));

    // Temperature change per time, base kelvin per second
    catalog.kind(V1_KIND + "TemperaturePerTimeUnit", "Temperature rate")
        .base(v1("KelvinPerSecond"), "Kelvin per Second", "K/s", "K/s").alias(codes("K-PER-SEC"))
        .unit(v1("KelvinPerMinute"), "Kelvin per Minute", "K/min", "K/min")
          .factor("1/60").alias(codes("K-PER-MIN"))
        .unit(v1("KelvinPerHour"), "Kelvin per Hour", "K/h", "K/h")
          .factor("1/3600").alias(codes("K-PER-HR"))
        .unit(v1("DegreeCelsiusPerSecond"), "Degree Celsius per Second", "degC/s", "°C/s")
          .alias(codes("DEG_C-PER-SEC"))
        .unit(v1("DegreeCelsiusPerMinute"), "Degree Celsius per Minute", "degC/min", "°C/min")
          .factor("1/60").alias(codes("DEG_C-PER-MIN"))
        .unit(v1("DegreeCelsiusPerHour"), "Degree Celsius per Hour", "degC/h", "°C/h")
          .factor("1/3600").alias(codes("DEG_C-PER-HR"));

    // Heat transfer coefficient (U-value)
    catalog.kind(V1_KIND + "CoefficientOfHeatTransferUnit", "Heat transfer coefficient")
        .base(v1("WattPerSquareMeterKelvin"), "Watt per Square Meter Kelvin", "W/(m2.K)", "W/(m²·K)")
          .alias(codes("W-PER-M2-K"));

    catalog.kind(V1_KIND + "ThermalConductivityUnit", "Thermal conductivity")
        .base(v1("WattPerMeterKelvin"), "Watt per Meter Kelvin", "W/(m.K)", "W/(m·K)")
          .alias(codes("W-PER-M-K"));

    catalog.kind(V1_KIND + "ThermalResistanceUnit", "Thermal resistance")
        .base(v1("KelvinPerWatt"), "Kelvin per Watt", "K/W", "K/W").alias(codes("K-PER-W"));

    // Thermal insulance (R-value)
    catalog.kind(V1_KIND + "ThermalInsulanceUnit", "Thermal insulance")
        .base(v1("SquareMeterKelvinPerWatt"), "Square Meter Kelvin per Watt", "m2.K/W", "m²·K/W")
          .alias(codes("M2-K-PER-W"));

    catalog.kind(V1_KIND + "SpecificHeatCapacityUnit", "Specific heat capacity")
        .base(v1("JoulePerKilogramKelvin"), "Joule per Kilogram Kelvin", "J/(kg.K)", "J/(kg·K)")
          .alias(codes("J-PER-KiloGM-K"));

    catalog.kind(V1_KIND + "SpecificEnergyUnit", "Specific energy")
        .base(v1("JoulePerKilogram"), "Joule per Kilogram", "J/kg", "J/kg").alias(codes("J-PER-KiloGM"))
        .unit(sp("KiloJ-PER-KiloGM"), "Kilojoule per Kilogram", "kJ/kg", "kJ/kg")
          .factor("1000").alias(v2("KiloJ-PER-KiloGM"));

    // ---------------------------------------------------------------- pressure

    // Pressure, base pascal (SI). bar: SI Brochure table 8. Others: B.8
    catalog.kind(V1_KIND + "PressureOrStressUnit", "Pressure")
        .base(v1("Pascal"), "Pascal", "Pa", "Pa").alias(codes("PA"))
        .unit(sp("HectoPA"), "Hectopascal", "hPa", "hPa").factor("100").alias(v2("HectoPA"))
        .unit(v1("KiloPascal"), "Kilopascal", "kPa", "kPa").factor("1000").alias(codes("KiloPA"))
        .unit(sp("MegaPA"), "Megapascal", "MPa", "MPa").factor("1000000").alias(v2("MegaPA"))
        .unit(sp("N-PER-MilliM2"), "Newton per Square Millimeter", "N/mm2", "N/mm²")
          .factor("1000000").alias(v2("N-PER-MilliM2"))
        .unit(sp("MicroBAR"), "Microbar", "ubar", "µbar").factor("0.1").alias(v2("MicroBAR"))
        .unit(v1("Millibar"), "Millibar", "mbar", "mbar").factor("100").alias(codes("MilliBAR"))
        .unit(v1("Bar"), "Bar", "bar", "bar").factor("100000").alias(codes("BAR"))
        .unit(v1("PoundForcePerSquareInch"), "Pound-force per Square Inch", "psi", "psi")
          .factor(LBF + "/0.00064516").alias(codes("PSI"))
        .unit(sp("KiloGM_F-PER-CentiM2"), "Kilogram-force per Square Centimeter", "kgf/cm2", "kgf/cm²")
          .factor(STANDARD_GRAVITY + "/0.0001").alias(v2("KiloGM_F-PER-CentiM2"))
        .unit(v1("AtmosphereStandard"), "Standard Atmosphere", "atm", "atm")
          .factor("101325").alias(codes("ATM"))
        .unit(v1("Torr"), "Torr", "Torr", "Torr").factor("101325/760").alias(codes("TORR"))
        .unit(v1("MilliTorr"), "Millitorr", "mTorr", "mTorr").factor("101325/760000").alias(codes("MilliTORR"))
        // conventional millimeter/inch of mercury and water column, B.8
        .unit(v1("MillimeterOfMercury"), "Millimeter of Mercury", "mmHg", "mmHg")
          .factor("13595.1*" + STANDARD_GRAVITY + "*0.001").alias(codes("MilliM_HG"))
        .unit(v1("InchOfMercury"), "Inch of Mercury", "inHg", "inHg")
          .factor("13595.1*" + STANDARD_GRAVITY + "*0.0254").alias(codes("IN_HG"))
        .unit(sp("MilliM_H2O"), "Millimeter of Water", "mmH2O", "mmH₂O")
          .factor("1000*" + STANDARD_GRAVITY + "*0.001").alias(v2("MilliM_H2O"))
        .unit(sp("CentiM_H2O"), "Centimeter of Water", "cmH2O", "cmH₂O")
          .factor("1000*" + STANDARD_GRAVITY + "*0.01").alias(v2("CentiM_H2O"))
        .unit(sp("M_H2O"), "Meter of Water", "mH2O", "mH₂O")
          .factor("1000*" + STANDARD_GRAVITY).alias(v2("M_H2O"))
        .unit(v1("InchOfWater"), "Inch of Water", "inH2O", "inH₂O")
          .factor("1000*" + STANDARD_GRAVITY + "*0.0254").alias(codes("IN_H2O"));

    // ---------------------------------------------------------------- geometry

    // Length, base meter (SI). Imperial units: B.8
    catalog.kind(V1_KIND + "LengthUnit", "Length")
        .base(v1("Meter"), "Meter", "m", "m").alias(codes("M"))
        .unit(v1("Micrometer"), "Micrometer", "um", "µm").factor("0.000001").alias(codes("MicroM"))
        .unit(v1("Millimeter"), "Millimeter", "mm", "mm").factor("0.001").alias(codes("MilliM"))
        .unit(v1("Centimeter"), "Centimeter", "cm", "cm").factor("0.01").alias(codes("CentiM"))
        .unit(v1("Decimeter"), "Decimeter", "dm", "dm").factor("0.1").alias(codes("DeciM"))
        .unit(v1("Kilometer"), "Kilometer", "km", "km").factor("1000").alias(codes("KiloM"))
        .unit(v1("Inch"), "Inch", "in", "in").factor("0.0254").alias(codes("IN"))
        .unit(v1("Foot"), "Foot", "ft", "ft").factor("0.3048").alias(codes("FT"))
        .unit(v1("Yard"), "Yard", "yd", "yd").factor("0.9144").alias(codes("YD"))
        .unit(v1("MileInternational"), "Mile", "mi", "mi").factor("1609.344").alias(codes("MI"))
        .unit(v1("NauticalMile"), "Nautical Mile", "nmi", "nmi").factor("1852").alias(codes("MI_N"));

    // Area, base square meter
    catalog.kind(V1_KIND + "AreaUnit", "Area")
        .base(v1("SquareMeter"), "Square Meter", "m2", "m²").alias(codes("M2"))
        .unit(sp("MilliM2"), "Square Millimeter", "mm2", "mm²").factor("0.000001").alias(v2("MilliM2"))
        .unit(v1("SquareCentimeter"), "Square Centimeter", "cm2", "cm²")
          .factor("0.0001").alias(codes("CentiM2"))
        .unit(v1("Hectare"), "Hectare", "ha", "ha").factor("10000").alias(codes("HA"))
        .unit(sp("KiloM2"), "Square Kilometer", "km2", "km²").factor("1000000").alias(v2("KiloM2"))
        .unit(v1("SquareInch"), "Square Inch", "in2", "in²").factor("0.00064516").alias(codes("IN2"))
        .unit(v1("SquareFoot"), "Square Foot", "ft2", "ft²").factor("0.09290304").alias(codes("FT2"))
        // international acre = 43 560 ft²
        .unit(v1("Acre"), "Acre", "ac", "ac").factor("43560*0.09290304").alias(codes("AC"));

    // Volume, base cubic meter. Liter: SI Brochure table 8. Others: B.8
    catalog.kind(V1_KIND + "VolumeUnit", "Volume")
        .base(v1("CubicMeter"), "Cubic Meter", "m3", "m³").alias(codes("M3"))
        .unit(v1("CubicMillimeter"), "Cubic Millimeter", "mm3", "mm³")
          .factor("0.000000001").alias(codes("MilliM3"))
        .unit(v1("Liter"), "Liter", "L", "L").factor("0.001").alias(codes("L"))
        .unit(sp("MilliL"), "Milliliter", "mL", "mL").factor("0.000001").alias(v2("MilliL"))
        .unit(v1("CubicCentimeter"), "Cubic Centimeter", "cm3", "cm³")
          .factor("0.000001").alias(codes("CentiM3"))
        .unit(v1("CubicInch"), "Cubic Inch", "in3", "in³").factor("0.000016387064").alias(codes("IN3"))
        .unit(v1("CubicFoot"), "Cubic Foot", "ft3", "ft³").factor(FT3).alias(codes("FT3"))
        .unit(v1("CubicYard"), "Cubic Yard", "yd3", "yd³").factor("0.9144*0.9144*0.9144").alias(codes("YD3"))
        .unit(v1("GallonUS"), "Gallon (US)", "gal", "gal").factor(GAL_US).alias(codes("GAL_US"))
        .unit(v1("GallonImperial"), "Gallon (Imperial)", "gal (UK)", "gal (UK)")
          .factor("0.00454609").alias(codes("GAL_IMP"))
        // US petroleum barrel = 42 US gallons
        .unit(v1("Barrel"), "Barrel (US petroleum)", "bbl", "bbl")
          .factor("42*" + GAL_US).alias(codes("BBL_US")).alias(v2("BBL"));

    // Gas volume at normal conditions (DIN 1343: 0 °C, 101.325 kPa). Not convertible to
    // operating volume without pressure and temperature, hence a separate kind.
    catalog.kind(SP_KIND + "NormalVolume", "Normal volume (0 °C, 101.325 kPa)")
        .base(sp("NM3"), "Normal Cubic Meter", "Nm3", "Nm³").alias(v2("NCM"))
        .unit(sp("NL"), "Normal Liter", "NL", "NL").factor("0.001");

    // Gas volume at US standard conditions (60 °F, 14.696 psia)
    catalog.kind(SP_KIND + "StandardVolume", "Standard volume (60 °F, 14.696 psia)")
        .base(sp("SCF"), "Standard Cubic Foot", "scf", "scf").alias(v2("SCF"));

    // ---------------------------------------------------------------- mass

    // Mass, base kilogram (SI). Tonne: SI Brochure table 8. Others: B.8
    catalog.kind(V1_KIND + "MassUnit", "Mass")
        .base(v1("Kilogram"), "Kilogram", "kg", "kg").alias(codes("KiloGM"))
        .unit(sp("MicroGM"), "Microgram", "ug", "µg").factor("0.000000001").alias(v2("MicroGM"))
        .unit(sp("MilliGM"), "Milligram", "mg", "mg").factor("0.000001").alias(v2("MilliGM"))
        .unit(v1("Gram"), "Gram", "g", "g").factor("0.001").alias(codes("GM"))
        .unit(v1("MetricTon"), "Tonne", "t", "t").factor("1000").alias(codes("TONNE")).alias(v2("TON_Metric"))
        .unit(v1("OunceMass"), "Ounce", "oz", "oz").factor(LB + "/16").alias(codes("OZ"))
        .unit(v1("PoundMass"), "Pound", "lb", "lb").factor(LB).alias(codes("LB"))
        .unit(v1("TonShort"), "Short Ton", "ton (US)", "ton (US)")
          .factor("2000*" + LB).alias(codes("TON_SHORT")).alias(v2("TON_US"));

    // Density and mass concentration, base kilogram per cubic meter
    catalog.kind(V1_KIND + "MassPerVolumeUnit", "Density")
        .base(v1("KilogramPerCubicMeter"), "Kilogram per Cubic Meter", "kg/m3", "kg/m³")
          .alias(codes("KiloGM-PER-M3"))
        .unit(sp("MicroGM-PER-M3"), "Microgram per Cubic Meter", "ug/m3", "µg/m³")
          .factor("0.000000001").alias(v2("MicroGM-PER-M3"))
        .unit(sp("MilliGM-PER-M3"), "Milligram per Cubic Meter", "mg/m3", "mg/m³")
          .factor("0.000001").alias(v2("MilliGM-PER-M3"))
        .unit(sp("GM-PER-M3"), "Gram per Cubic Meter", "g/m3", "g/m³").factor("0.001").alias(v2("GM-PER-M3"))
        .unit(sp("MilliGM-PER-L"), "Milligram per Liter", "mg/L", "mg/L").factor("0.001").alias(v2("MilliGM-PER-L"))
        .unit(sp("GM-PER-L"), "Gram per Liter", "g/L", "g/L").alias(v2("GM-PER-L"))
        .unit(sp("KiloGM-PER-L"), "Kilogram per Liter", "kg/L", "kg/L").factor("1000").alias(v2("KiloGM-PER-L"))
        .unit(sp("GM-PER-CentiM3"), "Gram per Cubic Centimeter", "g/cm3", "g/cm³")
          .factor("1000").alias(v2("GM-PER-CentiM3"))
        .unit(v1("PoundPerCubicFoot"), "Pound per Cubic Foot", "lb/ft3", "lb/ft³")
          .factor(LB + "/" + FT3).alias(codes("LB-PER-FT3"));

    // ---------------------------------------------------------------- time and rates

    // Time, base second (SI). Minute, hour, day: SI Brochure table 8
    catalog.kind(V1_KIND + "TimeUnit", "Time")
        .base(v1("SecondTime"), "Second", "s", "s").alias(codes("SEC"))
        .unit(sp("NanoSEC"), "Nanosecond", "ns", "ns").factor("0.000000001").alias(v2("NanoSEC"))
        .unit(v1("MicroSecond"), "Microsecond", "us", "µs").factor("0.000001").alias(codes("MicroSEC"))
        .unit(v1("MilliSecond"), "Millisecond", "ms", "ms").factor("0.001").alias(codes("MilliSEC"))
        .unit(v1("MinuteTime"), "Minute", "min", "min").factor("60").alias(codes("MIN"))
        .unit(v1("Hour"), "Hour", "h", "h").factor("3600").alias(codes("HR"))
        .unit(v1("Day"), "Day", "d", "d").factor("86400").alias(codes("DAY"))
        .unit(sp("WK"), "Week", "wk", "wk").factor("604800").alias(v2("WK"))
        // common year of 365 days (QUDT 2.x YR is the Julian year of 365.25 days)
        .unit(v1("Year365Day"), "Year (365 days)", "a", "a").factor("365*86400").alias(codes("YR_Common"));

    // Frequency, base hertz (SI)
    catalog.kind(V1_KIND + "FrequencyUnit", "Frequency")
        .base(v1("Hertz"), "Hertz", "Hz", "Hz").alias(codes("HZ"))
        .unit(v1("KiloHertz"), "Kilohertz", "kHz", "kHz").factor("1000").alias(codes("KiloHZ"))
        .unit(v1("MegaHertz"), "Megahertz", "MHz", "MHz").factor("1000000").alias(codes("MegaHZ"))
        .unit(v1("GigaHertz"), "Gigahertz", "GHz", "GHz").factor("1000000000").alias(codes("GigaHZ"));

    // Counted items (pieces, parts, events)
    catalog.kind(SP_KIND + "Count", "Count")
        .base(sp("NUM"), "Count", "pcs", "pcs").alias(v2("NUM"));

    // Counted items per time (production rate, cycle rate)
    catalog.kind(SP_KIND + "CountRate", "Count rate")
        .base(sp("NUM-PER-SEC"), "Count per Second", "pcs/s", "pcs/s").alias(v2("NUM-PER-SEC"))
        .unit(sp("NUM-PER-MIN"), "Count per Minute", "pcs/min", "pcs/min").factor("1/60").alias(v2("NUM-PER-MIN"))
        .unit(sp("NUM-PER-HR"), "Count per Hour", "pcs/h", "pcs/h").factor("1/3600").alias(v2("NUM-PER-HR"));

    // ---------------------------------------------------------------- motion

    // Velocity, base meter per second
    catalog.kind(V1_KIND + "LinearVelocityUnit", "Velocity")
        .base(v1("MeterPerSecond"), "Meter per Second", "m/s", "m/s").alias(codes("M-PER-SEC"))
        .unit(sp("MilliM-PER-SEC"), "Millimeter per Second", "mm/s", "mm/s")
          .factor("0.001").alias(v2("MilliM-PER-SEC"))
        .unit(v1("CentimeterPerSecond"), "Centimeter per Second", "cm/s", "cm/s")
          .factor("0.01").alias(codes("CentiM-PER-SEC"))
        .unit(sp("MilliM-PER-MIN"), "Millimeter per Minute", "mm/min", "mm/min")
          .factor("0.001/60").alias(v2("MilliM-PER-MIN"))
        .unit(v1("MeterPerMinute"), "Meter per Minute", "m/min", "m/min")
          .factor("1/60").alias(codes("M-PER-MIN"))
        .unit(v1("MeterPerHour"), "Meter per Hour", "m/h", "m/h").factor("1/3600").alias(codes("M-PER-HR"))
        .unit(v1("KilometerPerHour"), "Kilometer per Hour", "km/h", "km/h")
          .factor("1000/3600").alias(codes("KiloM-PER-HR"))
        .unit(v1("InchPerSecond"), "Inch per Second", "in/s", "in/s").factor("0.0254").alias(codes("IN-PER-SEC"))
        .unit(sp("IN-PER-MIN"), "Inch per Minute", "in/min", "in/min").factor("0.0254/60").alias(v2("IN-PER-MIN"))
        .unit(v1("FootPerSecond"), "Foot per Second", "ft/s", "ft/s")
          .factor("0.3048").alias(codes("FT-PER-SEC"))
        .unit(v1("FootPerMinute"), "Foot per Minute", "ft/min", "ft/min")
          .factor("0.3048/60").alias(codes("FT-PER-MIN"))
        .unit(v1("MilePerHour"), "Mile per Hour", "mph", "mph")
          .factor("1609.344/3600").alias(codes("MI-PER-HR"))
        // knot = 1 nautical mile per hour, SI Brochure table 8 note
        .unit(v1("Knot"), "Knot", "kn", "kn").factor("1852/3600").alias(codes("KN"));

    // Acceleration, base meter per second squared
    catalog.kind(V1_KIND + "LinearAccelerationUnit", "Acceleration")
        .base(v1("MeterPerSecondSquared"), "Meter per Second Squared", "m/s2", "m/s²")
          .alias(codes("M-PER-SEC2"))
        .unit(sp("MilliM-PER-SEC2"), "Millimeter per Second Squared", "mm/s2", "mm/s²")
          .factor("0.001").alias(v2("MilliM-PER-SEC2"))
        .unit(v1("CentimeterPerSecondSquared"), "Centimeter per Second Squared", "cm/s2", "cm/s²")
          .factor("0.01").alias(codes("CentiM-PER-SEC2"))
        .unit(v1("InchPerSecondSquared"), "Inch per Second Squared", "in/s2", "in/s²")
          .factor("0.0254").alias(codes("IN-PER-SEC2"))
        .unit(v1("FootPerSecondSquared"), "Foot per Second Squared", "ft/s2", "ft/s²")
          .factor("0.3048").alias(codes("FT-PER-SEC2"))
        // standard acceleration of gravity, B.8
        .unit(v1("Milligravity"), "Milligravity", "mgn", "mgₙ")
          .factor(STANDARD_GRAVITY + "/1000").alias(codes("MilliG"))
        .unit(v1("Gravity"), "Standard Gravity", "gn", "gₙ").factor(STANDARD_GRAVITY).alias(codes("G"));

    // Plane angle, base radian (SI). Degree, minute, second: SI Brochure table 8
    catalog.kind(V1_KIND + "AngleUnit", "Angle")
        .base(v1("Radian"), "Radian", "rad", "rad").alias(codes("RAD"))
        .unit(v1("DegreeAngle"), "Degree", "deg", "°").factor("pi/180").alias(codes("DEG"))
        .unit(v1("ArcMinute"), "Arcminute", "arcmin", "′")
          .factor("pi/10800").alias(codes("ARCMIN")).alias(v2("MIN_Angle"), v1("MinuteAngle"))
        .unit(v1("ArcSecond"), "Arcsecond", "arcsec", "″")
          .factor("pi/648000").alias(codes("ARCSEC")).alias(v1("SecondAngle"))
        .unit(sp("REV"), "Revolution", "rev", "rev").factor("2*pi").alias(v2("REV"));

    // Angular velocity, base radian per second
    catalog.kind(V1_KIND + "AngularVelocityUnit", "Angular velocity")
        .base(v1("RadianPerSecond"), "Radian per Second", "rad/s", "rad/s").alias(codes("RAD-PER-SEC"))
        .unit(v1("RadianPerMinute"), "Radian per Minute", "rad/min", "rad/min")
          .factor("1/60").alias(codes("RAD-PER-MIN"))
        .unit(v1("DegreePerSecond"), "Degree per Second", "deg/s", "°/s")
          .factor("pi/180").alias(codes("DEG-PER-SEC"))
        .unit(v1("DegreePerMinute"), "Degree per Minute", "deg/min", "°/min")
          .factor("pi/10800").alias(codes("DEG-PER-MIN"))
        .unit(v1("RevolutionPerHour"), "Revolution per Hour", "rev/h", "rev/h")
          .factor("2*pi/3600").alias(codes("REV-PER-HR"))
        .unit(v1("RevolutionPerMinute"), "Revolution per Minute", "rpm", "rpm")
          .factor("2*pi/60").alias(codes("REV-PER-MIN"))
        .unit(v1("RevolutionPerSecond"), "Revolution per Second", "rev/s", "rev/s")
          .factor("2*pi").alias(codes("REV-PER-SEC"));

    catalog.kind(V1_KIND + "AngularAccelerationUnit", "Angular acceleration")
        .base(v1("RadianPerSecondSquared"), "Radian per Second Squared", "rad/s2", "rad/s²")
          .alias(codes("RAD-PER-SEC2"))
        .unit(v1("DegreePerSecondSquared"), "Degree per Second Squared", "deg/s2", "°/s²")
          .factor("pi/180").alias(codes("DEG-PER-SEC2"));

    // ---------------------------------------------------------------- mechanics

    // Force, base newton (SI). kgf and lbf: B.8
    catalog.kind(V1_KIND + "ForceUnit", "Force")
        .base(v1("Newton"), "Newton", "N", "N").alias(codes("N"))
        .unit(sp("MilliN"), "Millinewton", "mN", "mN").factor("0.001").alias(v2("MilliN"))
        .unit(sp("DecaN"), "Decanewton", "daN", "daN").factor("10")
        .unit(sp("KiloN"), "Kilonewton", "kN", "kN").factor("1000").alias(v2("KiloN"))
        .unit(sp("MegaN"), "Meganewton", "MN", "MN").factor("1000000").alias(v2("MegaN"))
        .unit(v1("KilogramForce"), "Kilogram-force", "kgf", "kgf")
          .factor(STANDARD_GRAVITY).alias(codes("KiloGM_F"))
        .unit(v1("PoundForce"), "Pound-force", "lbf", "lbf").factor(LBF).alias(codes("LB_F"));

    // Torque, base newton meter. B.8
    catalog.kind(V1_KIND + "BendingMomentOrTorqueUnit", "Torque")
        .base(v1("NewtonMeter"), "Newton Meter", "N.m", "N·m").alias(codes("N-M"))
        .unit(sp("N-CentiM"), "Newton Centimeter", "N.cm", "N·cm").factor("0.01").alias(v2("N-CentiM"))
        .unit(sp("KiloN-M"), "Kilonewton Meter", "kN.m", "kN·m").factor("1000").alias(v2("KiloN-M"))
        .unit(v1("KilogramForceMeter"), "Kilogram-force Meter", "kgf.m", "kgf·m")
          .factor(STANDARD_GRAVITY).alias(codes("KiloGM_F-M"))
        .unit(v1("PoundForceFoot"), "Pound-force Foot", "lbf.ft", "lbf·ft")
          .factor(LBF + "*0.3048").alias(codes("LB_F-FT"))
        .unit(v1("PoundForceInch"), "Pound-force Inch", "lbf.in", "lbf·in")
          .factor(LBF + "*0.0254").alias(codes("LB_F-IN"));

    // Dynamic viscosity, base pascal second. Poise: B.8
    catalog.kind(V1_KIND + "DynamicViscosityUnit", "Dynamic viscosity")
        .base(v1("PascalSecond"), "Pascal Second", "Pa.s", "Pa·s").alias(codes("PA-SEC"))
        .unit(v1("Poise"), "Poise", "P", "P").factor("0.1").alias(codes("POISE"))
        .unit(v1("Centipoise"), "Centipoise", "cP", "cP").factor("0.001").alias(codes("CentiPOISE"));

    // Kinematic viscosity, base square meter per second. Stokes: B.8
    catalog.kind(V1_KIND + "KinematicViscosityUnit", "Kinematic viscosity")
        .base(v1("SquareMeterPerSecond"), "Square Meter per Second", "m2/s", "m²/s").alias(codes("M2-PER-SEC"))
        .unit(sp("MilliM2-PER-SEC"), "Square Millimeter per Second", "mm2/s", "mm²/s")
          .factor("0.000001").alias(v2("MilliM2-PER-SEC"))
        .unit(sp("CentiST"), "Centistokes", "cSt", "cSt").factor("0.000001").alias(v2("CentiST"));

    // ---------------------------------------------------------------- energy and power

    // Energy, base joule (SI). Calorie, BTU and therm: B.8
    catalog.kind(V1_KIND + "EnergyAndWorkUnit", "Energy")
        .base(v1("Joule"), "Joule", "J", "J").alias(codes("J"))
        .unit(sp("KiloJ"), "Kilojoule", "kJ", "kJ").factor("1000").alias(v2("KiloJ"))
        .unit(sp("MegaJ"), "Megajoule", "MJ", "MJ").factor("1000000").alias(v2("MegaJ"))
        .unit(sp("GigaJ"), "Gigajoule", "GJ", "GJ").factor("1000000000").alias(v2("GigaJ"))
        .unit(v1("Watthour"), "Watt Hour", "Wh", "Wh").factor("3600").alias(codes("W-HR"))
        .unit(v1("Kilowatthour"), "Kilowatt Hour", "kWh", "kWh").factor("3600000").alias(codes("KiloW-HR"))
        .unit(sp("MegaW-HR"), "Megawatt Hour", "MWh", "MWh").factor("3600000000").alias(v2("MegaW-HR"))
        .unit(sp("GigaW-HR"), "Gigawatt Hour", "GWh", "GWh").factor("3600000000000").alias(v2("GigaW-HR"))
        .unit(v1("Kilocalorie"), "Kilocalorie (thermochemical)", "kcal_th", "kcal_th")
          .factor("4184").alias(codes("KiloCAL"))
        .unit(sp("KiloCAL_IT"), "Kilocalorie (International Table)", "kcal_IT", "kcal_IT")
          .factor("4186.8").alias(v2("KiloCAL_IT"))
        .unit(v1("BritishThermalUnitInternationalTable"), "British Thermal Unit", "BTU", "BTU")
          .factor(BTU_IT).alias(codes("BTU_IT"))
        // US therm = 100 000 BTU (59 °F), B.8
        .unit(v1("ThermUS"), "Therm (US)", "thm", "thm").factor("105480400").alias(codes("THM_US"));

    // Power, base watt (SI). Horsepower and refrigeration ton: B.8
    catalog.kind(V1_KIND + "PowerUnit", "Power")
        .base(v1("Watt"), "Watt", "W", "W").alias(codes("W"))
        .unit(sp("MilliW"), "Milliwatt", "mW", "mW").factor("0.001").alias(v2("MilliW"))
        .unit(v1("Kilowatt"), "Kilowatt", "kW", "kW").factor("1000").alias(codes("KiloW"))
        .unit(sp("MegaW"), "Megawatt", "MW", "MW").factor("1000000").alias(v2("MegaW"))
        .unit(sp("GigaW"), "Gigawatt", "GW", "GW").factor("1000000000").alias(v2("GigaW"))
        // mechanical horsepower = 550 ft·lbf/s
        .unit(v1("Horsepower"), "Horsepower", "hp", "hp").factor("550*" + LBF + "*0.3048").alias(codes("HP"))
        // metric horsepower = 75 kgf·m/s
        .unit(v1("HorsepowerMetric"), "Metric Horsepower", "PS", "PS")
          .factor("75*" + STANDARD_GRAVITY).alias(codes("HP_Metric"))
        .unit(v1("BtuPerHour"), "BTU per Hour", "BTU/h", "BTU/h").factor(BTU_IT + "/3600").alias(codes("BTU_IT-PER-HR"))
        // ton of refrigeration = 12 000 BTU/h
        .unit(v1("TonOfRefrigeration"), "Ton of Refrigeration", "TR", "TR")
          .factor("12000*" + BTU_IT + "/3600").alias(codes("TON_FG"));

    // Apparent power. Same dimension as watt, but not convertible to active power.
    catalog.kind(SP_KIND + "ApparentPower", "Apparent power")
        .base(sp("V-A"), "Volt Ampere", "VA", "VA").alias(v2("V-A"))
        .unit(sp("KiloV-A"), "Kilovolt Ampere", "kVA", "kVA").factor("1000").alias(v2("KiloV-A"))
        .unit(sp("MegaV-A"), "Megavolt Ampere", "MVA", "MVA").factor("1000000").alias(v2("MegaV-A"));

    catalog.kind(SP_KIND + "ReactivePower", "Reactive power")
        .base(sp("V-A_Reactive"), "Volt Ampere Reactive", "var", "var").alias(v2("V-A_Reactive"))
        .unit(sp("KiloV-A_Reactive"), "Kilovolt Ampere Reactive", "kvar", "kvar")
          .factor("1000").alias(v2("KiloV-A_Reactive"))
        .unit(sp("MegaV-A_Reactive"), "Megavolt Ampere Reactive", "Mvar", "Mvar")
          .factor("1000000").alias(v2("MegaV-A_Reactive"));

    catalog.kind(SP_KIND + "ApparentEnergy", "Apparent energy")
        .base(sp("V-A-HR"), "Volt Ampere Hour", "VAh", "VAh").alias(v2("V-A-HR"))
        .unit(sp("KiloV-A-HR"), "Kilovolt Ampere Hour", "kVAh", "kVAh").factor("1000").alias(v2("KiloV-A-HR"))
        .unit(sp("MegaV-A-HR"), "Megavolt Ampere Hour", "MVAh", "MVAh").factor("1000000").alias(v2("MegaV-A-HR"));

    catalog.kind(SP_KIND + "ReactiveEnergy", "Reactive energy")
        .base(sp("V-A_Reactive-HR"), "Volt Ampere Reactive Hour", "varh", "varh").alias(v2("V-A_Reactive-HR"))
        .unit(sp("KiloV-A_Reactive-HR"), "Kilovolt Ampere Reactive Hour", "kvarh", "kvarh")
          .factor("1000").alias(v2("KiloV-A_Reactive-HR"))
        .unit(sp("MegaV-A_Reactive-HR"), "Megavolt Ampere Reactive Hour", "Mvarh", "Mvarh")
          .factor("1000000").alias(v2("MegaV-A_Reactive-HR"));

    // Power per area (irradiance, heat flux), base watt per square meter
    catalog.kind(V1_KIND + "PowerPerAreaUnit", "Power per area")
        .base(v1("WattPerSquareMeter"), "Watt per Square Meter", "W/m2", "W/m²").alias(codes("W-PER-M2"))
        .unit(sp("KiloW-PER-M2"), "Kilowatt per Square Meter", "kW/m2", "kW/m²")
          .factor("1000").alias(v2("KiloW-PER-M2"));

    // ---------------------------------------------------------------- flow

    // Volume flow rate, base cubic meter per second
    catalog.kind(V1_KIND + "VolumePerTimeUnit", "Volume flow rate")
        .base(v1("CubicMeterPerSecond"), "Cubic Meter per Second", "m3/s", "m³/s").alias(codes("M3-PER-SEC"))
        .unit(sp("M3-PER-MIN"), "Cubic Meter per Minute", "m3/min", "m³/min").factor("1/60").alias(v2("M3-PER-MIN"))
        .unit(v1("CubicMeterPerHour"), "Cubic Meter per Hour", "m3/h", "m³/h")
          .factor("1/3600").alias(codes("M3-PER-HR"))
        .unit(sp("M3-PER-DAY"), "Cubic Meter per Day", "m3/d", "m³/d").factor("1/86400").alias(v2("M3-PER-DAY"))
        .unit(sp("L-PER-SEC"), "Liter per Second", "L/s", "L/s").factor("0.001").alias(v2("L-PER-SEC"))
        .unit(sp("L-PER-MIN"), "Liter per Minute", "L/min", "L/min").factor("0.001/60").alias(v2("L-PER-MIN"))
        .unit(sp("L-PER-HR"), "Liter per Hour", "L/h", "L/h").factor("0.001/3600").alias(v2("L-PER-HR"))
        .unit(sp("L-PER-DAY"), "Liter per Day", "L/d", "L/d").factor("0.001/86400").alias(v2("L-PER-DAY"))
        .unit(v1("CubicFootPerSecond"), "Cubic Foot per Second", "cfs", "ft³/s").factor(FT3).alias(codes("FT3-PER-SEC"))
        .unit(v1("CubicFootPerMinute"), "Cubic Foot per Minute", "cfm", "ft³/min")
          .factor(FT3 + "/60").alias(codes("FT3-PER-MIN"))
        .unit(v1("GallonUSPerMinute"), "Gallon (US) per Minute", "gpm", "gal/min")
          .factor(GAL_US + "/60").alias(codes("GAL_US-PER-MIN"))
        .unit(v1("GallonUSPerDay"), "Gallon (US) per Day", "gpd", "gal/d")
          .factor(GAL_US + "/86400").alias(codes("GAL_US-PER-DAY"));

    // Gas flow at normal conditions (DIN 1343), not convertible to operating volume flow
    catalog.kind(SP_KIND + "NormalVolumeFlowRate", "Normal volume flow rate (0 °C, 101.325 kPa)")
        .base(sp("NM3-PER-HR"), "Normal Cubic Meter per Hour", "Nm3/h", "Nm³/h")
        .unit(sp("NM3-PER-MIN"), "Normal Cubic Meter per Minute", "Nm3/min", "Nm³/min").factor("60")
        .unit(sp("NL-PER-MIN"), "Normal Liter per Minute", "NL/min", "NL/min").factor("0.06");

    // Gas flow at US standard conditions (60 °F, 14.696 psia)
    catalog.kind(SP_KIND + "StandardVolumeFlowRate", "Standard volume flow rate (60 °F, 14.696 psia)")
        .base(sp("SCF-PER-MIN"), "Standard Cubic Foot per Minute", "scfm", "scfm").alias(v2("SCF-PER-MIN"))
        .unit(sp("SCF-PER-HR"), "Standard Cubic Foot per Hour", "scfh", "scfh")
          .factor("1/60").alias(v2("SCF-PER-HR"));

    // Mass flow rate, base kilogram per second
    catalog.kind(V1_KIND + "MassPerTimeUnit", "Mass flow rate")
        .base(v1("KilogramPerSecond"), "Kilogram per Second", "kg/s", "kg/s").alias(codes("KiloGM-PER-SEC"))
        .unit(sp("GM-PER-SEC"), "Gram per Second", "g/s", "g/s").factor("0.001").alias(v2("GM-PER-SEC"))
        .unit(sp("GM-PER-MIN"), "Gram per Minute", "g/min", "g/min").factor("0.001/60").alias(v2("GM-PER-MIN"))
        .unit(sp("KiloGM-PER-MIN"), "Kilogram per Minute", "kg/min", "kg/min").factor("1/60").alias(v2("KiloGM-PER-MIN"))
        .unit(v1("KilogramPerHour"), "Kilogram per Hour", "kg/h", "kg/h")
          .factor("1/3600").alias(codes("KiloGM-PER-HR"))
        .unit(sp("KiloGM-PER-DAY"), "Kilogram per Day", "kg/d", "kg/d").factor("1/86400").alias(v2("KiloGM-PER-DAY"))
        .unit(sp("TONNE-PER-HR"), "Tonne per Hour", "t/h", "t/h").factor("1000/3600").alias(v2("TONNE-PER-HR"))
        .unit(sp("TONNE-PER-DAY"), "Tonne per Day", "t/d", "t/d").factor("1000/86400").alias(v2("TONNE-PER-DAY"))
        .unit(v1("PoundPerHour"), "Pound per Hour", "lb/h", "lb/h")
          .factor(LB + "/3600").alias(codes("LB-PER-HR"));

    // ---------------------------------------------------------------- electricity and magnetism

    // Electric current, base ampere (SI)
    catalog.kind(V1_KIND + "ElectricCurrentUnit", "Electric current")
        .base(v1("Ampere"), "Ampere", "A", "A").alias(codes("A"))
        .unit(sp("MicroA"), "Microampere", "uA", "µA").factor("0.000001").alias(v2("MicroA"))
        .unit(sp("MilliA"), "Milliampere", "mA", "mA").factor("0.001").alias(v2("MilliA"))
        .unit(sp("KiloA"), "Kiloampere", "kA", "kA").factor("1000").alias(v2("KiloA"));

    // Electric potential, base volt (SI)
    catalog.kind(V1_KIND + "EnergyPerElectricChargeUnit", "Voltage")
        .base(v1("Volt"), "Volt", "V", "V").alias(codes("V"))
        .unit(sp("MicroV"), "Microvolt", "uV", "µV").factor("0.000001").alias(v2("MicroV"))
        .unit(sp("MilliV"), "Millivolt", "mV", "mV").factor("0.001").alias(v2("MilliV"))
        .unit(sp("KiloV"), "Kilovolt", "kV", "kV").factor("1000").alias(v2("KiloV"));

    // Electric resistance, base ohm (SI)
    catalog.kind(V1_KIND + "ResistanceUnit", "Electric resistance")
        .base(v1("Ohm"), "Ohm", "Ohm", "Ω").alias(codes("OHM"))
        .unit(sp("MilliOHM"), "Milliohm", "mOhm", "mΩ").factor("0.001").alias(v2("MilliOHM"))
        .unit(sp("KiloOHM"), "Kiloohm", "kOhm", "kΩ").factor("1000").alias(v2("KiloOHM"))
        .unit(sp("MegaOHM"), "Megaohm", "MOhm", "MΩ").factor("1000000").alias(v2("MegaOHM"));

    // Electric conductance, base siemens (SI)
    catalog.kind(V1_KIND + "ConductanceUnit", "Electric conductance")
        .base(v1("Siemens"), "Siemens", "S", "S").alias(codes("S"))
        .unit(sp("MicroS"), "Microsiemens", "uS", "µS").factor("0.000001").alias(v2("MicroS"))
        .unit(sp("MilliS"), "Millisiemens", "mS", "mS").factor("0.001").alias(v2("MilliS"));

    // Electric conductivity (water quality), base siemens per meter
    catalog.kind(SP_KIND + "ElectricConductivity", "Electric conductivity")
        .base(sp("S-PER-M"), "Siemens per Meter", "S/m", "S/m").alias(v2("S-PER-M"))
        .unit(sp("MilliS-PER-M"), "Millisiemens per Meter", "mS/m", "mS/m").factor("0.001").alias(v2("MilliS-PER-M"))
        .unit(sp("MicroS-PER-CentiM"), "Microsiemens per Centimeter", "uS/cm", "µS/cm")
          .factor("0.0001").alias(v2("MicroS-PER-CentiM"))
        .unit(sp("MilliS-PER-CentiM"), "Millisiemens per Centimeter", "mS/cm", "mS/cm")
          .factor("0.1").alias(v2("MilliS-PER-CentiM"));

    // Electric charge, base coulomb (SI)
    catalog.kind(V1_KIND + "ElectricChargeUnit", "Electric charge")
        .base(v1("Coulomb"), "Coulomb", "C", "C").alias(codes("C"))
        .unit(sp("MilliA-HR"), "Milliampere Hour", "mAh", "mAh").factor("3.6").alias(v2("MilliA-HR"))
        .unit(v1("AmpereHour"), "Ampere Hour", "Ah", "Ah").factor("3600").alias(codes("A-HR"));

    // Capacitance, base farad (SI)
    catalog.kind(V1_KIND + "CapacitanceUnit", "Capacitance")
        .base(v1("Farad"), "Farad", "F", "F").alias(codes("FARAD"))
        .unit(v1("PicoFarad"), "Picofarad", "pF", "pF").factor("0.000000000001").alias(codes("PicoFARAD"))
        .unit(v1("NanoFarad"), "Nanofarad", "nF", "nF").factor("0.000000001").alias(codes("NanoFARAD"))
        .unit(v1("MicroFarad"), "Microfarad", "uF", "µF").factor("0.000001").alias(codes("MicroFARAD"));

    // Inductance, base henry (SI)
    catalog.kind(V1_KIND + "InductanceUnit", "Inductance")
        .base(v1("Henry"), "Henry", "H", "H").alias(codes("H"))
        .unit(v1("MicroHenry"), "Microhenry", "uH", "µH").factor("0.000001").alias(codes("MicroH"))
        .unit(v1("MilliHenry"), "Millihenry", "mH", "mH").factor("0.001").alias(codes("MilliH"));

    // Magnetic flux density, base tesla (SI). Gauss: B.8
    catalog.kind(V1_KIND + "MagneticFluxDensityUnit", "Magnetic flux density")
        .base(v1("Tesla"), "Tesla", "T", "T").alias(codes("T"))
        .unit(sp("MilliT"), "Millitesla", "mT", "mT").factor("0.001").alias(v2("MilliT"))
        .unit(v1("Gauss"), "Gauss", "G", "G").factor("0.0001").alias(codes("GAUSS"));

    catalog.kind(V1_KIND + "MagneticFieldStrengthUnit", "Magnetic field strength")
        .base(v1("AmperePerMeter"), "Ampere per Meter", "A/m", "A/m").alias(codes("A-PER-M"));

    catalog.kind(V1_KIND + "ElectricFieldStrengthUnit", "Electric field strength")
        .base(v1("VoltPerMeter"), "Volt per Meter", "V/m", "V/m").alias(codes("V-PER-M"));

    // ---------------------------------------------------------------- light

    catalog.kind(V1_KIND + "IlluminanceUnit", "Illuminance")
        .base(v1("Lux"), "Lux", "lx", "lx").alias(codes("LUX"))
        // footcandle = 1 lm/ft², B.8
        .unit(v1("FootCandle"), "Footcandle", "fc", "fc").factor("1/0.09290304").alias(codes("FC"));

    catalog.kind(V1_KIND + "LuminousFluxUnit", "Luminous flux")
        .base(v1("Lumen"), "Lumen", "lm", "lm").alias(codes("LM"));

    catalog.kind(V1_KIND + "LuminousIntensityUnit", "Luminous intensity")
        .base(v1("Candela"), "Candela", "cd", "cd").alias(codes("CD"));

    catalog.kind(V1_KIND + "LuminanceUnit", "Luminance")
        .base(v1("CandelaPerSquareMeter"), "Candela per Square Meter", "cd/m2", "cd/m²").alias(codes("CD-PER-M2"));

    // ---------------------------------------------------------------- chemistry and quality

    catalog.kind(V1_KIND + "AmountOfSubstanceUnit", "Amount of substance")
        .base(v1("Mole"), "Mole", "mol", "mol").alias(codes("MOL"));

    catalog.kind(V1_KIND + "MolarConcentrationUnit", "Molar concentration")
        .base(sp("MOL-PER-M3"), "Mole per Cubic Meter", "mol/m3", "mol/m³").alias(v2("MOL-PER-M3"))
        .unit(sp("MilliMOL-PER-L"), "Millimole per Liter", "mmol/L", "mmol/L").alias(v2("MilliMOL-PER-L"))
        .unit(sp("MOL-PER-L"), "Mole per Liter", "mol/L", "mol/L").factor("1000").alias(v2("MOL-PER-L"));

    // pH is logarithmic and only meaningful on its own scale
    catalog.kind(SP_KIND + "Acidity", "Acidity")
        .base(sp("PH"), "pH", "pH", "pH").alias(v2("PH"));

    // Turbidity: NTU (white light) and FNU (infrared, ISO 7027) are different methods
    catalog.kind(SP_KIND + "TurbidityNephelometric", "Turbidity (nephelometric, white light)")
        .base(sp("NTU"), "Nephelometric Turbidity Unit", "NTU", "NTU").alias(v2("NTU"));

    catalog.kind(SP_KIND + "TurbidityFormazinNephelometric", "Turbidity (formazin nephelometric, infrared)")
        .base(sp("FNU"), "Formazin Nephelometric Unit", "FNU", "FNU");

    // Dimensionless ratios, base 1
    catalog.kind(V1_KIND + "DimensionlessUnit", "Dimensionless ratio")
        .base(v1("Unitless"), "Unitless", "1", "1").alias(codes("UNITLESS"))
        .unit(v1("Percent"), "Percent", "%", "%").factor("0.01").alias(codes("PERCENT"))
        .unit(sp("PPTH"), "Per Mille", "permille", "‰").factor("0.001").alias(v2("PPTH"), v2("PERMILLE"))
        .unit(sp("PPM"), "Parts per Million", "ppm", "ppm").factor("0.000001").alias(v2("PPM"))
        .unit(sp("PPB"), "Parts per Billion", "ppb", "ppb").factor("0.000000001").alias(v2("PPB"));

    // Relative humidity is a percentage of saturation, not a fraction of a quantity
    catalog.kind(SP_KIND + "RelativeHumidity", "Relative humidity")
        .base(sp("PERCENT_RH"), "Percent Relative Humidity", "%RH", "%RH").alias(v2("PERCENT_RH"));

    // Gas detection: percentage of the lower explosive limit of the measured gas
    catalog.kind(SP_KIND + "LowerExplosiveLimitFraction", "Lower explosive limit fraction")
        .base(sp("PERCENT_LEL"), "Percent of Lower Explosive Limit", "%LEL", "%LEL");

    // ---------------------------------------------------------------- information

    // Information, base bit. Decimal (kB) and binary (KiB) prefixes
    catalog.kind(V1_KIND + "InformationEntropyUnit", "Information")
        .base(v1("Bit"), "Bit", "bit", "bit").alias(codes("BIT"))
        .unit(v1("Byte"), "Byte", "B", "B").factor("8").alias(codes("BYTE"))
        .unit(sp("KiloBYTE"), "Kilobyte", "kB", "kB").factor("8000").alias(v2("KiloBYTE"))
        .unit(sp("MegaBYTE"), "Megabyte", "MB", "MB").factor("8000000").alias(v2("MegaBYTE"))
        .unit(sp("GigaBYTE"), "Gigabyte", "GB", "GB").factor("8000000000").alias(v2("GigaBYTE"))
        .unit(sp("TeraBYTE"), "Terabyte", "TB", "TB").factor("8000000000000").alias(v2("TeraBYTE"))
        .unit(sp("KibiBYTE"), "Kibibyte", "KiB", "KiB").factor("8*1024").alias(v2("KibiBYTE"))
        .unit(sp("MebiBYTE"), "Mebibyte", "MiB", "MiB").factor("8*1024*1024").alias(v2("MebiBYTE"))
        .unit(sp("GibiBYTE"), "Gibibyte", "GiB", "GiB").factor("8*1024*1024*1024").alias(v2("GibiBYTE"));

    // Data rate, base bit per second
    catalog.kind(V1_KIND + "DataRateUnit", "Data rate")
        .base(v1("BitsPerSecond"), "Bit per Second", "bit/s", "bit/s").alias(codes("BIT-PER-SEC"))
        .unit(v1("KilobitsPerSecond"), "Kilobit per Second", "kbit/s", "kbit/s")
          .factor("1000").alias(codes("KiloBIT-PER-SEC"))
        .unit(v1("MegabitsPerSecond"), "Megabit per Second", "Mbit/s", "Mbit/s")
          .factor("1000000").alias(codes("MegaBIT-PER-SEC"))
        .unit(sp("GigaBIT-PER-SEC"), "Gigabit per Second", "Gbit/s", "Gbit/s")
          .factor("1000000000").alias(v2("GigaBIT-PER-SEC"));

    // ---------------------------------------------------------------- acoustics

    // Logarithmic levels cannot be converted with a linear factor, and the frequency
    // weightings are different measurements, so each has its own kind.
    catalog.kind(V1_KIND + "LogarithmicUnit", "Logarithmic level")
        .base(v1("Decibel"), "Decibel", "dB", "dB").alias(codes("DeciB"));

    catalog.kind(SP_KIND + "SoundLevelAWeighted", "Sound level (A-weighted)")
        .base(sp("DeciB_A"), "Decibel (A-weighted)", "dB(A)", "dB(A)").alias(v2("DeciB_A"));

    catalog.kind(SP_KIND + "SoundLevelCWeighted", "Sound level (C-weighted)")
        .base(sp("DeciB_C"), "Decibel (C-weighted)", "dB(C)", "dB(C)").alias(v2("DeciB_C"));

    return catalog.build();
  }
}
