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
package org.apache.streampipes.processors.transformation.jvm.processor.array.split;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class SplitArrayProcessorTest {

  @Test
  void splitArray1() {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix(SplitArrayProcessor.ARRAY_FIELD_ID, "arrayProperty")
        .config(SplitArrayProcessor.KEEP_PROPERTIES_ID, List.of("::timestamp"))
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        Map.of("timestamp", 1623871500059L, "arrayProperty", List.of(1, 4)),
        Map.of("timestamp", 1623871508093L, "arrayProperty", List.of(1))
    );

    List<Map<String, Object>> expectedEvents = List.of(
        Map.of("timestamp", 1623871500059L, SplitArrayProcessor.VALUE, 1),
        Map.of("timestamp", 1623871500059L, SplitArrayProcessor.VALUE, 4),
        Map.of("timestamp", 1623871508093L, SplitArrayProcessor.VALUE, 1)
    );

    ProcessingElementTestExecutor testExecutor =
        new ProcessingElementTestExecutor(new SplitArrayProcessor(), configuration);

    testExecutor.run(inputEvents, expectedEvents);
  }
}
