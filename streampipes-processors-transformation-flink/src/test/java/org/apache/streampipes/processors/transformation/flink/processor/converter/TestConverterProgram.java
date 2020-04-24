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
package org.apache.streampipes.processors.transformation.flink.processor.converter;

import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
@Ignore
public class TestConverterProgram extends DataStreamTestBase {

  @Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {"1", 1, "http://www.w3.org/2001/XMLSchema#integer"},
            {"1.0", 1.0f, "http://www.w3.org/2001/XMLSchema#float"},

    });
  }

  @Parameterized.Parameter
  public String inputValue;

  @Parameterized.Parameter(1)
  public Object expectedValue;

  @Parameterized.Parameter(2)
  public String targetDatatype;

  @Test
  public void testConverterProgram() {
    FieldConverterParameters params = new FieldConverterParameters(InvocationGraphGenerator.makeEmptyInvocation(new FieldConverterController().declareModel()), "field", targetDatatype);

    FieldConverterProgram program = new FieldConverterProgram(params, true);

    DataStream<Event> stream = program.getApplicationLogic(createTestStream(makeInputData(inputValue)));

    ExpectedRecords<Event> expected =
            new ExpectedRecords<Event>().expect(makeTestData(expectedValue).get(0));

    assertStream(stream, expected);
  }

  private EventTimeInput<Event> makeInputData(String inputValue) {
    List<Event> testData = makeTestData(inputValue);
    EventTimeInputBuilder<Event> builder = EventTimeInputBuilder.startWith(testData.get(0));

    return builder;
  }

  private List<Event> makeTestData(Object inputValue) {
    List<Event> allEvents = new ArrayList<>();
    Event event = new Event();
    event.addField("field", inputValue);

    allEvents.add(event);

    return allEvents;
  }
}
