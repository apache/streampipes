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

import org.apache.streampipes.units.model.MeasurementUnit;

/**
 * Converts values between two units of the same quantity kind.
 */
public final class UnitConverter {

  private UnitConverter() {
  }

  /**
   * Creates a reusable conversion. Prefer this over {@link #convert} when converting many
   * values, e.g. in an adapter or a pipeline element: the units are checked once.
   *
   * @throws IllegalArgumentException if the units belong to different quantity kinds
   */
  public static UnitConversion between(MeasurementUnit from, MeasurementUnit to) {
    return new UnitConversion(from, to);
  }

  /**
   * Converts a single value.
   *
   * @param value the value, expressed in {@code from}
   * @param from  the unit of the value
   * @param to    the target unit
   * @return the value expressed in {@code to}
   * @throws IllegalArgumentException if the units belong to different quantity kinds
   */
  public static double convert(double value, MeasurementUnit from, MeasurementUnit to) {
    return between(from, to).applyAsDouble(value);
  }

  /**
   * Returns whether values can be converted between the two units.
   */
  public static boolean isConvertible(MeasurementUnit from, MeasurementUnit to) {
    return from.type().equals(to.type());
  }
}
