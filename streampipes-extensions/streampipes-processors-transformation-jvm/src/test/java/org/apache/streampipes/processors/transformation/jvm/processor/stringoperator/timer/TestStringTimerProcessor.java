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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer;

import org.apache.streampipes.test.executors.Approx;
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

import static org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor.FIELD_ID;
import static org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor.FIELD_VALUE_RUNTIME_NAME;
import static org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor.MEASURED_TIME_FIELD_RUNTIME_NAME;
import static org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor.MILLISECONDS;
import static org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor.ON_INPUT_EVENT;
import static org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor.OUTPUT_FREQUENCY;
import static org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor.OUTPUT_UNIT_ID;

public class TestStringTimerProcessor {
  private static final String KEY_1 = "key1";
  private static final String VALUE_1 = "v1";
  private static final String VALUE_2 = "v2";

  private StringTimerProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new StringTimerProcessor();
  }

  static Stream<Arguments> arguments() {
    return Stream.of(
        Arguments.of(
            List.of(Map.of(KEY_1, VALUE_1), Map.of(KEY_1, VALUE_1), Map.of(KEY_1, VALUE_2)),
            List.of(
                Map.of(
                    FIELD_VALUE_RUNTIME_NAME, VALUE_1,
                    MEASURED_TIME_FIELD_RUNTIME_NAME, new Approx(0.0, 2.0),
                    KEY_1, VALUE_1
                ),
                Map.of(
                    FIELD_VALUE_RUNTIME_NAME, VALUE_1,
                    MEASURED_TIME_FIELD_RUNTIME_NAME,  new Approx(1.0, 2.0),
                    KEY_1, VALUE_2
                )
            )
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
        .config(OUTPUT_FREQUENCY, ON_INPUT_EVENT)
        .config(OUTPUT_UNIT_ID, MILLISECONDS)
        .config(FIELD_ID, StreamPrefix.s0(KEY_1))
        .prefixStrategy(PrefixStrategy.SAME_PREFIX)
        .build();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(intpuEvents, outputEvents);
  }

}
