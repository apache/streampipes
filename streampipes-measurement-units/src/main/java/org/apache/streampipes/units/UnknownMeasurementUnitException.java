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

/**
 * Thrown when a unit identifier is not part of the unit catalog.
 *
 * <p>Extends {@link IllegalStateException} because that is what unit lookups threw before, so
 * existing handlers keep working.</p>
 */
public class UnknownMeasurementUnitException extends IllegalStateException {

  private final String unitId;

  public UnknownMeasurementUnitException(String unitId) {
    super("Unknown measurement unit: " + unitId);
    this.unitId = unitId;
  }

  public String getUnitId() {
    return unitId;
  }
}
