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
package org.streampipes.processors.aggregation.flink.processor.rate;

import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)
public class TestRateProgram extends DataStreamTestBase {

  @Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {1, 1000, TimeUnit.MILLISECONDS, 1.0f, 1},
            {10, 1000, TimeUnit.MILLISECONDS, 1.0f, 1},
            {100, 1000, TimeUnit.MILLISECONDS, 1.0f, 1},
            {10, 100, TimeUnit.MILLISECONDS, 1.0f, 10},
            {2, 500, TimeUnit.MILLISECONDS, 2.0f, 1},
            {4, 250, TimeUnit.MILLISECONDS, 4.0f, 1},
            {8, 250, TimeUnit.MILLISECONDS, 4.0f, 2},
    });
  }

  @Parameterized.Parameter
  public Integer numEvents;

  @Parameterized.Parameter(1)
  public Integer waitTime;

  @Parameterized.Parameter(2)
  public TimeUnit timeUnit;

  @Parameterized.Parameter(3)
  public Float expectedFrequency;

  @Parameterized.Parameter(4)
  public Integer timeWindowSize;

  @Test
  public void testRateProgram() {
    EventRateParameter params = new EventRateParameter(InvocationGraphGenerator.makeEmptyInvocation(new EventRateController().declareModel()), timeWindowSize);

    EventRateProgram program = new EventRateProgram(params, true);

    DataStream<Map<String, Object>> stream = program.getApplicationLogic(createTestStream(makeInputData(numEvents, waitTime, timeUnit)));

    ExpectedRecords<Map<String, Object>> expected =
            new ExpectedRecords<Map<String, Object>>().expectAll(getOutput(timeWindowSize, expectedFrequency, numEvents));

    assertStream(stream, expected);
  }

  private Collection<Map<String, Object>> getOutput(Integer timeWindowSize, Float eventsPerSecond, Integer numEvents) {
    List<Map<String, Object>> allEvents = new ArrayList<>();
    Map<String, Object> outMap = new HashMap<>();
    outMap.put("rate", eventsPerSecond);

    for (int i = 0; i < numEvents % timeWindowSize; i++) {
      allEvents.add(outMap);
    }

    return allEvents;
  }

  private EventTimeInput<Map<String, Object>> makeInputData(Integer count, Integer time, TimeUnit timeUnit) {
    List<Map<String, Object>> testData = makeTestData(count);
    EventTimeInputBuilder<Map<String, Object>> builder = EventTimeInputBuilder.startWith(testData.get(0));

    for (int i = 1; i < testData.size(); i++) {
      builder.emit(testData.get(i), after(time, timeUnit));
    }

    return builder;
  }

  private List<Map<String, Object>> makeTestData(Integer count) {
    List<Map<String, Object>> allEvents = new ArrayList<>();
    Map<String, Object> event = new HashMap<>();
    event.put("test", 1);

    for (int i = 0; i < count; i++) {
      allEvents.add(event);
    }

    return allEvents;
  }


}
