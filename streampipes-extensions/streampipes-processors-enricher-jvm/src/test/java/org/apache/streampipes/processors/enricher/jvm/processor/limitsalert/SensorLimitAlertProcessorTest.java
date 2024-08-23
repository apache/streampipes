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

package org.apache.streampipes.processors.enricher.jvm.processor.limitsalert;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class SensorLimitAlertProcessorTest {

  private final String temperature = "temperature";
  private final String upperControlLimit = "upperControlLimit";
  private final String upperWarningLimit = "upperWarningLimit";
  private final String lowerWarningLimit = "lowerWarningLimit";
  private final String lowerControlLimit = "lowerControlLimit";

  private final Map<String, Object> baseEvent = Map.of(
      upperControlLimit, 90.0,
      upperWarningLimit, 80.0,
      lowerWarningLimit, 20.0,
      lowerControlLimit, 10.0
  );

  private SensorLimitAlertProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new SensorLimitAlertProcessor();
  }

  static Stream<Arguments> arguments() {
    return Stream.of(
        Arguments.of(
            5.0,
            SensorLimitAlertProcessor.ALERT,
            SensorLimitAlertProcessor.LOWER_LIMIT
        ),
        Arguments.of(
            15.0,
            SensorLimitAlertProcessor.WARNING,
            SensorLimitAlertProcessor.LOWER_LIMIT
        ),
        Arguments.of(
            85.0,
            SensorLimitAlertProcessor.WARNING,
            SensorLimitAlertProcessor.UPPER_LIMIT
        ),
        Arguments.of(
            95.0,
            SensorLimitAlertProcessor.ALERT,
            SensorLimitAlertProcessor.UPPER_LIMIT
        )
    );
  }

  @ParameterizedTest
  @MethodSource("arguments")
  void onEvent_differentAlertsTest(
      double sensorValue,
      String alertValue,
      String limitBreachedValue
  ) {

    List<Map<String, Object>> inputEvents = List.of(
        new HashMap<>(baseEvent) {{
          put(temperature, sensorValue);
        }}
    );

    List<Map<String, Object>> outputEvents = List.of(
        new HashMap<>(baseEvent) {{
          put(temperature, sensorValue);
          put(SensorLimitAlertProcessor.ALERT_STATUS, alertValue);
          put(SensorLimitAlertProcessor.LIMIT_BREACHED, limitBreachedValue);
        }}
    );

    var configuration = getTestConfiguration();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }

  @Test
  void onEvent_noEventIsEmittedIfInValueRangeTest() {

    List<Map<String, Object>> inputEvents = List.of(
        new HashMap<>(baseEvent) {{
          put(temperature, 50.0);
        }}
    );

    List<Map<String, Object>> outputEvents = List.of();

    var configuration = getTestConfiguration();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }

  private TestConfiguration getTestConfiguration() {
    return TestConfiguration
        .builder()
        .configWithDefaultPrefix(SensorLimitAlertProcessor.SENSOR_VALUE_LABEL, temperature)
        .configWithDefaultPrefix(SensorLimitAlertProcessor.UPPER_CONTROL_LIMIT_LABEL, upperControlLimit)
        .configWithDefaultPrefix(SensorLimitAlertProcessor.UPPER_WARNING_LIMIT_LABEL, upperWarningLimit)
        .configWithDefaultPrefix(SensorLimitAlertProcessor.LOWER_WARNING_LIMIT_LABEL, lowerWarningLimit)
        .configWithDefaultPrefix(SensorLimitAlertProcessor.LOWER_CONTROL_LIMIT_LABEL, lowerControlLimit)
        .build();

  }

}