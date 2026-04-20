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
package org.apache.streampipes.processors.enricher.jvm.processor.trigonometry;

import org.apache.streampipes.test.executors.Approx;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class TrigonometryProcessorTest {

  private TrigonometryProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new TrigonometryProcessor();
  }

  @Test
  void trigonometry1() {
    executeFixtureTest(
        "sin",
        List.of(
            Map.of("timestamp", 1623871499055L, "temperature1", 90.0d),
            Map.of("timestamp", 1623871500059L, "temperature1", 180.0d)
        ),
        List.of(
            Map.of("timestamp", 1623871499055L, "temperature1", 90.0d,
                "trigonometryResult", new Approx(0.8939966636005579d, 0.0000000001d)),
            Map.of("timestamp", 1623871500059L, "temperature1", 180.0d,
                "trigonometryResult", new Approx(-0.8011526357338304d, 0.0000000001d))
        )
    );
  }

  @Test
  void trigonometry2() {
    executeFixtureTest(
        "cos",
        List.of(
            Map.of("timestamp", 1623871499055L, "temperature1", 120.0d),
            Map.of("timestamp", 1623871500059L, "temperature1", 150.0d)
        ),
        List.of(
            Map.of("timestamp", 1623871499055L, "temperature1", 120.0d,
                "trigonometryResult", new Approx(0.8141809705265618d, 0.0000000001d)),
            Map.of("timestamp", 1623871500059L, "temperature1", 150.0d,
                "trigonometryResult", new Approx(0.6992508064783751d, 0.0000000001d))
        )
    );
  }

  private void executeFixtureTest(
      String operation,
      List<Map<String, Object>> inputEvents,
      List<Map<String, Object>> outputEvents
  ) {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix("operand", "temperature1")
        .config("operation", operation)
        .build();

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);
    testExecutor.run(inputEvents, outputEvents);
  }
}
