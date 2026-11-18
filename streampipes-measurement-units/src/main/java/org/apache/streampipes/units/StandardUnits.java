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

/**
 * Frequently used units from the built-in catalog.
 *
 * <p>Use these constants instead of writing unit URIs in code. Each constant carries the
 * canonical identifier of the unit, so code that uses them keeps working when the canonical
 * identifiers move from QUDT to the StreamPipes namespace.</p>
 */
public final class StandardUnits {

  // temperature
  public static final MeasurementUnit KELVIN = unit("K");
  public static final MeasurementUnit DEGREE_CELSIUS = unit("DEG_C");
  public static final MeasurementUnit DEGREE_FAHRENHEIT = unit("DEG_F");

  // pressure
  public static final MeasurementUnit PASCAL = unit("PA");
  public static final MeasurementUnit HECTOPASCAL = unit("HectoPA");
  public static final MeasurementUnit KILOPASCAL = unit("KiloPA");
  public static final MeasurementUnit MILLIBAR = unit("MilliBAR");
  public static final MeasurementUnit BAR = unit("BAR");
  public static final MeasurementUnit PSI = unit("PSI");

  // length, area, volume
  public static final MeasurementUnit MILLIMETER = unit("MilliM");
  public static final MeasurementUnit METER = unit("M");
  public static final MeasurementUnit KILOMETER = unit("KiloM");
  public static final MeasurementUnit SQUARE_METER = unit("M2");
  public static final MeasurementUnit LITER = unit("L");
  public static final MeasurementUnit CUBIC_METER = unit("M3");

  // mass
  public static final MeasurementUnit GRAM = unit("GM");
  public static final MeasurementUnit KILOGRAM = unit("KiloGM");
  public static final MeasurementUnit TONNE = unit("TONNE");

  // time and frequency
  public static final MeasurementUnit MILLISECOND = unit("MilliSEC");
  public static final MeasurementUnit SECOND = unit("SEC");
  public static final MeasurementUnit MINUTE = unit("MIN");
  public static final MeasurementUnit HOUR = unit("HR");
  public static final MeasurementUnit HERTZ = unit("HZ");

  // motion
  public static final MeasurementUnit METER_PER_SECOND = unit("M-PER-SEC");
  public static final MeasurementUnit KILOMETER_PER_HOUR = unit("KiloM-PER-HR");
  public static final MeasurementUnit MILLIMETER_PER_SECOND = unit("MilliM-PER-SEC");
  public static final MeasurementUnit METER_PER_SECOND_SQUARED = unit("M-PER-SEC2");
  public static final MeasurementUnit DEGREE = unit("DEG");
  public static final MeasurementUnit RADIAN = unit("RAD");
  public static final MeasurementUnit REVOLUTION_PER_MINUTE = unit("REV-PER-MIN");

  // mechanics
  public static final MeasurementUnit NEWTON = unit("N");
  public static final MeasurementUnit NEWTON_METER = unit("N-M");

  // electricity, energy and power
  public static final MeasurementUnit AMPERE = unit("A");
  public static final MeasurementUnit VOLT = unit("V");
  public static final MeasurementUnit WATT = unit("W");
  public static final MeasurementUnit KILOWATT = unit("KiloW");
  public static final MeasurementUnit KILOWATT_HOUR = unit("KiloW-HR");
  public static final MeasurementUnit KILOVOLT_AMPERE = unit("KiloV-A");
  public static final MeasurementUnit JOULE = unit("J");

  // flow
  public static final MeasurementUnit LITER_PER_MINUTE = unit("L-PER-MIN");
  public static final MeasurementUnit CUBIC_METER_PER_HOUR = unit("M3-PER-HR");
  public static final MeasurementUnit KILOGRAM_PER_HOUR = unit("KiloGM-PER-HR");

  // ratios and counts
  public static final MeasurementUnit PERCENT = unit("PERCENT");
  public static final MeasurementUnit PARTS_PER_MILLION = unit("PPM");
  public static final MeasurementUnit PERCENT_RELATIVE_HUMIDITY = unit("PERCENT_RH");
  public static final MeasurementUnit COUNT = unit("NUM");
  public static final MeasurementUnit COUNT_PER_MINUTE = unit("NUM-PER-MIN");

  private StandardUnits() {
  }

  private static MeasurementUnit unit(String code) {
    return UnitCatalog.builtIn().get(UnitNamespace.STREAMPIPES_UNIT + code);
  }
}
