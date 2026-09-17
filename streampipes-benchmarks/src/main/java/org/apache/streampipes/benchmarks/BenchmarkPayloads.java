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


package org.apache.streampipes.benchmarks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BenchmarkPayloads {

  private BenchmarkPayloads() {
  }

  public static Map<String, Object> create(int fields, int depth) {
    Map<String, Object> values = new LinkedHashMap<>();
    for (int i = 0; i < fields; i++) {
      values.put("field" + i, i + 0.5);
    }
    values.put("samples", List.of(1.0, 2.0, 3.0, 4.0));
    for (int i = 0; i < depth; i++) {
      values = new LinkedHashMap<>(Map.of("nested", values));
    }
    return values;
  }
}
