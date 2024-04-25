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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state;


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.test.generator.EventStreamGenerator;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TestStringToStateProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestStringToStateProcessor.class);

  private static final String DEFAULT_STREAM_NAME = "stream1";

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {
            List.of(),
            List.of(Arrays.asList("t1", "t2", "t3")),
            List.of()
        },
        {
            List.of("c1"),
            List.of(Arrays.asList("t1", "t2", "t3")),
            List.of("t1")
        },
        {
            List.of("c1", "c2"),
            List.of(Arrays.asList("t1", "t2", "t3")),
            Arrays.asList("t1", "t2")
        },
        {
            List.of("c1", "c2"),
            Arrays.asList(
                Arrays.asList("t1-1", "t2-1", "t3-1"),
                Arrays.asList("t1-2", "t2-2", "t3-2")
            ),
            Arrays.asList("t1-2", "t2-2")
        },
        {
            List.of("c1", "c2", "c3"),
            Arrays.asList(
                Arrays.asList("t1-1", "t2-1", "t3-1"),
                Arrays.asList("t1-2", "t2-2", "t3-2"),
                Arrays.asList("t1-3", "t2-3", "t3-3")
            ),
            Arrays.asList("t1-3", "t2-3", "t3-3")
        }
    });
  }


  @ParameterizedTest
  @MethodSource("data")
  public void testStringToState(List<String> selectedFieldNames, List<List<String>> eventStrings,
                                List<String> expectedValue) {
    StringToStateProcessor stringToStateProcessor = new StringToStateProcessor();
    DataProcessorDescription originalGraph = stringToStateProcessor.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);
    graph.setInputStreams(Collections
        .singletonList(EventStreamGenerator
            .makeStreamWithProperties(Collections.singletonList("stream-in"))));
    graph.setOutputStream(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("stream-out")));
    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
        .setActualTopicName("output-topic");

    MappingPropertyNary mappingPropertyNary = graph.getStaticProperties().stream()
        .filter(p -> p instanceof MappingPropertyNary)
        .map(p -> (MappingPropertyNary) p)
        .filter(p -> p.getInternalName().equals(StringToStateProcessor.STRING_STATE_FIELD))
        .findFirst().orElse(null);

    assert mappingPropertyNary != null;
    mappingPropertyNary.setSelectedProperties(
        selectedFieldNames.stream().map(field -> DEFAULT_STREAM_NAME + "::" + field).toList());

    ProcessorParams params = new ProcessorParams(graph);

    SpOutputCollector spOutputCollector = new SpOutputCollector() {
      @Override
      public void registerConsumer(String routeId, InternalEventProcessor<Map<String, Object>> consumer) {
      }

      @Override
      public void unregisterConsumer(String routeId) {
      }

      @Override
      public void connect() throws SpRuntimeException {
      }

      @Override
      public void disconnect() throws SpRuntimeException {
      }

      @Override
      public void collect(Event event) {
      }
    };

    stringToStateProcessor.onInvocation(params, spOutputCollector, null);
    Object[] states = sendEvents(stringToStateProcessor, spOutputCollector, selectedFieldNames, eventStrings);
    LOG.info("Expected states is {}.", expectedValue);
    LOG.info("Actual states is {}.", Arrays.toString(states));
    assertArrayEquals(expectedValue.toArray(), states);
  }

  private Object[] sendEvents(StringToStateProcessor stateProcessor, SpOutputCollector spOut,
                              List<String> selectedFieldNames, List<List<String>> eventStrings) {
    List<Event> events = makeEvents(selectedFieldNames, eventStrings);
    Object[] states = null;
    for (Event event : events) {
      stateProcessor.onEvent(event, spOut);
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      try {
        states = (Object[]) event.getFieldBySelector(StringToStateProcessor.CURRENT_STATE)
            .getAsPrimitive().getRawValue();
        LOG.info("Current states: " + Arrays.toString(states));
      } catch (IllegalArgumentException e) {
        LOG.info(e.getMessage(), e);
      }
    }
    return states;
  }

  private List<Event> makeEvents(List<String> selectedFieldNames, List<List<String>> eventStrings) {
    List<Event> events = Lists.newArrayList();
    for (List<String> eventSetting : eventStrings) {
      events.add(makeEvent(selectedFieldNames, eventSetting));
    }
    return events;
  }

  private Event makeEvent(List<String> selectedFieldNames, List<String> value) {
    Map<String, Object> map = Maps.newHashMap();
    for (int i = 0; i < selectedFieldNames.size(); i++) {
      map.put(selectedFieldNames.get(i), value.get(i));
    }
    return EventFactory.fromMap(map,
        new SourceInfo("test-topic", DEFAULT_STREAM_NAME),
        new SchemaInfo(null, Lists.newArrayList()));
  }
}
