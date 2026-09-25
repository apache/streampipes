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

package org.apache.streampipes.dataexplorer.influx.sanitize;

import org.apache.streampipes.dataexplorer.InfluxDbReservedKeywords;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InfluxNameSanitizerTest {

  @Test
  public void reservedKeywordGetsUnderscoreAppended() {
    assertEquals("name_", InfluxNameSanitizer.renameReservedKeywords("name"));
    assertEquals("SELECT_", InfluxNameSanitizer.renameReservedKeywords("SELECT"));
  }

  @Test
  public void reservedKeywordIsDetectedRegardlessOfCase() {
    assertTrue(InfluxNameSanitizer.isReservedKeyword("name"));
    assertTrue(InfluxNameSanitizer.isReservedKeyword("NAME"));
    assertTrue(InfluxNameSanitizer.isReservedKeyword("Name"));
    assertEquals("Name_", InfluxNameSanitizer.renameReservedKeywords("Name"));
  }

  @Test
  public void ordinaryRuntimeNameIsUnchanged() {
    assertFalse(InfluxNameSanitizer.isReservedKeyword("temperature"));
    assertEquals("temperature", InfluxNameSanitizer.renameReservedKeywords("temperature"));
    assertEquals("sensor_fault_flags", InfluxNameSanitizer.renameReservedKeywords("sensor_fault_flags"));
  }

  @Test
  public void nameContainingKeywordIsNotRenamed() {
    assertFalse(InfluxNameSanitizer.isReservedKeyword("names"));
    assertFalse(InfluxNameSanitizer.isReservedKeyword("selected"));
    assertFalse(InfluxNameSanitizer.isReservedKeyword("by_value"));
    assertEquals("names", InfluxNameSanitizer.renameReservedKeywords("names"));
  }

  @Test
  public void alreadySanitizedNameIsNotRenamedAgain() {
    assertFalse(InfluxNameSanitizer.isReservedKeyword("name_"));
    assertEquals("name_", InfluxNameSanitizer.renameReservedKeywords("name_"));
  }

  @Test
  public void everyReservedKeywordIsRenamed() {
    for (var keyword : InfluxDbReservedKeywords.KEYWORD_LIST) {
      assertTrue(InfluxNameSanitizer.isReservedKeyword(keyword), keyword);
      assertEquals(keyword + "_", InfluxNameSanitizer.renameReservedKeywords(keyword));
      var lowerCase = keyword.toLowerCase(Locale.ROOT);
      assertEquals(lowerCase + "_", InfluxNameSanitizer.renameReservedKeywords(lowerCase));
    }
  }
}
