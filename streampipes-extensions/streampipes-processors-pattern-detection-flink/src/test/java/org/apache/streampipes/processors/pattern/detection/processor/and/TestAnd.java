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
package org.apache.streampipes.processors.pattern.detection.processor.and;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.pattern.detection.flink.PatternDetectionFlinkInit;
import org.apache.streampipes.processors.pattern.detection.flink.processor.and.AndController;
import org.apache.streampipes.processors.pattern.detection.flink.processor.and.AndParameters;
import org.apache.streampipes.processors.pattern.detection.flink.processor.and.AndProgram;
import org.apache.streampipes.processors.pattern.detection.flink.processor.and.TimeUnit;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;

import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;

@Ignore
@RunWith(Parameterized.class)
public class TestAnd extends DataStreamTestBase {


  @Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {2, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), true, 1, 1},
        {1, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), true, 1, 1},
        {10, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), false, 1, 12},
        {10, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), true, 3, 4},
        {1, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), false, 1, 2},
        {3600, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), true, 10, 3500},

    });
  }

  @Parameterized.Parameter
  public Integer timeWindow;

  @Parameterized.Parameter(1)
  public TimeUnit timeUnit;

  @Parameterized.Parameter(2)
  public List<String> leftMapping;

  @Parameterized.Parameter(3)
  public List<String> rightMapping;

  @Parameterized.Parameter(4)
  public Boolean shouldMatch;

  @Parameterized.Parameter(5)
  public Integer delayFirstEvent;

  @Parameterized.Parameter(6)
  public Integer delaySecondEvent;

  @Test
  public void testAndProgram() {
    DataProcessorDescription description = new AndController().declareModel();
    description.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());
    AndParameters params =
        new AndParameters(InvocationGraphGenerator.makeEmptyInvocation(description), timeUnit,
            timeWindow, leftMapping, rightMapping);

    ConfigExtractor configExtractor = ConfigExtractor.from(PatternDetectionFlinkInit.SERVICE_GROUP);
    AndProgram program = new AndProgram(params, configExtractor, null);

    DataStream<Event> stream =
        program.getApplicationLogic(createTestStream(makeInputData(delayFirstEvent, makeMap("field1"))),
            createTestStream(makeInputData(delaySecondEvent, makeMap("field2"))));

    assertStream(stream, equalTo(getOutput(shouldMatch)));
  }

  private Collection<Event> getOutput(Boolean shouldMatch) {
    List<Event> allEvents = new ArrayList<>();

    if (shouldMatch) {
      Event outMap = new Event();
      outMap.addField("id", "a");
      outMap.addField("field1", 1);
      outMap.addField("field2", 1);
      allEvents.add(outMap);
    }

    return allEvents;
  }

  private EventTimeInput<Event> makeInputData(Integer delayEvent, List<Event> inputMap) {
    List<Event> testData = inputMap;
    EventTimeInputBuilder<Event> builder = EventTimeInputBuilder.startWith(testData.get(0), after(delayEvent, seconds));

    return builder;
  }

  private List<Event> makeMap(String fieldName) {
    List<Event> allEvents = new ArrayList<>();
    Event event = new Event();
    event.addField("id", "a");
    event.addField(fieldName, 1);

    allEvents.add(event);

    return allEvents;
  }

}
