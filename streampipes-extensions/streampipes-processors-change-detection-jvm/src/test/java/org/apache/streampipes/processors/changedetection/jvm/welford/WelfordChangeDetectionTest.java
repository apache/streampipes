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

package org.apache.streampipes.processors.changedetection.jvm.welford;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class WelfordChangeDetectionTest {

  @Test
  void keepsStableSeriesBelowAlarmThreshold() {
    var configuration = createConfiguration(0.5d, 5.0d);

    List<Map<String, Object>> inputEvents = List.of(
        event("temperature", 10.0d),
        event("temperature", 10.2d),
        event("temperature", 9.8d),
        event("temperature", 10.1d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("temperature", 10.0d, "changeDetectedLow", false, "changeDetectedHigh", false),
        event("temperature", 10.2d, "changeDetectedLow", false, "changeDetectedHigh", false),
        event("temperature", 9.8d, "changeDetectedLow", false, "changeDetectedHigh", false),
        event("temperature", 10.1d, "changeDetectedLow", false, "changeDetectedHigh", false)
    );

    new ProcessingElementTestExecutor(new WelfordChangeDetection(), configuration)
        .run(inputEvents, expectedEvents);
  }

  @Test
  void detectsPositiveLevelShift() {
    var configuration = createConfiguration(0.1d, 0.5d);

    List<Map<String, Object>> inputEvents = List.of(
        event("temperature", 10.0d),
        event("temperature", 10.0d),
        event("temperature", 10.0d),
        event("temperature", 20.0d),
        event("temperature", 20.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("temperature", 10.0d, "changeDetectedLow", false, "changeDetectedHigh", false),
        event("temperature", 10.0d, "changeDetectedLow", false, "changeDetectedHigh", false),
        event("temperature", 10.0d, "changeDetectedLow", false, "changeDetectedHigh", false),
        event("temperature", 20.0d, "changeDetectedLow", false, "changeDetectedHigh", true),
        event("temperature", 20.0d, "changeDetectedLow", false, "changeDetectedHigh", false)
    );

    new ProcessingElementTestExecutor(new WelfordChangeDetection(), configuration)
        .run(inputEvents, expectedEvents);
  }

  private TestConfiguration createConfiguration(double k, double h) {
    return TestConfiguration.builder()
        .configWithDefaultPrefix("number-mapping", "temperature")
        .config("param-k", k)
        .config("param-h", h)
        .build();
  }

  private Map<String, Object> event(Object... keyValuePairs) {
    var event = new LinkedHashMap<String, Object>();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      event.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
    }
    return event;
  }
}
