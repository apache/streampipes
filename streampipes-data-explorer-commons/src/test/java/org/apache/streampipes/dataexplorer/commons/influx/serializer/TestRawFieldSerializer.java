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

package org.apache.streampipes.dataexplorer.commons.influx.serializer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestRawFieldSerializer {
  private RawFieldSerializer rawFieldSerializer = new RawFieldSerializer();
  private Map<String, Object> primitives = new HashMap<>();

  public TestRawFieldSerializer() {
    primitives.put("Integer", 1);
    primitives.put("Long", 1L);
    primitives.put("Float", 1.0f);
    primitives.put("Double", 1.0d);
    primitives.put("Boolean", true);
    primitives.put("String", "1");
  }

  // Test able to deserialize back the original data
  @Test
  public void testRawFieldSerializerListInMap() {
    var rawListField = new ArrayList<>();
    rawListField.addAll(primitives.values());

    var rawNestedField = new HashMap<String, Object>();
    rawNestedField.putAll(primitives);
    rawNestedField.put("List", rawListField);

    var json = rawFieldSerializer.serialize(rawNestedField);

    assertEquals(rawNestedField, rawFieldSerializer.deserialize(json));
  }

  @Test
  public void testRawFieldSerializerMapInList() {
    var rawNestedField = new HashMap<String, Object>();
    rawNestedField.putAll(primitives);

    var rawListField = new ArrayList<>();
    rawListField.addAll(primitives.values());
    rawListField.add(rawNestedField);

    var json = rawFieldSerializer.serialize(rawListField);

    assertEquals(rawListField, rawFieldSerializer.deserialize(json));
  }
}
