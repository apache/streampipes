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

package org.apache.streampipes.integration.adapters.opcua.contract;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ExtensionObjectAssertions {

  private ExtensionObjectAssertions() {
  }

  public static void assertNoDecodeError(Map<String, Object> record, String message) {
    assertFalse(record.containsKey("_decodeError"), message);
  }

  public static void assertExtensionObjectRecord(Map<String, Object> record,
                                                 short expectedInt16,
                                                 double expectedDouble,
                                                 String expectedString,
                                                 boolean expectedBoolean,
                                                 Long expectedUInt32) {
    assertTrue(record.containsKey("Int16Field"), "Missing Int16Field");
    assertTrue(record.containsKey("DoubleField"), "Missing DoubleField");
    assertTrue(record.containsKey("StringField"), "Missing StringField");
    assertTrue(record.containsKey("BooleanField"), "Missing BooleanField");

    Object int16Field = record.get("Int16Field");
    assertNotNull(int16Field, "Int16Field must not be null");
    assertInstanceOf(Number.class, int16Field, "Int16Field must be numeric");
    assertEquals(expectedInt16, ((Number) int16Field).shortValue(), "Unexpected Int16Field value");

    Object doubleField = record.get("DoubleField");
    assertNotNull(doubleField, "DoubleField must not be null");
    assertInstanceOf(Number.class, doubleField, "DoubleField must be numeric");
    assertEquals(expectedDouble, ((Number) doubleField).doubleValue(), "Unexpected DoubleField value");

    assertEquals(expectedString, String.valueOf(record.get("StringField")), "Unexpected StringField value");
    assertEquals(expectedBoolean, record.get("BooleanField"), "Unexpected BooleanField value");

    if (expectedUInt32 == null) {
      assertFalse(record.containsKey("UInt32Field"), "UInt32Field must not be present");
    } else {
      Object uint32Field = record.get("UInt32Field");
      assertNotNull(uint32Field, "UInt32Field must not be null");
      assertInstanceOf(Number.class, uint32Field, "UInt32Field must be numeric");
      assertEquals(expectedUInt32.longValue(), ((Number) uint32Field).longValue(), "Unexpected UInt32Field value");
    }
  }
}

