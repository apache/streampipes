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
package org.apache.streampipes.processors.filters.jvm.processor.textfilter;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestTextFilterProcessor {

  TextFilterProcessor processor;

  public static final String FIELD_NAME = "selectedField";

  @BeforeEach
  public void setup() {
    processor = new TextFilterProcessor();
  }

  @ParameterizedTest
  @MethodSource("data")
  public void test(String keyword, StringOperator stringOperator, List<String> eventValues,
          List<String> outputEventValues) {

    TestConfiguration configuration = TestConfiguration.builder()
            .configWithDefaultPrefix(TextFilterProcessor.MAPPING_PROPERTY_ID, FIELD_NAME)
            .config(TextFilterProcessor.OPERATION_ID, stringOperator).config(TextFilterProcessor.KEYWORD_ID, keyword)
            .build();

    List<Map<String, Object>> events = new ArrayList<>();
    eventValues.forEach(value -> events.add(Map.of(FIELD_NAME, value)));

    List<Map<String, Object>> outputEvents = new ArrayList<>();
    outputEventValues.forEach(value -> outputEvents.add(Map.of(FIELD_NAME, value)));

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(events, outputEvents);
  }

  static Stream<Arguments> data() {
    return Stream.of(
            Arguments.of("keyword", StringOperator.MATCHES, List.of("keyword", "KeyWord", "KEYWORD"),
                    List.of("keyword")),
            Arguments.of("KEYWORD", StringOperator.MATCHES, List.of("keyword", "KeyWord", "KEYWORD"),
                    List.of("KEYWORD")),
            Arguments.of("KeyWord", StringOperator.MATCHES, List.of("keyword", "KeyWord", "KEYWORD"),
                    List.of("KeyWord")),
            Arguments.of("keYWord", StringOperator.MATCHES, List.of("keyword", "KeyWord", "KEYWORD"), List.of()),
            Arguments.of("KeyWord", StringOperator.MATCHES, List.of("keyword", "KeyWord", "KEYWORD", "KeyWord"),
                    List.of("KeyWord", "KeyWord")),
            Arguments.of("keyword", StringOperator.CONTAINS, List.of("text contains keyword", "text doesn't have word"),
                    List.of("text contains keyword")),
            Arguments.of("keyword", StringOperator.CONTAINS, List.of("text is empty", "text doesn't have word"),
                    List.of()));
  }
}
