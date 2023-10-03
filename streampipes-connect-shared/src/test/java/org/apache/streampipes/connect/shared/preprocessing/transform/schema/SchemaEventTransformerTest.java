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

package org.apache.streampipes.connect.shared.preprocessing.transform.schema;

import org.apache.streampipes.connect.shared.preprocessing.transform.TransformationRule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SchemaEventTransformerTest {

  @Test
  @SuppressWarnings("unchecked")
  public void transform() {
    Map<String, Object> result = getFirstEvent();

    List<TransformationRule> rules = new ArrayList<>();
    rules.add(new RenameTransformationRule(List.of("a"), "a1"));
    rules.add(new RenameTransformationRule(List.of("b"), "b1"));
    rules.add(new RenameTransformationRule(List.of("c"), "c1"));
    rules.add(new RenameTransformationRule(List.of("c1", "d"), "d1"));
    rules.add(new CreateNestedTransformationRule(List.of("c1", "f")));
    rules.add(new MoveTransformationRule(List.of("b1"), List.of("c1", "f")));
    rules.add(new DeleteTransformationRule(List.of("e")));

    for (var rule : rules) {
      result = rule.apply(result);
    }

    assertEquals(2, result.keySet().size());
    assertTrue(result.containsKey("a1"));
    assertTrue(result.containsKey("c1"));

    Map<String, Object> nested = ((Map<String, Object>) result.get("c1"));

    assertEquals(2, nested.keySet().size());
    assertTrue(nested.containsKey("f"));

    nested = (Map<String, Object>) nested.get("f");
    assertEquals(1, nested.keySet().size());
    assertEquals("z", nested.get("b1"));

  }


  private Map<String, Object> getFirstEvent() {
    Map<String, Object> nested = new HashMap<>();
    nested.put("d", "z");

    Map<String, Object> event = new HashMap<>();
    event.put("a", 1);
    event.put("b", "z");
    event.put("e", "z");
    event.put("c", nested);

    return event;
  }
}
