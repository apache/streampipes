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

package org.apache.streampipes.processors.enricher.jvm.processor.expression;

import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class MathExpressionProcessorTest {


  private MathExpressionProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new MathExpressionProcessor();
  }

  @Test
  public void testProcessor() {

    List<Map<String, Object>> inputEvents = List.of(Map.of(
        "temperature", 10.1,
        "flowrate", 2
    ));

    List<Map<String, Object>> outputEvents = List.of(Map.of(
        "temperature", 10.1,
        "flowrate", 2,
        "result1", 22.1,
        "result2", 20.2
    ));

    var config =
        List.of(Map.of(
                MathExpressionProcessor.FIELD_NAME, "result1",
                MathExpressionProcessor.EXPRESSION, "temperature+12"
            ),
            Map.of(
                MathExpressionProcessor.FIELD_NAME, "result2",
                MathExpressionProcessor.EXPRESSION, "temperature*flowrate"
            )
        );

    var configuration = TestConfiguration
        .builder()
        .config(MathExpressionProcessor.ENRICHED_FIELDS, config)
        .prefixStrategy(PrefixStrategy.SAME_PREFIX)
        .build();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }
}
