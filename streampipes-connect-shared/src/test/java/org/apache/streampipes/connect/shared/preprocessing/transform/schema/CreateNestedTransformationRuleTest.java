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

@SuppressWarnings("unchecked")
public class CreateNestedTransformationRuleTest {

  @Test
  public void transformSimple() {
    Map<String, Object> event = new HashMap<>();


    List<String> key = new ArrayList<>();
    key.add("key");
    CreateNestedTransformationRule createNested = new CreateNestedTransformationRule(key);

    Map<String, Object> result = createNested.transform(event);

    assertEquals(1, result.keySet().size());
    assertEquals(0, ((Map<String, Object>) result.get("key")).keySet().size());
  }


  @Test
  public void transformNested() {
    Map<String, Object> event = new HashMap<>();
    event.put("parent", new HashMap<>());


    List<String> key = new ArrayList<>();
    key.add("parent");
    key.add("child");

    CreateNestedTransformationRule createNested = new CreateNestedTransformationRule(key);

    Map<String, Object> result = createNested.transform(event);

    assertEquals(1, result.keySet().size());
    assertEquals(1, ((Map<String, Object>) result.get("parent")).keySet().size());
    assertEquals(0,
        (((Map<String, Object>) ((Map<String, Object>) result.get("parent")).get("child")).keySet().size()));
  }
}
