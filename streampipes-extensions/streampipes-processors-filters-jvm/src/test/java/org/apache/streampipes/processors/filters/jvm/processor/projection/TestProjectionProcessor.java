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
package org.apache.streampipes.processors.filters.jvm.processor.projection;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class TestProjectionProcessor {


  @Test
  public void projection1() {
    var configuration = TestConfiguration
        .builder()
        .customOutputStrategy(List.of("timestamp", "a"))
        .build();
    List<Map<String, Object>> events = List.of(
        Map.of(
            "timestamp", 1623871499055L,
            "remove", 62.0d,
            "a", "a"
        ),
        Map.of(
            "timestamp", 1623871504082L,
            "remove", 56.0d,
            "a", "a"
        ),
        Map.of(
            "timestamp", 1623871505084L,
            "remove", 95.0d,
            "a", "a"
        ),
        Map.of(
            "timestamp", 1623871506086L,
            "remove", 77.0d,
            "a", "a"
        ),
        Map.of(
            "timestamp", 1623871507091L,
            "remove", 85.0d,
            "a", "a"
        )
    );

    List<Map<String, Object>> outputEvents = List.of(
        Map.of(
            "timestamp", 1623871499055L,
            "a", "a"
        ),
        Map.of(
            "timestamp", 1623871504082L,
            "a", "a"
        ),
        Map.of(
            "timestamp", 1623871505084L,
            "a", "a"
        ),
        Map.of(
            "timestamp", 1623871506086L,
            "a", "a"
        ),
        Map.of(
            "timestamp", 1623871507091L,
            "a", "a"
        )
    );

    var testExecutor = new ProcessingElementTestExecutor(new ProjectionProcessor(), configuration);

    testExecutor.run(events, outputEvents);
  }

  @Test
  public void projectsSingleField() {
    var configuration = TestConfiguration
        .builder()
        .customOutputStrategy(List.of("remove"))
        .build();

    List<Map<String, Object>> events = List.of(
        Map.of("timestamp", 1L, "remove", 62.0d, "a", "x"),
        Map.of("timestamp", 2L, "remove", 56.0d, "a", "y")
    );

    List<Map<String, Object>> outputEvents = List.of(
        Map.of("remove", 62.0d),
        Map.of("remove", 56.0d)
    );

    new ProcessingElementTestExecutor(new ProjectionProcessor(), configuration)
        .run(events, outputEvents);
  }

}
