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

import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithm;

import java.util.*;

public class TestFieldHasherUtils {

  public static List<String> testData = Arrays.asList("test1", "test2", "test3", "test4");

  public static List<Map<String, Object>> makeTestData(boolean originalValue, HashAlgorithm hashAlgorithm) {
    List<Map<String, Object>> data = new ArrayList<>();
    for(int i = 0; i < 3; i++) {
      Map<String, Object> testData = new HashMap<>();
      testData.put("timestamp", i);
      testData.put("field", originalValue ? testData.get(i) : hashAlgorithm.toHashValue(testData.get(i)));
      data.add(testData);
    }
    return data;
  }

}
