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

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DeleteTransformationRuleTest {

  @Test
  public void transformSimple() {
    Map<String, Object> event = new HashMap<>();
    event.put("key", "value");

    DeleteTransformationRule deleteRule = new DeleteTransformationRule(List.of("key"));

    Map<String, Object> result = deleteRule.transform(event);

    assertEquals(0, result.keySet().size());
  }

  @Test
  public void transformNested() {
    Map<String, Object> child = new HashMap<>();
    child.put("child", "value");
    Map<String, Object> event = new HashMap<>();
    event.put("parent", child);

    DeleteTransformationRule deleteRule = new DeleteTransformationRule(Arrays.asList("parent", "child"));

    Map<String, Object> result = deleteRule.transform(event);

    assertEquals(1, result.keySet().size());
  }
}
