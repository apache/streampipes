/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.processors.aggregation.flink.processor.aggregation;

import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import io.flinkspector.datastream.input.EventTimeInput;
import io.flinkspector.datastream.input.EventTimeInputBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Test;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.*;

//@RunWith(Parameterized.class)
public class TestAggregationProgram extends DataStreamTestBase {

//  @Parameterized.Parameters
//  public static Iterable<Object[]> algorithm() {
//    return Arrays.asList(new Object[][]{
//            {"a", 1},
//            {new Sha1HashAlgorithm(), HashAlgorithmType.SHA1},
//            {new Sha2HashAlgorithm(), HashAlgorithmType.SHA2}
//    });
//  }


  @Test
  public void testAggregationProgram() {
    AggregationParameters params = makeParams();
    AggregationProgram program = new AggregationProgram(params, true);
    AggregationTestData testData = new AggregationTestData();

    DataStream<Map<String, Object>> stream = program.getApplicationLogic(createTestStream(makeInputData(testData)));

    ExpectedRecords<Map<String, Object>> expected =
            new ExpectedRecords<Map<String, Object>>().expectAll(testData.getExpectedOutput());

    assertStream(stream, expected);
  }

  private AggregationParameters makeParams() {
    return new AggregationParameters(InvocationGraphGenerator.makeEmptyInvocation(new AggregationController().declareModel()),
            AggregationType.AVG,
            1,
            Arrays.asList("sensorId"),
            "value",
            10,
            Arrays.asList("value"));
  }

  private EventTimeInput<Map<String, Object>> makeInputData(AggregationTestData testData) {
    return EventTimeInputBuilder.startWith(testData.getInput().get(0))
            .emit(testData.getInput().get(1), after(1, seconds));
  }

}
