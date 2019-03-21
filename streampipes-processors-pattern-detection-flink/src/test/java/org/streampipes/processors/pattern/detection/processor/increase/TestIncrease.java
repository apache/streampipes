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
package org.streampipes.processors.pattern.detection.processor.increase;

import static org.hamcrest.core.IsEqual.equalTo;

import io.flinkspector.core.input.Input;
import io.flinkspector.core.input.InputBuilder;
import io.flinkspector.datastream.DataStreamTestBase;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.pattern.detection.flink.processor.increase.IncreaseController;
import org.streampipes.processors.pattern.detection.flink.processor.increase.IncreaseParameters;
import org.streampipes.processors.pattern.detection.flink.processor.increase.IncreaseProgram;
import org.streampipes.processors.pattern.detection.flink.processor.increase.Operation;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class TestIncrease extends DataStreamTestBase {

  @Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {Operation.INCREASE, 100, 10, 1.0f, 2.0f, 5000, true},
            {Operation.DECREASE, 100, 10, 2.0f, 0.0f, 5000, true},
            {Operation.DECREASE, 50, 10, 2.0f, 1.0f, 5000, true},
            {Operation.DECREASE, 50, 10, 2.0f, 1.5f, 5000, false},
            {Operation.INCREASE, 100, 10, 1.0f, 2.0f, 11000, false},
    });
  }

  @Parameterized.Parameter
  public Operation operation;

  @Parameterized.Parameter(1)
  public Integer increase;

  @Parameterized.Parameter(2)
  public Integer duration;

  @Parameterized.Parameter(3)
  public Float value1;

  @Parameterized.Parameter(4)
  public Float value2;

  @Parameterized.Parameter(5)
  public Integer waitForMs;

  @Parameterized.Parameter(6)
  public Boolean shouldMatch;


  @Test
  public void testIncreaseProgram() {
    IncreaseParameters params = new IncreaseParameters(InvocationGraphGenerator
            .makeInvocationWithOutputProperties(new IncreaseController().declareModel(), Arrays.asList("id", "timestamp", "value")),
            operation,
            increase,
            duration,
            "value",
            "id",
            "timestamp");


    IncreaseProgram program = new IncreaseProgram(params, true);

    DataStream<Event> stream = program.getApplicationLogic(createTestStream(makeInputData(makeMap())));

    assertStream(stream, equalTo(getOutput(shouldMatch)));
  }

  private Collection<Event> getOutput(Boolean shouldMatch) {
    List<Event> allEvents = new ArrayList<>();

    if (shouldMatch) {
     allEvents.add(makeMap().get(1));
    }

    return allEvents;
  }

  private Input<Event> makeInputData(List<Event> inputMap) {
    List<Event> testData = inputMap;
    InputBuilder<Event> builder = InputBuilder.startWith(testData.get(0));
    for(int i = 1; i < inputMap.size(); i++) {
      builder.emit(inputMap.get(i));
    }
    return builder;
  }

  private List<Event> makeMap() {
    List<Event> allEvents = new ArrayList<>();
    Event event1 = new Event();
    event1.addField("id", "a");
    event1.addField("timestamp", 0);
    event1.addField("value", value1);

    allEvents.add(event1);

    Event event2 = new Event();
    event2.addField("id", "a");
    event2.addField("timestamp", waitForMs);
    event2.addField("value", value2);

    allEvents.add(event2);

    return allEvents;
  }


}
