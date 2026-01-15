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

package org.apache.streampipes.extensions.management.connect.adapter.parser.json;

import org.apache.streampipes.extensions.api.connect.IParserEventHandler;
import org.apache.streampipes.extensions.management.connect.adapter.parser.ParserTest;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class JsonArrayKeyParserTest extends ParserTest {

  JsonArrayKeyParser parser = new JsonArrayKeyParser("arr");

  InputStream event = toStream("{\"arr\":[{\"k1\": \"v1\", \"k2\": 2},{\"k1\": \"v2\", \"k2\": 3}]}");

  @Test
  public void parse() {
    var mockEventHandler = mock(IParserEventHandler.class);
    parser.parse(event, mockEventHandler);

    Map<String, Object> expectedEvent = new HashMap<>();
    expectedEvent.put(K1, "v1");
    expectedEvent.put(K2, 2);
    verify(mockEventHandler, times(1)).handle(expectedEvent);

    expectedEvent.put(K1, "v2");
    expectedEvent.put(K2, 3);
    verify(mockEventHandler, times(1)).handle(expectedEvent);
  }

  @Test
  public void parseEmptyArray() {
    var mockEventHandler = mock(IParserEventHandler.class);
    parser.parse(toStream("{\"arr\":[]}"), mockEventHandler);
  }
}
