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
package org.streampipes.processors.transformation.flink.processor.rename;

import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.*;

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

    DataStream<Map<String, Object>> stream = program.getApplicationLogic(createTestStream(makeInputData()));

    ExpectedRecords<Map<String, Object>> expected =
            new ExpectedRecords<Map<String, Object>>().expectAll(getOutput(oldPropertyName, newPropertyName));

    assertStream(stream, expected);
  }

  private Collection<Map<String, Object>> getOutput(String oldPropertyName, String newPropertyName) {
    List<Map<String, Object>> allEvents = new ArrayList<>();
    Map<String, Object> outMap = makeTestData().get(0);
    Object value = outMap.get(oldPropertyName);
    outMap.remove(oldPropertyName);
    outMap.put(newPropertyName, value);
    allEvents.add(outMap);

    return allEvents;
  }

  private EventTimeInput<Map<String, Object>> makeInputData() {
    List<Map<String, Object>> testData = makeTestData();

    return EventTimeInputBuilder.startWith(testData.get(0));
  }

  private List<Map<String, Object>> makeTestData() {
    List<Map<String, Object>> allEvents = new ArrayList<>();
    Map<String, Object> event = new HashMap<>();
    event.put("fieldA", "a");
    event.put("fieldB", "b");

    allEvents.add(event);

    return allEvents;
  }
}
