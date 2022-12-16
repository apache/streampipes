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
package org.apache.streampipes.processors.transformation.flink.processor.hasher;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFieldHasherUtils {

  public static List<String> testData = Arrays.asList("test1", "test2", "test3", "test4");

  public static List<Event> makeTestData(boolean originalValue, HashAlgorithm hashAlgorithm) {
    List<Event> data = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Event event = new Event();
      event.addField("timestamp", i);
      event.addField("field", originalValue ? testData.get(i) : hashAlgorithm.toHashValue
          (testData
              .get(i)));
      data.add(event);
    }
    return data;
  }

}
