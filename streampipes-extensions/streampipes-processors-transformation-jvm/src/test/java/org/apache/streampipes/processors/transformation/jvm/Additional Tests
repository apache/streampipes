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

package org.apache.streampipes.processors.transformation.jvm.processor;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TextFilterProcessorTest {

    @Mock
    private ProcessorParams processorParams;

    @Mock
    private SpOutputCollector spOutputCollector;

    @Mock
    private EventProcessorRuntimeContext eventProcessorRuntimeContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConstructorInitialization() {
        TextFilterProcessor processor = new TextFilterProcessor();
        assertNull(processor.getKeyword());
        assertNull(processor.getStringOperator());
        assertNull(processor.getFilterProperty());
    }

    @Test
    public void testModelDeclaration() {
        TextFilterProcessor processor = new TextFilterProcessor();
        ModelDeclaration model = processor.declareModel();
        assertNotNull(model);

        // Checking that the model description is not null
        String description = model.getDescription();
        assertNotNull(description);

        // Checking that the description is a non-empty string
        assertTrue(description.length() > 0);
    }

    @Test
    public void testOnInvocation() {
        TextFilterProcessor processor = new TextFilterProcessor();
        when(processorParams.extractor().singleValueParameter(anyString(), eq(String.class))).thenReturn("keyword");
        when(processorParams.extractor().selectedSingleValue(anyString(), eq(String.class))).thenReturn("MATCHES");
        when(processorParams.extractor().mappingPropertyValue(anyString())).thenReturn("text");

        assertDoesNotThrow(() -> processor.onInvocation(processorParams, spOutputCollector, eventProcessorRuntimeContext));

        assertEquals("keyword", processor.getKeyword());
        assertEquals(TextFilterProcessor.StringOperator.MATCHES, processor.getStringOperator());
        assertEquals("text", processor.getFilterProperty());
    }

    @Test
    public void testOnEvent() {
        TextFilterProcessor processor = new TextFilterProcessor();
        when(processorParams.extractor().singleValueParameter(anyString(), eq(String.class))).thenReturn("keyword");
        when(processorParams.extractor().selectedSingleValue(anyString(), eq(String.class))).thenReturn("MATCHES");
        when(processorParams.extractor().mappingPropertyValue(anyString())).thenReturn("text");

        processor.onInvocation(processorParams, spOutputCollector, eventProcessorRuntimeContext);

        Event matchingEvent = createMockEvent("keyword");
        Event containingEvent = createMockEvent("Some text that CONTAINS the keyword");
        Event nonMatchingEvent = createMockEvent("Some text that does not match or contain the keyword");

        spOutputCollector.resetCollectedEvents(); // Reset collected events for each test case

        processor.onEvent(matchingEvent, spOutputCollector);
        assertEquals(1, spOutputCollector.getCollectedEvents().size());

        processor.onEvent(containingEvent, spOutputCollector);
        assertEquals(1, spOutputCollector.getCollectedEvents().size());

        processor.onEvent(nonMatchingEvent, spOutputCollector);
        assertEquals(0, spOutputCollector.getCollectedEvents().size());
    }

    @Test
    public void testOnDetach() {
        TextFilterProcessor processor = new TextFilterProcessor();
        assertDoesNotThrow(() -> processor.onDetach());
    }

    private Event createMockEvent(String text) {
        Event event = new Event();
        event.addField("text", text);
        return event;
    }
}
