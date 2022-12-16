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

package org.apache.streampipesdataexplorer.v4.query.writer.item;

import org.apache.streampipes.dataexplorer.v4.query.writer.item.JsonItemWriter;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJsonItemWriter extends TestItemWriter {

  private static final String Expected = "{\"time\": 1668578077051,\"string\": \"test\",\"number\": 1}";

  @Test
  public void testJsonWriter() {
    var writer = new JsonItemWriter(new Gson());

    String result = writer.createItem(row, columns);

    assertEquals(Expected, result);
  }
}
