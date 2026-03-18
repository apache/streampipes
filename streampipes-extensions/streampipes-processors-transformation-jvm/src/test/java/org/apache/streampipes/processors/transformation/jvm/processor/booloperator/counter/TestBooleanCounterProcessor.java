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
package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.counter;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class TestBooleanCounterProcessor {

  private BooleanCounterProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new BooleanCounterProcessor();
  }

  @Test
  void booleanCounter1() {
    executeFixtureTest(
        "booleanToCount",
        List.of(
            Map.of("timestamp", 1623871499055L, "booleanToCount", true),
            Map.of("timestamp", 1623871500059L, "booleanToCount", true),
            Map.of("timestamp", 1623871501064L, "booleanToCount", true),
            Map.of("timestamp", 1623871502070L, "booleanToCount", false),
            Map.of("timestamp", 1623871503078L, "booleanToCount", false),
            Map.of("timestamp", 1623871504082L, "booleanToCount", false),
            Map.of("timestamp", 1623871505084L, "booleanToCount", true),
            Map.of("timestamp", 1623871506086L, "booleanToCount", true),
            Map.of("timestamp", 1623871507091L, "booleanToCount", false),
            Map.of("timestamp", 1623871508093L, "booleanToCount", true)
        ),
        List.of(
            Map.of("timestamp", 1623871499055L, "booleanToCount", true, "counter", 1),
            Map.of("timestamp", 1623871502070L, "booleanToCount", false, "counter", 2),
            Map.of("timestamp", 1623871505084L, "booleanToCount", true, "counter", 3),
            Map.of("timestamp", 1623871507091L, "booleanToCount", false, "counter", 4),
            Map.of("timestamp", 1623871508093L, "booleanToCount", true, "counter", 5)
        )
    );
  }

  @Test
  void booleanCounter2() {
    executeFixtureTest(
        "randomboolean",
        List.of(
            Map.of("timestamp", 1623871499055L, "randomboolean", false),
            Map.of("timestamp", 1623871500059L, "randomboolean", false),
            Map.of("timestamp", 1623871501064L, "randomboolean", true),
            Map.of("timestamp", 1623871502070L, "randomboolean", false),
            Map.of("timestamp", 1623871503078L, "randomboolean", true),
            Map.of("timestamp", 1623871504082L, "randomboolean", false),
            Map.of("timestamp", 1623871505084L, "randomboolean", true),
            Map.of("timestamp", 1623871506086L, "randomboolean", false),
            Map.of("timestamp", 1623871507091L, "randomboolean", true),
            Map.of("timestamp", 1623871508093L, "randomboolean", true)
        ),
        List.of(
            Map.of("timestamp", 1623871501064L, "randomboolean", true, "counter", 1),
            Map.of("timestamp", 1623871502070L, "randomboolean", false, "counter", 2),
            Map.of("timestamp", 1623871503078L, "randomboolean", true, "counter", 3),
            Map.of("timestamp", 1623871504082L, "randomboolean", false, "counter", 4),
            Map.of("timestamp", 1623871505084L, "randomboolean", true, "counter", 5),
            Map.of("timestamp", 1623871506086L, "randomboolean", false, "counter", 6),
            Map.of("timestamp", 1623871507091L, "randomboolean", true, "counter", 7)
        )
    );
  }

  private void executeFixtureTest(
      String fieldName,
      List<Map<String, Object>> inputEvents,
      List<Map<String, Object>> expectedEvents
  ) {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix(BooleanCounterProcessor.FIELD_ID, fieldName)
        .config(BooleanCounterProcessor.FLANK_ID, "BOTH")
        .build();

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);
    testExecutor.run(inputEvents, expectedEvents);
  }
}
