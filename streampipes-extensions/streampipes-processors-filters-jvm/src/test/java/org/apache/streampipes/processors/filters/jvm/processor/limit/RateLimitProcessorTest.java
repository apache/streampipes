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
package org.apache.streampipes.processors.filters.jvm.processor.limit;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class RateLimitProcessorTest {

  @Test
  void rateLimit1() {
    TestConfiguration configuration = TestConfiguration.builder()
        .config("grouping-enabled", "False")
        .configWithDefaultPrefix("grouping-field", "randomnumber")
        .config("window-type", "length-window")
        .config("length-window-size", 5)
        .config("event-selection", "Last")
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        Map.of("timestamp", 1623871490000L, "randomnumber", 62.0d),
        Map.of("timestamp", 1623871491000L, "randomnumber", 46.0d),
        Map.of("timestamp", 1623871492000L, "randomnumber", 41.0d),
        Map.of("timestamp", 1623871493000L, "randomnumber", 41.0d),
        Map.of("timestamp", 1623871494000L, "randomnumber", 22.0d),
        Map.of("timestamp", 1623871495000L, "randomnumber", 56.0d),
        Map.of("timestamp", 1623871496000L, "randomnumber", 95.0d),
        Map.of("timestamp", 1623871497000L, "randomnumber", 77.0d),
        Map.of("timestamp", 1623871498000L, "randomnumber", 85.0d),
        Map.of("timestamp", 1623871499000L, "randomnumber", 26.0d),
        Map.of("timestamp", 1623871500000L, "randomnumber", 21.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        Map.of("timestamp", 1623871494000L, "randomnumber", 22.0d),
        Map.of("timestamp", 1623871499000L, "randomnumber", 26.0d)
    );

    ProcessingElementTestExecutor testExecutor =
        new ProcessingElementTestExecutor(new RateLimitProcessor(), configuration);

    testExecutor.run(inputEvents, expectedEvents);
  }
}
