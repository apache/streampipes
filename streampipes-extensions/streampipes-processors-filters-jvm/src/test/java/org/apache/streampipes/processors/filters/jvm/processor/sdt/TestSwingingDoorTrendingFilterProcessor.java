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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwingingDoorTrendingFilterProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestSwingingDoorTrendingFilterProcessor.class);


  private final String sdtTimestampField = "sdtTimestampField";
  private final String sdtValueField = "sdtValueField";


  public static Iterable<Object[]> data() {

    return Arrays.asList(new Object[][]{
        {"testWithOneEvent", "10.0", "100", "500", 1, List.of(Pair.of(9, 50.0)), false, ""},
        {"testFullFilter", "10.0", "100", "500", 4, List.of(
            Pair.of(0, 50.0),      //true
            Pair.of(50, 50.0),     //false
            Pair.of(200, 100.0),   //false
            Pair.of(270, 140.0),   //false
            Pair.of(300, 250.0),   //true
            Pair.of(900, 500.0),   //true
            Pair.of(1100, 800.0),  //false
            Pair.of(1250, 1600.0)  //true
        )
            , false, ""},
        {"testWithNegativeCompressionDeviation", "-10.0", "100", "500", 1, new ArrayList<>(), true
            , "Compression Deviation should be positive!"},
        {"testWithNegativeMinInterval", "10.0", "-100", "500", 1, new ArrayList<>(), true
            , "Compression Minimum Time Interval should be >= 0!"},
        {"testWithMinInterval>MaxInterval", "10.0", "1000", "500", 1, new ArrayList<>(), true
            , "Compression Minimum Time Interval should be < Compression Maximum Time Interval!"}
    });
  }


  @ParameterizedTest
  @MethodSource("data")
  public void testSdtFilter(String testName, String sdtCompressionDeviation, String sdtCompressionMinTimeInterval,
                            String sdtCompressionMaxTimeInterval, int expectedFilteredCount,
                            List<Pair<Long, Double>> eventSettings, boolean expectException,
                            String expectedErrorMessage) {
    LOG.info("Executing test: {}", testName);
    SwingingDoorTrendingFilterProcessor processor = new SwingingDoorTrendingFilterProcessor();
    DataProcessorDescription originalGraph = processor.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph =
        InvocationGraphGenerator.makeEmptyInvocation(originalGraph);

    ProcessorParams params = new ProcessorParams(graph);
    params.extractor().getStaticPropertyByName(SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY
            , MappingPropertyUnary.class)
        .setSelectedProperty("test::" + sdtTimestampField);
    params.extractor().getStaticPropertyByName(SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY
            , MappingPropertyUnary.class)
        .setSelectedProperty("test::" + sdtValueField);
    params.extractor().getStaticPropertyByName(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY
            , FreeTextStaticProperty.class)
        .setValue(sdtCompressionDeviation);
    params.extractor().getStaticPropertyByName(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY
            , FreeTextStaticProperty.class)
        .setValue(sdtCompressionMinTimeInterval);
    params.extractor().getStaticPropertyByName(SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY
            , FreeTextStaticProperty.class)
        .setValue(sdtCompressionMaxTimeInterval);


    StoreEventCollector eventCollector = new StoreEventCollector();
    if (expectException) {
      LOG.info("Expecting Error Message: {}", expectedErrorMessage);
      SpRuntimeException ex = assertThrows(SpRuntimeException.class, () -> {
        processor.onInvocation(params, eventCollector, null);
      });
      assertTrue(ex.getMessage().contains(expectedErrorMessage));
    } else {
      processor.onInvocation(params, eventCollector, null);
      int result = sendEvents(processor, eventCollector, eventSettings);
      LOG.info("Expected SDT filtered count is: {}", expectedFilteredCount);
      LOG.info("Actual SDT filtered count is: {}", result);
      assertEquals(expectedFilteredCount, result);
    }
  }


  private int sendEvents(SwingingDoorTrendingFilterProcessor processor, StoreEventCollector collector,
                         List<Pair<Long, Double>> eventSettings) {
    List<Event> events = makeEvents(eventSettings);
    for (Event event : events) {
      LOG.info("Sending event with timestamp: "
          + event.getFieldBySelector("test::" + sdtTimestampField).getAsPrimitive().getAsLong()
          + ", and value: "
          + event.getFieldBySelector("test::" + sdtValueField).getAsPrimitive().getAsFloat());
      processor.onEvent(event, collector);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        LOG.info(e.getMessage(), e);
      }
    }
    return collector.getEvents().size();
  }

  private List<Event> makeEvents(List<Pair<Long, Double>> eventSettings) {
    List<Event> events = new ArrayList<>();
    for (Pair<Long, Double> eventSetting : eventSettings) {
      events.add(makeEvent(eventSetting));
    }
    return events;
  }

  private Event makeEvent(Pair<Long, Double> eventSetting) {
    Map<String, Object> map = new HashMap<>();
    map.put(sdtTimestampField, eventSetting.getKey());
    map.put(sdtValueField, eventSetting.getValue());
    return EventFactory.fromMap(map, new SourceInfo("test" + "-topic", "test"),
        new SchemaInfo(null, new ArrayList<>()));
  }
}