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
package org.apache.streampipes.processors.filters.jvm.processor.numericalfilter;

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NumericalFilterProcessorTest {

  private static final String PROPERTY_NAME = "propertyName";
  private NumericalFilterProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new NumericalFilterProcessor();
  }

  @Test
  public void testLowerThenOperatorFilterNotApplied() {

    TestConfiguration configuration = TestConfiguration.builder()
            .configWithDefaultPrefix(NumericalFilterProcessor.NUMBER_MAPPING, PROPERTY_NAME)
            .config(NumericalFilterProcessor.VALUE, 10.0).config(NumericalFilterProcessor.OPERATION, "<").build();

    List<Map<String, Object>> inputEvents = List.of(Map.of(PROPERTY_NAME, 1.0f));

    List<Map<String, Object>> outputEvents = List.of(Map.of(PROPERTY_NAME, 1.0f));

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }

  @Test
  public void testLowerThenOperatorFilterApplied() {

    TestConfiguration configuration = TestConfiguration.builder()
            .configWithDefaultPrefix(NumericalFilterProcessor.NUMBER_MAPPING, PROPERTY_NAME)
            .config(NumericalFilterProcessor.VALUE, 10.0).config(NumericalFilterProcessor.OPERATION, "<").build();

    List<Map<String, Object>> inputEvents = List.of(Map.of(PROPERTY_NAME, 11.0f));

    List<Map<String, Object>> outputEvents = List.of();

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }

}