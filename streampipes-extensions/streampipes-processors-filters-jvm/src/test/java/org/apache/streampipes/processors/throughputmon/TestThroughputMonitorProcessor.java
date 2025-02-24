package org.apache.streampipes.processors.throughputmon;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.processors.filters.jvm.processor.throughputmon.ThroughputMonitorProcessor;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestThroughputMonitorProcessor {

    private ThroughputMonitorProcessor processor;
    private SpOutputCollector mockCollector;
    private ProcessorParams mockParams;
    private EventProcessorRuntimeContext mockContext;
    private ProcessingElementParameterExtractor mockExtractor;

    @BeforeEach
    void setUp() throws SpRuntimeException {
        processor = new ThroughputMonitorProcessor();
        mockCollector = mock(SpOutputCollector.class);
        mockParams = mock(ProcessorParams.class);
        mockContext = mock(EventProcessorRuntimeContext.class);
        mockExtractor = mock(ProcessingElementParameterExtractor.class);

        // Ensure mockParams.extractor() returns the mocked extractor
        when(mockParams.extractor()).thenReturn(mockExtractor);
        when(mockExtractor.singleValueParameter("batch-window-key", Integer.class)).thenReturn(3); // Set batchSize to 3

        processor.onInvocation(mockParams, mockCollector, mockContext);
    }

    @Test
    void testProcessorInitialization() {
        assertNotNull(processor);
    }

    @Test
    void testEventProcessing() throws SpRuntimeException {
        Event event1 = createEvent();
        Event event2 = createEvent();
        Event event3 = createEvent(); // Should trigger collection

        processor.onEvent(event1, mockCollector);
        processor.onEvent(event2, mockCollector);
        processor.onEvent(event3, mockCollector);

        // Verify event collection occurs exactly once after 3 events
        verify(mockCollector, times(1)).collect(any(Event.class));
    }

    @Test
    void testOutputEventStructure() throws SpRuntimeException {
        Event event1 = createEvent();
        Event event2 = createEvent();
        Event event3 = createEvent();

        processor.onEvent(event1, mockCollector);
        processor.onEvent(event2, mockCollector);
        processor.onEvent(event3, mockCollector);

        // Capture the collected event
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(mockCollector).collect(eventCaptor.capture());

        Event capturedEvent = eventCaptor.getValue();

        assertNotNull(capturedEvent.getFieldBySelector("timestamp"));
        assertNotNull(capturedEvent.getFieldBySelector("starttime"));
        assertNotNull(capturedEvent.getFieldBySelector("endtime"));
        assertNotNull(capturedEvent.getFieldBySelector("duration"));
        assertNotNull(capturedEvent.getFieldBySelector("eventcount"));
        assertNotNull(capturedEvent.getFieldBySelector("throughput"));
    }

    @Test
    void testProcessorDetachment() throws SpRuntimeException {
        assertDoesNotThrow(() -> processor.onDetach());
    }

    private Event createEvent() {
        Event event = new Event();
        event.addField("sensorValue", 42);
        return event;
    }
}
