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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CorrectionValueTest {

  private Map<String, Object> event;

  private final String doubleProperty = "basicValue";
  private final String stringProperty = "otherValue";

  @BeforeEach
  public void setUp() {
    event = new HashMap<>();
    event.put(doubleProperty, 100.0);
    event.put(stringProperty, "Hello");
  }

  @Test
  public void testAdd() {

    var correctionRule = new CorrectionValueTransformationRule(
         List.of(doubleProperty),
         10.0,
         "ADD"
     );

    var resultEvent = correctionRule.apply(event);
    assertNotNull(resultEvent);
    assertEquals(110.0, resultEvent.get(doubleProperty));
  }

  @Test
  public void testSubtract() {

    var correctionRule = new CorrectionValueTransformationRule(
        List.of(doubleProperty),
        10.0,
        "SUBTRACT"
    );
    var resultEvent = correctionRule.apply(event);
    assertNotNull(resultEvent);
    assertEquals(90.0, resultEvent.get(doubleProperty));
  }

  @Test
  public void testMultiply() {

    var correctionRule = new CorrectionValueTransformationRule(
        List.of(doubleProperty),
        1.5,
        "MULTIPLY"
    );
    var resultEvent = correctionRule.apply(event);
    assertNotNull(resultEvent);
    assertEquals(150.0, resultEvent.get(doubleProperty));
  }

  @Test
  public void testDivide() {

    var correctionRule = new CorrectionValueTransformationRule(
        List.of(doubleProperty),
        5,
        "DIVIDE"
    );
    var resultEvent = correctionRule.apply(event);
    assertNotNull(resultEvent);
    assertEquals(20.0, resultEvent.get(doubleProperty));
  }

  @Test
  public void testDivideByZero() {

    var correctionRule = new CorrectionValueTransformationRule(
        List.of(doubleProperty),
        0.0,
        "DIVIDE"
    );
    var resultEvent = correctionRule.apply(event);
    assertNotNull(resultEvent);
    assertEquals(Double.POSITIVE_INFINITY, resultEvent.get(doubleProperty));
  }

  @Test
  public void testNonNumericValue() {

    var correctionRule = new CorrectionValueTransformationRule(
        List.of(stringProperty),
        10.0,
        "ADD"
    );
    assertThrows(
        RuntimeException.class,
        () -> correctionRule.apply(event).get(stringProperty)
    );
  }

  @Test
  public void testUnsupportedOperation() {

    var correctionRule = new CorrectionValueTransformationRule(
        List.of(doubleProperty),
        10.0,
        "TEST"
    );
    var resultEvent = correctionRule.apply(event);
    assertNotNull(resultEvent);
    assertEquals(100.0, resultEvent.get(doubleProperty));
  }
}
