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
package org.streampipes.processors.pattern.detection.processor.absence;

import static org.hamcrest.core.IsEqual.equalTo;

import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.pattern.detection.flink.processor.absence.AbsenceController;
import org.streampipes.processors.pattern.detection.flink.processor.absence.AbsenceParameters;
import org.streampipes.processors.pattern.detection.flink.processor.absence.AbsenceProgram;
import org.streampipes.processors.pattern.detection.flink.processor.and.TimeUnit;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class TestAbsence extends DataStreamTestBase {

  @Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {10, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), true, 12},
            {10, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), false, 5},
            {5, TimeUnit.Seconds, Arrays.asList("id"), Arrays.asList("id"), true, 6},
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
  public Integer waitForMs;


  @Test
  public void testAbsenceProgram() {
    AbsenceParameters params = new AbsenceParameters(InvocationGraphGenerator.makeEmptyInvocation(new AbsenceController().declareModel()),  Arrays.asList("id", "timestamp", "value"), timeWindow, timeUnit);

    AbsenceProgram program = new AbsenceProgram(params, true);

    DataStream<Event> stream = program.getApplicationLogic(createTestStream(makeInputData(1, makeMap(), 0)), createTestStream(makeInputData(waitForMs, makeMap(), 1)));

    assertStream(stream, equalTo(getOutput(shouldMatch)));
  }

  private Collection<Event> getOutput(Boolean shouldMatch) {
    List<Event> allEvents = new ArrayList<>();

    if (shouldMatch) {
      allEvents.add(makeMap().get(0));
    }

    return allEvents;
  }

  private EventTimeInput<Event> makeInputData(Integer delayEvent, List<Event> inputMap, Integer i) {
    List<Event> testData = inputMap;
    EventTimeInputBuilder<Event> builder = EventTimeInputBuilder.startWith(testData.get(i), after(delayEvent, seconds));

    return builder;
  }

  private List<Event> makeMap() {
    List<Event> allEvents = new ArrayList<>();
    Event event1 = new Event();
    event1.addField("id", "a");
    event1.addField("timestamp", 0);

    allEvents.add(event1);

    Event event2 = new Event();
    event2.addField("id", "a");
    event2.addField("timestamp", waitForMs);

    allEvents.add(event2);

    return allEvents;
  }
}
