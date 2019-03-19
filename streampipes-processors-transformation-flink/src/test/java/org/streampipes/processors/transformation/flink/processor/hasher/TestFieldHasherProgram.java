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
package org.streampipes.processors.transformation.flink.processor.hasher;

import static org.streampipes.processors.transformation.flink.processor.hasher.TestFieldHasherUtils.makeTestData;

import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithm;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithmType;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.Md5HashAlgorithm;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.Sha1HashAlgorithm;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.Sha2HashAlgorithm;
import org.streampipes.test.generator.InvocationGraphGenerator;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class TestFieldHasherProgram extends DataStreamTestBase {

  @Parameterized.Parameters
  public static Iterable<Object[]> algorithm() {
    return Arrays.asList(new Object[][] {
            {new Md5HashAlgorithm(), HashAlgorithmType.MD5},
            {new Sha1HashAlgorithm(), HashAlgorithmType.SHA1},
            {new Sha2HashAlgorithm(), HashAlgorithmType.SHA2}
    });
  }

  @Parameterized.Parameter()
  public HashAlgorithm hashAlgorithm;

  @Parameterized.Parameter(1)
  public HashAlgorithmType hashAlgorithmType;

  @Test
  public void testFieldHasherProgram() {

    FieldHasherParameters params = makeParams();
    FieldHasherProgram program = new FieldHasherProgram(params);

    DataStream<Event> stream = program.getApplicationLogic(createTestStream(makeTestData(true, hashAlgorithm)));

    ExpectedRecords<Event> expected =
            new ExpectedRecords<Event>().expectAll(makeTestData(false, hashAlgorithm));

    assertStream(stream, expected);
  }

  private FieldHasherParameters makeParams() {
    return new FieldHasherParameters(InvocationGraphGenerator.makeEmptyInvocation(new FieldHasherController().declareModel()), "field", hashAlgorithmType);
  }



}
