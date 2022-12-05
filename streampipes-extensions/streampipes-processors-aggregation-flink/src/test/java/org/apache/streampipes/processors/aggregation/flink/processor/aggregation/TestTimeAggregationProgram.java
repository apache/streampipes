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
package org.apache.streampipes.processors.aggregation.flink.processor.aggregation;

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

import java.util.Arrays;

@Ignore
//@RunWith(Parameterized.class)
public class TestTimeAggregationProgram extends DataStreamTestBase {

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
    ConfigExtractor configExtractor = ConfigExtractor.from(AggregationFlinkInit.SERVICE_GROUP);
    AggregationProgram program = new AggregationProgram(params, configExtractor, null);
    AggregationTestData testData = new AggregationTestData();

    DataStream<Event> stream = program.getApplicationLogic(createTestStream(makeInputData
        (testData)));

    ExpectedRecords<Event> expected =
        new ExpectedRecords<Event>().expectAll(testData.getExpectedOutput());

    assertStream(stream, expected);
  }

  private AggregationParameters makeParams() {
    return new AggregationParameters(
        InvocationGraphGenerator.makeEmptyInvocation(new AggregationController().declareModel()),
        AggregationType.AVG,
        1,
        Arrays.asList("sensorId"),
        Arrays.asList("value"),
        10,
        Arrays.asList("value"),
        true);
  }

  private EventTimeInput<Event> makeInputData(AggregationTestData testData) {
    return EventTimeInputBuilder.startWith(testData.getInput().get(0))
        .emit(testData.getInput().get(1), after(1, seconds));
  }

}
