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

package org.apache.streampipes.model.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleTypeParserTest {

  private enum Mode { FAST, SLOW }

  @Test
  public void parsesNumbersAfterTrimming() {
    assertEquals(5, SimpleTypeParser.parse(" 5 ", Integer.class));
    assertEquals(3, SimpleTypeParser.parse("+3", Integer.class));
    assertEquals(7, SimpleTypeParser.parse(" 7", int.class));
    assertEquals(5L, SimpleTypeParser.parse("5", Long.class));
    assertEquals((short) 2, SimpleTypeParser.parse("2", Short.class));
    assertEquals(1000.0, SimpleTypeParser.parse("1e3", Double.class));
    assertEquals(5.0f, SimpleTypeParser.parse(" 5 ", Float.class));
    assertEquals(new BigDecimal("5.0"), SimpleTypeParser.parse("5.0", BigDecimal.class));
  }

  @Test
  public void rejectsInvalidNumbers() {
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("5.0", Integer.class));
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("", Integer.class));
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("abc", Double.class));
  }

  @Test
  public void parsesBooleansCaseInsensitively() {
    assertEquals(true, SimpleTypeParser.parse(" TRUE ", Boolean.class));
    assertEquals(false, SimpleTypeParser.parse("false", boolean.class));
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("yes", Boolean.class));
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("1", Boolean.class));
  }

  @Test
  public void keepsStringsUntrimmed() {
    assertEquals(" 5 ", SimpleTypeParser.parse(" 5 ", String.class));
    assertEquals("", SimpleTypeParser.parse("", String.class));
    assertEquals("x", SimpleTypeParser.parse("x", Object.class));
  }

  @Test
  public void convertsNullStringToNull() {
    assertNull(SimpleTypeParser.parse("null", String.class));
    assertNull(SimpleTypeParser.parse(" NULL ", Integer.class));
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("null", int.class));
  }

  @Test
  public void parsesEnumsAndCharacters() {
    assertEquals(Mode.SLOW, SimpleTypeParser.parse("SLOW ", Mode.class));
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("slow", Mode.class));
    assertEquals('a', SimpleTypeParser.parse("a", Character.class));
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("ab", Character.class));
  }

  @Test
  public void rejectsUnsupportedTypes() {
    assertThrows(IllegalArgumentException.class, () -> SimpleTypeParser.parse("x", Date.class));
  }
}
