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

package org.apache.streampipes.dataexplorer.query.writer;

import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParams;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TestConfiguredJsonOutputWriter extends TestConfiguredOutputWriter {

  private static final String Expected = "[{\"time\": 1668578077051,\"string\": \"test\",\"number\": 1}"
      + ",{\"time\": 1668578127050,\"string\": \"test2\",\"number\": 2}]";

  @Test
  public void testJsonOutputWriter() throws IOException {
    var writer = new ConfiguredJsonOutputWriter();
    writer.configure(new ProvidedRestQueryParams(null, new HashMap<>()), true);

    try (var outputStream = new ByteArrayOutputStream()) {
      writer.beforeFirstItem(outputStream);

      for (int i = 0; i < rows.size(); i++) {
        writer.writeItem(outputStream, rows.get(i), columns, i == 0);
      }

      writer.afterLastItem(outputStream);
      assertEquals(Expected, outputStream.toString(StandardCharsets.UTF_8));
    }
  }
}
