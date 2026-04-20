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

package org.apache.streampipes.processors.filters.jvm.processor.enrich;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class MergeByEnrichProcessorTest {

  @Test
  void enrichesSelectedFirstStreamWithLastSecondStreamEvent() {
    var configuration = TestConfiguration.builder()
        .customPrefixStrategy(List.of("s1", "s0", "s1", "s0"))
        .config("select-stream", "Stream 1")
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        event("location", "room-1"),
        event("deviceId", "sensor-1", "temperature", 21.5d),
        event("location", "room-2"),
        event("deviceId", "sensor-2", "temperature", 22.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("deviceId", "sensor-1", "temperature", 21.5d, "location", "room-1"),
        event("deviceId", "sensor-2", "temperature", 22.0d, "location", "room-2")
    );

    new ProcessingElementTestExecutor(
        new MergeByEnrichProcessor(),
        configuration,
        setOutputKeys("s0::deviceId", "s0::temperature", "s1::location")
    ).run(inputEvents, expectedEvents);
  }

  @Test
  void enrichesSelectedSecondStreamWithLastFirstStreamEvent() {
    var configuration = TestConfiguration.builder()
        .customPrefixStrategy(List.of("s0", "s1", "s0", "s1"))
        .config("select-stream", "Stream 2")
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        event("deviceId", "sensor-1"),
        event("location", "room-1", "humidity", 40.0d),
        event("deviceId", "sensor-2"),
        event("location", "room-2", "humidity", 41.5d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("deviceId", "sensor-1", "location", "room-1", "humidity", 40.0d),
        event("deviceId", "sensor-2", "location", "room-2", "humidity", 41.5d)
    );

    new ProcessingElementTestExecutor(
        new MergeByEnrichProcessor(),
        configuration,
        setOutputKeys("s0::deviceId", "s1::location", "s1::humidity")
    ).run(inputEvents, expectedEvents);
  }

  private Consumer<DataProcessorInvocation> setOutputKeys(String... selectors) {
    return invocation -> invocation.getOutputStrategies()
        .stream()
        .filter(CustomOutputStrategy.class::isInstance)
        .map(CustomOutputStrategy.class::cast)
        .findFirst()
        .ifPresent(strategy -> strategy.setSelectedPropertyKeys(List.of(selectors)));
  }

  private Map<String, Object> event(Object... keyValuePairs) {
    var event = new LinkedHashMap<String, Object>();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      event.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
    }
    return event;
  }
}
