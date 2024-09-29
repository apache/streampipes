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

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFieldAddition {

  @Test
  public void testFieldAddition() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.simpleMap();
    Event event = RuntimeTestUtils.makeSimpleEvent(runtimeMap, RuntimeTestUtils.getSourceInfo());

    event.addField("append", 10);

    Assertions.assertEquals(2, event.getFields().size());
    Assertions.assertEquals("append", event.getFieldByRuntimeName("append").getFieldNameIn());

    Map<String, Object> outMap = new EventConverter(event).toMap();
    Assertions.assertEquals(2, outMap.size());
    Assertions.assertTrue(outMap.containsKey("append"));
  }
}
