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
import org.apache.streampipes.extensions.api.connect.IParserEventHandler;
import org.apache.streampipes.model.connect.guess.GuessSchema;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CsvParserTest extends ParserTest {

  protected GuessSchema sampleExpected = GuessSchemaBuilder.create()
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
      .sample(K2, 2.0f)
      .build();

  @Test
  public void getGuessSchemaWithHeaderAndComma() {
    var parser = new CsvParser(true, ',');

    var event = toStream("k1,k2\nv1,2");

    var result = parser.getGuessSchema(event);

    assertEquals(sampleExpected, result);
  }

  @Test
  public void getGuessSchemaWithHeaderAndSemicolon() {
    var parser = new CsvParser(true, ';');

    var event = toStream("k1;k2\nv1;2");

    var result = parser.getGuessSchema(event);

    assertEquals(sampleExpected, result);
  }

  @Test
  public void getGuessSchemaWithoutHeader() {
    var expected = GuessSchemaBuilder.create()
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "key_1")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .description("")
            .build())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, "key_0")
            .description("")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("key_1", 2.0f)
        .sample("key_0", "v1")
        .build();

    var parser = new CsvParser(false, ',');

    InputStream event = toStream("v1,2");

    var result = parser.getGuessSchema(event);

    assertEquals(expected, result);
  }

  @Test(expected = ParseException.class)
  public void getGuessSchemaParseException() {
    var parser = new CsvParser(true, ';');

    InputStream event = toStream("k1;k2\nv1");

    parser.getGuessSchema(event);
  }

  @Test(expected = ParseException.class)
  public void getGuessSchemaNoValuesParseException() {
    var parser = new CsvParser(true, ';');
    InputStream event = toStream("k1;k2");
    parser.getGuessSchema(event);
  }

  @Test
  public void parseOneEvent() {
    var event = toStream("k1;k2\nv1;2");
    var mockEventHandler = mock(IParserEventHandler.class);

    var parser = new CsvParser(true, ';');
    parser.parse(event, mockEventHandler);

    Map<String, Object> expectedEvent = new HashMap<>();
    expectedEvent.put(K1, "v1");
    expectedEvent.put(K2, 2);
    verify(mockEventHandler).handle(expectedEvent);
  }

  @Test
  public void parseEventWithTimestamp() {
    var event = toStream("timestamp\n1683783150548");
    var mockEventHandler = mock(IParserEventHandler.class);

    var parser = new CsvParser(true, ';');
    parser.parse(event, mockEventHandler);

    Map<String, Object> expectedEvent = new HashMap<>();
    expectedEvent.put("timestamp", 1683783150548L);
    verify(mockEventHandler).handle(expectedEvent);
  }

  @Test
  public void parseMultiplEvent() {
    var event = toStream("k1;k2\nv1;2\nv2;3");
    var mockEventHandler = mock(IParserEventHandler.class);

    var parser = new CsvParser(true, ';');
    parser.parse(event, mockEventHandler);

    Map<String, Object> expectedEvent = new HashMap<>();
    expectedEvent.put(K1, "v1");
    expectedEvent.put(K2, 2);
    verify(mockEventHandler, times(1)).handle(expectedEvent);

    expectedEvent.put(K1, "v2");
    expectedEvent.put(K2, 3);
    verify(mockEventHandler, times(1)).handle(expectedEvent);
  }

}
