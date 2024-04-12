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


import org.apache.streampipes.processors.filters.jvm.processor.numericalfilter.ProcessingElementTestExecutor;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TestBooleanFilterProcessor {

  private static final String FIELD_NAME = "Test";
  private static final String FIELD_NAME_WITH_PREFIX = "s0::" + FIELD_NAME;
  @ParameterizedTest
  @MethodSource("data")
  public void test(
      String boolToKeep,
      List<Boolean> eventBooleans,
      List<Boolean> outputEventBooleans
  ) {

    var processor = new BooleanFilterProcessor();

    Map<String, Object> userConfiguration =
        Map.of(
            BooleanFilterProcessor.BOOLEAN_MAPPING, FIELD_NAME_WITH_PREFIX,
            BooleanFilterProcessor.VALUE, boolToKeep
        );

    List<Map<String, Object>> events = new ArrayList<>();
    eventBooleans.forEach(bool->events.add(Map.of(FIELD_NAME_WITH_PREFIX, bool)));

    List<Map<String, Object>> outputEvents = new ArrayList<>();
    outputEventBooleans.forEach(bool->outputEvents.add(Map.of(FIELD_NAME, bool)));

    ProcessingElementTestExecutor.run(processor, userConfiguration, events, outputEvents, null);
  }

  static Stream<Arguments> data() {
    return Stream.of(
        Arguments.of("True",
            Arrays.asList(true, true, false, true, false, true, false, true),
            Arrays.asList(true, true, true, true, true)),
        Arguments.of("True",
            Arrays.asList(true, true, true),
            Arrays.asList(true, true, true)),
        Arguments.of("True",
            Arrays.asList(false, false, false),
            Collections.emptyList()),
        Arguments.of("True",
            Collections.emptyList(),
            Collections.emptyList()),
        Arguments.of("False",
            Arrays.asList(true, false, true, false, true, false, true, false, true),
            Arrays.asList(false, false, false, false)),
        Arguments.of("False",
            Arrays.asList(true, true, true),
            Collections.emptyList()),
        Arguments.of("False",
            Arrays.asList(false, false, false),
            Arrays.asList(false, false, false)),
        Arguments.of("False",
            Collections.emptyList(),
            Collections.emptyList())
    );
  }
}
