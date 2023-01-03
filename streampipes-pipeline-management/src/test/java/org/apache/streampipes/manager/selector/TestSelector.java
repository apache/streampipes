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
package org.apache.streampipes.manager.selector;

import org.apache.streampipes.model.schema.EventProperty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.apache.streampipes.manager.selector.TestSelectorUtils.makeSchema;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestSelector {

  @Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return List.of(new Object[][]{
        {List.of("s0::testDimension"), 1},
        {List.of("s0::location", "s0::location::latitude"), 1},
        {List.of("s0::testDimension", "s0::testMeasurement"), 2}

    });
  }

  @Parameterized.Parameter
  public List<String> fieldSelectors;

  @Parameterized.Parameter(1)
  public int expectedPropertyCount;

  @Test
  public void test() {
    List<EventProperty> newProperties = new PropertySelector(makeSchema())
        .createPropertyList(fieldSelectors);

    assertEquals(expectedPropertyCount, newProperties.size());

  }
}
