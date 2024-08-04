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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter;

import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.StreamPrefix;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TestStringCounterProcessor {
  private static final String KEY_1 = "key1";

  private StringCounterProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new StringCounterProcessor();
  }


  static Stream<Arguments> arguments() {
    return Stream.of(
        Arguments.of(
            List.of(Map.of(KEY_1, "v1"), Map.of(KEY_1, "v2")),
            List.of(Map.of(
                KEY_1, "v2",
                StringCounterProcessor.CHANGE_FROM_FIELD_RUNTIME_NAME, "v1",
                StringCounterProcessor.CHANGE_TO_FIELD_RUNTIME_NAME, "v2",
                StringCounterProcessor.COUNT_FIELD_RUNTIME_NAME, 1
            ))
        )
    );
  }

  @ParameterizedTest
  @MethodSource("arguments")
  public void testStringToState(
      List<Map<String, Object>> intpuEvents,
      List<Map<String, Object>> outputEvents
  ) {

    var configuration = TestConfiguration
        .builder()
        .config(StringCounterProcessor.FIELD_ID, StreamPrefix.s0(KEY_1))
        .prefixStrategy(PrefixStrategy.SAME_PREFIX)
        .build();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(intpuEvents, outputEvents);
  }

}