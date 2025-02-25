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
 */

package org.apache.streampipes.processors.filters.jvm.processor.schema;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.InputStreamParams;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestMergeBySchemaProcessor {

  private MergeBySchemaProcessor processor;

  @Mock
  private ProcessorParams processorParams;

  @Mock
  private SpOutputCollector outputCollector;

  @Mock
  private EventProcessorRuntimeContext runtimeContext;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    processor = new MergeBySchemaProcessor();
  }

  /**
   * Creates an InputStreamParams object with a real SchemaInfo instance.
   */
  private InputStreamParams createInputStreamParam(EventSchema schema) {
    InputStreamParams inputStreamParams = mock(InputStreamParams.class);
    SchemaInfo schemaInfo = mock(SchemaInfo.class);

    // Ensure getEventSchema() returns the provided schema
    when(schemaInfo.getEventSchema()).thenReturn(schema);
    when(inputStreamParams.getSchemaInfo()).thenReturn(schemaInfo);

    return inputStreamParams;
  }

  @Test
  void testOnInvocationMatchingSchemas() {
    EventSchema schema = new EventSchema();
    List<InputStreamParams> inputParams = Arrays.asList(
        createInputStreamParam(schema), // Stream 1
        createInputStreamParam(schema) // Stream 2
    );

    when(processorParams.getInputStreamParams()).thenReturn(inputParams);

    assertDoesNotThrow(() -> processor.onInvocation(processorParams, outputCollector, runtimeContext));
  }

  @Test
  void testOnInvocationMismatchingSchemas() {
    EventSchema schema1 = new EventSchema();
    EventSchema schema2 = new EventSchema();

    EventProperty property1 = new EventPropertyPrimitive(
        "string",
        "field1",
        null,
        "description1"
    );

    EventProperty property2 = new EventPropertyPrimitive(
        "integer",
        "field1",
        null,
        "description1"
    );

    schema1.addEventProperty(property1);
    schema2.addEventProperty(property2);

    List<InputStreamParams> inputParams = Arrays.asList(
        createInputStreamParam(schema1),
        createInputStreamParam(schema2)
    );

    when(processorParams.getInputStreamParams()).thenReturn(inputParams);


    SpRuntimeException exception = assertThrows(SpRuntimeException.class,
        () -> processor.onInvocation(processorParams, outputCollector, runtimeContext));

    assertTrue(exception.getMessage().contains("Schemas does not match"),
        "Actual message: " + exception.getMessage());
  }


  @Test
  void testOnEventProcessing() {
    Event event = new Event();

    assertDoesNotThrow(() -> processor.onEvent(event, outputCollector));
    verify(outputCollector, times(1)).collect(event);
  }
}
