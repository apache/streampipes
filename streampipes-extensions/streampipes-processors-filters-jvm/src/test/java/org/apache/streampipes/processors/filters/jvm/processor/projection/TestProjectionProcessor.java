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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestProjectionProcessor {

  private ProjectionProcessor processor;
  private ProcessorParams processorParams;
  private SpOutputCollector outputCollector;
  private EventProcessorRuntimeContext runtimeContext;

  @BeforeEach
  void setUp() {
    processor = new ProjectionProcessor();
    processorParams = mock(ProcessorParams.class);
    outputCollector = mock(SpOutputCollector.class);
    runtimeContext = mock(EventProcessorRuntimeContext.class);

    ProcessingElementParameterExtractor mockExtractor = mock(ProcessingElementParameterExtractor.class);
    when(processorParams.extractor()).thenReturn(mockExtractor);

    when(mockExtractor.outputKeySelectors()).thenReturn(List.of("field1", "field2"));
  }

  @Test
  void testOnInvocationExtractsKeys() throws SpRuntimeException {
    processor.onInvocation(processorParams, outputCollector, runtimeContext);

    verify(processorParams.extractor(), times(1)).outputKeySelectors();
  }

  @Test
  void testOnEventFiltersEvent() throws SpRuntimeException {
    processor.onInvocation(processorParams, outputCollector, runtimeContext);

    Event inputEvent = new Event();
    inputEvent.addField("field1", "value1");
    inputEvent.addField("field2", "value2");
    inputEvent.addField("field3", "ignoredValue");

    processor.onEvent(inputEvent, outputCollector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(outputCollector, times(1)).collect(eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertEquals(2, capturedEvent.getFields().size());
    assertEquals("value1", capturedEvent.getFieldBySelector("field1").getAsPrimitive().getAsString());
    assertEquals("value2", capturedEvent.getFieldBySelector("field2").getAsPrimitive().getAsString());
  }

  @Test
  void testOnEventWithNoKeys() throws SpRuntimeException {
    when(processorParams.extractor().outputKeySelectors()).thenReturn(Collections.emptyList());

    processor.onInvocation(processorParams, outputCollector, runtimeContext);

    Event inputEvent = new Event();
    inputEvent.addField("field1", "value1");
    inputEvent.addField("field2", "value2");

    processor.onEvent(inputEvent, outputCollector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(outputCollector, times(1)).collect(eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertEquals(0, capturedEvent.getFields().size());
  }

  @Test
  void testOnEventThrowsExceptionWhenNotInvoked() {
    Event inputEvent = new Event();
    inputEvent.addField("field1", "value1");

    assertThrows(NullPointerException.class, () -> processor.onEvent(inputEvent, outputCollector));
  }
}
