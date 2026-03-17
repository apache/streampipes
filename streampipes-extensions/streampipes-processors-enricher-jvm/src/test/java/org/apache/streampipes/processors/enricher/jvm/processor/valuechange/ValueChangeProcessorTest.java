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
package org.apache.streampipes.processors.enricher.jvm.processor.valuechange;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ValueChangeProcessorTest {

  @Test
  void valueChange1() {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix("change-value-mapping", "numberlist")
        .config("from-property-value", 3.0f)
        .config("to-property-value", 4.0f)
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        Map.of("timestamp", 1623871499055L, "numberlist", 2.0f),
        Map.of("timestamp", 1623871500059L, "numberlist", 3.0f),
        Map.of("timestamp", 1623871501064L, "numberlist", 4.0f),
        Map.of("timestamp", 1623871502070L, "numberlist", 5.0f),
        Map.of("timestamp", 1623871503078L, "numberlist", 2.0f),
        Map.of("timestamp", 1623871504082L, "numberlist", 3.0f),
        Map.of("timestamp", 1623871505084L, "numberlist", 4.0f),
        Map.of("timestamp", 1623871506086L, "numberlist", 5.0f),
        Map.of("timestamp", 1623871507091L, "numberlist", 6.0f),
        Map.of("timestamp", 1623871508093L, "numberlist", 7.0f)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        Map.of("timestamp", 1623871499055L, "numberlist", 2.0f, "isChanged", false),
        Map.of("timestamp", 1623871500059L, "numberlist", 3.0f, "isChanged", false),
        Map.of("timestamp", 1623871501064L, "numberlist", 4.0f, "isChanged", true),
        Map.of("timestamp", 1623871502070L, "numberlist", 5.0f, "isChanged", false),
        Map.of("timestamp", 1623871503078L, "numberlist", 2.0f, "isChanged", false),
        Map.of("timestamp", 1623871504082L, "numberlist", 3.0f, "isChanged", false),
        Map.of("timestamp", 1623871505084L, "numberlist", 4.0f, "isChanged", true),
        Map.of("timestamp", 1623871506086L, "numberlist", 5.0f, "isChanged", false),
        Map.of("timestamp", 1623871507091L, "numberlist", 6.0f, "isChanged", false),
        Map.of("timestamp", 1623871508093L, "numberlist", 7.0f, "isChanged", false)
    );

    ProcessingElementTestExecutor testExecutor =
        new ProcessingElementTestExecutor(new ValueChangeProcessor(), configuration);

    testExecutor.run(inputEvents, expectedEvents);
  }

  @Test
  void detectsDifferentTransitionConfiguration() {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix("change-value-mapping", "numberlist")
        .config("from-property-value", 2.0f)
        .config("to-property-value", 5.0f)
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        Map.of("timestamp", 1L, "numberlist", 2.0f),
        Map.of("timestamp", 2L, "numberlist", 5.0f),
        Map.of("timestamp", 3L, "numberlist", 5.0f),
        Map.of("timestamp", 4L, "numberlist", 2.0f),
        Map.of("timestamp", 5L, "numberlist", 5.0f)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        Map.of("timestamp", 1L, "numberlist", 2.0f, "isChanged", false),
        Map.of("timestamp", 2L, "numberlist", 5.0f, "isChanged", true),
        Map.of("timestamp", 3L, "numberlist", 5.0f, "isChanged", false),
        Map.of("timestamp", 4L, "numberlist", 2.0f, "isChanged", false),
        Map.of("timestamp", 5L, "numberlist", 5.0f, "isChanged", true)
    );

    new ProcessingElementTestExecutor(new ValueChangeProcessor(), configuration)
        .run(inputEvents, expectedEvents);
  }
}
