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
package org.apache.streampipes.processors.filters.jvm.processor.compose;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestComposeProcessor {

  ComposeProcessor processor;
  private static final String SELECTOR_1 = "key-selector1";
  private static final String SELECTOR_2 = "key-selector2";
  private static final String INVALID_SELECTOR = "invalid-selector";
  private static final String S0_PREFIX = "s0::";
  private static final String S1_PREFIX = "s1::";

  @BeforeEach
  public void setup() {
    processor = new ComposeProcessor();
  }

  @ParameterizedTest
  @MethodSource("data")
  public void test(List<Map<String, Object>> events, List<Map<String, Object>> outputEvents,
          PrefixStrategy prefixStrategy) {

    Consumer<DataProcessorInvocation> invocationConfig = (invocation -> {
      List<OutputStrategy> outputStrategies = new ArrayList<>();
      outputStrategies.add(new CustomOutputStrategy(List.of(S0_PREFIX + SELECTOR_1, S1_PREFIX + SELECTOR_2)));
      invocation.setOutputStrategies(outputStrategies);
    });

    TestConfiguration configuration = TestConfiguration.builder().prefixStrategy(prefixStrategy).build();

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration,
            invocationConfig);

    testExecutor.run(events, outputEvents);
  }
  static Stream<Arguments> data() {

    var object1 = new Object();
    var object2 = new Object();

    return Stream.of(Arguments.of(List.of(Map.of(SELECTOR_1, object1)), List.of(), PrefixStrategy.SAME_PREFIX),
            Arguments.of(List.of(Map.of(SELECTOR_1, object1), Map.of(SELECTOR_2, object2)), List.of(),
                    PrefixStrategy.SAME_PREFIX),
            Arguments.of(List.of(Map.of(SELECTOR_1, object1), Map.of(SELECTOR_2, object2)),
                    List.of(Map.of(SELECTOR_1, object1, SELECTOR_2, object2)), PrefixStrategy.ALTERNATE),
            Arguments.of(List.of(Map.of(SELECTOR_1, object1), Map.of(INVALID_SELECTOR, object2)),
                    List.of(Map.of(SELECTOR_1, object1)), PrefixStrategy.ALTERNATE),
            Arguments.of(List.of(Map.of(INVALID_SELECTOR, object1), Map.of(INVALID_SELECTOR, object2)),
                    List.of(Map.of()), PrefixStrategy.ALTERNATE));
  }
}
