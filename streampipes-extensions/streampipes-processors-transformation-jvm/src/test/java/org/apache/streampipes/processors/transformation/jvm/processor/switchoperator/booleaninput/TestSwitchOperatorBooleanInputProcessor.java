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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.booleaninput;

import org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.AbstractSwitchOperatorProcessor;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestSwitchOperatorBooleanInputProcessor {
  private static final String SWITCH_FILTER_OUTPUT_KEY = AbstractSwitchOperatorProcessor.SWITCH_FILTER_OUTPUT_KEY;
  private static final String SWITCH_FILTER_INPUT_FIELD_KEY =
      AbstractSwitchOperatorProcessor.SWITCH_FILTER_INPUT_FIELD_KEY;
  private static final String SWITCH_CASE_VALUE_KEY = AbstractSwitchOperatorProcessor.SWITCH_CASE_VALUE_KEY;
  private static final String SWITCH_CASE_OUTPUT_VALUE_KEY =
      AbstractSwitchOperatorProcessor.SWITCH_CASE_OUTPUT_VALUE_KEY;
  private static final String SWITCH_CASE_GROUP_KEY = AbstractSwitchOperatorProcessor.SWITCH_CASE_GROUP_KEY;
  private static final String OUTPUT_TYPE_SELECTION_KEY = AbstractSwitchOperatorProcessor.OUTPUT_TYPE_SELECTION_KEY;
  private static final String DEFAULT_OUTPUT_VALUE_KEY = AbstractSwitchOperatorProcessor.DEFAULT_OUTPUT_VALUE_KEY;

  private SwitchOperatorBooleanInputProcessor processor;

  @BeforeEach
  void setUp() {
    this.processor = new SwitchOperatorBooleanInputProcessor();
  }

  // Helper to create a single switch case configuration for the collection static property
  private Map<String, Object> createSingleBooleanSwitchCaseConfig(String caseValue, String outputValue) {
    return Map.of(
        SWITCH_CASE_VALUE_KEY, caseValue, // Use new constant
        SWITCH_CASE_OUTPUT_VALUE_KEY, outputValue // Use new constant
    );
  }

  private List<Map<String, Object>> createMultipleBooleanSwitchCaseConfigs(Map<String, String> cases) {
    List<Map<String, Object>> configList = new java.util.ArrayList<>();
    cases.forEach((caseVal, outputVal) ->
        configList.add(Map.of(
            SWITCH_CASE_VALUE_KEY, caseVal, // Use new constant
            SWITCH_CASE_OUTPUT_VALUE_KEY, outputVal // Use new constant
        ))
    );
    return configList;
  }

  @Test
  void onEvent_StringOutputType_MatchingTrueCase() {
    String inputField = "status";
    String expectedOutput = "STATUS_ACTIVE";

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField, // Use new constant
        OUTPUT_TYPE_SELECTION_KEY, "String", // Use new constant
        SWITCH_CASE_GROUP_KEY, createMultipleBooleanSwitchCaseConfigs(Map.of("true", expectedOutput, "false",
            "STATUS_INACTIVE")), // Use new constant
        DEFAULT_OUTPUT_VALUE_KEY, "UNKNOWN" // Use new constant
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, true)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, true, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_StringOutputType_MatchingFalseCase() {
    String inputField = "status";
    String expectedOutput = "STATUS_INACTIVE";

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "String",
        SWITCH_CASE_GROUP_KEY, createMultipleBooleanSwitchCaseConfigs(Map.of("true", "STATUS_ACTIVE", "false",
            expectedOutput)),
        DEFAULT_OUTPUT_VALUE_KEY, "UNKNOWN"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, false)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, false, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_StringOutputType_NoMatchingCase_UsesDefault() {
    String inputField = "status";
    String defaultOutput = "DEFAULT_STATUS";

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "String",
        SWITCH_CASE_GROUP_KEY, Collections.emptyList(),
        DEFAULT_OUTPUT_VALUE_KEY, defaultOutput
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, true)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, true, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_MatchingTrueCase() {
    String inputField = "enable";
    Boolean expectedOutput = true;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, createMultipleBooleanSwitchCaseConfigs(Map.of("true", "true", "false", "false")),
        DEFAULT_OUTPUT_VALUE_KEY, "false"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, true)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, true, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_MatchingFalseCase() {
    String inputField = "enable";
    Boolean expectedOutput = false;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, createMultipleBooleanSwitchCaseConfigs(Map.of("true", "true", "false", "false")),
        DEFAULT_OUTPUT_VALUE_KEY, "true"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, false)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, false, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_NoMatchingCase_UsesDefaultFalse() {
    String inputField = "enable";
    Boolean defaultOutput = false;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, createMultipleBooleanSwitchCaseConfigs(Map.of("true", "true")),
        DEFAULT_OUTPUT_VALUE_KEY, "false"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, false)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, false, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_MatchingCase() {
    String inputField = "flag";
    Integer expectedOutput = 100;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, createMultipleBooleanSwitchCaseConfigs(Map.of("true", "100", "false", "50")),
        DEFAULT_OUTPUT_VALUE_KEY, "0"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, true)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, true, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_NoMatchingCase_UsesDefault() {
    String inputField = "flag";
    Integer defaultOutput = 999;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, Collections.emptyList(),
        DEFAULT_OUTPUT_VALUE_KEY, defaultOutput.toString()
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, false)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, false, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_DefaultNotParsable() {
    String inputField = "count";
    Integer expectedDefault = 0;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, Collections.emptyList(),
        DEFAULT_OUTPUT_VALUE_KEY, "NOT_A_NUMBER"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, true)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, true, SWITCH_FILTER_OUTPUT_KEY, expectedDefault)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_CaseValueNotParsable() {
    String inputField = "is_valid";
    Boolean expectedDefault = false;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, createMultipleBooleanSwitchCaseConfigs(Map.of("true", "INVALID_BOOLEAN")),
        DEFAULT_OUTPUT_VALUE_KEY, "false"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, true)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, true, SWITCH_FILTER_OUTPUT_KEY, expectedDefault)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }
}
