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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TestPropertyRenaming {

  @Test
  public void testSimpleRenaming() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.simpleMap();
    Event event = RuntimeTestUtils.makeSimpleEventWithRenameRule(runtimeMap, RuntimeTestUtils.getSourceInfo());

    Map<String, Object> outMap = new EventConverter(event).toMap();

    Assertions.assertTrue(outMap.containsKey("ts"));
    Assertions.assertEquals(1,
                            outMap.keySet().size());
  }

  @Test
  public void testNoRenaming() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.simpleMap();
    Event event = RuntimeTestUtils.makeSimpleEvent(runtimeMap, RuntimeTestUtils.getSourceInfo());

    Map<String, Object> outMap = new EventConverter(event).toMap();

    Assertions.assertTrue(outMap.containsKey("timestamp"));
    Assertions.assertEquals(1,
                            outMap.keySet().size());
  }

  @Test
  public void testNestedRenaming() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.nestedMap();
    Event event = RuntimeTestUtils.makeNestedEventWithRenameRule(runtimeMap, RuntimeTestUtils
        .getSourceInfo());

    Map<String, Object> outMap = new EventConverter(event).toMap();

    Assertions.assertTrue(outMap.containsKey("ns"));
    Assertions.assertTrue(Map.class.isInstance(outMap.get("ns")));

    Map<String, Object> nestedMap = (Map<String, Object>) outMap.get("ns");
    Assertions.assertTrue(nestedMap.containsKey("ts2"));
    Assertions.assertEquals(2,
                            nestedMap.keySet().size());
  }


}
