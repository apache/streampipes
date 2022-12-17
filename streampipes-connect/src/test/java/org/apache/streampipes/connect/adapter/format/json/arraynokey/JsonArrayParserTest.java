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

package org.apache.streampipes.connect.adapter.format.json.arraynokey;


import org.apache.streampipes.connect.api.exception.AdapterException;

import com.google.gson.JsonArray;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.streampipes.connect.adapter.TestUtils.getJsonArrayWithThreeElements;
import static org.apache.streampipes.connect.adapter.TestUtils.makeJsonObject;
import static org.junit.Assert.assertEquals;

public class JsonArrayParserTest {

  @Test
  public void parseOneEvent() throws AdapterException {

    String jo = getJsonArrayWithThreeElements();

    JsonArrayParser parser = new JsonArrayParser();

    List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 1);

    assertEquals(parsedEvent.size(), 1);
    String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

    assertEquals(parsedStringEvent, "{\"one\":1}");
  }


  @Test
  public void parseThreeEvents() throws AdapterException {

    String jo = getJsonArrayWithThreeElements();
    JsonArrayParser parser = new JsonArrayParser();


    List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 3);

    assertEquals(3, parsedEvent.size());
    String parsedStringEventOne = new String(parsedEvent.get(0), StandardCharsets.UTF_8);
    String parsedStringEventTwo = new String(parsedEvent.get(1), StandardCharsets.UTF_8);
    String parsedStringEventThree = new String(parsedEvent.get(2), StandardCharsets.UTF_8);

    assertEquals("{\"one\":1}", parsedStringEventOne);
    assertEquals("{\"one\":2}", parsedStringEventTwo);
    assertEquals("{\"one\":3}", parsedStringEventThree);
  }


  @Test
  public void parseMoreThenExist() throws AdapterException {

    JsonArray jsonArray = new JsonArray();
    jsonArray.add(makeJsonObject("one", 1));

    JsonArrayParser parser = new JsonArrayParser();

    List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jsonArray.toString()), 10);

    assertEquals(1, parsedEvent.size());
    String parsedStringEventOne = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

    assertEquals("{\"one\":1}", parsedStringEventOne);
  }

  private InputStream getInputStream(String s) {
    return IOUtils.toInputStream(s, "UTF-8");
  }

}
