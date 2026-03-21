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

package org.apache.streampipes.processors.filters.jvm.processor.schema;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;
import org.apache.streampipes.test.generator.EventStreamGenerator;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MergeBySchemaProcessorTest {

  @Test
  void forwardsEventsWhenSchemasMatch() {
    var configuration = TestConfiguration.builder()
        .prefixStrategy(PrefixStrategy.ALTERNATE)
        .build();

    var invocationConfig = withInputStreams(
        EventStreamGenerator.makeStreamWithProperties(List.of("timestamp", "temperature")),
        EventStreamGenerator.makeStreamWithProperties(List.of("timestamp", "temperature"))
    );

    List<Map<String, Object>> inputEvents = List.of(
        event("timestamp", 1L, "temperature", 25.5d),
        event("timestamp", 2L, "temperature", 26.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        event("timestamp", 1L, "temperature", 25.5d),
        event("timestamp", 2L, "temperature", 26.0d)
    );

    new ProcessingElementTestExecutor(new MergeBySchemaProcessor(), configuration, invocationConfig)
        .run(inputEvents, expectedEvents);
  }

  @Test
  void rejectsDifferentSchemas() {
    var configuration = TestConfiguration.builder()
        .prefixStrategy(PrefixStrategy.ALTERNATE)
        .build();

    var invocationConfig = withInputStreams(
        EventStreamGenerator.makeStreamWithProperties(List.of("timestamp", "temperature")),
        EventStreamGenerator.makeStreamWithProperties(List.of("timestamp", "humidity"))
    );

    List<Map<String, Object>> inputEvents = List.of(
        event("timestamp", 1L, "temperature", 25.5d),
        event("timestamp", 2L, "humidity", 60.0d)
    );

    assertThrows(
        SpRuntimeException.class,
        () -> new ProcessingElementTestExecutor(new MergeBySchemaProcessor(), configuration, invocationConfig)
            .run(inputEvents, List.of())
    );
  }

  private Consumer<DataProcessorInvocation> withInputStreams(SpDataStream firstStream, SpDataStream secondStream) {
    return invocation -> invocation.setInputStreams(List.of(firstStream, secondStream));
  }

  private Map<String, Object> event(Object... keyValuePairs) {
    var event = new LinkedHashMap<String, Object>();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      event.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
    }
    return event;
  }
}
