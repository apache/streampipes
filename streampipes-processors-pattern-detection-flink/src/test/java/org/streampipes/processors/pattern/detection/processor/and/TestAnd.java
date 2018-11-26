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

import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.streampipes.processors.pattern.detection.flink.processor.and.AndController;
import org.streampipes.processors.pattern.detection.flink.processor.and.AndParameters;
import org.streampipes.processors.pattern.detection.flink.processor.and.AndProgram;
import org.streampipes.processors.pattern.detection.flink.processor.and.TimeUnit;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.*;

import static org.hamcrest.core.IsEqual.equalTo;

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

    DataStream<Map<String, Object>> stream = program.getApplicationLogic(createTestStream(makeInputData(delayFirstEvent, makeMap("field1"))), createTestStream(makeInputData(delaySecondEvent, makeMap("field2"))));

    assertStream(stream, equalTo(getOutput(shouldMatch)));
  }

  private Collection<Map<String, Object>> getOutput(Boolean shouldMatch) {
    List<Map<String, Object>> allEvents = new ArrayList<>();

    if (shouldMatch) {
      Map<String, Object> outMap = new HashMap<>();
      outMap.put("id", "a");
      outMap.put("field1", 1);
      outMap.put("field2", 1);
      allEvents.add(outMap);
    }

    return allEvents;
  }

  private EventTimeInput<Map<String, Object>> makeInputData(Integer delayEvent, List<Map<String, Object>> inputMap) {
    List<Map<String, Object>> testData = inputMap;
    EventTimeInputBuilder<Map<String, Object>> builder = EventTimeInputBuilder.startWith(testData.get(0), after(delayEvent, seconds));

    return builder;
  }

  private List<Map<String, Object>> makeMap(String fieldName) {
    List<Map<String, Object>> allEvents = new ArrayList<>();
    Map<String, Object> event = new HashMap<>();
    event.put("id", "a");
    event.put(fieldName, 1);

    allEvents.add(event);

    return allEvents;
  }

}
