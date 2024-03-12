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

//@RunWith(Parameterized.class)
public class TestDateTimeFromStringProcessor {
//  private static final Logger LOG = LoggerFactory.getLogger(TestDateTimeFromStringProcessor.class);
//
//  @org.junit.runners.Parameterized.Parameters
//  public static Iterable<Object[]> data() {
//
//    var expectedTimestamp = 1700222655537L;
//
//    return Arrays.asList(new Object[][]{
//        // the first test just demonstrates that the testing and the source code is
//        // functioning
//        {
//            "inputField", "US/Eastern",
//            List.of("2020-11-13T21:07:38.146120+01:00", "2023-11-14T16:17:01.286299-05:00",
//                    "2023-11-14T14:05:57.519543100"
//            ),
//            List.of(1605298058146L, 1699996621286L, 1699988757519L)
//        },
//        /*
//         * the next three tests demonstrate two things: (1) if the DateTime string has
//         * the zone information already in it, the user input on the timezone will not
//         * affect the DateTime variable. (2) The same instant in the real world will
//         * result in the same instant in the datetime variable
//         */
//        {
//            "inputField", "US/Pacific", List.of("2023-11-17T04:04:15.537187600-08:00[US/Pacific]"),
//            List.of(expectedTimestamp)
//        },
//        {
//            "inputField", "US/Pacific", List.of("2023-11-17T05:04:15.537187600-07:00[US/Arizona]"),
//            List.of(expectedTimestamp)
//        },
//        {
//            "inputField", "US/Pacific", List.of("2023-11-17T07:04:15.537187600-05:00[US/Eastern]"),
//            List.of(expectedTimestamp)
//        },
//        /*
//         * The next three tests demonstrate that if a localdatetime is given, when the
//         * user selects a time zone. An instant in time will be created for that
//         * specific timezone.
//         */
//        {
//            "inputField", "US/Pacific", List.of("2023-11-17T04:04:15.537187600"),
//            List.of(expectedTimestamp)
//        },
//        {
//            "inputField", "US/Arizona", List.of("2023-11-17T04:04:15.537187600"),
//            List.of(1700219055537L)
//        },
//        {
//            "inputField", "US/Eastern", List.of("2023-11-17T04:04:15.537187600"),
//            List.of(1700211855537L)
//        },
//
//        });
//  }
//
//  @org.junit.runners.Parameterized.Parameter
//  public String streamInputDateTimeFieldName;
//
//  @org.junit.runners.Parameterized.Parameter(1)
//  public String selectedTimeZone;
//
//  @org.junit.runners.Parameterized.Parameter(2)
//  public List<String> eventsString;
//
//  @org.junit.runners.Parameterized.Parameter(3)
//  public List<String> expectedValues;
//
//  public static final String DEFAULT_STREAM_PREFIX = "Stream";
//
//  @Test
//  public void testDateTime() {
//    DateTimeFromStringProcessor dateTime = new DateTimeFromStringProcessor();
//    DataProcessorDescription originalGraph = dateTime.declareModel();
//    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());
//
//    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);
//    graph.setInputStreams(Collections.singletonList(
//        EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("in-stream"))));
//    graph.setOutputStream(
//        EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("out-stream")));
//    graph.getOutputStream()
//         .getEventGrounding()
//         .getTransportProtocol()
//         .getTopicDefinition()
//         .setActualTopicName("output-topic");
//
//    MappingPropertyUnary mappingPropertyUnary = graph.getStaticProperties()
//                                                     .stream()
//                                                     .filter(p -> p instanceof MappingPropertyUnary)
//                                                     .map(p -> (MappingPropertyUnary) p)
//                                                     .filter(p -> DateTimeFromStringProcessor.FIELD_ID.equals(
//                                                                 p.getInternalName()
//                                                             )
//                                                     )
//                                                     .findFirst()
//                                                     .orElse(null);
//
//    assert mappingPropertyUnary != null;
//    mappingPropertyUnary.setSelectedProperty(DEFAULT_STREAM_PREFIX + "::" + streamInputDateTimeFieldName);
//
//    OneOfStaticProperty selectedTimeZoneProperty = graph.getStaticProperties()
//                                                        .stream()
//                                                        .filter(p -> p instanceof OneOfStaticProperty)
//                                                        .map(p -> (OneOfStaticProperty) p)
//                                                        .filter(p -> (
//                                                            DateTimeFromStringProcessor.INPUT_TIMEZONE_KEY.equals(
//                                                                p.getInternalName()
//                                                            )
//                                                        ))
//                                                        .findFirst()
//                                                        .orElse(null);
//    assert selectedTimeZoneProperty != null;
//    Option selectedTimeZoneOption = selectedTimeZoneProperty.getOptions()
//                                                            .stream()
//                                                            .filter(item -> item.getName()
//                                                                                .equals(selectedTimeZone))
//                                                            .findFirst()
//                                                            .orElse(null);
//    assert selectedTimeZoneOption != null;
//    selectedTimeZoneOption.setSelected(true);
//
//    ProcessorParams params = new ProcessorParams(graph);
//    SpOutputCollector spOut = new StoreEventCollector();
//    dateTime.onInvocation(params, spOut, null);
//    Tuple2<String, List<Long>> res = sendEvents(dateTime, spOut);
//    List<Long> resValues = res.v;
//    assert eventsString.size() == expectedValues.size();
//    int size = eventsString.size();
//    for (int i = 0; i < size; i++) {
//      LOG.info("Expected value is {}.", expectedValues.get(i));
//      LOG.info(
//          "Actual value is {}.",
//          resValues.get(i)
//                   .toString()
//      );
//      assertEquals(
//          expectedValues.get(i),
//          resValues.get(i)
//      );
//    }
//  }
//
//  private Tuple2<String, List<Long>> sendEvents(DateTimeFromStringProcessor dateTime, SpOutputCollector spOut) {
//    String field = "";
//    Long timestampValue = null;
//    List<Event> events = makeEvents();
//    List<Long> dateTimeValueList = new ArrayList<>();
//    for (Event event : events) {
//      LOG.info("sending event with value "
//                   + event.getFieldBySelector(DEFAULT_STREAM_PREFIX + "::" + streamInputDateTimeFieldName)
//                          .getAsPrimitive()
//                          .getAsString());
//      dateTime.onEvent(event, spOut);
//
//      try {
//        TimeUnit.MILLISECONDS.sleep(100);
//      } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//      }
//      try {
//        field = event.getFieldBySelector(DEFAULT_STREAM_PREFIX + "::" + DateTimeFromStringProcessor.FIELD_ID)
//                     .getAsPrimitive()
//                     .getAsString();
//        timestampValue = event
//            .getFieldBySelector(DateTimeFromStringProcessor.OUTPUT_TIMESTAMP_RUNTIME_NAME)
//            .getAsPrimitive()
//            .getAsLong();
//        LOG.info(field + ":" + timestampValue);
//      } catch (IllegalArgumentException e) {
//        throw new RuntimeException(e);
//      }
//      dateTimeValueList.add(timestampValue);
//    }
//    return new Tuple2<>(field, dateTimeValueList);
//  }
//
//  private List<Event> makeEvents() {
//    List<Event> events = new ArrayList<>();
//    for (String eventString : eventsString) {
//      events.add(makeEvent(eventString));
//    }
//    return events;
//  }
//
//  private Event makeEvent(String value) {
//    Map<String, Object> map = new HashMap<>();
//    map.put(streamInputDateTimeFieldName, value);
//    return EventFactory.fromMap(
//        map,
//        new SourceInfo("test-topic", DEFAULT_STREAM_PREFIX),
//        new SchemaInfo(null, new ArrayList<>())
//    );
//  }
}
