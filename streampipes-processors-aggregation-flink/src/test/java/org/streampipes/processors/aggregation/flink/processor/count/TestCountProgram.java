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
package org.streampipes.processors.aggregation.flink.processor.count;

import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.*;

public class TestCountProgram extends DataStreamTestBase {

  @Test
  public void testCountProgram() {

    EventTimeInput input = makeInputData(makeTestData(), makeTestData().size());

    ExpectedRecords<Map<String, Object>> expected =
            new ExpectedRecords<Map<String, Object>>().expectAll(getOutput());

    runProgram(input, expected);
  }

  @Test
  public void testOutOfWindow() {

    EventTimeInput input = makeInputData(makeTestData(), 2);

    ExpectedRecords<Map<String, Object>> expected =
            new ExpectedRecords<Map<String, Object>>().expectAll(getOutOfWindowOutput());

    runProgram(input, expected);
  }

  private void runProgram(EventTimeInput<Map<String, Object>> input, ExpectedRecords<Map<String, Object>> expected) {
    CountParameters params = new CountParameters(InvocationGraphGenerator.makeEmptyInvocation(new CountController().declareModel()), Time.seconds(10), "field");

    CountProgram program = new CountProgram(params, true);

    DataStream<Map<String, Object>> stream = program.getApplicationLogic(createTestStream(input));

    assertStream(stream, expected);
  }

  private Collection<Map<String, Object>> getOutput() {
    List<Map<String, Object>> outRecords = new ArrayList<>();
    outRecords.add(makeOutMap("v1", 1));
    outRecords.add(makeOutMap("v2", 1));
    outRecords.add(makeOutMap("v1", 2));
    outRecords.add(makeOutMap("v3", 1));
    outRecords.add(makeOutMap("v2", 2));

    return outRecords;
  }

  private Collection<Map<String, Object>> getOutOfWindowOutput() {
    List<Map<String, Object>> outRecords = new ArrayList<>();
    outRecords.add(makeOutMap("v1", 1));
    outRecords.add(makeOutMap("v2", 1));
    outRecords.add(makeOutMap("v1", 1));
    outRecords.add(makeOutMap("v3", 1));
    outRecords.add(makeOutMap("v2", 1));

    return outRecords;
  }

  private Map<String, Object> makeOutMap(String key, Integer count) {
    Map<String, Object> outMap = new HashMap<>();
    outMap.put("value", key);
    outMap.put("count", count);
    return outMap;
  }

  private EventTimeInput<Map<String, Object>> makeInputData(List<Map<String, Object>> testData, Integer splitIndex) {
    EventTimeInputBuilder<Map<String, Object>> builder = EventTimeInputBuilder.startWith(testData.get(0));

    for (int i = 1; i < splitIndex; i++) {
      builder.emit(testData.get(i), after(1, seconds));
    }

    for (int j = splitIndex; j < testData.size(); j++) {
      builder.emit(testData.get(j), after(10, seconds));
    }

    return builder;
  }

  private List<Map<String, Object>> makeTestData() {
    List<Map<String, Object>> inMap = new ArrayList<>();
    inMap.add(makeMap("v1"));
    inMap.add(makeMap("v2"));
    inMap.add(makeMap("v1"));
    inMap.add(makeMap("v3"));
    inMap.add(makeMap("v2"));

    return inMap;
  }

  private Map<String, Object> makeMap(String s) {
    Map<String, Object> testMap = new HashMap<>();
    testMap.put("field", s);
    return testMap;
  }
}
