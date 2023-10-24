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

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.EventStreamGenerator;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestBooleanFilterProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestBooleanFilterProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    List<Boolean> allTrue = Arrays.asList(true, true, true);
    List<Boolean> allFalse = Arrays.asList(false, false, false);
    List<Boolean> someTrueSomeFalse = Arrays.asList(true, false, true, false, true, false, true, false, true);
    List<Boolean> empty = Arrays.asList();
    return Arrays.asList(new Object[][]{
        {"True", "Test", someTrueSomeFalse, 5},
        {"True", "Test", allTrue, 3},
        {"True", "Test", allFalse, 0},
        {"True", "Test", empty, 0},
        {"False", "Test", someTrueSomeFalse, 4},
        {"False", "Test", allTrue, 0},
        {"False", "Test", allFalse, 3},
        {"False", "Test", empty, 0},
    });
  }

  @org.junit.runners.Parameterized.Parameter
  public String boolToKeep;

  @org.junit.runners.Parameterized.Parameter(1)
  public String fieldName;

  @org.junit.runners.Parameterized.Parameter(2)
  public List<Boolean> eventBooleans;

  @org.junit.runners.Parameterized.Parameter(3)
  public int expectedFilteredBooleanCount;

  @Test
  public void testBoolenFilter() {
    BooleanFilterProcessor bfp = new BooleanFilterProcessor();
    DataProcessorDescription originalGraph = bfp.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph =
        InvocationGraphGenerator.makeEmptyInvocation(originalGraph);

    graph.setInputStreams(Collections
        .singletonList(EventStreamGenerator
            .makeStreamWithProperties(Collections.singletonList(fieldName))));

    graph.setOutputStream(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList(fieldName)));

    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
        .setActualTopicName("output-topic");

    graph.getStaticProperties().stream()
        .filter(p -> p instanceof MappingPropertyUnary)
        .map((p -> (MappingPropertyUnary) p))
        // Must hardcode since BOOLEAN_MAPPING is private
        .filter(p -> p.getInternalName().equals("boolean-mapping"))
        .findFirst().get().setSelectedProperty("s0::" + fieldName);
    ProcessorParams params = new ProcessorParams(graph);
    params.extractor().getStaticPropertyByName(BooleanFilterProcessor.VALUE, OneOfStaticProperty.class).getOptions()
        .stream().filter(ot -> ot.getName().equals(boolToKeep)).findFirst()
        .get().setSelected(true);
    StoreEventCollector collector = new StoreEventCollector();

    bfp.onInvocation(params, collector, null);

    int result = sendEvents(bfp, collector);

    LOG.info("Expected filtered boolean count is {}", expectedFilteredBooleanCount);
    LOG.info("Actual filtered boolean count is {}", result);
    assertEquals(expectedFilteredBooleanCount, result);
  }

  private int sendEvents(BooleanFilterProcessor processor, StoreEventCollector collector) {
    List<Event> events = makeEvents();
    for (Event event : events) {
      LOG.info("Sending event with value "
          + event.getFieldBySelector("s0::" + fieldName).getAsPrimitive().getAsBoolean());
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
    for (Boolean eventSetting : eventBooleans) {
      events.add(makeEvent(eventSetting));
    }
    return events;
  }

  private Event makeEvent(Boolean value) {
    Map<String, Object> map = new HashMap<>();
    map.put(fieldName, value);
    return EventFactory.fromMap(map, new SourceInfo("test" + "-topic", "s0"),
        new SchemaInfo(null, new ArrayList<>()));
  }
}
