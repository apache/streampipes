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

package org.apache.streampipes.dataexplorer.param;

import org.apache.streampipes.dataexplorer.api.query.QuerySpec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestQueryParameterValidatorTest {

  @Test
  public void testAcceptsSafeTimeIntervals() {
    assertDoesNotThrow(() -> new QuerySpec.TimeInterval("1ms"));
    assertDoesNotThrow(() -> new QuerySpec.TimeInterval("1s"));
    assertDoesNotThrow(() -> new QuerySpec.TimeInterval("1m"));
    assertDoesNotThrow(() -> new QuerySpec.TimeInterval("1h"));
    assertDoesNotThrow(() -> new QuerySpec.TimeInterval("1d"));
    assertDoesNotThrow(() -> new QuerySpec.TimeInterval("1w"));
  }

  @Test
  public void testRejectsUnsafeTimeIntervals() {
    assertThrows(IllegalArgumentException.class,
        () -> new QuerySpec.TimeInterval("1 h"));
    assertThrows(IllegalArgumentException.class,
        () -> new QuerySpec.TimeInterval("1H"));
    assertThrows(IllegalArgumentException.class,
        () -> new QuerySpec.TimeInterval("-1h"));
    assertThrows(IllegalArgumentException.class,
        () -> new QuerySpec.TimeInterval("1month"));
    assertThrows(IllegalArgumentException.class,
        () -> new QuerySpec.TimeInterval("1h)"));
    assertThrows(IllegalArgumentException.class,
        () -> new QuerySpec.TimeInterval("1h;SHOW"));
  }

  @Test
  public void testAcceptsSafeIdentifiers() {
    assertDoesNotThrow(() -> RestQueryParameterValidator.requireSafeIdentifier("sensorId"));
    assertDoesNotThrow(() -> RestQueryParameterValidator.requireSafeIdentifier("_sensorId"));
    assertDoesNotThrow(() -> RestQueryParameterValidator.requireSafeIdentifier("sensor_id_2"));
    assertDoesNotThrow(() -> RestQueryParameterValidator.requireSafeIdentifier("sensor-id"));
    assertDoesNotThrow(() -> RestQueryParameterValidator.requireSafeIdentifier("sensor:id"));
    assertDoesNotThrow(() -> RestQueryParameterValidator.requireSafeIdentifier("sensor$id"));
    assertDoesNotThrow(() -> RestQueryParameterValidator.requireSafeIdentifier("sensor$id"));
  }

  @Test
  public void testRejectsUnsafeIdentifiers() {
    assertThrows(IllegalArgumentException.class,
        () -> RestQueryParameterValidator.requireSafeIdentifier("time(1h)"));
    assertThrows(IllegalArgumentException.class,
        () -> RestQueryParameterValidator.requireSafeIdentifier("sensor id"));
    assertThrows(IllegalArgumentException.class,
        () -> RestQueryParameterValidator.requireSafeIdentifier("SHOW"));
    assertThrows(IllegalArgumentException.class,
        () -> RestQueryParameterValidator.requireSafeIdentifier("show"));
    assertThrows(IllegalArgumentException.class,
        () -> RestQueryParameterValidator.requireSafeIdentifier("sensorId;SHOW"));
  }
}
