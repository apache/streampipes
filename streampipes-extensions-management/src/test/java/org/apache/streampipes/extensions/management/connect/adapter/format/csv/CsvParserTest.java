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

package org.apache.streampipes.extensions.management.connect.adapter.format.csv;


import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvParserTest {

  @Test
  public void getSchemaAndSampleTimestamp() {
    var csvValue = List.of(
        "timestamp,value".getBytes(StandardCharsets.UTF_8),
        "1667904471000,1".getBytes(StandardCharsets.UTF_8)
    );

    var csvParser = new CsvParser(",", true);
    var result = csvParser.getSchemaAndSample(csvValue);

    assertEquals(result.getEventPreview().size(), 1);
    assertEquals(1667904471000.0, result.getEventPreview().get(0).get("timestamp").getValue());
  }

}