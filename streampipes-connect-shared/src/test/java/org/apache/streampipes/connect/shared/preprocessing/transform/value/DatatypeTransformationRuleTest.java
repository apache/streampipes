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

import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatatypeTransformationRuleTest {

  private static final String PROPERTY = "property";

  private final DatatypeTransformationRule rule =
      new DatatypeTransformationRule("test-adapter", PROPERTY, XSD.DOUBLE.toString());

  @Test
  public void preserveMissingValue() {
    Map<String, Object> event = new HashMap<>();

    var result = rule.apply(event);

    assertFalse(result.containsKey(PROPERTY));
  }

  @Test
  public void preserveNullValue() {
    Map<String, Object> event = new HashMap<>();
    event.put(PROPERTY, null);

    var result = rule.apply(event);

    assertTrue(result.containsKey(PROPERTY));
    assertNull(result.get(PROPERTY));
  }
}
