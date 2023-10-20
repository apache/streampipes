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
package org.apache.streampipes.processors.filters.jvm.processor.merge;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.EventStreamGenerator;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TestMergeByTimeProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestMergeByTimeProcessor.class);

  private static final Integer timeInterval = 100;
  @org.junit.runners.Parameterized.Parameter
  public String testName;
  @org.junit.runners.Parameterized.Parameter(1)
  public List<String> eventStrings;
  @org.junit.runners.Parameterized.Parameter(2)
  public List<String> expectedValue;

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {"testWithInInterval", Arrays.asList("s0:0", "s1:90"), List.of("(90,0)")},
        {"testNotWithInInterval", Arrays.asList("s0:0", "s1:110"), List.of()},
        {"testWithInAndNotWithInInterval", Arrays.asList("s0:0", "s1:80", "s0:110", "s1:500"),
            List.of("(80,0)")},
        {"testFigGvnInDocs",
            Arrays.asList("s1:0", "s0:10", "s0:110", "s1:115", "s0:120", "s1:230", "s0:340", "s0:500",
                "s1:510"),
            Arrays.asList("(0,10)", "(115,110)", "(510,500)")}
    });
  }

  @Test
  public void testMergeByTimeProcessor() {
    MergeByTimeProcessor mergeByTimeProcessor = new MergeByTimeProcessor();
    DataProcessorDescription originalGraph = mergeByTimeProcessor.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);
    graph.setInputStreams(Arrays.asList(
        EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("in-stram0")),
        EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("in-stream1"))
    ));

    graph.setOutputStream(
        EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("out-stream"))
    );
    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
        .setActualTopicName("output-topic");

    List<String> outputKeySelectors = graph.getOutputStrategies()
        .stream()
        .filter(CustomOutputStrategy.class::isInstance)
        .map(o -> (CustomOutputStrategy) o)
        .findFirst()
        .map(CustomOutputStrategy::getSelectedPropertyKeys)
        .orElse(new ArrayList<>());
    outputKeySelectors.add("s0::timestamp_mapping_stream_1");
    outputKeySelectors.add("s1::timestamp_mapping_stream_2");

    List<MappingPropertyUnary> mappingPropertyUnaries = graph.getStaticProperties()
        .stream()
        .filter(p -> p instanceof MappingPropertyUnary)
        .map((p -> (MappingPropertyUnary) p))
        .filter(p -> Arrays.asList(
                MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY,
                MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY)
            .contains(p.getInternalName()))
        .collect(Collectors.toList());

    assert mappingPropertyUnaries.size() == 2;
    mappingPropertyUnaries.get(0)
        .setSelectedProperty("s0::" + MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY);
    mappingPropertyUnaries.get(1)
        .setSelectedProperty("s1::" + MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY);

    FreeTextStaticProperty fsp = graph.getStaticProperties().stream()
        .filter(p -> p instanceof FreeTextStaticProperty)
        .map((p -> (FreeTextStaticProperty) p))
        .filter(p -> p.getInternalName().equals(MergeByTimeProcessor.TIME_INTERVAL))
        .findFirst().orElse(null);
    assert fsp != null;
    fsp.setValue(String.valueOf(timeInterval));

    ProcessorParams params = new ProcessorParams(graph);

    StoreEventCollector collector = new StoreEventCollector();

    mergeByTimeProcessor.onInvocation(params, collector, null);
    sendEvents(mergeByTimeProcessor, collector);

    List<String> actualCollectedEvents = collector.getEvents().stream()
        .map(e -> formatMergedEvent(e))
        .collect(Collectors.toList());

    LOG.info("Expected merged event is {}", expectedValue);
    LOG.info("Actual merged event is {}", actualCollectedEvents);
    assertTrue(eventsEquals(expectedValue, actualCollectedEvents));
  }

  private boolean eventsEquals(List<String> expectedValue, List<String> actualCollectedEvents) {
    if (expectedValue.size() != actualCollectedEvents.size()) {
      return false;
    }
    for (int i = 0; i < expectedValue.size(); i++) {
      if (!expectedValue.get(i).equalsIgnoreCase(actualCollectedEvents.get(i))) {
        return false;
      }
    }
    return true;
  }

  private String formatMergedEvent(Event mergedEvent) {
    return String.format("(%s)", mergedEvent.getFields().values().stream()
        .map(m -> m.getAsPrimitive().getAsString()).collect(Collectors.joining(",")));
  }

  private void sendEvents(MergeByTimeProcessor mergeByTimeProcessor, StoreEventCollector spOut) {
    List<Event> events = makeEvents();
    for (Event event : events) {
      mergeByTimeProcessor.onEvent(event, spOut);
    }

  }

  private List<Event> makeEvents() {
    List<Event> events = Lists.newArrayList();
    for (String eventString : eventStrings) {
      events.add(makeEvent(eventString));
    }
    return events;
  }

  private Event makeEvent(String eventString) {
    Map<String, Object> map = Maps.newHashMap();
    String streamId = eventString.split(":")[0];
    String timestamp = eventString.split(":")[1];
    if (streamId.equals("s0")) {
      map.put(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, timestamp);
    } else {
      map.put(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, timestamp);
    }
    return EventFactory.fromMap(map,
        new SourceInfo("test", streamId),
        new SchemaInfo(null, Lists.newArrayList()));
  }
}