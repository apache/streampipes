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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.numericalinput;

import org.apache.streampipes.processors.transformation.jvm.processor.switchoperator.AbstractSwitchOperatorProcessor;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TestSwitchOperatorNumericalInputProcessor {

  // Constants from the abstract processor (now used directly in tests)
  private static final String SWITCH_FILTER_OUTPUT_KEY = AbstractSwitchOperatorProcessor.SWITCH_FILTER_OUTPUT_KEY;
  private static final String SWITCH_FILTER_INPUT_FIELD_KEY =
      AbstractSwitchOperatorProcessor.SWITCH_FILTER_INPUT_FIELD_KEY;
  private static final String SWITCH_CASE_VALUE_KEY = AbstractSwitchOperatorProcessor.SWITCH_CASE_VALUE_KEY; // Base
  // key for value
  private static final String SWITCH_CASE_OPERATOR_KEY = AbstractSwitchOperatorProcessor.SWITCH_CASE_OPERATOR_KEY;
  private static final String SWITCH_CASE_OUTPUT_VALUE_KEY =
      AbstractSwitchOperatorProcessor.SWITCH_CASE_OUTPUT_VALUE_KEY;
  private static final String SWITCH_CASE_GROUP_KEY = AbstractSwitchOperatorProcessor.SWITCH_CASE_GROUP_KEY;
  private static final String OUTPUT_TYPE_SELECTION_KEY = AbstractSwitchOperatorProcessor.OUTPUT_TYPE_SELECTION_KEY;
  private static final String DEFAULT_OUTPUT_VALUE_KEY = AbstractSwitchOperatorProcessor.DEFAULT_OUTPUT_VALUE_KEY;


  private SwitchOperatorNumericalInputProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new SwitchOperatorNumericalInputProcessor();
  }

  // Helper to create a single switch case configuration for the collection static property
  private Map<String, Object> createSingleNumericalSwitchCaseConfig(String caseValue, String operator,
                                                                    String outputValue) {
    return Map.of(
        SWITCH_CASE_VALUE_KEY, caseValue, // Use new constant
        SWITCH_CASE_OPERATOR_KEY, operator, // Use new constant
        SWITCH_CASE_OUTPUT_VALUE_KEY, outputValue // Use new constant
    );
  }

  // --- Test Cases Using ProcessingElementTestExecutor ---

  @Test
  void onEvent_StringOutputType_MatchingEqualsCase() {
    String inputField = "temperature";
    Double inputTemperature = 25.0;
    String expectedOutput = "Normal_Temp";

    Map<String, Object> processorConfig = Map.of(SWITCH_FILTER_INPUT_FIELD_KEY,
        "s0::" + inputField, // Use new constant
        OUTPUT_TYPE_SELECTION_KEY, "String", // Use new constant
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("25.0", "==", expectedOutput),
            createSingleNumericalSwitchCaseConfig("30.0", ">", "High_Temp")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "Unknown_Temp" // Use new constant
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputTemperature)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputTemperature, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_StringOutputType_MatchingGreaterThanCase() {
    String inputField = "pressure";
    Double inputPressure = 105.0;
    String expectedOutput = "High_Pressure";

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "String",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("100.0", ">", expectedOutput),
            createSingleNumericalSwitchCaseConfig("50.0", "<", "Low_Pressure")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "Normal_Pressure"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputPressure)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputPressure, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_StringOutputType_NoMatchingCase_UsesDefault() {
    String inputField = "flowRate";
    Double inputFlowRate = 75.0;
    String defaultOutput = "Default_Flow";

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "String",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("100.0", ">", "High_Flow"),
            createSingleNumericalSwitchCaseConfig("50.0", "<", "Low_Flow")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, defaultOutput
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputFlowRate)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputFlowRate, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_MatchingLessThanOrEqualsCase() {
    String inputField = "level";
    Double inputLevel = 10.0;
    Boolean expectedOutput = true;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("10.0", "<=", "true"),
            createSingleNumericalSwitchCaseConfig("5.0", ">", "false")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "false"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputLevel)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputLevel, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_NoMatchingCase_UsesDefaultFalse() {
    String inputField = "speed";
    Double inputSpeed = 120.0;
    Boolean defaultOutput = false;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("100.0", "<", "true"),
            createSingleNumericalSwitchCaseConfig("150.0", ">", "true")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "false"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputSpeed)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputSpeed, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_MatchingNotEqualsCase() {
    String inputField = "errorCode";
    Double inputErrorCode = 5.0; // Changed input to something not equal to 0.0
    Integer expectedOutput = 1;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("0.0", "!=", expectedOutput.toString()),
            createSingleNumericalSwitchCaseConfig("1.0", "==", "0")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "999"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputErrorCode)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputErrorCode, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_NoMatchingCase_UsesDefault() {
    String inputField = "batteryLevel";
    Double inputBatteryLevel = 60.0;
    Integer defaultOutput = -1;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("80.0", ">", "1"),
            createSingleNumericalSwitchCaseConfig("20.0", "<", "-2")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, defaultOutput.toString()
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputBatteryLevel)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputBatteryLevel, SWITCH_FILTER_OUTPUT_KEY, defaultOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_IntegerOutputType_CaseOutputNotParsable_UsesDefaultZero() {
    String inputField = "sensorValue";
    Double inputSensorValue = 10.0;
    Integer expectedOutput = 0;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Integer",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("10.0", "==", "NOT_AN_INTEGER")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "5"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputSensorValue)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputSensorValue, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }

  @Test
  void onEvent_BooleanOutputType_CaseOutputNotParsable_UsesDefaultFalse() {
    String inputField = "statusFlag";
    Double inputStatusFlag = 1.0;
    Boolean expectedOutput = false;

    Map<String, Object> processorConfig = Map.of(
        SWITCH_FILTER_INPUT_FIELD_KEY, "s0::" + inputField,
        OUTPUT_TYPE_SELECTION_KEY, "Boolean",
        SWITCH_CASE_GROUP_KEY, List.of(
            createSingleNumericalSwitchCaseConfig("1.0", "==", "INVALID_BOOLEAN_STRING")
        ),
        DEFAULT_OUTPUT_VALUE_KEY, "true"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(inputField, inputStatusFlag)
    );

    List<Map<String, Object>> expectedOutputEvents = List.of(
        Map.of(inputField, inputStatusFlag, SWITCH_FILTER_OUTPUT_KEY, expectedOutput)
    );

    new ProcessingElementTestExecutor(
        processor,
        new TestConfiguration(processorConfig, List.of("s0"))
    ).run(inputEvents, expectedOutputEvents);
  }
}