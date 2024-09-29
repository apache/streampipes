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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.TimestampTranformationRuleMode;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class FileReplayAdapterTest {

  private FileReplayAdapter fileReplayAdapter;
  private IEventCollector collector;
  private static final String TIMESTAMP = "timestamp";
  private static final long TIMESTAMP_VALUE = 1622544682000L;
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
    when(adapterDescription.getRules()).thenReturn(List.of(new AddTimestampRuleDescription()));

    assertThrows(
        AdapterException.class,
        () -> FileReplayAdapter.throwExceptionWhenAddTimestampRuleIsSelected(extractor)
    );
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
  void processEvent_shouldNotCollectEventWhenTimestampCouldNotBeProcessed() {
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
  void getTimestampFromEvent_withLongInSeconds() throws AdapterException {
    setupNumberTransformationRule(1622544682L);
    assertEventTimestamp(TIMESTAMP_VALUE);
  }

  @Test
  void getTimestampFromEvent_withIntegerInSeconds() throws AdapterException {
    setupNumberTransformationRule(1622544682);
    assertEventTimestamp(TIMESTAMP_VALUE);
  }

  @Test
  void getTimestampFromEvent_withStringFormatInUtc() throws AdapterException {
    setupStringTransformationRule("2024-07-01T12:00:00.000Z", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    assertEventTimestamp(1719835200000L);
  }

  @Test
  void getTimestampFromEvent_withStringFormatAndPositiveOffset() throws AdapterException {
    setupStringTransformationRule("2024-07-01T12:00:00.000+03:00", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    assertEventTimestamp(1719824400000L);
  }

  @Test
  void getTimestampFromEvent_withStringFormatAndNegativeOffset() throws AdapterException {
    setupStringTransformationRule("2024-07-01T05:00:00.000-10:00", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    assertEventTimestamp(1719846000000L);
  }

  @Test
  void getTimestampFromEvent_withStringFormatAndZeroOffset() throws AdapterException {
    setupStringTransformationRule("2024-07-01T12:00:00.000+00:00", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    assertEventTimestamp(1719835200000L);
  }

  @Test
  void getTimestampFromEvent_withStringFormatWithoutTimeZone() throws AdapterException {
    setupStringTransformationRule("01.07.2024 12:00:00", "dd.MM.yyyy HH:mm:ss");
    assertEventTimestamp(1719835200000L);
  }

  /**
   * Sets up a string transformation rule for timestamp processing. This method configures a transformation rule that
   * interprets and converts a timestamp string according to the specified format string.
   */
  private void setupStringTransformationRule(String timestampValue, String formatString) {
    event.put(TIMESTAMP, timestampValue);

    var rule = new TimestampTranfsformationRuleDescription();
    rule.setRuntimeKey(TIMESTAMP);
    rule.setMode(TimestampTranformationRuleMode.FORMAT_STRING.internalName());
    rule.setFormatString(formatString);

    fileReplayAdapter.setTimestampTranfsformationRuleDescription(rule);
  }

  /**
   * Sets up a number transformation rule for timestamp processing. This method configures a transformation rule that
   * multiplies a given timestamp value by a specified multiplier.
   */
  private void setupNumberTransformationRule(long timestampValue) {
    event.put(TIMESTAMP, timestampValue);

    var rule = new TimestampTranfsformationRuleDescription();
    rule.setRuntimeKey(TIMESTAMP);
    rule.setMode(TimestampTranformationRuleMode.TIME_UNIT.internalName());
    rule.setMultiplier(1000L);

    fileReplayAdapter.setTimestampTranfsformationRuleDescription(rule);
  }

  private void assertEventTimestamp(long expected) throws AdapterException {
    assertEquals(expected, fileReplayAdapter.getTimestampFromEvent(event));
  }

}