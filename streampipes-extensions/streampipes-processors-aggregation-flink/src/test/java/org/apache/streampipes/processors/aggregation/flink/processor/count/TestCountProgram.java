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
package org.apache.streampipes.processors.aggregation.flink.processor.count;

import org.apache.streampipes.container.config.ConfigExtractor;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.aggregation.flink.AggregationFlinkInit;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;

import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Ignore
public class TestCountProgram extends DataStreamTestBase {

  @Test
  public void testCountProgram() {

    EventTimeInput input = makeInputData(makeTestData(), makeTestData().size());

    ExpectedRecords<Event> expected =
        new ExpectedRecords<Event>().expectAll(getOutput());

    runProgram(input, expected);
  }

  @Test
  public void testOutOfWindow() {

    EventTimeInput input = makeInputData(makeTestData(), 2);

    ExpectedRecords<Event> expected =
        new ExpectedRecords<Event>().expectAll(getOutOfWindowOutput());

    runProgram(input, expected);
  }

  private void runProgram(EventTimeInput<Event> input, ExpectedRecords<Event>
      expected) {
    CountParameters params =
        new CountParameters(InvocationGraphGenerator.makeEmptyInvocation(new CountController().declareModel()), 10,
            "SECONDS", "field");
    ConfigExtractor configExtractor = ConfigExtractor.from(AggregationFlinkInit.SERVICE_GROUP);
    CountProgram program = new CountProgram(params, configExtractor, null);

    DataStream<Event> stream = program.getApplicationLogic(createTestStream(input));

    assertStream(stream, expected);
  }

  private Collection<Event> getOutput() {
    List<Event> outRecords = new ArrayList<>();
    outRecords.add(makeOutMap("v1", 1));
    outRecords.add(makeOutMap("v2", 1));
    outRecords.add(makeOutMap("v1", 2));
    outRecords.add(makeOutMap("v3", 1));
    outRecords.add(makeOutMap("v2", 2));

    return outRecords;
  }

  private Collection<Event> getOutOfWindowOutput() {
    List<Event> outRecords = new ArrayList<>();
    outRecords.add(makeOutMap("v1", 1));
    outRecords.add(makeOutMap("v2", 1));
    outRecords.add(makeOutMap("v1", 1));
    outRecords.add(makeOutMap("v3", 1));
    outRecords.add(makeOutMap("v2", 1));

    return outRecords;
  }

  private Event makeOutMap(String key, Integer count) {
    Event outEvent = new Event();
    outEvent.addField("value", key);
    outEvent.addField("count", count);
    return outEvent;
  }

  private EventTimeInput<Event> makeInputData(List<Event> testData, Integer
      splitIndex) {
    EventTimeInputBuilder<Event> builder = EventTimeInputBuilder.startWith(testData.get(0));

    for (int i = 1; i < splitIndex; i++) {
      builder.emit(testData.get(i), after(1, seconds));
    }

    for (int j = splitIndex; j < testData.size(); j++) {
      builder.emit(testData.get(j), after(10, seconds));
    }

    return builder;
  }

  private List<Event> makeTestData() {
    List<Event> inEvent = new ArrayList<>();
    inEvent.add(makeMap("v1"));
    inEvent.add(makeMap("v2"));
    inEvent.add(makeMap("v1"));
    inEvent.add(makeMap("v3"));
    inEvent.add(makeMap("v2"));

    return inEvent;
  }

  private Event makeMap(String s) {
    Event testEvent = new Event();
    testEvent.addField("field", s);
    return testEvent;
  }
}
