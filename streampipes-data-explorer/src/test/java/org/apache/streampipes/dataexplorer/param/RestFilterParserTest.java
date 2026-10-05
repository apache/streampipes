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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestFilterParserTest {

  @Test
  public void acceptsPlainFilterCondition() {
    assertDoesNotThrow(() -> RestFilterParser.parse(null, null, "value;=;1", null));
    assertDoesNotThrow(() -> RestFilterParser.parse(null, null, "temperature.a;>;10", null));
  }

  @Test
  public void rejectsFilterFieldThatBreaksOutOfTheIdentifier() {
    assertThrows(IllegalArgumentException.class,
        () -> RestFilterParser.parse(null, null, "value\" FROM \"other\" WHERE \"x;=;1", null));
  }

  @Test
  public void rejectsFilterFieldInJsonExpression() {
    String expression = "{\"type\":\"group\",\"operator\":\"AND\",\"children\":[{\"type\":\"condition\","
        + "\"field\":\"value\\\" FROM \\\"other\\\" WHERE \\\"x\","
        + "\"operator\":\"=\",\"condition\":\"1\"}]}";
    var thrown = assertThrows(IllegalArgumentException.class,
        () -> RestFilterParser.parse(null, null, null, expression));
    // Proves the field reached identifier validation rather than failing earlier at JSON parsing.
    assertEquals("Invalid query identifier", thrown.getMessage());
  }
}
