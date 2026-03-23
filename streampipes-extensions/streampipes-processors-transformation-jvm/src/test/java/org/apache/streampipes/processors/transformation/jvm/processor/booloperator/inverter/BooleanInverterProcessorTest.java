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
package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class BooleanInverterProcessorTest {

  @Test
  void booleanInverter1() {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix(BooleanInverterProcessor.INVERT_FIELD_ID, "booleanToInvert")
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        Map.of("timestamp", 1623871499055L, "booleanToInvert", true),
        Map.of("timestamp", 1623871500059L, "booleanToInvert", true),
        Map.of("timestamp", 1623871501064L, "booleanToInvert", true),
        Map.of("timestamp", 1623871502070L, "booleanToInvert", false),
        Map.of("timestamp", 1623871503078L, "booleanToInvert", false),
        Map.of("timestamp", 1623871504082L, "booleanToInvert", false),
        Map.of("timestamp", 1623871505084L, "booleanToInvert", true),
        Map.of("timestamp", 1623871506086L, "booleanToInvert", true),
        Map.of("timestamp", 1623871507091L, "booleanToInvert", false),
        Map.of("timestamp", 1623871508093L, "booleanToInvert", true)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        Map.of("timestamp", 1623871499055L, "booleanToInvert", false),
        Map.of("timestamp", 1623871500059L, "booleanToInvert", false),
        Map.of("timestamp", 1623871501064L, "booleanToInvert", false),
        Map.of("timestamp", 1623871502070L, "booleanToInvert", true),
        Map.of("timestamp", 1623871503078L, "booleanToInvert", true),
        Map.of("timestamp", 1623871504082L, "booleanToInvert", true),
        Map.of("timestamp", 1623871505084L, "booleanToInvert", false),
        Map.of("timestamp", 1623871506086L, "booleanToInvert", false),
        Map.of("timestamp", 1623871507091L, "booleanToInvert", true),
        Map.of("timestamp", 1623871508093L, "booleanToInvert", false)
    );

    ProcessingElementTestExecutor testExecutor =
        new ProcessingElementTestExecutor(new BooleanInverterProcessor(), configuration);

    testExecutor.run(inputEvents, expectedEvents);
  }
}
