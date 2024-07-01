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
import org.apache.streampipes.connect.shared.preprocessing.transform.value.TimestampTranformationRuleMode;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class FileReplayAdapterTest {

  private FileReplayAdapter fileReplayAdapter;
  private IEventCollector collector;
  private final static String TIMESTAMP = "timestamp";
  private final static long TIMESTAMP_VALUE = 1622544682000L;
  private Map<String, Object> event;
  private ArgumentCaptor<Map<String, Object>> resultEventCapture;
  private IAdapterParameterExtractor extractor;
  private AdapterDescription adapterDescription;

  @BeforeEach
  void setUp() {
    collector = mock(IEventCollector.class);
    extractor = mock(IAdapterParameterExtractor.class);
    adapterDescription = mock(AdapterDescription.class);
    when(extractor.getAdapterDescription()).thenReturn(adapterDescription);
    fileReplayAdapter = new FileReplayAdapter();
    fileReplayAdapter.setTimestampSourceFieldName(TIMESTAMP);
    event = new HashMap<>();
    resultEventCapture = ArgumentCaptor.forClass(Map.class);
  }


  @Test
  public void testThrowExceptionWhenAddTimestampRuleIsSelected_withAddTimestampRule() {
    when(adapterDescription.getRules()).thenReturn(Arrays.asList(new AddTimestampRuleDescription()));

    assertThrows(AdapterException.class, () -> FileReplayAdapter.throwExceptionWhenAddTimestampRuleIsSelected(extractor));
  }

  @Test
  public void testThrowExceptionWhenAddTimestampRuleIsSelected_withoutAddTimestampRule() {
    when(adapterDescription.getRules()).thenReturn(Collections.emptyList());
  }

  @Test
  void processEvent_shouldCollectEventWhenTimestampIsLong() throws AdapterException {
    event.put(TIMESTAMP, TIMESTAMP_VALUE);

    fileReplayAdapter.processEvent(collector, event);

    verify(collector, times(1)).collect(event);
  }

  @Test
  void processEvent_shouldCollectEventWhenTimestampIsInteger() throws AdapterException {
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
  void processEvent_shouldNotCollectEventWhenTimestampCouldNotBeProcessed() throws AdapterException {
    event.put(TIMESTAMP, -1);

    assertThrows(AdapterException.class, () -> fileReplayAdapter.processEvent(collector, event));
  }

  @Test
  void processEvent_shouldCollectEventWhenTimestampIsReplaced() throws AdapterException {
    event.put(TIMESTAMP, TIMESTAMP_VALUE);

    fileReplayAdapter.setReplaceTimestamp(true);
    fileReplayAdapter.processEvent(collector, event);

    verify(collector, times(1)).collect(resultEventCapture.capture());

    var restultEvent = resultEventCapture.getValue();
    assertEquals(restultEvent.size(), 1);
    assertNotEquals(restultEvent.get(TIMESTAMP), TIMESTAMP_VALUE);
  }


  @Test
  void getTimestampFromEvent_returnsLongTimestamp() throws AdapterException {
    event.put(TIMESTAMP, TIMESTAMP_VALUE);

    long actualEventTimestamp = fileReplayAdapter.getTimestampFromEvent(event);

    assertEquals(TIMESTAMP_VALUE, actualEventTimestamp);
  }

  @Test
  void getTimestampFromEvent_forTimestampRuleInSecondsAsLong() throws AdapterException {
    setupEventAndRule(
        TIMESTAMP,
        1622544682L,
        TimestampTranformationRuleMode.TIME_UNIT,
        1000L
    );
    assertEventTimestamp(TIMESTAMP_VALUE);
  }

  @Test
  void getTimestampFromEvent_forTimestampRuleInSecondsAsInteger() throws AdapterException {
    setupEventAndRule(
        TIMESTAMP,
        1622544682,
        TimestampTranformationRuleMode.TIME_UNIT,
        1000L
    );
    assertEventTimestamp(TIMESTAMP_VALUE);
  }

  @Test
  void getTimestampFromEvent_forTimestampRuleAsString() throws AdapterException {
    setupEventAndRule(
        TIMESTAMP,
        "2024-07-01T12:00:00.000Z",
        TimestampTranformationRuleMode.FORMAT_STRING,
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    );
    assertEventTimestamp(1719828000000L);
  }


  private void setupEventAndRule(
      String key,
      Object value,
      TimestampTranformationRuleMode mode,
      Object additional
  ) {
    event.put(key, value);
    var rule = new TimestampTranfsformationRuleDescription();
    rule.setRuntimeKey(key);
    rule.setMode(mode.internalName());
    if (additional instanceof Long) {rule.setMultiplier((Long) additional);}
    if (additional instanceof String) {rule.setFormatString((String) additional);}
    fileReplayAdapter.setTimestampTranfsformationRuleDescription(rule);
  }

  private void assertEventTimestamp(long expected) throws AdapterException {
    assertEquals(expected, fileReplayAdapter.getTimestampFromEvent(event));
  }


}