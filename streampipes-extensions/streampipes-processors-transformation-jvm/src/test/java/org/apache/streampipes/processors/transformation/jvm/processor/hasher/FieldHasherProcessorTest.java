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
package org.apache.streampipes.processors.transformation.jvm.processor.hasher;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class FieldHasherProcessorTest {

  @Test
  void fieldHasher1() {
    executeFixtureTest(
        "SHA1",
        List.of(
            Map.of("timestamp", 1623871499055L, "sensorId", "abc"),
            Map.of("timestamp", 1623871500059L, "sensorId", "123")
        ),
        List.of(
            Map.of("timestamp", 1623871499055L, "sensorId", "a9993e364706816aba3e25717850c26c9cd0d89d"),
            Map.of("timestamp", 1623871500059L, "sensorId", "40bd001563085fc35165329ea1ff5c5ecbdbbeef")
        )
    );
  }

  @Test
  void fieldHasher2() {
    executeFixtureTest(
        "MD5",
        List.of(
            Map.of("timestamp", 1623871499055L, "sensorId", "abc"),
            Map.of("timestamp", 1623871500059L, "sensorId", "123")
        ),
        List.of(
            Map.of("timestamp", 1623871499055L, "sensorId", "900150983cd24fb0d6963f7d28e17f72"),
            Map.of("timestamp", 1623871500059L, "sensorId", "202cb962ac59075b964b07152d234b70")
        )
    );
  }

  private void executeFixtureTest(
      String algorithm,
      List<Map<String, Object>> inputEvents,
      List<Map<String, Object>> expectedEvents
  ) {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix("property-mapping", "sensorId")
        .config("hash-algorithm", algorithm)
        .build();

    ProcessingElementTestExecutor testExecutor =
        new ProcessingElementTestExecutor(new FieldHasherProcessor(), configuration);

    testExecutor.run(inputEvents, expectedEvents);
  }
}
