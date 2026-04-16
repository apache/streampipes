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

package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.sdk.helpers.EpProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class FileReplayAdapterTest {

  private FileReplayAdapter fileReplayAdapter;
  private IEventCollector collector;
  private static final String TIMESTAMP = "timestamp";
  private static final long TIMESTAMP_VALUE = 1622544682000L;
  private Map<String, Object> event;
  private IAdapterParameterExtractor extractor;
  private AdapterDescription adapterDescription;

  @BeforeEach
  void setUp() {
    collector = mock(IEventCollector.class);
    extractor = mock(IAdapterParameterExtractor.class);
    adapterDescription = new AdapterDescription();
    adapterDescription.getEventSchema().addEventProperty(EpProperties.timestampProperty(TIMESTAMP));
    when(extractor.getAdapterDescription()).thenReturn(adapterDescription);
    fileReplayAdapter = new FileReplayAdapter();
    fileReplayAdapter.setTimestampRuntimeName(TIMESTAMP);
    event = new HashMap<>();
  }



  @Test
  void processEvent_shouldCollectEventWhenTimestampIsLong() throws AdapterException, InterruptedException {
    event.put(TIMESTAMP, TIMESTAMP_VALUE);

    fileReplayAdapter.processEvent(collector, event);

    verify(collector, times(1)).collect(event);
  }

  @Test
  void processEvent_shouldCollectEventWhenTimestampIsInteger() throws AdapterException, InterruptedException {
    event.put(TIMESTAMP, 1622544682);

    fileReplayAdapter.processEvent(collector, event);

    verify(collector, times(1)).collect(event);
  }

  @Test
  void processEvent_shouldThrowAdapterExceptionWhenTimestampIsNotUnixTimestampInMs() {
    event.put(TIMESTAMP, "not a timestamp");

    assertThrows(AdapterException.class, () -> fileReplayAdapter.processEvent(collector, event));
  }

  @Test
  void processEvent_shouldNotCollectEventWhenTimestampCouldNotBeProcessed()  {
    event.put(TIMESTAMP, -1);

    assertThrows(AdapterException.class, () -> fileReplayAdapter.processEvent(collector, event));
  }


  @Test
  void getTimestampFromEvent_returnsLongTimestamp() throws AdapterException {
    event.put(TIMESTAMP, TIMESTAMP_VALUE);

    long actualEventTimestamp = fileReplayAdapter.getTimestampFromEvent(event);

    assertEquals(TIMESTAMP_VALUE, actualEventTimestamp);
  }

  @Test
  void validateTimestampFieldInInputEvent_shouldAcceptLongTimestampInOriginalInput() throws AdapterException {
    adapterDescription.getTransformationConfig().setInputs(List.of(Map.of(TIMESTAMP, TIMESTAMP_VALUE)));

    fileReplayAdapter.validateTimestampFieldInInputEvent(extractor);
  }

  @Test
  void validateTimestampFieldInInputEvent_shouldThrowForStringTimestampInOriginalInput() {
    adapterDescription.getTransformationConfig().setInputs(List.of(Map.of(TIMESTAMP, "2021-12-24T12:55:12.123+01:00")));

    assertThrows(AdapterException.class, () -> fileReplayAdapter.validateTimestampFieldInInputEvent(extractor));
  }

  @Test
  void processEvent_shouldReplaceTimestampWhenConfigured() throws AdapterException, InterruptedException {
    event.put(TIMESTAMP, TIMESTAMP_VALUE);
    fileReplayAdapter.setReplaceTimestamp(true);

    fileReplayAdapter.processEvent(collector, event);

    verify(collector, times(1)).collect(event);
    assertEquals(Long.class, event.get(TIMESTAMP).getClass());
  }

}
