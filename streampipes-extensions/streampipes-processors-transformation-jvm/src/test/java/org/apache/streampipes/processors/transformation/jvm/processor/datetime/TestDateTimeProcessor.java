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

package org.apache.streampipes.processors.transformation.jvm.processor.datetime;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.PropertyRenameRule;
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestDateTimeProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(TestDateTimeProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    Iterable<Object[]> data = Arrays.asList(new Object[][] {
      // the first test just demonstrates that the testing and the source code is
      // functioning
      { "dateTimeField", "US/Eastern",
        List.of("2020-11-13T21:07:38.146120+01:00", "2023-11-14T16:17:01.286299-05:00",
            "2023-11-14T14:05:57.519543100"),
        List.of("1605298058", "1699996621", "1699988757")
      },
      /*
       * the next three tests demonstrate two things: (1) if the DateTime string has
       * the zone information already in it, the user input on the timezone will not
       * affect the DateTime variable. (2) The same instant in the real world will
       * result in the same instant in the datetime variable
       */
      { "dateTimeField", "US/Pacific", List.of("2023-11-17T04:04:15.537187600-08:00[US/Pacific]"),
        List.of("1700222655") },
      { "dateTimeField", "US/Pacific", List.of("2023-11-17T05:04:15.537187600-07:00[US/Arizona]"),
          List.of("1700222655") },
      { "dateTimeField", "US/Pacific", List.of("2023-11-17T07:04:15.537187600-05:00[US/Eastern]"),
            List.of("1700222655") },
      /*
       * The next three tests demonstrate that if a localdatetime is given, when the
       * user selects a time zone. An instant in time will be created for that
       * specific timezone.
       */
      { "dateTimeField", "US/Pacific", List.of("2023-11-17T04:04:15.537187600"),
              List.of("1700222655") },
      { "dateTimeField", "US/Arizona", List.of("2023-11-17T04:04:15.537187600"),
                List.of("1700219055") },
      { "dateTimeField", "US/Eastern", List.of("2023-11-17T04:04:15.537187600"),
                  List.of("1700211855") },

    });

    return data;
  }

  @org.junit.runners.Parameterized.Parameter
  public String streamInputDateTimeFieldName;

  @org.junit.runners.Parameterized.Parameter(1)
  public String selectedTimeZone;

  @org.junit.runners.Parameterized.Parameter(2)
  public List<String> eventsString;

  @org.junit.runners.Parameterized.Parameter(3)
  public List<String> expectedValues;

  public static final String DEFAULT_STREAM_PREFIX = "Stream";

  @Test
  public void testDateTime() {
    DateTimeProcessor dateTime = new DateTimeProcessor();
    DataProcessorDescription originalGraph = dateTime.declareModel();
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
        .filter(p -> (DateTimeProcessor.INPUT_DATETIME_FIELD_ID).equals(p.getInternalName()))
        .findFirst().orElse(null);

    assert mappingPropertyUnary != null;
    mappingPropertyUnary.setSelectedProperty(DEFAULT_STREAM_PREFIX + "::" + streamInputDateTimeFieldName);

    OneOfStaticProperty selectedTimeZoneProperty = graph.getStaticProperties().stream()
        .filter(p -> p instanceof OneOfStaticProperty)
        .map(p -> (OneOfStaticProperty) p)
        .filter(p -> (DateTimeProcessor.SELECTED_INPUT_TIMEZONE.equals(p.getInternalName())))
        .findFirst().orElse(null);
    assert selectedTimeZoneProperty != null;
    Option selectedTimeZoneOption = selectedTimeZoneProperty.getOptions().stream()
        .filter(item -> item.getName().equals(selectedTimeZone))
        .findFirst().orElse(null);
    assert selectedTimeZoneOption != null;
    selectedTimeZoneOption.setSelected(true);

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
    dateTime.onInvocation(params, spOut, null);
    Tuple2<String, List<Long>> res = sendEvents(dateTime, spOut);
    List<Long> resValues = res.v;
    assert eventsString.size() == expectedValues.size();
    int size = eventsString.size();
    for (int i = 0; i < size; i++) {
      LOG.info("Expected value is {}.", expectedValues.get(i));
      LOG.info("Actual value is {}.", resValues.get(i).toString());
      assertEquals(expectedValues.get(i), resValues.get(i).toString());
    }
  }

  private Tuple2<String, List<Long>> sendEvents(DateTimeProcessor dateTime, SpOutputCollector spOut) {
    String field = "";
    ZonedDateTime dateTimeValue = null;
    List<Event> events = makeEvents();
    List<Long> dateTimeValueList = new ArrayList<Long>();
    for (Event event : events) {
      LOG.info("sending event with value "
          + event.getFieldBySelector(DEFAULT_STREAM_PREFIX + "::" + streamInputDateTimeFieldName).getAsPrimitive()
          .getAsString());
      dateTime.onEvent(event, spOut);

      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      try {
        field = event.getFieldBySelector(DEFAULT_STREAM_PREFIX + "::" + DateTimeProcessor.INPUT_DATETIME_FIELD_ID)
            .getAsPrimitive()
            .getAsString();
        dateTimeValue = (ZonedDateTime) event.getFieldBySelector(DateTimeProcessor.OUTPUT_DATETIME_RUNTIME_NAME)
            .getRawValue();
        LOG.info(field + ":" + dateTimeValue);
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(e);
      }
      dateTimeValueList.add(dateTimeValue.toEpochSecond());
    }
    return new Tuple2<>(field, dateTimeValueList);
  }

  private List<Event> makeEvents() {
    List<Event> events = new ArrayList<Event>();
    for (String eventString : eventsString) {
      events.add(makeEvent(eventString));
    }
    return events;
  }

  private Event makeEvent(String value) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(streamInputDateTimeFieldName, value);
    return EventFactory.fromMap(map,
        new SourceInfo("test-topic", DEFAULT_STREAM_PREFIX),
        new SchemaInfo(null, new ArrayList<PropertyRenameRule>()));
  }
}
