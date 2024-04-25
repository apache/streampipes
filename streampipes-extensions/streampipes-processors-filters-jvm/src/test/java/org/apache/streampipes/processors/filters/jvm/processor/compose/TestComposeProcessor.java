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

package org.apache.streampipes.processors.filters.jvm.processor.compose;


import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

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

public class TestComposeProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestComposeProcessor.class);

  private static final String outputKeySelector1 = "key-selector1";
  private static final String outputKeySelector2 = "key-selector2";

  public static Iterable<Object[]> data() {
    Map<String, Object> mapWithFirstOutputSelector = new HashMap<>();
    mapWithFirstOutputSelector.put(outputKeySelector1, new Object());

    Map<String, Object> mapWithSecondOutputSelector = new HashMap<>();
    mapWithSecondOutputSelector.put(outputKeySelector2, new Object());

    Map<String, Object> mapWithInvalidOutputSelector = new HashMap<>();
    mapWithInvalidOutputSelector.put("invalid-selector", new Object());

    List<Map<String, Object>> singleMap = new ArrayList<>();
    singleMap.add(mapWithFirstOutputSelector);

    List<Map<String, Object>> twoMapsMatching = new ArrayList<>();
    twoMapsMatching.add(mapWithFirstOutputSelector);
    twoMapsMatching.add(mapWithSecondOutputSelector);

    List<Map<String, Object>> twoMapsOneMatching = new ArrayList<>();
    twoMapsOneMatching.add(mapWithFirstOutputSelector);
    twoMapsOneMatching.add(mapWithInvalidOutputSelector);

    List<Map<String, Object>> twoMapsNoneMatching = new ArrayList<>();
    twoMapsNoneMatching.add(mapWithInvalidOutputSelector);
    twoMapsNoneMatching.add(new HashMap<>(mapWithInvalidOutputSelector));

    return Arrays.asList(new Object[][]{
        {"testWithOneEvent", singleMap, List.of("s0"), 0, 0},
        {"testWithTwoEventsSamePrefix", twoMapsMatching, List.of("s0", "s0"), 0, 0},
        {"testWithTwoEvents", twoMapsMatching, List.of("s0", "s1"), 1, 2},
        {"testWithTwoEventsAnd1InvalidSelector", twoMapsOneMatching, List.of("s0", "s1"), 1, 1},
        {"testWithTwoEventsWithInvalidSelectors", twoMapsNoneMatching, List.of("s0", "s1"), 1, 0}
    });
  }


  @ParameterizedTest
  @MethodSource("data")
  public void testComposeProcessor(String testName, List<Map<String, Object>> eventMaps, List<String> selectorPrefixes,
                                   int expectedNumOfEvents, int expectedEventSize) {
    LOG.info("Executing test: {}", testName);
    var processor = new ComposeProcessor();
    var originalGraph = processor.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    var graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);
    List<OutputStrategy> outputStrategies = new ArrayList<>();
    outputStrategies.add(new CustomOutputStrategy(List.of("s0::" + outputKeySelector1, "s1::" + outputKeySelector2)));
    graph.setOutputStrategies(outputStrategies);
    var params = new ProcessorParams(graph);

    var eventCollector = new StoreEventCollector();
    processor.onInvocation(params, eventCollector, null);

    List<Event> collectedEvents = sendEvents(processor, eventCollector, eventMaps, selectorPrefixes);

    LOG.info("Expected collected event count is: {}", expectedNumOfEvents);
    LOG.info("Actual collected event count is: {}", collectedEvents.size());
    assertEquals(expectedNumOfEvents, collectedEvents.size());

    if (!collectedEvents.isEmpty()) {
      int eventSize = collectedEvents.get(0).getFields().size();

      LOG.info("Expected event size is: {}", expectedEventSize);
      LOG.info("Actual event size is: {}", eventSize);
      assertEquals(expectedEventSize, eventSize);
    }
  }

  private List<Event> sendEvents(ComposeProcessor processor, StoreEventCollector collector,
                                 List<Map<String, Object>> eventMaps, List<String> selectorPrefixes) {
    List<Event> events = makeEvents(eventMaps, selectorPrefixes);
    for (Event event : events) {
      LOG.info("Sending event with map: " + event.getFields()
          + ", and prefix selector: " + event.getSourceInfo().getSelectorPrefix());
      processor.onEvent(event, collector);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        LOG.info(e.getMessage(), e);
      }
    }
    return collector.getEvents();
  }

  private List<Event> makeEvents(List<Map<String, Object>> eventMaps, List<String> selectorPrefixes) {
    List<Event> events = new ArrayList<>();
    for (int i = 0; i < eventMaps.size(); i++) {
      events.add(makeEvent(eventMaps.get(i), selectorPrefixes.get(i)));
    }
    return events;
  }

  private Event makeEvent(Map<String, Object> eventMap, String selectorPrefix) {
    return EventFactory.fromMap(eventMap, new SourceInfo("test" + "-topic", selectorPrefix),
        new SchemaInfo(null, new ArrayList<>()));
  }
}



