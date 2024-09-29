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
package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.counter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.test.generator.EventStreamGenerator;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBooleanCounterProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(TestBooleanCounterProcessor.class);

  static Stream<Arguments> data() {
    return Stream.of(Arguments.of("Test", "BOTH", Arrays.asList(false, true), 1),
            Arguments.of("Test", "BOTH", Arrays.asList(false, true, false), 2),
            Arguments.of("Test", "BOTH", Arrays.asList(false), 0),
            Arguments.of("Test", "TRUE -> FALSE", Arrays.asList(false, true, false, false, true), 2),
            Arguments.of("Test", "TRUE -> FALSE", Arrays.asList(true, false), 1),
            Arguments.of("Test", "TRUE -> FALSE", Arrays.asList(false), 1),
            Arguments.of("Test", "FALSE -> TRUE", Arrays.asList(false), 0),
            Arguments.of("Test", "FALSE -> TRUE", Arrays.asList(false, false, true), 1),
            Arguments.of("Test", "FALSE -> TRUE", Arrays.asList(false, false, true), 1),
            Arguments.of("Test", "FALSE -> TRUE", Arrays.asList(false, true, true, false), 1));
  }

  // /**
  // * flankUp defines which boolean changes should be counted
  // * 0: BOTH
  // * 1: TRUE -> FALSE
  // * 2: FALSE -> TRUE
  // */
  @ParameterizedTest
  @MethodSource("data")
  public void testBooleanCounter(String invertFieldName, String flankUp, List<Boolean> eventBooleans,
          Integer expectedBooleanCount) {
    BooleanCounterProcessor booleanCounter = new BooleanCounterProcessor();
    DataProcessorDescription originalGraph = booleanCounter.declareConfig().getDescription();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);

    graph.setInputStreams(Collections
            .singletonList(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList(invertFieldName))));

    graph.setOutputStream(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList(invertFieldName)));

    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
            .setActualTopicName("output-topic");

    graph.getStaticProperties().stream().filter(p -> p instanceof MappingPropertyUnary)
            .map((p -> (MappingPropertyUnary) p))
            .filter(p -> p.getInternalName().equals(BooleanCounterProcessor.FIELD_ID)).findFirst().get()
            .setSelectedProperty("s0::" + invertFieldName);
    ProcessorParams params = new ProcessorParams(graph);
    params.extractor().getStaticPropertyByName("flank", OneOfStaticProperty.class).getOptions().stream()
            .filter(ot -> ot.getName().equals(flankUp)).findFirst().get().setSelected(true);

    SpOutputCollector spOut = new SpOutputCollector() {
      @Override
      public void collect(Event event) {
      }
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
    };

    booleanCounter.onPipelineStarted(params, spOut, null);
    Integer counter = sendEvents(booleanCounter, spOut, eventBooleans, invertFieldName);
    LOG.info("Expected match count is {}", expectedBooleanCount);
    LOG.info("Actual match count is {}", counter);
    assertEquals(expectedBooleanCount, counter);
  }

  private Integer sendEvents(BooleanCounterProcessor booleanCounter, SpOutputCollector spOut,
          List<Boolean> eventBooleans, String invertFieldName) {
    int counter = 0;
    List<Event> events = makeEvents(eventBooleans, invertFieldName);
    for (Event event : events) {
      LOG.info("Sending event with value "
              + event.getFieldBySelector("s0::" + invertFieldName).getAsPrimitive().getAsBoolean());
      booleanCounter.onEvent(event, spOut);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      try {
        counter = event.getFieldBySelector(BooleanCounterProcessor.COUNT_FIELD_RUNTIME_NAME).getAsPrimitive()
                .getAsInt();
      } catch (IllegalArgumentException e) {

      }
    }

    return counter;
  }

  private List<Event> makeEvents(List<Boolean> eventBooleans, String invertFieldName) {
    List<Event> events = new ArrayList<>();
    for (Boolean eventSetting : eventBooleans) {
      events.add(makeEvent(eventSetting, invertFieldName));
    }
    return events;
  }

  private Event makeEvent(Boolean value, String invertFieldName) {
    Map<String, Object> map = new HashMap<>();
    map.put(invertFieldName, value);
    return EventFactory.fromMap(map, new SourceInfo("test" + "-topic", "s0"), new SchemaInfo(null, new ArrayList<>()));
  }
}
