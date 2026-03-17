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
package org.apache.streampipes.processors.transformation.jvm.processor.fieldrename;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class FiledRenameProcessorTest {

  @Test
  void fieldRenamer1() {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix("convert-property", "count")
        .config("field-name", "newname")
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        Map.of("timestamp", 1623871499055L, "count", 122.0d),
        Map.of("timestamp", 1623871500059L, "count", 123.0d),
        Map.of("timestamp", 1623871501064L, "count", 124.0d),
        Map.of("timestamp", 1623871502070L, "count", 125.0d),
        Map.of("timestamp", 1623871503078L, "count", 126.0d),
        Map.of("timestamp", 1623871504082L, "count", 127.0d),
        Map.of("timestamp", 1623871505084L, "count", 128.0d),
        Map.of("timestamp", 1623871506086L, "count", 129.0d),
        Map.of("timestamp", 1623871507091L, "count", 130.0d),
        Map.of("timestamp", 1623871508093L, "count", 131.0d)
    );

    List<Map<String, Object>> expectedEvents = List.of(
        Map.of("timestamp", 1623871499055L, "newname", 122.0d),
        Map.of("timestamp", 1623871500059L, "newname", 123.0d),
        Map.of("timestamp", 1623871501064L, "newname", 124.0d),
        Map.of("timestamp", 1623871502070L, "newname", 125.0d),
        Map.of("timestamp", 1623871503078L, "newname", 126.0d),
        Map.of("timestamp", 1623871504082L, "newname", 127.0d),
        Map.of("timestamp", 1623871505084L, "newname", 128.0d),
        Map.of("timestamp", 1623871506086L, "newname", 129.0d),
        Map.of("timestamp", 1623871507091L, "newname", 130.0d),
        Map.of("timestamp", 1623871508093L, "newname", 131.0d)
    );

    ProcessingElementTestExecutor testExecutor =
        new ProcessingElementTestExecutor(new FiledRenameProcessor(), configuration);

    testExecutor.run(inputEvents, expectedEvents);
  }
}
