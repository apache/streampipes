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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter;

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
import org.apache.streampipes.sdk.helpers.Tuple3;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class TestStringCounterProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(TestStringCounterProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"Test", List.of("t1"), new Tuple3<>("", "", 0)},
        {"Test", Arrays.asList("t1", "t2"), new Tuple3<>("t1", "t2", 1)},
        {"Test", Arrays.asList("t1", "t2", "t1", "t2"), new Tuple3<>("t1", "t2", 2)},
        {"Test", Arrays.asList("t1", "t2", "t1", "t3"), new Tuple3<>("t1", "t3", 1)}
    });
  }

  @org.junit.runners.Parameterized.Parameter
  public String selectedFieldName;

  @org.junit.runners.Parameterized.Parameter(1)
  public List<String> eventStrings;

  @org.junit.runners.Parameterized.Parameter(2)
  public Tuple3<String, String, Integer> expectedValue;

  @Test
  public void testStringCounter() {
    StringCounterProcessor stringCounter = new StringCounterProcessor();
    DataProcessorDescription originalGraph = stringCounter.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);
    graph.setInputStreams(Collections
        .singletonList(EventStreamGenerator
            .makeStreamWithProperties(Collections.singletonList(selectedFieldName))));
    graph.setOutputStream(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList(selectedFieldName)));
    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
        .setActualTopicName("output-topic");

    MappingPropertyUnary mappingPropertyUnary = graph.getStaticProperties().stream()
        .filter(p -> p instanceof MappingPropertyUnary)
        .map((p -> (MappingPropertyUnary) p))
        .filter(p -> p.getInternalName().equals(StringCounterProcessor.FIELD_ID))
        .findFirst().orElse(null);
    assert mappingPropertyUnary != null;
    mappingPropertyUnary.setSelectedProperty("s0::" + selectedFieldName);
    ProcessorParams params = new ProcessorParams(graph);

    SpOutputCollector spOut = new SpOutputCollector() {
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

    stringCounter.onInvocation(params, spOut, null);
    Tuple3<String, String, Integer> tuple = sendEvents(stringCounter, spOut);
    LOG.info("Expected match count is {}.", expectedValue.x);
    LOG.info("Actual match count is {}.", tuple.x);
    assertEquals(expectedValue.x, tuple.x);
    LOG.info("Expected change from is {}.", expectedValue.k);
    LOG.info("Actual change from is {}.", tuple.k);
    assertEquals(expectedValue.k, tuple.k);
    LOG.info("Expected change to is {}.", expectedValue.k);
    LOG.info("Actual change to is {}.", tuple.k);
    assertEquals(expectedValue.v, tuple.v);
  }

  private Tuple3<String, String, Integer> sendEvents(StringCounterProcessor stringCounter, SpOutputCollector spOut) {
    int counter = 0;
    String changeFrom = "", changeTo = "";
    List<Event> events = makeEvents();
    for (Event event : events) {
      LOG.info("Sending event with value "
          + event.getFieldBySelector("s0::" + selectedFieldName).getAsPrimitive().getAsString());
      stringCounter.onEvent(event, spOut);
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      try {
        counter = event.getFieldBySelector(StringCounterProcessor.COUNT_FIELD_RUNTIME_NAME)
            .getAsPrimitive()
            .getAsInt();
        changeFrom = event.getFieldBySelector(StringCounterProcessor.CHANGE_FROM_FIELD_RUNTIME_NAME)
            .getAsPrimitive()
            .getAsString();
        changeTo = event.getFieldBySelector(StringCounterProcessor.CHANGE_TO_FIELD_RUNTIME_NAME)
            .getAsPrimitive()
            .getAsString();
        LOG.info(changeFrom + " change to " + changeTo + ", value = " + counter);
      } catch (IllegalArgumentException e) {

      }
    }
    return new Tuple3<>(changeFrom, changeTo, counter);
  }


  private List<Event> makeEvents() {
    List<Event> events = Lists.newArrayList();
    for (String eventSetting : eventStrings) {
      events.add(makeEvent(eventSetting));
    }
    return events;
  }

  private Event makeEvent(String value) {
    Map<String, Object> map = Maps.newHashMap();
    map.put(selectedFieldName, value);
    return EventFactory.fromMap(map,
        new SourceInfo("test" + "-topic", "s0"),
        new SchemaInfo(null, Lists.newArrayList()));
  }
}
