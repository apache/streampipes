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
package org.apache.streampipes.processors.filters.jvm.processor.sdt;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSwingingDoorTrendingFilterProcessor {

  private final String sdtTimestampField = "sdtTimestampField";
  private final String sdtValueField = "sdtValueField";

  private SwingingDoorTrendingFilterProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new SwingingDoorTrendingFilterProcessor();
  }

  @Test
  public void test() {
    TestConfiguration configuration = TestConfiguration.builder()
            .configWithDefaultPrefix(SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY, sdtTimestampField)
            .configWithDefaultPrefix(SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY, sdtValueField)
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY, "10.0")
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY, "100")
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY, "500").build();

    List<Map<String, Object>> inputEvents = List.of(Map.of(sdtTimestampField, 0, sdtValueField, 50.0),
            Map.of(sdtTimestampField, 50, sdtValueField, 50.0), Map.of(sdtTimestampField, 200, sdtValueField, 100.0),
            Map.of(sdtTimestampField, 270, sdtValueField, 140.0), Map.of(sdtTimestampField, 300, sdtValueField, 250.0),
            Map.of(sdtTimestampField, 900, sdtValueField, 500.0), Map.of(sdtTimestampField, 1100, sdtValueField, 800.0),
            Map.of(sdtTimestampField, 1250, sdtValueField, 1600.0));

    List<Map<String, Object>> outputEvents = List.of(Map.of(sdtTimestampField, 0, sdtValueField, 50.0),
            Map.of(sdtTimestampField, 270, sdtValueField, 140.0), Map.of(sdtTimestampField, 900, sdtValueField, 500.0),
            Map.of(sdtTimestampField, 1100, sdtValueField, 800.0));

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }

  @Test
  public void testInvalidDeviationKey() {
    TestConfiguration configuration = TestConfiguration.builder()
            .configWithDefaultPrefix(SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY, sdtTimestampField)
            .configWithDefaultPrefix(SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY, sdtValueField)
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY, "-10.0")
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY, "100")
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY, "500").build();

    List<Map<String, Object>> inputEvents = new ArrayList<>();

    List<Map<String, Object>> outputEvents = new ArrayList<>();

    Exception expectedException = new SpRuntimeException("Compression Deviation should be positive!");

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    Exception exception = assertThrows(expectedException.getClass(), () -> {
      testExecutor.run(inputEvents, outputEvents);
    });
    String expectedMessage = expectedException.getMessage();
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));

  }

  @Test
  public void testNegativeMinimumTime() {
    TestConfiguration configuration = TestConfiguration.builder()
            .configWithDefaultPrefix(SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY, sdtTimestampField)
            .configWithDefaultPrefix(SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY, sdtValueField)
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY, "10.0")
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY, "-100")
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY, "500").build();

    List<Map<String, Object>> inputEvents = new ArrayList<>();

    List<Map<String, Object>> outputEvents = new ArrayList<>();

    Exception expectedException = new SpRuntimeException("Compression Minimum Time Interval should be >= 0!");

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    Exception exception = assertThrows(expectedException.getClass(), () -> {
      testExecutor.run(inputEvents, outputEvents);
    });
    String expectedMessage = expectedException.getMessage();
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testInvalidTimeInterval() {
    TestConfiguration configuration = TestConfiguration.builder()
            .configWithDefaultPrefix(SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY, sdtTimestampField)
            .configWithDefaultPrefix(SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY, sdtValueField)
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY, "10.0")
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY, "1000")
            .config(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY, "500").build();

    List<Map<String, Object>> inputEvents = new ArrayList<>();

    List<Map<String, Object>> outputEvents = new ArrayList<>();

    Exception expectedException = new SpRuntimeException(
            "Compression Minimum Time Interval should be < Compression Maximum Time Interval!");

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    Exception exception = assertThrows(expectedException.getClass(), () -> {
      testExecutor.run(inputEvents, outputEvents);
    });
    String expectedMessage = expectedException.getMessage();
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
}