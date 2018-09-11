/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.adapter.generic.transform;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class EventTransformerTest {

    @Test
    public void transform() {
        Map<String, Object> event = getFirstEvent();

        List<TransformationRule> rules = new ArrayList<>();
        rules.add(new RenameTransformationRule(Arrays.asList("a"), "a1"));
        rules.add(new RenameTransformationRule(Arrays.asList("b"), "b1"));
        rules.add(new RenameTransformationRule(Arrays.asList("c"), "c1"));
        rules.add(new RenameTransformationRule(Arrays.asList("c1", "d"), "d1"));
        rules.add(new CreateNestedTransformationRule(Arrays.asList("c1", "f")));
        rules.add(new MoveTransformationRule(Arrays.asList("b1"), Arrays.asList("c1", "f")));
        rules.add(new DeleteTransformationRule(Arrays.asList("e")));
        rules.add(new UnitTransformationRule(Arrays.asList("f"), "Kelvin", "Degree Celsius"));

        EventTransformer eventTransformer = new EventTransformer(rules);

        Map<String, Object> result = eventTransformer.transform(event);


        assertEquals(3, result.keySet().size());
        assertTrue(result.containsKey("a1"));
        assertTrue(result.containsKey("c1"));

        Map<String, Object> nested = ((Map<String, Object>) result.get("c1"));

        assertEquals(2, nested.keySet().size());
        assertTrue(nested.containsKey("f"));

        nested = (Map<String, Object>) nested.get("f");
        assertEquals(1, nested.keySet().size());
        assertEquals("z", nested.get("b1"));

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