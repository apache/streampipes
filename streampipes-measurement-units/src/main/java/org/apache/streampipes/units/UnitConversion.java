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

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * A conversion from one unit to another unit of the same quantity kind.
 *
 * <p>Create it once, when a pipeline element or adapter starts, and apply it to every event.
 * Creating it fails if the units cannot be converted into each other.</p>
 *
 * @param from the unit of the input values
 * @param to   the unit of the output values
 */
public record UnitConversion(MeasurementUnit from, MeasurementUnit to) implements DoubleUnaryOperator {

  /**
   * @throws IllegalArgumentException if the units belong to different quantity kinds
   */
  public UnitConversion {
    Objects.requireNonNull(from, "Source unit must not be null");
    Objects.requireNonNull(to, "Target unit must not be null");
    if (!from.type().equals(to.type())) {
      throw new IllegalArgumentException(
          "Cannot convert from %s to %s: the units measure different quantities (%s, %s)".formatted(
              from.resource(), to.resource(), from.type(), to.type()));
    }
  }

  @Override
  public double applyAsDouble(double value) {
    if (from.resource().equals(to.resource())) {
      return value;
    }
    double valueInBaseUnit = value * from.multiplier().multiplier() + from.multiplier().offset();
    return (valueInBaseUnit - to.multiplier().offset()) / to.multiplier().multiplier();
  }
}
