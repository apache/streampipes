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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CorrectionValueTest {

  private Map<String, Object> event;

  private final String propertyNameBasicValue = "basicValue";
  private final String propertyNameOtherValue = "otherValue";

  @BeforeEach
  public void setUp() {

    EventSchema eventSchema = new EventSchema();
    EventProperty eventProperty = new EventPropertyPrimitive();
    eventProperty.setLabel(propertyNameBasicValue);
    eventProperty.setRuntimeName(propertyNameBasicValue);
    eventSchema.addEventProperty(eventProperty);

    EventProperty eventPropertyOther = new EventPropertyPrimitive();
    eventPropertyOther.setLabel(propertyNameBasicValue);
    eventPropertyOther.setRuntimeName(propertyNameBasicValue);
    eventSchema.addEventProperty(eventPropertyOther);

    event = new HashMap<>();
    event.put(propertyNameBasicValue, 100.0);
    event.put(propertyNameOtherValue, "Hello");
  }

  @Test
  public void testAdd() {

    var correctionRule = new CorrectionValueTransformationRule(List.of(propertyNameBasicValue), 10.0, "ADD");

    var resultEvent = correctionRule.apply(event);
    Assertions.assertNotNull(resultEvent);
    Assertions.assertEquals(110.0, resultEvent.get(propertyNameBasicValue));
  }

  @Test
  public void testSubtract() {

    var correctionRule = new CorrectionValueTransformationRule(List.of(propertyNameBasicValue), 10.0, "SUBTRACT");
    var resultEvent = correctionRule.apply(event);
    Assertions.assertNotNull(resultEvent);
    Assertions.assertEquals(90.0, resultEvent.get(propertyNameBasicValue));
  }

  @Test
  public void testMultiply() {

    var correctionRule = new CorrectionValueTransformationRule(List.of(propertyNameBasicValue), 1.5, "MULTIPLY");
    var resultEvent = correctionRule.apply(event);
    Assertions.assertNotNull(resultEvent);
    Assertions.assertEquals(150.0, resultEvent.get(propertyNameBasicValue));
  }

  @Test
  public void testDivide() {

    var correctionRule = new CorrectionValueTransformationRule(List.of(propertyNameBasicValue), 5, "DIVIDE");
    var resultEvent = correctionRule.apply(event);
    Assertions.assertNotNull(resultEvent);
    Assertions.assertEquals(20.0, resultEvent.get(propertyNameBasicValue));
  }

  @Test
  public void testDivideByZero() {

    var correctionRule = new CorrectionValueTransformationRule(List.of(propertyNameBasicValue), 0.0, "DIVIDE");
    var resultEvent = correctionRule.apply(event);
    Assertions.assertNotNull(resultEvent);
    Assertions.assertEquals(Double.POSITIVE_INFINITY, resultEvent.get(propertyNameBasicValue));
  }

  @Test
  public void testNonNumericValue() {

    var correctionRule = new CorrectionValueTransformationRule(List.of(propertyNameOtherValue), 10.0, "ADD");
    assertThrows(RuntimeException.class, () -> correctionRule.apply(event).get(propertyNameOtherValue));

  }

  @Test
  public void testUnsupportedOperation() {

    var correctionRule = new CorrectionValueTransformationRule(List.of(propertyNameBasicValue), 10.0, "TEST");
    var resultEvent = correctionRule.apply(event);
    Assertions.assertNotNull(resultEvent);
    Assertions.assertEquals(100.0, resultEvent.get(propertyNameBasicValue));
  }
}
