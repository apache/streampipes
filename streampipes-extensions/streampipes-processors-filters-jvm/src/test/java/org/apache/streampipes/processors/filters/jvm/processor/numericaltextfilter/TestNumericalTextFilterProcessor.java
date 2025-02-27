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

package org.apache.streampipes.processors.filters.jvm.processor.numericaltextfilter;

import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestNumericalTextFilterProcessor {

  private NumericalTextFilterProcessor processor;
  private ProcessorParams params;
  private ProcessingElementParameterExtractor extractor;
  private SpOutputCollector outputCollector;

  private static final String NUMBER_FIELD = "s0::number";
  private static final String TEXT_FIELD = "s0::text";

  @BeforeEach
  void setUp() {
    processor = new NumericalTextFilterProcessor();
    params = mock(ProcessorParams.class);
    extractor = mock(ProcessingElementParameterExtractor.class);
    outputCollector = mock(SpOutputCollector.class);
    when(params.extractor()).thenReturn(extractor);
  }

  private void setupExtractor(Map<String, Object> fieldConfig) {
    when(extractor.mappingPropertyValue("number-mapping")).thenReturn((String) fieldConfig.get("number-mapping"));
    when(extractor.singleValueParameter("number-value", Double.class)).thenReturn(
        (Double) fieldConfig.get("number-value"));
    when(extractor.selectedSingleValue("number-operation", String.class)).thenReturn(
        (String) fieldConfig.get("number-operation"));
    when(extractor.mappingPropertyValue("text-mapping")).thenReturn((String) fieldConfig.get("text-mapping"));
    when(extractor.singleValueParameter("text-keyword", String.class)).thenReturn(
        (String) fieldConfig.get("text-keyword"));
    when(extractor.selectedSingleValue("text-operation", String.class)).thenReturn(
        (String) fieldConfig.get("text-operation"));
  }

  @Test
  void testFilterPasses() {
    Map<String, Object> fieldConfig = Map.of(
        "number-mapping", NUMBER_FIELD,
        "number-value", 10.0,
        "number-operation", ">",
        "text-mapping", TEXT_FIELD,
        "text-keyword", "hello",
        "text-operation", "CONTAINS"
    );
    setupExtractor(fieldConfig);
    processor.onInvocation(params, outputCollector, null);

    ProcessingElementTestExecutor testExecutor = getElementTestExecutor(fieldConfig);
    Map<String, Object> eventMap = Map.of("number", 50.0, "text", "hello world");
    testExecutor.run(List.of(eventMap), List.of(eventMap));
  }

  @Test
  void testFilterFails() {
    Map<String, Object> fieldConfig = Map.of(
        "number-mapping", NUMBER_FIELD,
        "number-value", 40.0,
        "number-operation", ">=",
        "text-mapping", TEXT_FIELD,
        "text-keyword", "hello",
        "text-operation", "CONTAINS"
    );
    setupExtractor(fieldConfig);
    processor.onInvocation(params, outputCollector, null);

    ProcessingElementTestExecutor testExecutor = getElementTestExecutor(fieldConfig);
    Map<String, Object> eventMap = Map.of("number", 30.0, "text", "hello world");
    testExecutor.run(List.of(eventMap), Collections.emptyList());
  }

  @Test
  void testEqualsOperator() {
    Map<String, Object> fieldConfig = Map.of(
        "number-mapping", NUMBER_FIELD,
        "number-value", 42.0,
        "number-operation", "==",
        "text-mapping", TEXT_FIELD,
        "text-keyword", "exact",
        "text-operation", "MATCHES"
    );
    setupExtractor(fieldConfig);
    processor.onInvocation(params, outputCollector, null);

    ProcessingElementTestExecutor testExecutor = getElementTestExecutor(fieldConfig);
    Map<String, Object> eventMap = Map.of("number", 42.0, "text", "exact");
    testExecutor.run(List.of(eventMap), List.of(eventMap));
  }

  @Test
  void testFailsOnTextCondition() {
    Map<String, Object> fieldConfig = Map.of(
        "number-mapping", NUMBER_FIELD,
        "number-value", 10.0,
        "number-operation", "<",
        "text-mapping", TEXT_FIELD,
        "text-keyword", "missing",
        "text-operation", "CONTAINS"
    );
    setupExtractor(fieldConfig);
    processor.onInvocation(params, outputCollector, null);

    ProcessingElementTestExecutor testExecutor = getElementTestExecutor(fieldConfig);
    Map<String, Object> eventMap = Map.of("number", 5.0, "text", "hello world");
    testExecutor.run(List.of(eventMap), Collections.emptyList());
  }

  private static ProcessingElementTestExecutor getElementTestExecutor(Map<String, Object> fieldConfig) {
    NumericalTextFilterProcessor testProcessor = new NumericalTextFilterProcessor();
    TestConfiguration testConfig = new TestConfiguration(fieldConfig, List.of("s0"));
    return new ProcessingElementTestExecutor(testProcessor, testConfig);
  }
}
