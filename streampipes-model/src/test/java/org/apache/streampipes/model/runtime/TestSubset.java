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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestSubset {

  @Test
  public void testSubsetGeneration() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.multiplePropertiesMap();
    Event event = RuntimeTestUtils.makeSimpleEvent(runtimeMap, RuntimeTestUtils.getSourceInfo());

    List<String> selectors = Arrays.asList("s0::timestamp", "s0::sensor1");

    Event subset = event.getSubset(selectors);

    assertEquals(2, subset.getFields().size());
    assertEquals("timestamp", subset.getFieldBySelector("s0::timestamp").getFieldNameIn());
    assertEquals("sensor1", subset.getFieldBySelector("s0::sensor1").getFieldNameIn());

  }

  @Test
  public void testComplexSubsetGeneration() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.nestedMap();
    Event event = RuntimeTestUtils.makeSimpleEvent(runtimeMap, RuntimeTestUtils.getSourceInfo());

    List<String> selectors = Arrays.asList("s0::timestamp", "s0::nested", "s0::nested::timestamp2");

    Event subset = event.getSubset(selectors);

    assertEquals(2, subset.getFields().size());
    assertEquals("timestamp", subset.getFieldBySelector("s0::timestamp").getFieldNameIn());
    assertEquals("timestamp2", subset.getFieldBySelector("s0::nested::timestamp2").getFieldNameIn
        ());
    assertEquals(1, subset.getFieldBySelector("s0::nested").getAsComposite().getRawValue().size());

  }
}
