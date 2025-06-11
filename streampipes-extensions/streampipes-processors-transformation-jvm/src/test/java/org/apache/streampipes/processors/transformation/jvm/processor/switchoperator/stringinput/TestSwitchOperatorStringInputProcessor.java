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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.stringinput;

import org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.AbstractSwitchOperatorProcessor;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TestSwitchOperatorStringInputProcessor {

  // Constants from the abstract processor (now used directly in tests)
  private static final String SWITCH_FILTER_OUTPUT_KEY = AbstractSwitchOperatorProcessor.SWITCH_FILTER_OUTPUT_KEY;
  private static final String SWITCH_FILTER_INPUT_FIELD_KEY =
      AbstractSwitchOperatorProcessor.SWITCH_FILTER_INPUT_FIELD_KEY;
  private static final String SWITCH_CASE_VALUE_KEY = AbstractSwitchOperatorProcessor.SWITCH_CASE_VALUE_KEY; // Base
  // key for value
  private static final String SWITCH_CASE_OUTPUT_VALUE_KEY =
      AbstractSwitchOperatorProcessor.SWITCH_CASE_OUTPUT_VALUE_KEY;
  private static final String SWITCH_CASE_GROUP_KEY = AbstractSwitchOperatorProcessor.SWITCH_CASE_GROUP_KEY;
  private static final String OUTPUT_TYPE_SELECTION_KEY = AbstractSwitchOperatorProcessor.OUTPUT_TYPE_SELECTION_KEY;
  private static final String DEFAULT_OUTPUT_VALUE_KEY = AbstractSwitchOperatorProcessor.DEFAULT_OUTPUT_VALUE_KEY;

  private SwitchOperatorStringInputProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new SwitchOperatorStringInputProcessor();
  }

  // Helper to create a single switch case configuration for the collection static property
  private Map<String, Object> createSingleStringSwitchCaseConfig(String caseValue, String outputValue) {
    return Map.of(
        SWITCH_CASE_VALUE_KEY, caseValue, // Use new constant
        SWITCH_CASE_OUTPUT_VALUE_KEY, outputValue // Use new constant
    );
  }

  // --- Test Cases Using ProcessingElementTestExecutor ---

  @Test
  void onEvent_StringOutputType_MatchingCase() {
    String inputField = "status";
    String inputValue = "RUNNING";
    String expectedOutput = "Process_Running";

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField, // Use new constant
        OUTPUT_TYPE_SELECTION_KEY, "String", // Use new constant
        SWITCH_CASE_GROUP_KEY, List.of(createSingleStringSwitchCaseConfig("RUNNING", expectedOutput),
            createSingleStringSwitchCaseConfig("STOPPED", "Process_Stopped")),
        DEFAULT_OUTPUT_VALUE_KEY, "Unknown_Status" // Use new constant
    );

    List<Map<String, Object>> inputEvents = List.of(Map.of(inputField, inputValue));

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputValue, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_StringOutputType_NoMatchingCase_UsesDefault() {
    String inputField = "color";
    String inputValue = "PURPLE";
    String defaultOutput = "Default_Color";

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "String",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleStringSwitchCaseConfig("RED", "Red_Detected"),
            createSingleStringSwitchCaseConfig("BLUE", "Blue_Detected")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, defaultOutput
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputValue)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputValue, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_MatchingCase_True() {
    String inputField = "isActive";
    String inputValue = "ON";
    Boolean expectedOutput = true;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleStringSwitchCaseConfig("ON", "true"),
            createSingleStringSwitchCaseConfig("OFF", "false")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "false"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputValue)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputValue, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_NoMatchingCase_UsesDefaultFalse() {
    String inputField = "isValid";
    String inputValue = "MAYBE";
    Boolean defaultOutput = false;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleStringSwitchCaseConfig("YES", "true"),
            createSingleStringSwitchCaseConfig("NO", "false")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "false"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputValue)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputValue, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_CaseOutputNotParsable_UsesDefaultFalse() {
    String inputField = "state";
    String inputValue = "ACTIVE";
    Boolean expectedOutput = false;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleStringSwitchCaseConfig("ACTIVE", "NOT_A_BOOLEAN_STRING")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "true"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputValue)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputValue, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_MatchingCase() {
    String inputField = "severity";
    String inputValue = "CRITICAL";
    Integer expectedOutput = 10;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleStringSwitchCaseConfig("CRITICAL", "10"),
            createSingleStringSwitchCaseConfig("WARNING", "5")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "0"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputValue)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputValue, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_NoMatchingCase_UsesDefault() {
    String inputField = "priority";
    String inputValue = "LOW";
    Integer defaultOutput = -1;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleStringSwitchCaseConfig("HIGH", "1"),
            createSingleStringSwitchCaseConfig("MEDIUM", "0")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, defaultOutput.toString()
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputValue)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputValue, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_CaseOutputNotParsable_UsesDefaultZero() {
    String inputField = "value";
    String inputValue = "ABC";
    Integer expectedOutput = 0;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleStringSwitchCaseConfig("ABC", "NOT_AN_INTEGER")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "5"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputValue)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputValue, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }
}