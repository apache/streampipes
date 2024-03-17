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

package org.apache.streampipes.processors.filters.jvm.processor.booleanfilter;

import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestBooleanFilterProcessor {
  private static final String STREAM_PREFIX = "s0::";

  private static final Logger LOG = LoggerFactory.getLogger(TestBooleanFilterProcessor.class);

  static Stream<Arguments> data() {
    return Stream.of(
        Arguments.of("True", Arrays.asList(true, true, false, true, false, true, false, true), 5),
        Arguments.of("True", Arrays.asList(true, true, true), 3),
        Arguments.of("True", Arrays.asList(false, false, false), 0),
        Arguments.of("True", Collections.emptyList(), 0),
        Arguments.of("False", Arrays.asList(true, false, true, false, true, false, true, false, true), 4),
        Arguments.of("False", Arrays.asList(true, true, true), 0),
        Arguments.of("False", Arrays.asList(false, false, false), 3),
        Arguments.of("False", Collections.emptyList(), 0)
    );
  }

  @ParameterizedTest
  @MethodSource("data")
  public void testBoolenFilter(
      String boolToKeep,
      List<Boolean> eventBooleans,
      int expectedFilteredBooleanCount
  ) {

    var fieldName = "Test";
    var processorParams = mock(ProcessorParams.class);
    var eventProcessorRuntimeContext = mock(EventProcessorRuntimeContext.class);

    var processor = new BooleanFilterProcessor();
    var extractor = mock(ProcessingElementParameterExtractor.class);
    when(processorParams.extractor()).thenReturn(extractor);
    when(extractor.mappingPropertyValue(BooleanFilterProcessor.BOOLEAN_MAPPING)).thenReturn(STREAM_PREFIX + fieldName);
    when(extractor.selectedSingleValue(BooleanFilterProcessor.VALUE, String.class)).thenReturn(boolToKeep);

    var collector = new StoreEventCollector();
    processor.onInvocation(processorParams, collector, eventProcessorRuntimeContext);

    sendEvents(processor, collector, eventBooleans, fieldName);

    assertEquals(expectedFilteredBooleanCount, collector.getEvents().size());
  }

  private void sendEvents(
      BooleanFilterProcessor processor,
      StoreEventCollector collector,
      List<Boolean> eventBooleans,
      String fieldName) {
    List<Event> events = makeEvents(eventBooleans, fieldName);
    for (Event event : events) {
      LOG.info("Sending event with value "
                   + event.getFieldBySelector(STREAM_PREFIX + fieldName)
                          .getAsPrimitive()
                          .getAsBoolean());
      processor.onEvent(event, collector);

    }
  }

  private List<Event> makeEvents(List<Boolean> eventBooleans, String fieldName) {
    List<Event> events = new ArrayList<>();
    for (Boolean eventSetting : eventBooleans) {
      events.add(makeEvent(eventSetting, fieldName));
    }
    return events;
  }

  private Event makeEvent(Boolean value, String fieldName) {
    Map<String, Object> map = new HashMap<>();
    map.put(fieldName, value);
    return EventFactory.fromMap(map, new SourceInfo("test" + "-topic", "s0"),
                                new SchemaInfo(null, new ArrayList<>())
    );
  }
}
