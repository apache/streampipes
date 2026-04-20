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

package org.apache.streampipes.processors.filters.jvm.processor.throughputmon;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ThroughputMonitorProcessorTest {

  @Test
  void emitsStatisticsAfterConfiguredBatchSize() {
    var configuration = TestConfiguration.builder()
        .config("batch-window-key", 2)
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        event("value", 1),
        event("value", 2),
        event("value", 3)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("eventcount", 2)
    );

    new ProcessingElementTestExecutor(new ThroughputMonitorProcessor(), configuration)
        .run(inputEvents, expectedEvents);
  }

  @Test
  void resetsAfterEachBatchWindow() {
    var configuration = TestConfiguration.builder()
        .config("batch-window-key", 3)
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        event("value", 1),
        event("value", 2),
        event("value", 3),
        event("value", 4),
        event("value", 5),
        event("value", 6)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("eventcount", 3),
        event("eventcount", 3)
    );

    new ProcessingElementTestExecutor(new ThroughputMonitorProcessor(), configuration)
        .run(inputEvents, expectedEvents);
  }

  private Map<String, Object> event(Object... keyValuePairs) {
    var event = new LinkedHashMap<String, Object>();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      event.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
    }
    return event;
  }
}
