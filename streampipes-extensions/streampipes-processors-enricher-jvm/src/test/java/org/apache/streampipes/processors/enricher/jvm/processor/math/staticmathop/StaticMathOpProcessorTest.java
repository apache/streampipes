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
package org.apache.streampipes.processors.enricher.jvm.processor.math.staticmathop;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class StaticMathOpProcessorTest {

  private StaticMathOpProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new StaticMathOpProcessor();
  }

  @Test
  void staticmath1() {
    executeFixtureTest(
        "*",
        5.0d,
        List.of(
            Map.of("timestamp", 1623871499055L, "temperature1", 4.0d),
            Map.of("timestamp", 1623871500059L, "temperature1", 3.5d)
        ),
        List.of(
            Map.of("timestamp", 1623871499055L, "temperature1", 20.0d),
            Map.of("timestamp", 1623871500059L, "temperature1", 17.5d)
        )
    );
  }

  @Test
  void staticmath2() {
    executeFixtureTest(
        "/",
        1.5d,
        List.of(
            Map.of("timestamp", 1623871499055L, "temperature1", 6.0d),
            Map.of("timestamp", 1623871500059L, "temperature1", 3.0d)
        ),
        List.of(
            Map.of("timestamp", 1623871499055L, "temperature1", 4.0d),
            Map.of("timestamp", 1623871500059L, "temperature1", 2.0d)
        )
    );
  }

  private void executeFixtureTest(
      String operation,
      double rightOperand,
      List<Map<String, Object>> inputEvents,
      List<Map<String, Object>> outputEvents
  ) {
    TestConfiguration configuration = TestConfiguration.builder()
        .configWithDefaultPrefix("leftOperand", "temperature1")
        .config("rightOperandValue", rightOperand)
        .config("operation", operation)
        .build();

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);
    testExecutor.run(inputEvents, outputEvents);
  }
}
