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
package org.apache.streampipes.processors.transformation.jvm.processor.mapper;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class FieldMapperProcessorTest {

  @Test
  void fieldMapper1() {
    TestConfiguration configuration = TestConfiguration.builder()
        .config("replaceProperties", List.of("::value1", "::value2"))
        .config("fieldName", "hashedField")
        .build();

    List<Map<String, Object>> inputEvents = List.of(
        Map.of("timestamp", 1623871499055L, "value1", "abc", "value2", "def"),
        Map.of("timestamp", 1623871500059L, "value1", "123", "value2", "456")
    );

    List<Map<String, Object>> expectedEvents = List.of(
        Map.of("timestamp", 1623871499055L, "hashedField", "e80b5017098950fc58aad83c8c14978e"),
        Map.of("timestamp", 1623871500059L, "hashedField", "e10adc3949ba59abbe56e057f20f883e")
    );

    ProcessingElementTestExecutor testExecutor =
        new ProcessingElementTestExecutor(new FieldMapperProcessor(), configuration);

    testExecutor.run(inputEvents, expectedEvents);
  }
}
