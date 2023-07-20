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

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class UnitTransformRuleTest {

  @Test
  public void transformList() {
    EventSchema eventSchema = new EventSchema();
    EventPropertyList eventPropertyList = new EventPropertyList();
    eventPropertyList.setRuntimeName("list");
    EventProperty eventPropertyValue = new EventPropertyPrimitive();
    eventPropertyValue.setLabel("value");
    eventPropertyValue.setRuntimeName("value");
    eventPropertyList.setEventProperty(eventPropertyValue);
    eventSchema.setEventProperties(Collections.singletonList(eventPropertyList));

    Map<String, Object> event = new HashMap<>();
    Map<String, Object> subEvent = new HashMap<>();
    subEvent.put("value", 0.0);
    event.put("list", subEvent);

    List<String> keys = new ArrayList<>();
    keys.add("list");
    keys.add("value");

    UnitTransformationRule unitTransformationRule = new UnitTransformationRule(keys,
        "http://qudt.org/vocab/unit#DegreeCelsius", "http://qudt.org/vocab/unit#Kelvin");

    var result = unitTransformationRule.transform(event);

    assertEquals(1, result.keySet().size());
    assertEquals(273.15,
        ((Map<String, Object>) result.get(eventPropertyList.getRuntimeName()))
            .get(eventPropertyValue.getRuntimeName()));
  }


  @Test
  public void transformNested() {
    EventSchema eventSchema = new EventSchema();
    EventPropertyNested eventPropertyMainKey = new EventPropertyNested();
    eventPropertyMainKey.setLabel("mainKey");
    eventPropertyMainKey.setRuntimeName("mainKey");
    EventProperty eventPropertyValue = new EventPropertyPrimitive();
    eventPropertyValue.setLabel("value");
    eventPropertyValue.setRuntimeName("value");
    eventPropertyMainKey.setEventProperties(Collections.singletonList(eventPropertyValue));
    eventSchema.setEventProperties(Collections.singletonList(eventPropertyMainKey));

    Map<String, Object> event = new HashMap<>();
    Map<String, Object> subEvent = new HashMap<>();
    subEvent.put("value", 10.0);
    event.put("mainKey", subEvent);

    List<String> keys = new ArrayList<>();
    keys.add("mainKey");
    keys.add("value");

    UnitTransformationRule unitTransformationRule = new UnitTransformationRule(keys,
        "http://qudt.org/vocab/unit#DegreeCelsius", "http://qudt.org/vocab/unit#Kelvin");

    var result = unitTransformationRule.transform(event);

    assertEquals(1, result.keySet().size());
    assertEquals(283.15,
        ((Map<String, Object>) result.get(eventPropertyMainKey.getRuntimeName()))
            .get(eventPropertyValue.getRuntimeName()));
  }


  @Test
  public void transformMultiEvent() {
    EventSchema eventSchema = new EventSchema();
    EventProperty eventPropertyValue1 = new EventPropertyPrimitive();
    eventPropertyValue1.setLabel("value1");
    eventPropertyValue1.setRuntimeName("value1");
    EventProperty eventPropertyValue2 = new EventPropertyPrimitive();
    eventPropertyValue2.setLabel("value2");
    eventPropertyValue2.setRuntimeName("value2");
    eventSchema.addEventProperty(eventPropertyValue1);
    eventSchema.addEventProperty(eventPropertyValue2);

    List<String> keys = new ArrayList<>();
    keys.add("value2");

    UnitTransformationRule unitTransformationRule = new UnitTransformationRule(keys,
        "http://qudt.org/vocab/unit#DegreeCelsius", "http://qudt.org/vocab/unit#Kelvin");
    Map<String, Object> event = new HashMap<>();
    event.put("value1", 0.0);
    event.put("value2", 10.0);

    var result = unitTransformationRule.transform(event);
    assertEquals(2, result.keySet().size());
    assertEquals(283.15, result.get(eventPropertyValue2.getLabel()));


    event = new HashMap<>();
    event.put("value1", 20.0);
    event.put("value2", 20.0);

    result = unitTransformationRule.transform(event);
    assertEquals(2, result.keySet().size());
    assertEquals(293.15, result.get(eventPropertyValue2.getRuntimeName()));


    event = new HashMap<>();
    event.put("value1", 0.0);
    event.put("value2", 0.0);

    result = unitTransformationRule.transform(event);
    assertEquals(2, result.keySet().size());
    assertEquals(273.15, result.get(eventPropertyValue2.getRuntimeName()));
  }

}
