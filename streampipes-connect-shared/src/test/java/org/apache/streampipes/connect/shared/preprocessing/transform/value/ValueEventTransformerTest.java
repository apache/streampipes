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

public class ValueEventTransformerTest {

  private static final String UNIT_DEGREE_CELSIUS = "http://qudt.org/vocab/unit#DegreeCelsius";
  private static final String UNIT_KELVIN = "http://qudt.org/vocab/unit#Kelvin";
  private static final String EVENT_PROPEPRTY = "property";

  @Test
  public void transform() {

    Map<String, Object> event = new HashMap<>();
    event.put(EVENT_PROPEPRTY, 273.15);

    var unitTransformationRule = new UnitTransformationRule(
        List.of(EVENT_PROPEPRTY),
        UNIT_KELVIN,
        UNIT_DEGREE_CELSIUS
    );

    var correctionRule = new CorrectionValueTransformationRule(
        List.of(EVENT_PROPEPRTY),
        10.0,
        "ADD"
    );

    var rules = List.of(unitTransformationRule, correctionRule);

    for (var rule : rules) {
      event = rule.apply(event);
    }

    assertEquals(10.0, event.get(EVENT_PROPEPRTY));
  }

}
