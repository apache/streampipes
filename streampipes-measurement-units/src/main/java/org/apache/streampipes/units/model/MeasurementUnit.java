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

package org.apache.streampipes.units.model;

import java.net.URI;

/**
 * A measurement unit from the unit catalog.
 *
 * <p>The record components match the fields of the former jQUDT {@code Unit} class, so the
 * JSON returned by the REST API keeps its shape.</p>
 *
 * @param resource     the canonical identifier of the unit (a URI)
 * @param label        a human-readable name
 * @param abbreviation an ASCII abbreviation, e.g. {@code degC}
 * @param symbol       the unit symbol, e.g. {@code °C}
 * @param type         the identifier of the {@link QuantityKind} of the unit
 * @param multiplier   the conversion to the base unit of the quantity kind
 */
public record MeasurementUnit(URI resource,
                              String label,
                              String abbreviation,
                              String symbol,
                              URI type,
                              Multiplier multiplier) {

  @Override
  public String toString() {
    return resource.toString();
  }
}
