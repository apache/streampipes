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
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;

import java.util.*;

import static org.junit.Assert.*;


public class ValueEventTransformerTest {

    @Test
    public void transform() {
        EventSchema eventSchema = new EventSchema();
        EventProperty eventPropertyf = new EventPropertyPrimitive();
        eventPropertyf.setLabel("a");
        eventPropertyf.setRuntimeName("a");
        eventSchema.addEventProperty(eventPropertyf);

        Map<String, Object> event = new HashMap<>();
        event.put("a", 273.15);

        List<String> keys = new ArrayList<>();
        keys.add(eventPropertyf.getPropertyId());

        List<ValueTransformationRule> rules = new ArrayList<>();
        rules.add(new UnitTransformationRule(eventSchema, keys,
               "http://qudt.org/vocab/unit#Kelvin","http://qudt.org/vocab/unit#DegreeCelsius"));

        ValueEventTransformer eventTransformer = new ValueEventTransformer(rules);
        Map<String, Object> result = eventTransformer.transform(event);

        assertEquals(0.0, result.get(eventPropertyf.getRuntimeName()));

    }

}