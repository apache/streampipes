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
import org.streampipes.connect.adapter.generic.transform.TransformationRule;
import org.streampipes.connect.adapter.generic.transform.schema.*;
import org.streampipes.model.schema.EventSchema;

import java.util.*;

import static org.junit.Assert.*;


public class ValueEventTransformerTest {

    @Test
    public void transform() {
        Map<String, Object> event = getFirstEvent();
        EventSchema eventSchema = new EventSchema();

        List<ValueTransformationRule> rules = new ArrayList<>();

        rules.add(new UnitTransformationRule(eventSchema, Arrays.asList("f"), "Kelvin", "Degree Celsius"));

        ValueEventTransformer eventTransformer = new ValueEventTransformer(rules);

        Map<String, Object> result = eventTransformer.transform(event);


        assertEquals(0.0, result.get("f"));

    }


    private Map<String, Object> getFirstEvent() {
        Map<String, Object> nested = new HashMap<>();
        nested.put("d", "z");

        Map<String, Object> event = new HashMap<>();
        event.put("a", 1);
        event.put("b", "z");
        event.put("e", "z");
        event.put("c", nested);
        event.put("f", 273.15);

        return event;
    }

}