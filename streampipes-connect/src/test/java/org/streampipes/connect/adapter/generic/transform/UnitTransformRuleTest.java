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

package org.streampipes.connect.adapter.generic.transform;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UnitTransformRuleTest {

    @Test
    public void transformSimple() {
        Map<String, Object> event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", 10);


        List<String> keys = new ArrayList<>();
        keys.add("value1");

        UnitTransformationRule unitTransformationRule = new UnitTransformationRule(keys, "Degree Celsius", "Kelvin");

        Map result = unitTransformationRule.transform(event);

        assertEquals(2, result.keySet().size());
        assertEquals(273.15, result.get(keys.get(0))) ;

    }


    @Test
    public void transformNested() {
        Map<String, Object> event = new HashMap<>();

        Map<String, Object> subEvent = new HashMap<>();
        subEvent.put("value", 10.0);

        event.put("mainKey",subEvent);

        List<String> keys = new ArrayList<>();
        keys.add("mainKey");
        keys.add("value");

        UnitTransformationRule unitTransformationRule = new UnitTransformationRule(keys, "Degree Celsius", "Kelvin");

        Map result = unitTransformationRule.transform(event);

        assertEquals(1, result.keySet().size());
        assertEquals(283.15, ((Map) result.get(keys.get(0))).get(keys.get(1))) ;

    }
}
