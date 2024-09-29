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
package org.apache.streampipes.processors.filters.jvm.processor.booleanfilter;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestBooleanFilterProcessor {

  BooleanFilterProcessor processor;
  private static final String FIELD_NAME = "Test";

  @BeforeEach
  public void setup() {
    processor = new BooleanFilterProcessor();
  }
  @ParameterizedTest
  @MethodSource("data")
  public void test(String boolToKeep, List<Boolean> eventBooleans, List<Boolean> outputEventBooleans) {

    TestConfiguration configuration = TestConfiguration.builder()
            .configWithDefaultPrefix(BooleanFilterProcessor.BOOLEAN_MAPPING, FIELD_NAME)
            .config(BooleanFilterProcessor.VALUE, boolToKeep).build();

    List<Map<String, Object>> events = new ArrayList<>();
    eventBooleans.forEach(bool -> events.add(Map.of(FIELD_NAME, bool)));

    List<Map<String, Object>> outputEvents = new ArrayList<>();
    outputEventBooleans.forEach(bool -> outputEvents.add(Map.of(FIELD_NAME, bool)));

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(events, outputEvents);
  }

  static Stream<Arguments> data() {
    return Stream.of(
            Arguments.of("True", Arrays.asList(true, true, false, true, false, true, false, true),
                    Arrays.asList(true, true, true, true, true)),
            Arguments.of("True", Arrays.asList(true, true, true), Arrays.asList(true, true, true)),
            Arguments.of("True", Arrays.asList(false, false, false), Collections.emptyList()),
            Arguments.of("True", Collections.emptyList(), Collections.emptyList()),
            Arguments.of("False", Arrays.asList(true, false, true, false, true, false, true, false, true),
                    Arrays.asList(false, false, false, false)),
            Arguments.of("False", Arrays.asList(true, true, true), Collections.emptyList()),
            Arguments.of("False", Arrays.asList(false, false, false), Arrays.asList(false, false, false)),
            Arguments.of("False", Collections.emptyList(), Collections.emptyList()));
  }
}
