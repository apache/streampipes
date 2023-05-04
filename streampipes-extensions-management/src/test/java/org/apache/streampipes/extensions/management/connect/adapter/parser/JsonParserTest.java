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

package org.apache.streampipes.extensions.management.connect.adapter.parser;

import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.model.connect.adapter.IEventCollector;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;

import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JsonParserTest extends ParserTest {

  JsonParser parser = new JsonParser();

  InputStream event = toStream("{\"k1\": \"v1\", \"k2\": 2}");


  @Test
  public void getGuessSchema() {
    var expected = GuessSchemaBuilder.create()
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, K1)
            .description("")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, K2)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .description("")
            .build())
        .sample(K1, "v1")
        .sample(K2, 2)
        .build();

    var result = parser.getGuessSchema(event);

    assertEquals(expected, result);
  }


  @Test
  public void parse() {
    var mockEventCollector = mock(IEventCollector.class);
    parser.parse(event, mockEventCollector);

    Map<String, Object> expectedEvent = new HashMap<>();
    expectedEvent.put(K1, "v1");
    expectedEvent.put(K2, 2);
    verify(mockEventCollector).collect(expectedEvent);
  }

  @Test(expected = ParseException.class)
  public void parseNullCheck() {
    parser.parse(null, mock(IEventCollector.class));
  }

  @Test(expected = ParseException.class)
  public void parseEmptyString() {
    parser.parse(toStream(""), mock(IEventCollector.class));
  }

  @Test(expected = ParseException.class)
  public void parseInvalidJson() {
    parser.parse(toStream("{\"f\","), mock(IEventCollector.class));
  }

}