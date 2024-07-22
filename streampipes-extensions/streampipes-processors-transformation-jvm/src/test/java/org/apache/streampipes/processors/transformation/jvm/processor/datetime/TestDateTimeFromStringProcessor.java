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

package org.apache.streampipes.processors.transformation.jvm.processor.datetime;

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

import static org.apache.streampipes.processors.transformation.jvm.processor.datetime.DateTimeFromStringProcessor.OUTPUT_TIMESTAMP_RUNTIME_NAME;
import static org.apache.streampipes.processors.transformation.jvm.processor.datetime.DateTimeFromStringProcessor.OUTPUT_TIMEZONE_RUNTIME_NAME;

public class TestDateTimeFromStringProcessor {

  private static final String KEY_1 = "key1";
  private static final String TIMEZONE = "US/Eastern";

  private DateTimeFromStringProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new DateTimeFromStringProcessor();
  }

  private static final Map<String, Long> cases = Map.of(
      "2020-11-13T21:07:38.146120+01:00", 1605298058146L,
      "2023-11-14T16:17:01.286299-05:00", 1699996621286L,
      "2023-11-14T14:05:57.519543100", 1699988757519L
  );

  static Stream<Arguments> arguments() {
    return cases.entrySet().stream().map(entry -> {
      Map<String, Object> inputEvent = Map.of(KEY_1, entry.getKey());
      Map<String, Object> outputEvent = Map.of(
          KEY_1, entry.getKey(),
          OUTPUT_TIMEZONE_RUNTIME_NAME, TIMEZONE,
          OUTPUT_TIMESTAMP_RUNTIME_NAME, entry.getValue()
      );
      return Arguments.of(List.of(inputEvent), List.of(outputEvent));
    });
  }

  @ParameterizedTest
  @MethodSource("arguments")
  public void testStringToState(
      List<Map<String, Object>> intpuEvents,
      List<Map<String, Object>> outputEvents
  ) {

    var configuration = TestConfiguration
        .builder()
        .config(DateTimeFromStringProcessor.FIELD_ID, StreamPrefix.s0(KEY_1))
        .config(DateTimeFromStringProcessor.INPUT_TIMEZONE_KEY, TIMEZONE)
        .prefixStrategy(PrefixStrategy.SAME_PREFIX)
        .build();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(intpuEvents, outputEvents);
  }

}
