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

import io.flinkspector.datastream.DataStreamTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithm;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.Md5HashAlgorithm;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.Sha1HashAlgorithm;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.Sha2HashAlgorithm;
import org.streampipes.processors.transformation.flink.utils.DummyCollector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestFieldHasher extends DataStreamTestBase {

  @Parameterized.Parameters
  public static Iterable<Object[]> algorithm() {
    return Arrays.asList(new Object[][]{
            {"field1", "field2", "1"},
            {"field1", "field2", "1"},
            {"field1", "field2", 3},
    });
  }

  @Parameterized.Parameter
  public String fieldToHash;

  @Parameterized.Parameter(1)
  public String fieldNotToHash;

  @Parameterized.Parameter(2)
  public Object valueToHash;

  private Map<String, Object> inputMap;
  private Map<String, Object> expectedMap;

  @Before
  public void generateMaps() {
    inputMap = new HashMap<>();
    inputMap.put(fieldToHash, valueToHash);
    inputMap.put(fieldNotToHash, valueToHash);

    expectedMap = new HashMap<>();
    expectedMap.put(fieldToHash, valueToHash);
    expectedMap.put(fieldNotToHash, valueToHash);
  }

  @Test
  public void testFieldHasherMd5() {
    HashAlgorithm algorithm = new Md5HashAlgorithm();
    FieldHasher fieldHasher = new FieldHasher(fieldToHash, algorithm);
    expectedMap.put(fieldToHash, algorithm.toHashValue(valueToHash));

    testFieldHasher(fieldHasher);

  }

  @Test
  public void testFieldHasherSha1() {
    HashAlgorithm algorithm = new Sha1HashAlgorithm();
    FieldHasher fieldHasher = new FieldHasher(fieldToHash, algorithm);
    expectedMap.put(fieldToHash, algorithm.toHashValue(valueToHash));

    testFieldHasher(fieldHasher);

  }

  @Test
  public void testFieldHasherSha2() {
    HashAlgorithm algorithm = new Sha2HashAlgorithm();
    FieldHasher fieldHasher = new FieldHasher(fieldToHash, algorithm);
    expectedMap.put(fieldToHash, algorithm.toHashValue(valueToHash));

    testFieldHasher(fieldHasher);

  }

  private void testFieldHasher(FieldHasher fieldHasher) {
    DummyCollector collector = new DummyCollector();
    try {
      fieldHasher.flatMap(inputMap, collector);

      List<Map<String, Object>> output = collector.getOutput();

      if (output.size() != 1) {
        fail();
      } else {
        assertEquals(expectedMap, output.get(0));
      }
    } catch (Exception e) {
      fail();
    }
  }

}
