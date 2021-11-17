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
package org.apache.streampipes.processors.transformation.flink.processor.rename;

import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
@Ignore
public class TestRenameProgram extends DataStreamTestBase {

  @Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
            {"fieldA", "fieldC"},
            {"fieldB", "fieldD"},

    });
  }

  @Parameterized.Parameter
  public String oldPropertyName;

  @Parameterized.Parameter(1)
  public String newPropertyName;

  @Test
  public void testConverterProgram() {
    FieldRenamerParameters params = new FieldRenamerParameters(InvocationGraphGenerator.makeEmptyInvocation(new FieldRenamerController().declareModel()), oldPropertyName, newPropertyName);

    FieldRenamerProgram program = new FieldRenamerProgram(params, true);

    DataStream<Event> stream = program.getApplicationLogic(createTestStream(makeInputData()));

    ExpectedRecords<Event> expected =
            new ExpectedRecords<Event>().expectAll(getOutput(oldPropertyName, newPropertyName));

    assertStream(stream, expected);
  }

  private Collection<Event> getOutput(String oldPropertyName, String newPropertyName) {
    List<Event> allEvents = new ArrayList<>();
    Event outMap = makeTestData().get(0);
    Object value = outMap.getFieldBySelector(oldPropertyName);
    outMap.removeFieldBySelector(oldPropertyName);
    outMap.addField(newPropertyName, value);
    allEvents.add(outMap);

    return allEvents;
  }

  private EventTimeInput<Event> makeInputData() {
    List<Event> testData = makeTestData();

    return EventTimeInputBuilder.startWith(testData.get(0));
  }

  private List<Event> makeTestData() {
    List<Event> allEvents = new ArrayList<>();
    Event event = new Event();
    event.addField("fieldA", "a");
    event.addField("fieldB", "b");

    allEvents.add(event);

    return allEvents;
  }
}
