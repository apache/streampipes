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

package org.apache.streampipes.processors.filters.jvm.processor.threshold;

import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestThresholdDetectionProcessor {

  private ThresholdDetectionProcessor processor;

  @Mock
  private ProcessorParams processorParams;

  @Mock
  private SpOutputCollector outputCollector;

  @Mock
  private EventProcessorRuntimeContext runtimeContext;

  @Mock
  private ProcessingElementParameterExtractor extractor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    processor = new ThresholdDetectionProcessor();

    when(processorParams.extractor()).thenReturn(extractor);
  }

  private void initializeProcessor(double threshold, String operator, String property) {
    when(extractor.singleValueParameter("value", Double.class)).thenReturn(threshold);
    when(extractor.selectedSingleValue("operation", String.class)).thenReturn(operator);
    when(extractor.mappingPropertyValue("number-mapping")).thenReturn(property);

    assertDoesNotThrow(() -> processor.onInvocation(processorParams, outputCollector, runtimeContext));
  }

  @Test
  void testOnInvocation() {
    initializeProcessor(10.0, ">=", "value");
  }

  @Test
  void testOnEventThresholdExceeded() {
    initializeProcessor(10.0, ">", "value");

    Event event = new Event();
    event.addField("value", 15.0);

    processor.onEvent(event, outputCollector);

    assertTrue(event.getFieldBySelector("thresholdDetected").getAsPrimitive().getAsBoolean());
    verify(outputCollector, times(1)).collect(event);
  }

  @Test
  void testOnEventThresholdNotExceeded() {
    initializeProcessor(10.0, "<", "value");

    Event event = new Event();
    event.addField("value", 15.0);

    processor.onEvent(event, outputCollector);

    assertFalse(event.getFieldBySelector("thresholdDetected").getAsPrimitive().getAsBoolean());
    verify(outputCollector, times(1)).collect(event);
  }

  @Test
  void testOnEventEqualThreshold() {
    initializeProcessor(10.0, "==", "value");

    Event event = new Event();
    event.addField("value", 10.0);

    processor.onEvent(event, outputCollector);

    assertNotNull(event.getFieldBySelector("thresholdDetected"), "Field 'thresholdDetected' should be present");
    assertTrue(event.getFieldBySelector("thresholdDetected").getAsPrimitive().getAsBoolean(),
        "Threshold should be detected as true");
  }
}
