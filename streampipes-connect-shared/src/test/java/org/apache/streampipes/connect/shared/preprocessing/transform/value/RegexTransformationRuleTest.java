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

package org.apache.streampipes.connect.shared.preprocessing.transform.value;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexTransformationRuleTest {
  private static final String PROPERTY = "property";

  private Map<String, Object> event;

  @BeforeEach
  public void setUp() {
    event = new HashMap<>();
  }

  @Test
  void applyTransformation_ReplaceAllComma() {
    event.put(PROPERTY, "a,b,");
    var rule = getRegexTransformationRule(",", "", true);

    rule.apply(event);

    assertEquals("ab", event.get(PROPERTY));
  }

  @Test
  void applyTransformation_ReplaceOneComma() {
    event.put(PROPERTY, "a,b,");
    var rule = getRegexTransformationRule(",", "", false);

    rule.apply(event);

    assertEquals("ab,", event.get(PROPERTY));
  }

  @Test
  void applyTransformation_NoReplacement() {
    event.put(PROPERTY, "ab");
    var rule = getRegexTransformationRule(",", "", false);

    rule.apply(event);

    assertEquals("ab", event.get(PROPERTY));
  }

  @Test
  void applyTransformation_WrongDataType() {
    event.put(PROPERTY, 5);
    var rule = getRegexTransformationRule(",", "", false);

    rule.apply(event);

    assertEquals("", event.get(PROPERTY));
  }

  @Test
  void applyTransformation_ValueNotPresent() {
    var rule = getRegexTransformationRule(",", "", false);

    rule.apply(event);

    assertEquals("", event.get(PROPERTY));
  }

  private RegexTransformationRule getRegexTransformationRule(
      String regex,
      String replaceWith,
      boolean replaceAll
  ) {
    return new RegexTransformationRule(List.of(PROPERTY), regex, replaceWith, replaceAll);
  }
}