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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter;

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
public class TestBooleanInverterProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestBooleanInverterProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {"Test", Arrays.asList(false, true), false},
        {"Test", Arrays.asList(false, true, false), true},
        {"Test", Arrays.asList(false), true},
        {"Test", Arrays.asList(false, true, false, false, true), false},
        {"Test", Arrays.asList(true, false), true},
        {"Test", Arrays.asList(true), false},
    });
  }

  @org.junit.runners.Parameterized.Parameter
  public String invertFieldName;

  @org.junit.runners.Parameterized.Parameter(1)
  public List<Boolean> eventBooleans;

  @org.junit.runners.Parameterized.Parameter(2)
  public Boolean expectedBooleanCount;

  @Test
  public void testBoolenInverter() {
    BooleanInverterProcessor bip = new BooleanInverterProcessor();
    DataProcessorDescription originalGraph =  bip.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph =
        InvocationGraphGenerator.makeEmptyInvocation(originalGraph);

    graph.setInputStreams(Collections
        .singletonList(EventStreamGenerator
            .makeStreamWithProperties(Collections.singletonList(invertFieldName))));

    graph.setOutputStream(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList(invertFieldName)));

    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
        .setActualTopicName("output-topic");

    graph.getStaticProperties().stream()
        .filter(p -> p instanceof MappingPropertyUnary)
        .map((p -> (MappingPropertyUnary) p))
        .filter(p -> p.getInternalName().equals(BooleanInverterProcessor.INVERT_FIELD_ID))
        .findFirst().get().setSelectedProperty("s0::" + invertFieldName);
    ProcessorParams params = new ProcessorParams(graph);
    SpOutputCollector spOut = new SpOutputCollector() {
        @Override
        public void collect(Event event) {}

        @Override
        public void registerConsumer(String routeId, InternalEventProcessor<Map<String, Object>> consumer) {}

        @Override
        public void unregisterConsumer(String routeId) {}

        @Override
        public void connect() throws SpRuntimeException {}

        @Override
        public void disconnect() throws SpRuntimeException {}
    };

    bip.onInvocation(params, spOut, null);

    boolean result = sendEvents(bip, spOut);

    LOG.info("Expected boolean is {}", expectedBooleanCount);
    LOG.info("Actual boolean is {}", result);
    assertEquals(expectedBooleanCount, result);
  }

  private boolean sendEvents(BooleanInverterProcessor trend, SpOutputCollector spOut) {
    boolean result = false;
    List<Event> events = makeEvents();
    for (Event event : events) {
      LOG.info("Sending event with value "
          + event.getFieldBySelector("s0::" + invertFieldName).getAsPrimitive().getAsBoolean());
      trend.onEvent(event, spOut);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      result = event.getFieldBySelector("s0::" + invertFieldName).getAsPrimitive().getAsBoolean();
    }

    return result;
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
    map.put(invertFieldName, value);
    return EventFactory.fromMap(map, new SourceInfo("test" + "-topic", "s0"),
            new SchemaInfo(null, new ArrayList<>()));
  }
}
