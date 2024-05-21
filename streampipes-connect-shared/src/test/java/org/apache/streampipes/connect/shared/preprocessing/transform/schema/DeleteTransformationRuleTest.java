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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteTransformationRuleTest {

  @Test
  public void transformSimple() {
    var event = new HashMap<String, Object>();
    event.put("key", "value");

    var deleteRule = new DeleteTransformationRule(List.of("key"));

    var result = deleteRule.apply(event);

    assertEquals(0, result.keySet().size());
  }

  @Test
  public void transformNested() {
    var child = new HashMap<String, Object>();
    child.put("child", "value");
    child.put("secondChild", "value");
    var event = new HashMap<String, Object>();
    event.put("parent", child);
    event.put("keepProperty", "test");

    var deleteRule = new DeleteTransformationRule(List.of("parent", "child"));

    var result = deleteRule.apply(event);

    assertEquals(2, result.keySet().size());
  }

  @Test
  // verifying that mathod applyTransformation method works when passed a null event.
  public void applyTransformationWithNullParameter() {
    new DeleteTransformationRule(List.of())
        .applyTransformation(null, List.of());
  }

  @Test
  public void deleteNestedChildWithParentProperty() {
    var child = new HashMap<String, Object>();
    child.put("child", "value");
    var event = new HashMap<String, Object>();
    event.put("parent", child);
    event.put("keepProperty", "test");

    var deleteRule = new DeleteTransformationRule(Arrays.asList("parent", "child"));

    var result = deleteRule.apply(event);

    assertEquals(1, result.keySet().size());
    assertEquals("test", result.get("keepProperty"));
  }

}
