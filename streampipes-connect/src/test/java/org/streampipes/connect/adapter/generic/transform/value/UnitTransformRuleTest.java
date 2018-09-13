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
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class UnitTransformRuleTest {

    @Test
    public void transformSimple() {
        Map<String, Object> event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", 10.0);

        EventSchema eventSchema = new EventSchema();

        List<String> keys = new ArrayList<>();
        keys.add("value1");

        UnitTransformationRule unitTransformationRule = new UnitTransformationRule(eventSchema, keys, "Degree Celsius", "Kelvin");

        Map result = unitTransformationRule.transform(event);

        assertEquals(2, result.keySet().size());
        assertEquals(273.15, result.get(keys.get(0)));
    }


    @Test
    public void transformNested() {
        Map<String, Object> event = new HashMap<>();
        EventSchema eventSchema = new EventSchema();


        EventPropertyNested eventPropertyMainKey = new EventPropertyNested();
        eventPropertyMainKey.setLabel("mainKey");
        eventPropertyMainKey.setRuntimeName("mainKey");
        EventProperty eventPropertyValue = new EventPropertyPrimitive();
        eventPropertyValue.setLabel("value");
        eventPropertyValue.setRuntimeName("value");
        eventPropertyMainKey.setEventProperties(Collections.singletonList(eventPropertyValue));
        eventSchema.setEventProperties(Collections.singletonList(eventPropertyMainKey));

        Map<String, Object> subEvent = new HashMap<>();
        subEvent.put("value", 10.0);

        event.put("mainKey",subEvent);

        List<String> keys = new ArrayList<>();
        keys.add("mainKey");
        keys.add("value");

        UnitTransformationRule unitTransformationRule = new UnitTransformationRule(eventSchema, keys, "Degree Celsius", "Kelvin");

        Map result = unitTransformationRule.transform(event);

        assertEquals(1, result.keySet().size());
        assertEquals(283.15, ((Map) result.get(keys.get(0))).get(keys.get(1))) ;
    }


    @Test
    public void transformMulti() {
        List<String> keys = new ArrayList<>();
        EventSchema eventSchema = new EventSchema();

        keys.add("value1");
        UnitTransformationRule unitTransformationRule = new UnitTransformationRule(eventSchema, keys, "Degree Celsius", "Kelvin");

        Map<String, Object> event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", 10.0);

        Map result = unitTransformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(273.15, result.get(keys.get(0)));


        event = new HashMap<>();
        event.put("value1", 20.0);
        event.put("value2", 20.0);

        result = unitTransformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(293.15, result.get(keys.get(0)));


        event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", 10.0);

        result = unitTransformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(273.15, result.get(keys.get(0)));
    }

}
