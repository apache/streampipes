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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RenameTransformationRuleTest {

  @Test
  public void renameSimple() {
    Map<String, Object> event = new HashMap<>();
    event.put("old_key", "test");

    List<String> oldKey = new ArrayList<>();
    oldKey.add("old_key");
    RenameTransformationRule renameRule = new RenameTransformationRule(oldKey, "new_key");

    Map<String, Object> result = renameRule.apply(event);

    assertEquals(1, result.keySet().size());
    assertEquals("test", result.get("new_key"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void renameNested() {
    Map<String, Object> nestedEvent = new HashMap<>();
    nestedEvent.put("old_key", "test");

    Map<String, Object> event = new HashMap<>();
    event.put("key", nestedEvent);

    List<String> oldKey = new ArrayList<>();
    oldKey.add("key");
    oldKey.add("old_key");
    RenameTransformationRule renameRule = new RenameTransformationRule(oldKey, "new_key");

    Map<String, Object> result = renameRule.apply(event);

    assertEquals(1, result.keySet().size());
    Map<String, Object> resultNested = (Map<String, Object>) result.get("key");
    assertEquals(1, resultNested.keySet().size());
    assertEquals("test", resultNested.get("new_key"));
  }
}
