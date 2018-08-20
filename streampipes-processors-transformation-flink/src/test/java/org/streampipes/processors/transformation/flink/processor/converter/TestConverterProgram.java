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
package org.streampipes.processors.transformation.flink.processor.converter;

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

@RunWith(Parameterized.class)
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

    DataStream<Map<String, Object>> stream = program.getApplicationLogic(createTestStream(makeInputData(inputValue)));

    ExpectedRecords<Map<String, Object>> expected =
            new ExpectedRecords<Map<String, Object>>().expectAll(getOutput(expectedValue));

    assertStream(stream, expected);
  }

  private Collection<Map<String, Object>> getOutput(Object expectedValue) {
    List<Map<String, Object>> allEvents = new ArrayList<>();
    Map<String, Object> outMap = new HashMap<>();
    outMap.put("field", expectedValue);

    return allEvents;
  }

  private EventTimeInput<Map<String, Object>> makeInputData(String inputValue) {
    List<Map<String, Object>> testData = makeTestData(inputValue);
    EventTimeInputBuilder<Map<String, Object>> builder = EventTimeInputBuilder.startWith(testData.get(0));

    return builder;
  }

  private List<Map<String, Object>> makeTestData(String inputValue) {
    List<Map<String, Object>> allEvents = new ArrayList<>();
    Map<String, Object> event = new HashMap<>();
    event.put("field", inputValue);

    allEvents.add(event);

    return allEvents;
  }
}
