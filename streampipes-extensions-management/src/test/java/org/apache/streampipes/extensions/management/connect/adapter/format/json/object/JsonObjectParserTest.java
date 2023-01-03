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

package org.apache.streampipes.extensions.management.connect.adapter.format.json.object;

import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.streampipes.extensions.management.connect.adapter.TestUtils.makeJsonObject;
import static org.junit.Assert.assertEquals;

public class JsonObjectParserTest {

  @Test
  public void parseOneEvent() {

    JsonObject jo = makeJsonObject("one", 1);

    JsonObjectParser parser = new JsonObjectParser();

    List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo.toString()), 1);

    assertEquals(parsedEvent.size(), 1);
    String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

    assertEquals(parsedStringEvent, "{\"one\":1}");
  }

  @Test
  public void parseMoreThenExist() {

    JsonObject jo = makeJsonObject("one", 1);

    JsonObjectParser parser = new JsonObjectParser();

    List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo.toString()), 10);

    assertEquals(1, parsedEvent.size());
    String parsedStringEventOne = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

    assertEquals("{\"one\":1}", parsedStringEventOne);
  }

  private InputStream getInputStream(String s) {
    return IOUtils.toInputStream(s, "UTF-8");
  }
}
