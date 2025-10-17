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

package org.apache.streampipes.processors.filters.jvm.processor.threshold;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class TestThresholdDetectionProcessor {

  private static final String FIELD_NAME = "field1";

  private ThresholdDetectionProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ThresholdDetectionProcessor();
  }

  @Test
  void onEvent_ThresholdGreaterThanOrEqual() {
    var configuration = createConfiguration(
        ">=",
        5.0
    );
    var inputEvent = createInputEvent(10.0);
    var expectedEvents = createOutputEvent(10.0, true);

    executeTest(configuration, inputEvent, expectedEvents);
  }

  @Test
  void onEvent_ThresholdLessThan() {
    var configuration = createConfiguration(
        "<",
        5.0
    );
    var inputEvent = createInputEvent(3.0);
    var expectedEvents = createOutputEvent(3.0, true);

    executeTest(configuration, inputEvent, expectedEvents);
  }

  @Test
  void onEvent_Equal() {
    var configuration = createConfiguration(
        "==",
        5.0
    );
    var inputEvent = createInputEvent(5.0);
    var expectedEvents = createOutputEvent(5.0, true);

    executeTest(configuration, inputEvent, expectedEvents);
  }

  @Test
  void onEvent_EqualFail() {
    var configuration = createConfiguration(
        "==",
        5.0
    );
    var inputEvent = createInputEvent(6.0);
    var expectedEvents = createOutputEvent(6.0, false);

    executeTest(configuration, inputEvent, expectedEvents);
  }


  @Test
  void onEvent_ThresholdNotEqual() {
    var configuration = createConfiguration(
        "!=",
        5.0
    );
    var inputEvent = createInputEvent(3.0);
    var expectedEvents = createOutputEvent(3.0, true);

    executeTest(configuration, inputEvent, expectedEvents);
  }

  @Test
  void onEvent_ThresholdGreaterThan() {
    var configuration = createConfiguration(
        ">",
        5.0
    );
    var inputEvent = createInputEvent(6.0);
    var expectedEvents = createOutputEvent(6.0, true);

    executeTest(configuration, inputEvent, expectedEvents);
  }

  @Test
  void onEvent_ThresholdLessThanOrEqual() {
    var configuration = createConfiguration(
        "<=",
        5.0
    );
    var inputEvent = createInputEvent(5.0);
    var expectedEvents = createOutputEvent(5.0, true);

    executeTest(configuration, inputEvent, expectedEvents);
  }

  private TestConfiguration createConfiguration(String operation, double value) {
    return TestConfiguration.builder()
        .configWithDefaultPrefix(ThresholdDetectionProcessor.NUMBER_MAPPING, FIELD_NAME)
        .config(ThresholdDetectionProcessor.OPERATION, operation)
        .config(ThresholdDetectionProcessor.VALUE, value)
        .build();
  }

  private List<Map<String, Object>> createInputEvent(double value) {
    return List.of(Map.of(FIELD_NAME, value));
  }

  private List<Map<String, Object>> createOutputEvent(double value, boolean thresholdDetected) {
    return List.of(Map.of(FIELD_NAME, value, ThresholdDetectionProcessor.RESULT_FIELD, thresholdDetected));
  }

  private void executeTest(
      TestConfiguration configuration,
      List<Map<String, Object>> inputEvent,
      List<Map<String, Object>> expectedEvents
  ) {
    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
    testExecutor.run(inputEvent, expectedEvents);
  }
}
