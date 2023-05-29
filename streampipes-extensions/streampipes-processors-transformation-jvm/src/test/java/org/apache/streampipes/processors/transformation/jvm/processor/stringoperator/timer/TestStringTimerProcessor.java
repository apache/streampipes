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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer;

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
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.helpers.Tuple2;
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
public class TestStringTimerProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestStringTimerProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"select_field", "On Input Event", "Milliseconds", List.of(""), ""},
        {"select_field", "On Input Event", "Milliseconds", List.of("t1"), ""},
        {"select_field", "On Input Event", "Milliseconds", List.of("t1", "t1"), "t1"},
        {"select_field", "On Input Event", "Seconds", List.of("t1", "t1", "t2"), "t1"},
        {"select_field", "On Input Event", "Minutes", List.of("t1", "t2", "t3"), "t2"},
        {"select_field", "When String Value Changes", "Milliseconds", List.of(""), ""},
        {"select_field", "When String Value Changes", "Milliseconds", List.of("t1"), ""},
        {"select_field", "When String Value Changes", "Milliseconds", List.of("t1", "t2"), "t1"},
        {"select_field", "When String Value Changes", "Seconds", List.of("t1", "t1", "t2"), "t1"},
        {"select_field", "When String Value Changes", "Minutes", List.of("t1", "t2", "t3"), "t2"}
    });
  }

  @org.junit.runners.Parameterized.Parameter
  public String selectedFieldName;

  @org.junit.runners.Parameterized.Parameter(1)
  public String outputFrequency;

  @org.junit.runners.Parameterized.Parameter(2)
  public String outputUnit;

  @org.junit.runners.Parameterized.Parameter(3)
  public List<String> eventStrings;

  @org.junit.runners.Parameterized.Parameter(4)
  public String expectedValue;

  public static final String DEFAULT_STREAM_PREFIX = "stream";

  @Test
  public void testStringTimer() {
    StringTimerProcessor stringTimer = new StringTimerProcessor();
    DataProcessorDescription originalGraph = stringTimer.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);
    graph.setInputStreams(Collections.singletonList(
        EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("in-stream"))));
    graph.setOutputStream(
        EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("out-stream")));
    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
        .setActualTopicName("output-topic");

    MappingPropertyUnary mappingPropertyUnary = graph.getStaticProperties().stream()
        .filter(p -> p instanceof MappingPropertyUnary)
        .map(p -> (MappingPropertyUnary) p)
        .filter(p -> (StringTimerProcessor.FIELD_ID).equals(p.getInternalName()))
        .findFirst().orElse(null);

    assert mappingPropertyUnary != null;
    mappingPropertyUnary.setSelectedProperty(DEFAULT_STREAM_PREFIX + "::" + selectedFieldName);

    OneOfStaticProperty outputFrequencyStaticProperty = graph.getStaticProperties().stream()
        .filter(p -> p instanceof OneOfStaticProperty)
        .map(p -> (OneOfStaticProperty) p)
        .filter(p -> (StringTimerProcessor.OUTPUT_FREQUENCY.equals(p.getInternalName())))
        .findFirst().orElse(null);
    assert outputFrequencyStaticProperty != null;
    Option outputFrequencyOption = outputFrequencyStaticProperty.getOptions().stream()
        .filter(item -> item.getName().equals(outputFrequency))
        .findFirst().orElse(null);
    assert  outputFrequencyOption != null;
    outputFrequencyOption.setSelected(true);

    OneOfStaticProperty outputUnitStaticProperty = graph.getStaticProperties().stream()
        .filter(p -> p instanceof OneOfStaticProperty)
        .map(p -> (OneOfStaticProperty) p)
        .filter(p -> (StringTimerProcessor.OUTPUT_UNIT_ID.equals(p.getInternalName())))
        .findFirst().orElse(null);
    assert  outputUnitStaticProperty != null;
    Option outputUnitOption = outputUnitStaticProperty.getOptions().stream()
        .filter(item -> item.getName().equals(outputUnit))
        .findFirst().orElse(null);
    assert outputUnitOption != null;
    outputUnitOption.setSelected(true);

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
    stringTimer.onInvocation(params, spOut, null);
    Tuple2<String, Double> res = sendEvents(stringTimer, spOut);
    LOG.info("Expected value is {}.", expectedValue);
    LOG.info("Actual value is {}.", res.k);
    assertEquals(expectedValue, res.k);
  }

  private Tuple2<String, Double> sendEvents(StringTimerProcessor stringTimer, SpOutputCollector spOut) {
    String field = "";
    double timeDiff = 0.0;
    List<Event> events = makeEvents();
    for (Event event : events) {
      LOG.info("Sending event with value "
          + event.getFieldBySelector(DEFAULT_STREAM_PREFIX + "::" + selectedFieldName).getAsPrimitive().getAsString());
      stringTimer.onEvent(event, spOut);
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      try {
        field = event.getFieldBySelector(StringTimerProcessor.FIELD_VALUE_RUNTIME_NAME)
            .getAsPrimitive()
            .getAsString();
        timeDiff = event.getFieldBySelector(StringTimerProcessor.MEASURED_TIME_FIELD_RUNTIME_NAME)
            .getAsPrimitive()
                .getAsDouble();
        LOG.info(field + " time: " + timeDiff);
      } catch (IllegalArgumentException e) {

      }
    }
    return new Tuple2<>(field, timeDiff);
  }

  private List<Event> makeEvents() {
    List<Event> events = Lists.newArrayList();
    for (String eventString : eventStrings) {
      events.add(makeEvent(eventString));
    }
    return events;
  }
  private Event makeEvent(String value) {
    Map<String, Object> map = Maps.newHashMap();
    map.put(selectedFieldName, value);
    return EventFactory.fromMap(map,
        new SourceInfo("test-topic", DEFAULT_STREAM_PREFIX),
        new SchemaInfo(null, Lists.newArrayList())
    );
  }
}
