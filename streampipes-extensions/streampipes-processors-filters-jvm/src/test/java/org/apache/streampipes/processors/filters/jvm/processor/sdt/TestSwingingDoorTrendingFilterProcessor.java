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
import org.apache.streampipes.processors.filters.jvm.processor.merge.TestMergeByTimeProcessor;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestSwingingDoorTrendingFilterProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestMergeByTimeProcessor.class);

  @org.junit.runners.Parameterized.Parameter
  public String testName;
  @org.junit.runners.Parameterized.Parameter(1)
  public String sdtCompressionDeviation;
  @org.junit.runners.Parameterized.Parameter(2)
  public String sdtCompressionMinTimeInterval;
  @org.junit.runners.Parameterized.Parameter(3)
  public String sdtCompressionMaxTimeInterval;
  @org.junit.runners.Parameterized.Parameter(4)
  public int expectedFilteredCount;
  @org.junit.runners.Parameterized.Parameter(5)
  public List<Pair<Long, Double>> eventSettings;
  @org.junit.runners.Parameterized.Parameter(6)
  public boolean expectException;
  @org.junit.runners.Parameterized.Parameter(7)
  public String expectedErrorMessage;

  private final String sdtTimestampField = "sdtTimestampField";
  private final String sdtValueField = "sdtValueField";

  @org.junit.runners.Parameterized.Parameters
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

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testSdtFilter() {
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

    if (expectException){
      LOG.info("Expecting Error Message: {}", expectedErrorMessage);
      exceptionRule.expect(SpRuntimeException.class);
      exceptionRule.expectMessage(expectedErrorMessage);
    }

    StoreEventCollector eventCollector = new StoreEventCollector();
    processor.onInvocation(params, eventCollector, null);

    int result = sendEvents(processor, eventCollector);

    LOG.info("Expected SDT filtered count is: {}", expectedFilteredCount);
    LOG.info("Actual SDT filtered count is: {}", result);
    assertEquals(expectedFilteredCount, result);
  }


  private int sendEvents(SwingingDoorTrendingFilterProcessor processor, StoreEventCollector collector) {
    List<Event> events = makeEvents();
    for (Event event : events) {
      LOG.info("Sending event with timestamp: "
              + event.getFieldBySelector("test::" + sdtTimestampField).getAsPrimitive().getAsLong()
              + ", and value: "
              + event.getFieldBySelector("test::" + sdtValueField).getAsPrimitive().getAsFloat());
      processor.onEvent(event, collector);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return collector.getEvents().size();
  }

  private List<Event> makeEvents() {
    List<Event> events = new ArrayList<>();
    for (Pair<Long, Double> eventSetting: eventSettings) {
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