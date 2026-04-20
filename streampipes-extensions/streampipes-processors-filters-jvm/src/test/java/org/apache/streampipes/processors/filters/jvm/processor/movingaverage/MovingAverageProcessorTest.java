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

package org.apache.streampipes.processors.filters.jvm.processor.movingaverage;

import org.apache.streampipes.test.executors.Approx;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class MovingAverageProcessorTest {

  @Test
  void meanMethodUsesSlidingAverage() {
    var configuration = createConfiguration(3, "mean");

    List<Map<String, Object>> inputEvents = List.of(
        event("timestamp", 1L, "temperature", 1.0d),
        event("timestamp", 2L, "temperature", 2.0d),
        event("timestamp", 3L, "temperature", 3.0d),
        event("timestamp", 4L, "temperature", 6.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("timestamp", 1L, "temperature", 1.0d, "filterResult", new Approx(1.0d, 0.0000001d)),
        event("timestamp", 2L, "temperature", 2.0d, "filterResult", new Approx(1.5d, 0.0000001d)),
        event("timestamp", 3L, "temperature", 3.0d, "filterResult", new Approx(2.0d, 0.0000001d)),
        event("timestamp", 4L, "temperature", 6.0d, "filterResult", new Approx(3.6666666667d, 0.0000001d))
    );

    new ProcessingElementTestExecutor(new MovingAverageProcessor(), configuration)
        .run(inputEvents, expectedEvents);
  }

  @Test
  void medianMethodHandlesOutliers() {
    var configuration = createConfiguration(3, "median");

    List<Map<String, Object>> inputEvents = List.of(
        event("timestamp", 1L, "temperature", 1.0d),
        event("timestamp", 2L, "temperature", 100.0d),
        event("timestamp", 3L, "temperature", 2.0d),
        event("timestamp", 4L, "temperature", 3.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("timestamp", 1L, "temperature", 1.0d, "filterResult", new Approx(1.0d, 0.0000001d)),
        event("timestamp", 2L, "temperature", 100.0d, "filterResult", new Approx(50.5d, 0.0000001d)),
        event("timestamp", 3L, "temperature", 2.0d, "filterResult", new Approx(2.0d, 0.0000001d)),
        event("timestamp", 4L, "temperature", 3.0d, "filterResult", new Approx(3.0d, 0.0000001d))
    );

    new ProcessingElementTestExecutor(new MovingAverageProcessor(), configuration)
        .run(inputEvents, expectedEvents);
  }

  private TestConfiguration createConfiguration(int windowSize, String method) {
    return TestConfiguration.builder()
        .configWithDefaultPrefix("number", "temperature")
        .config("n", windowSize)
        .config("method", method)
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
