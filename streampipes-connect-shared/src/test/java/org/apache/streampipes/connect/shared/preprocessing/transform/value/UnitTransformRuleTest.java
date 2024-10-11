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

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTransformRuleTest {

  private static final String PARENT_PROPERTY = "parentProperty";
  private static final String CHILD_PROPERTY = "childProperty";
  private static final String PROPERTY_ONE = "property1";
  private static final String PROPERTY_TWO = "property2";

  private static final String UNIT_DEGREE_CELSIUS = "http://qudt.org/vocab/unit#DegreeCelsius";
  private static final String UNIT_KELVIN = "http://qudt.org/vocab/unit#Kelvin";

  @Test
  public void transformList() {
    Map<String, Object> event = new HashMap<>();
    Map<String, Object> subEvent = new HashMap<>();
    subEvent.put(CHILD_PROPERTY, 0.0);
    event.put(PARENT_PROPERTY, subEvent);

    var unitTransformationRule = new UnitTransformationRule(
        List.of(PARENT_PROPERTY, CHILD_PROPERTY),
        UNIT_DEGREE_CELSIUS,
        UNIT_KELVIN
    );

    var result = unitTransformationRule.apply(event);

    assertEquals(
        1,
        result.keySet()
              .size()
    );

    assertEquals(273.15, ((Map<String, Object>) result.get(PARENT_PROPERTY))
        .get(CHILD_PROPERTY));
  }


  @Test
  public void transformNested() {
    Map<String, Object> event = new HashMap<>();
    Map<String, Object> subEvent = new HashMap<>();
    subEvent.put(CHILD_PROPERTY, 10.0);
    event.put(PARENT_PROPERTY, subEvent);

    var unitTransformationRule = new UnitTransformationRule(
        List.of(PARENT_PROPERTY, CHILD_PROPERTY),
        UNIT_DEGREE_CELSIUS,
        UNIT_KELVIN
    );

    var result = unitTransformationRule.apply(event);

    assertEquals(
        1,
        result.keySet()
              .size()
    );
    assertEquals(283.15, ((Map<String, Object>) result.get(PARENT_PROPERTY))
        .get(CHILD_PROPERTY));
  }


  @Test
  public void transformMultiEvent() {

    var unitTransformationRule = new UnitTransformationRule(
        List.of(PROPERTY_TWO),
        UNIT_DEGREE_CELSIUS,
        UNIT_KELVIN
    );

    Map<String, Object> event = getEventWithTwoProperties(0.0, 10.0);

    var result = unitTransformationRule.apply(event);
    assertEquals(
        2,
        result.keySet()
              .size()
    );
    assertEquals(283.15, result.get(PROPERTY_TWO));


    event = getEventWithTwoProperties(20.0, 20.0);

    result = unitTransformationRule.apply(event);
    assertEquals(
        2,
        result.keySet()
              .size()
    );
    assertEquals(293.15, result.get(PROPERTY_TWO));


    event = getEventWithTwoProperties(0.0, 0.0);

    result = unitTransformationRule.apply(event);
    assertEquals(
        2,
        result.keySet()
              .size()
    );
    assertEquals(273.15, result.get(PROPERTY_TWO));
  }

  private Map<String, Object> getEventWithTwoProperties(double value1, double value2) {
    Map<String, Object> event = new HashMap<>();
    event.put(PROPERTY_ONE, value1);
    event.put(PROPERTY_TWO, value2);
    return event;
  }

}
