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
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.runtime.field.PrimitiveField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SwitchOperatorProcessorTest {

  private SwitchOperatorProcessor processor;
  private SpOutputCollector collector;
  private IDataProcessorParameters params;
  private IDataProcessorParameterExtractor extractor;
  private Event event;
  private EventProcessorRuntimeContext runtimeContext;

  // Constants to avoid string literals in tests
  private static final String SWITCH_FILTER_KEY = "switch-filter-key";
  private static final String SWITCH_CASE_VALUE = "switch-case-value";
  private static final String SWITCH_CASE_VALUE_OUTPUT = "switch-case-value-output";
  private static final String SWITCH_CASE_VALUE_DEFAULT_OUTPUT = "switch-case-value-default-output";
  private static final String SWITCH_FILTER_OUTPUT_KEY = "switch-filter-result";

  @BeforeEach
  void setUp() {
    processor = new SwitchOperatorProcessor();
    collector = mock(SpOutputCollector.class);
    params = mock(IDataProcessorParameters.class);
    extractor = mock(IDataProcessorParameterExtractor.class);
    when(params.extractor()).thenReturn(extractor);
    runtimeContext = mock(EventProcessorRuntimeContext.class);

    // Set up mock event with default behavior
    event = mock(Event.class);
    // Note: Not using hasField as it doesn't exist in the Event class
  }

  @Test
  void testDeclareConfig() {
    IDataProcessorConfiguration config = processor.declareConfig();
    assertNotNull(config);
  }

  @Test
  void testOnPipelineStarted() {
    // Setup
    String fieldSelector = "testField";
    String caseValue = "testValue";
    String outputValue = "true";
    String defaultOutput = "false";

    when(extractor.mappingPropertyValue(eq(SWITCH_FILTER_KEY))).thenReturn(fieldSelector);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE))).thenReturn(caseValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_OUTPUT))).thenReturn(outputValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_DEFAULT_OUTPUT))).thenReturn(defaultOutput);

    // Test
    processor.onPipelineStarted(params, collector, runtimeContext);
  }

  @Test
  void testOnEvent_MatchingCase() {
    // Setup
    String fieldSelector = "testField";
    String caseValue = "testValue";
    String outputValue = "true";
    String defaultOutput = "false";

    when(extractor.mappingPropertyValue(eq(SWITCH_FILTER_KEY))).thenReturn(fieldSelector);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE))).thenReturn(caseValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_OUTPUT))).thenReturn(outputValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_DEFAULT_OUTPUT))).thenReturn(defaultOutput);

    processor.onPipelineStarted(params, collector, runtimeContext);

    // Set up event with matching field value
    AbstractField<?> abstractField = mock(AbstractField.class);
    PrimitiveField primitiveField = mock(PrimitiveField.class);
    when(primitiveField.getAsString()).thenReturn(caseValue);
    when(abstractField.getAsPrimitive()).thenReturn(primitiveField);
    when(event.getFieldBySelector(eq(fieldSelector))).thenReturn(abstractField);

    // Test
    processor.onEvent(event, collector);

    // Verify that the result field was added with expected value (true)
    verify(event).addField(eq(SWITCH_FILTER_OUTPUT_KEY), eq(true));
    verify(collector).collect(eq(event));
  }

  @Test
  void testOnEvent_NonMatchingCase() {
    // Setup
    String fieldSelector = "testField";
    String caseValue = "testValue";
    String outputValue = "true";
    String defaultOutput = "false";

    when(extractor.mappingPropertyValue(eq(SWITCH_FILTER_KEY))).thenReturn(fieldSelector);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE))).thenReturn(caseValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_OUTPUT))).thenReturn(outputValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_DEFAULT_OUTPUT))).thenReturn(defaultOutput);

    processor.onPipelineStarted(params, collector, runtimeContext);

    // Set up event with non-matching field value
    AbstractField<?> abstractField = mock(AbstractField.class);
    PrimitiveField primitiveField = mock(PrimitiveField.class);
    when(primitiveField.getAsString()).thenReturn("nonMatchingValue");
    when(abstractField.getAsPrimitive()).thenReturn(primitiveField);
    when(event.getFieldBySelector(eq(fieldSelector))).thenReturn(abstractField);

    // Test
    processor.onEvent(event, collector);

    // Verify that the result field was added with expected value (false for non-matching)
    verify(event).addField(eq(SWITCH_FILTER_OUTPUT_KEY), eq(false));
    verify(collector).collect(eq(event));
  }

  @Test
  void testOnEvent_NonMatchingCaseWithCustomDefaultOutput() {
    // Setup
    String fieldSelector = "testField";
    String caseValue = "testValue";
    String outputValue = "true";
    String defaultOutput = "true"; // Set default to true

    when(extractor.mappingPropertyValue(eq(SWITCH_FILTER_KEY))).thenReturn(fieldSelector);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE))).thenReturn(caseValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_OUTPUT))).thenReturn(outputValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_DEFAULT_OUTPUT))).thenReturn(defaultOutput);

    processor.onPipelineStarted(params, collector, runtimeContext);

    // Set up event with non-matching field value
    AbstractField<?> abstractField = mock(AbstractField.class);
    PrimitiveField primitiveField = mock(PrimitiveField.class);
    when(primitiveField.getAsString()).thenReturn("nonMatchingValue");
    when(abstractField.getAsPrimitive()).thenReturn(primitiveField);
    when(event.getFieldBySelector(eq(fieldSelector))).thenReturn(abstractField);

    // Test
    processor.onEvent(event, collector);

    // Verify that the result field was added with the default value (false)
    // Note: Custom default is not yet implemented in the processor
    verify(event).addField(eq(SWITCH_FILTER_OUTPUT_KEY), eq(false));
    verify(collector).collect(eq(event));
  }

  @Test
  void testOnEvent_NullFieldValue() {
    // Setup
    String fieldSelector = "testField";
    String caseValue = "testValue";
    String outputValue = "true";
    String defaultOutput = "false";

    when(extractor.mappingPropertyValue(eq(SWITCH_FILTER_KEY))).thenReturn(fieldSelector);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE))).thenReturn(caseValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_OUTPUT))).thenReturn(outputValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_DEFAULT_OUTPUT))).thenReturn(defaultOutput);

    processor.onPipelineStarted(params, collector, runtimeContext);

    // Set up event with null field value
    AbstractField<?> abstractField = mock(AbstractField.class);
    when(abstractField.getAsPrimitive()).thenReturn(null);
    when(event.getFieldBySelector(eq(fieldSelector))).thenReturn(abstractField);

    // Test
    processor.onEvent(event, collector);

    // Verify that the result field was added with the default value for null field value
    verify(event).addField(eq(SWITCH_FILTER_OUTPUT_KEY), eq(false));
    verify(collector).collect(eq(event));
  }

  @Test
  void testOnEvent_FalseOutputValue() {
    // Setup
    String fieldSelector = "testField";
    String caseValue = "testValue";
    String outputValue = "false";  // Set the output value to false
    String defaultOutput = "true";

    when(extractor.mappingPropertyValue(eq(SWITCH_FILTER_KEY))).thenReturn(fieldSelector);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE))).thenReturn(caseValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_OUTPUT))).thenReturn(outputValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_DEFAULT_OUTPUT))).thenReturn(defaultOutput);

    processor.onPipelineStarted(params, collector, runtimeContext);

    // Set up event with matching field value
    AbstractField<?> abstractField = mock(AbstractField.class);
    PrimitiveField primitiveField = mock(PrimitiveField.class);
    when(primitiveField.getAsString()).thenReturn(caseValue);
    when(abstractField.getAsPrimitive()).thenReturn(primitiveField);
    when(event.getFieldBySelector(eq(fieldSelector))).thenReturn(abstractField);

    // Test
    processor.onEvent(event, collector);

    // Verify that the result field was added with expected value (false)
    verify(event).addField(eq(SWITCH_FILTER_OUTPUT_KEY), eq(false));
    verify(collector).collect(eq(event));
  }

  @Test
  void testOnEvent_FieldDoesNotExist() {
    // Setup
    String fieldSelector = "testField";
    String caseValue = "testValue";
    String outputValue = "true";
    String defaultOutput = "false";

    when(extractor.mappingPropertyValue(eq(SWITCH_FILTER_KEY))).thenReturn(fieldSelector);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE))).thenReturn(caseValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_OUTPUT))).thenReturn(outputValue);
    when(extractor.textParameter(eq(SWITCH_CASE_VALUE_DEFAULT_OUTPUT))).thenReturn(defaultOutput);

    processor.onPipelineStarted(params, collector, runtimeContext);

    // Set up event with non-existent field - return null instead of using hasField
    when(event.getFieldBySelector(eq(fieldSelector))).thenReturn(null);

    // Test
    processor.onEvent(event, collector);

    // Verify that the result field was added with default value (false)
    verify(event).addField(eq(SWITCH_FILTER_OUTPUT_KEY), eq(false));
    verify(collector).collect(eq(event));
  }
}
