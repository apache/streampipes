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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter;

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

import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter.BooleanInverterProcessor.INVERT_FIELD_ID;

public class TestBooleanInverterProcessor {
  private static final String KEY_1 = "key1";

  private BooleanInverterProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new BooleanInverterProcessor();
  }

  static Stream<Arguments> arguments() {
    return Stream.of(
        Arguments.of(
            List.of(Map.of(KEY_1, true)),
            List.of(Map.of(KEY_1, false))
        ),
        Arguments.of(
            List.of(Map.of(KEY_1, false)),
            List.of(Map.of(KEY_1, true))
        ),
        Arguments.of(
            List.of(Map.of(KEY_1, true), Map.of(KEY_1, false), Map.of(KEY_1, true)),
            List.of(Map.of(KEY_1, false), Map.of(KEY_1, true), Map.of(KEY_1, false))
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
        .config(INVERT_FIELD_ID, StreamPrefix.s0(KEY_1))
        .prefixStrategy(PrefixStrategy.SAME_PREFIX)
        .build();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(intpuEvents, outputEvents);
  }

}
