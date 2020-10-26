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
package org.apache.streampipes.processors.siddhi.trend;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.test.generator.EventStreamGenerator;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestTrendProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestTrendProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {1, 100, TrendOperator.INCREASE, Arrays.asList(new Tuple2<>(100, 1),
                    new Tuple2<>(100,2)), 1},
            {1, 100, TrendOperator.INCREASE, Arrays.asList(
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100,2))
                    , 1},
            {1, 100, TrendOperator.DECREASE, Arrays.asList(
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100,0))
                    , 1},
            {1, 100, TrendOperator.INCREASE, Arrays.asList(
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100,2),
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100,2))
                    , 2},
            {1, 200, TrendOperator.INCREASE, Arrays.asList(
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100,2),
                    new Tuple2<>(100, 1),
                    new Tuple2<>(100,2))
                    , 0},

    });
  }

  @org.junit.runners.Parameterized.Parameter
  public Integer timeWindow;

  @org.junit.runners.Parameterized.Parameter(1)
  public Integer increase;

  @org.junit.runners.Parameterized.Parameter(2)
  public TrendOperator trendOperator;

  @org.junit.runners.Parameterized.Parameter(3)
  public List<Tuple2<Integer, Integer>> eventSettings;

  @org.junit.runners.Parameterized.Parameter(4)
  public Integer expectedMatchCount;

  @Test
  public void testTrend() {
    final Integer[] actualMatchCount = {0};
    DataProcessorDescription originalGraph = new TrendController().declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph =
            InvocationGraphGenerator.makeEmptyInvocation(originalGraph);

    graph.setInputStreams(Collections
            .singletonList(EventStreamGenerator
                    .makeStreamWithProperties(Collections.singletonList("randomValue"))));

    graph.setOutputStream(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("randomValue")));

    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition().setActualTopicName("output-topic");
    TrendParameters params = new TrendParameters(graph, trendOperator, increase, timeWindow, "s0" +
            "::randomValue", Arrays.asList("s0::randomValue"));

    Trend trend = new Trend(event -> actualMatchCount[0]++);
    trend.onInvocation(params, null, null);

    sendEvents(trend);
    LOG.info("Expected match count is {}", expectedMatchCount);
    LOG.info("Actual match count is {}", actualMatchCount[0]);
    assertEquals(expectedMatchCount, actualMatchCount[0]);
  }

  private void sendEvents(Trend trend) {
    List<Tuple2<Integer, Event>> events = makeEvents();
    for(Tuple2<Integer, Event> event : events) {
      LOG.info("Sending event with value " +event.b.getFieldBySelector("s0::randomValue"));
      trend.onEvent(event.b, null);
      try {
        Thread.sleep(event.a);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private List<Tuple2<Integer, Event>> makeEvents() {
    List<Tuple2<Integer, Event>> events = new ArrayList<>();
    for(Tuple2<Integer, Integer> eventSetting : eventSettings) {
      events.add(makeEvent(eventSetting.a, eventSetting.b));
    }
    return events;
  }

  private Tuple2<Integer, Event> makeEvent(Integer timeout, Integer value) {
    Map<String, Object> map = new HashMap<>();
    map.put("randomValue", value);
    return new Tuple2<>(timeout, EventFactory.fromMap(map, new SourceInfo("test" +
                    "-topic", "s0"),
            new SchemaInfo(null,
                    new ArrayList<>())));
  }
}
