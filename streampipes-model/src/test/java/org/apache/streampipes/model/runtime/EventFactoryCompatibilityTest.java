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
package org.apache.streampipes.model.runtime;

import org.apache.streampipes.model.output.PropertyRenameRule;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class EventFactoryCompatibilityTest {

  @Test
  void preservesRootAndNestedInsertionOrderIncludingHashCollisions() {
    Map<String, Object> nested = new LinkedHashMap<>();
    nested.put("z", 1);
    nested.put("Aa", 2);
    nested.put("BB", 3);
    nested.put("a", 4);
    Map<String, Object> values = new LinkedHashMap<>(nested);
    values.put("nested", nested);
    var event = EventFactory.fromMap(values);
    assertEquals(List.of("o::z", "o::Aa", "o::BB", "o::a", "o::nested"),
        new ArrayList<>(event.getFields().keySet()));
    assertEquals(List.of("o::nested::z", "o::nested::Aa", "o::nested::BB", "o::nested::a"),
        new ArrayList<>(event.getFieldBySelector("o::nested").getAsComposite().getRawValue().keySet()));
    assertEquals(values, event.getRaw());
  }

  @Test
  void preservesFirstMatchingRenameAndLastValueForCollidingOutputNames() {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("a", 1);
    values.put("b", 2);
    values.put("c", 3);
    var rules = List.of(new PropertyRenameRule("s0::a", "same"),
        new PropertyRenameRule("s0::a", "ignored"),
        new PropertyRenameRule("s0::b", "same"),
        new PropertyRenameRule("s0::c", null),
        new PropertyRenameRule("s0::c", "ignored"));
    var event = EventFactory.fromMap(values, new SourceInfo("test", "s0"), new SchemaInfo(null, rules));
    assertEquals("same", event.getFieldBySelector("s0::a").getFieldNameOut());
    assertEquals("c", event.getFieldBySelector("s0::c").getFieldNameOut());
    assertEquals(Map.of("same", 2, "c", 3), new EventConverter(event).toMap());
    assertEquals(values, event.getRaw());
  }

  @Test
  void observesRenameRuleChangesBetweenEventsWithoutChangingExistingFields() {
    var rules = new ArrayList<PropertyRenameRule>();
    var schema = new SchemaInfo(null, rules);
    var source = new SourceInfo("test", "s0");
    var values = Map.<String, Object>of("a", 1);
    var first = EventFactory.fromMap(values, source, schema);
    var rule = new PropertyRenameRule("s0::a", "renamed");
    rules.add(rule);
    var second = EventFactory.fromMap(values, source, schema);
    rule.setNewRuntimeName("changed");
    var third = EventFactory.fromMap(values, source, schema);
    assertEquals("a", first.getFieldBySelector("s0::a").getFieldNameOut());
    assertEquals("renamed", second.getFieldBySelector("s0::a").getFieldNameOut());
    assertEquals("changed", third.getFieldBySelector("s0::a").getFieldNameOut());
    assertSame(source, third.getSourceInfo());
    assertSame(schema, third.getSchemaInfo());
  }

  @Test
  void preservesListIndexesNestedRenamesAndIndependentMutableFields() {
    var values = new ArrayList<Object>();
    for (int i = 0; i < 260; i++) {
      values.add(Map.of("value", i));
    }
    var raw = Map.<String, Object>of("items", values);
    var schema = new SchemaInfo(null, List.of(new PropertyRenameRule("s0::items::259::value", "last")));
    var source = new SourceInfo("test", "s0");
    var first = EventFactory.fromMap(raw, source, schema);
    var second = EventFactory.fromMap(raw, source, schema);
    var firstItems = first.getFieldBySelector("s0::items").getAsList().getRawValue();
    assertEquals(260, firstItems.size());
    var last = firstItems.get(259).getAsComposite().getRawValue().get("s0::items::259::value");
    assertEquals("last", last.getFieldNameOut());
    assertEquals(259, last.getAsPrimitive().getAsInt());
    last.getAsPrimitive().setValue(999);
    assertEquals(raw, second.getRaw());
    assertEquals(Map.of("value", 259), values.get(259));
  }

  @Test
  void preservesNullsEmptyContainersAndMixedLists() {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("null", null);
    values.put("emptyObject", Map.of());
    values.put("emptyList", List.of());
    values.put("mixed", Arrays.asList(null, 1, "text", Map.of("value", 2), List.of(3, 4)));
    var event = EventFactory.fromMap(values);
    assertNull(event.getFieldBySelector("o::null").getRawValue());
    assertEquals(values, event.getRaw());
    assertEquals(Map.of(), EventFactory.fromMap(Map.of()).getRaw());
  }
}
