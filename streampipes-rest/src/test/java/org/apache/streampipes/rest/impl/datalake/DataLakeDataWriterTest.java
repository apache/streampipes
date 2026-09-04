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

package org.apache.streampipes.rest.impl.datalake;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataLakeDataWriterTest {

  @Test
  void shouldSkipNullValuesWhenBuildingEventMap() {
    var headers = List.of("timestamp", "temperature", "status");
    List<Object> row = new java.util.ArrayList<>();
    row.add(1710000000000L);
    row.add(null);
    row.add("ok");

    var eventMap = DataLakeDataWriter.toEventMap(row, headers);

    assertEquals(2, eventMap.size());
    assertEquals(1710000000000L, eventMap.get("timestamp"));
    assertEquals("ok", eventMap.get("status"));
    assertFalse(eventMap.containsKey("temperature"));
  }

  @Test
  void shouldAllowMissingFieldsWhenConfigured() {
    var expected = new HashSet<>(List.of("timestamp", "temperature", "status"));
    var actual = new HashSet<>(List.of("timestamp", "status"));

    assertTrue(DataLakeDataWriter.matchesRuntimeNames(expected, actual, true));
    assertFalse(DataLakeDataWriter.matchesRuntimeNames(expected, actual, false));
  }
}
