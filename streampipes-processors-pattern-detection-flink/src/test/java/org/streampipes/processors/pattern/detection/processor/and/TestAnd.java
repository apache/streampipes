/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.processors.pattern.detection.processor.and;

import static org.hamcrest.core.IsEqual.equalTo;

import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.pattern.detection.flink.processor.and.AndController;
import org.streampipes.processors.pattern.detection.flink.processor.and.AndParameters;
import org.streampipes.processors.pattern.detection.flink.processor.and.AndProgram;
import org.streampipes.processors.pattern.detection.flink.processor.and.TimeUnit;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    AndParameters params = new AndParameters(InvocationGraphGenerator.makeEmptyInvocation(new AndController().declareModel()), timeUnit, timeWindow, leftMapping, rightMapping);

    AndProgram program = new AndProgram(params, true);

    DataStream<Event> stream = program.getApplicationLogic(createTestStream(makeInputData(delayFirstEvent, makeMap("field1"))), createTestStream(makeInputData(delaySecondEvent, makeMap("field2"))));

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
