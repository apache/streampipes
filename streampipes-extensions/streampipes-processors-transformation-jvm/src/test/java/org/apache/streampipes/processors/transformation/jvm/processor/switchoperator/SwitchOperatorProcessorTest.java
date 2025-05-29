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

package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SwitchOperatorProcessorTest {
  private static final String SWITCH_FILTER_KEY = "switch-filter-key";
  private static final String SWITCH_CASE_VALUE = "switch-case-value";
  private static final String SWITCH_CASE_VALUE_OUTPUT = "switch-case-value-output";
  private static final String SWITCH_FILTER_OUTPUT_KEY = "switch-filter-result";
  private static final String OUTPUT_TYPE_KEY = "output-type";

  private SwitchOperatorProcessor processor;
  private IDataProcessorParameters params;
  private IDataProcessorParameterExtractor extractor;
  private SpOutputCollector collector;
  private EventProcessorRuntimeContext context;
  private Event event;

  @BeforeEach
  void setUp() {
    processor = new SwitchOperatorProcessor();
    params = mock(IDataProcessorParameters.class);
    extractor = mock(IDataProcessorParameterExtractor.class);
    collector = mock(SpOutputCollector.class);
    context = mock(EventProcessorRuntimeContext.class);
    event = new Event();

    when(params.extractor()).thenReturn(extractor);
  }

  @Test
  void testDeclareConfig() {
    var config = processor.declareConfig();

    assertNotNull(config, "Configuration should not be null");
    assertNotNull(config.getDescription(), "Processing element description should not be null");

    var description = config.getDescription();
    assertEquals("sp:org.apache.streampipes.processors.transformation.jvm.switchoperator", description.getElementId(), "Element ID should match");
    assertEquals(4, description.getStaticProperties().size(), "Should have four static properties");
    assertEquals(List.of(DataProcessorType.TRANSFORM.name()), description.getCategory(), "Category should be FILTER");

    var dataStreams = description.getSpDataStreams();
    assertEquals(1, dataStreams.size(), "Should have one input stream");
    var eventProperties = dataStreams.get(0).getEventSchema().getEventProperties();
    assertTrue(eventProperties.stream().anyMatch(ep -> ep instanceof EventPropertyPrimitive
            && ep.getRuntimeName().equals(SWITCH_FILTER_KEY)),
        "Stream should require a property with correct runtime name");

    var outputStrategies = description.getOutputStrategies();
    assertEquals(1, outputStrategies.size(), "Should have one output strategy");
    assertTrue(outputStrategies.get(0) instanceof AppendOutputStrategy,
        "Output strategy should be AppendOutputStrategy");
    var appendStrategy = (AppendOutputStrategy) outputStrategies.get(0);
    assertEquals(1, appendStrategy.getEventProperties().size(),
        "Should append one property");
  }

  @Test
  void testOnPipelineStarted() {
    when(extractor.mappingPropertyValue(SWITCH_FILTER_KEY)).thenReturn("field1");
    when(extractor.textParameter(SWITCH_CASE_VALUE)).thenReturn("value1");
    when(extractor.selectedSingleValue(OUTPUT_TYPE_KEY, String.class)).thenReturn("Boolean");
    when(extractor.selectedSingleValue(SWITCH_CASE_VALUE_OUTPUT, String.class)).thenReturn("true");

    processor.onPipelineStarted(params, collector, context);

    event.addField(new PrimitiveField("field1", "field1", "value1"));
    processor.onEvent(event, collector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(collector).collect(eventCaptor.capture());
    assertEquals("true",
        eventCaptor.getValue().getFieldByRuntimeName(SWITCH_FILTER_OUTPUT_KEY).getAsPrimitive().getAsString(),
        "Output should be 'true' for matching case after pipeline start");
  }

  @Test
  void testOnEventMatchCaseBoolean() {
    when(extractor.mappingPropertyValue(SWITCH_FILTER_KEY)).thenReturn("field1");
    when(extractor.textParameter(SWITCH_CASE_VALUE)).thenReturn("value1");
    when(extractor.selectedSingleValue(OUTPUT_TYPE_KEY, String.class)).thenReturn("Boolean");
    when(extractor.selectedSingleValue(SWITCH_CASE_VALUE_OUTPUT, String.class)).thenReturn("true");
    processor.onPipelineStarted(params, collector, context);

    event.addField(new PrimitiveField("field1", "field1", "value1"));

    processor.onEvent(event, collector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(collector).collect(eventCaptor.capture());
    assertEquals("true",
        eventCaptor.getValue().getFieldByRuntimeName(SWITCH_FILTER_OUTPUT_KEY).getAsPrimitive().getAsString(),
        "Output should be 'true' for matching case with Boolean output");
  }

  @Test
  void testOnEventMatchCaseString() {
    when(extractor.mappingPropertyValue(SWITCH_FILTER_KEY)).thenReturn("field1");
    when(extractor.textParameter(SWITCH_CASE_VALUE)).thenReturn("value1");
    when(extractor.selectedSingleValue(OUTPUT_TYPE_KEY, String.class)).thenReturn("String");
    when(extractor.selectedSingleValue(SWITCH_CASE_VALUE_OUTPUT, String.class)).thenReturn("match");
    processor.onPipelineStarted(params, collector, context);

    event.addField(new PrimitiveField("field1", "field1", "value1"));

    processor.onEvent(event, collector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(collector).collect(eventCaptor.capture());
    assertEquals("match",
        eventCaptor.getValue().getFieldByRuntimeName(SWITCH_FILTER_OUTPUT_KEY).getAsPrimitive().getAsString(),
        "Output should be 'match' for matching case with String output");
  }

  @Test
  void testOnEventMatchCaseInteger() {
    when(extractor.mappingPropertyValue(SWITCH_FILTER_KEY)).thenReturn("field1");
    when(extractor.textParameter(SWITCH_CASE_VALUE)).thenReturn("value1");
    when(extractor.selectedSingleValue(OUTPUT_TYPE_KEY, String.class)).thenReturn("Integer");
    when(extractor.selectedSingleValue(SWITCH_CASE_VALUE_OUTPUT, String.class)).thenReturn("42");
    processor.onPipelineStarted(params, collector, context);

    event.addField(new PrimitiveField("field1", "field1", "value1"));

    processor.onEvent(event, collector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(collector).collect(eventCaptor.capture());
    assertEquals("42",
        eventCaptor.getValue().getFieldByRuntimeName(SWITCH_FILTER_OUTPUT_KEY).getAsPrimitive().getAsString(),
        "Output should be '42' for matching case with Integer output");
  }

  @Test
  void testOnEventNoMatch() {
    when(extractor.mappingPropertyValue(SWITCH_FILTER_KEY)).thenReturn("field1");
    when(extractor.textParameter(SWITCH_CASE_VALUE)).thenReturn("value1");
    when(extractor.selectedSingleValue(OUTPUT_TYPE_KEY, String.class)).thenReturn("Boolean");
    when(extractor.selectedSingleValue(SWITCH_CASE_VALUE_OUTPUT, String.class)).thenReturn("true");
    processor.onPipelineStarted(params, collector, context);

    event.addField(new PrimitiveField("field1", "field1", "value2"));

    processor.onEvent(event, collector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(collector).collect(eventCaptor.capture());
    assertEquals("false",
        eventCaptor.getValue().getFieldByRuntimeName(SWITCH_FILTER_OUTPUT_KEY).getAsPrimitive().getAsString(),
        "Output should be 'false' for non-matching case with Boolean output");
  }

  @Test
  void testOnEventNullField() {
    when(extractor.mappingPropertyValue(SWITCH_FILTER_KEY)).thenReturn("field1");
    when(extractor.textParameter(SWITCH_CASE_VALUE)).thenReturn("value1");
    when(extractor.selectedSingleValue(OUTPUT_TYPE_KEY, String.class)).thenReturn("String");
    when(extractor.selectedSingleValue(SWITCH_CASE_VALUE_OUTPUT, String.class)).thenReturn("match");
    processor.onPipelineStarted(params, collector, context);

    // No field added, simulating null/missing field

    processor.onEvent(event, collector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(collector).collect(eventCaptor.capture());
    assertEquals("",
        eventCaptor.getValue().getFieldByRuntimeName(SWITCH_FILTER_OUTPUT_KEY).getAsPrimitive().getAsString(),
        "Output should be empty string for null field with String output");
  }

  @Test
  void testOnEventExceptionHandling() {
    when(extractor.mappingPropertyValue(SWITCH_FILTER_KEY)).thenReturn("field1");
    when(extractor.textParameter(SWITCH_CASE_VALUE)).thenReturn("value1");
    when(extractor.selectedSingleValue(OUTPUT_TYPE_KEY, String.class)).thenReturn("Integer");
    when(extractor.selectedSingleValue(SWITCH_CASE_VALUE_OUTPUT, String.class)).thenReturn("42");
    processor.onPipelineStarted(params, collector, context);

    // Add a non-string field to trigger a potential ClassCastException
    event.addField(new PrimitiveField("field1", "field1", 123));

    processor.onEvent(event, collector);

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(collector).collect(eventCaptor.capture());
    assertEquals("0",
        eventCaptor.getValue().getFieldByRuntimeName(SWITCH_FILTER_OUTPUT_KEY).getAsPrimitive().getAsString(),
        "Output should be '0' when exception occurs with Integer output");
  }
}
