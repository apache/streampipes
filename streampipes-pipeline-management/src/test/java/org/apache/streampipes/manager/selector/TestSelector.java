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

import static org.apache.streampipes.manager.selector.TestSelectorUtils.makeSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.streampipes.model.schema.EventProperty;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestSelector {

  static Stream<Arguments> data() {
    return Stream.of(Arguments.of(List.of("s0::testDimension"), 1),
            Arguments.of(List.of("s0::location", "s0::location::latitude"), 1),
            Arguments.of(List.of("s0::testDimension", "s0::testMeasurement"), 2));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void test(List<String> fieldSelectors, int expectedPropertyCount) {
    List<EventProperty> newProperties = new PropertySelector(makeSchema()).createPropertyList(fieldSelectors);

    assertEquals(expectedPropertyCount, newProperties.size());
  }
}