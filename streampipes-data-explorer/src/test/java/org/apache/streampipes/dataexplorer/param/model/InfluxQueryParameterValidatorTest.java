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

package org.apache.streampipes.dataexplorer.param.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InfluxQueryParameterValidatorTest {

  @Test
  public void testAcceptsSafeTimeIntervals() {
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeTimeInterval("1ms"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeTimeInterval("1s"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeTimeInterval("1m"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeTimeInterval("1h"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeTimeInterval("1d"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeTimeInterval("1w"));
  }

  @Test
  public void testRejectsUnsafeTimeIntervals() {
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeTimeInterval("1 h"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeTimeInterval("1H"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeTimeInterval("-1h"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeTimeInterval("1month"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeTimeInterval("1h)"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeTimeInterval("1h;SHOW"));
  }

  @Test
  public void testAcceptsSafeIdentifiers() {
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeIdentifier("sensorId"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeIdentifier("_sensorId"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeIdentifier("sensor_id_2"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeIdentifier("sensor-id"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeIdentifier("sensor:id"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeIdentifier("sensor$id"));
    assertDoesNotThrow(() -> InfluxQueryParameterValidator.requireSafeIdentifier("sensor$id"));
  }

  @Test
  public void testRejectsUnsafeIdentifiers() {
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeIdentifier("time(1h)"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeIdentifier("sensor id"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeIdentifier("SHOW"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeIdentifier("show"));
    assertThrows(IllegalArgumentException.class,
        () -> InfluxQueryParameterValidator.requireSafeIdentifier("sensorId;SHOW"));
  }
}
