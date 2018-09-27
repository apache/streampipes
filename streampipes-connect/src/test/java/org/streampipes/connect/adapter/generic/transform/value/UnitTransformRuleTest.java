/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic.transform.value;

import org.junit.Test;
import org.streampipes.model.connect.unit.UnitDescription;
import org.streampipes.model.schema.*;

import java.util.*;

import static org.junit.Assert.assertEquals;

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
        event.put("list",subEvent);

        List<String> keys = new ArrayList<>();
        keys.add(eventPropertyList.getPropertyId());
        keys.add(eventPropertyValue.getPropertyId());

        UnitTransformationRule unitTransformationRule = new UnitTransformationRule(eventSchema, keys,
                "http://qudt.org/vocab/unit#DegreeCelsius", "http://qudt.org/vocab/unit#Kelvin");

        Map result = unitTransformationRule.transform(event);

        assertEquals(1, result.keySet().size());
        assertEquals(273.15, ((Map) result.get(eventPropertyList.getRuntimeName())).get(eventPropertyValue.getRuntimeName()));
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
        event.put("mainKey",subEvent);

        List<String> keys = new ArrayList<>();
        keys.add(eventPropertyMainKey.getPropertyId());
        keys.add(eventPropertyValue.getPropertyId());

        UnitTransformationRule unitTransformationRule = new UnitTransformationRule(eventSchema, keys,
                "http://qudt.org/vocab/unit#DegreeCelsius",          "http://qudt.org/vocab/unit#Kelvin");

        Map result = unitTransformationRule.transform(event);

        assertEquals(1, result.keySet().size());
        assertEquals(283.15, ((Map) result.get(eventPropertyMainKey.getRuntimeName())).get(eventPropertyValue.getRuntimeName()));
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
        keys.add(eventPropertyValue2.getPropertyId());

        UnitTransformationRule unitTransformationRule = new UnitTransformationRule(eventSchema, keys,
                "http://qudt.org/vocab/unit#DegreeCelsius", "http://qudt.org/vocab/unit#Kelvin");
        Map<String, Object> event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", 10.0);

        Map result = unitTransformationRule.transform(event);
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
