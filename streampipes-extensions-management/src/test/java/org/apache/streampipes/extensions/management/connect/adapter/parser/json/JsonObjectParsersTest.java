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

import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.api.connect.IParserEventHandler;
import org.apache.streampipes.extensions.management.connect.adapter.parser.ParserTest;
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

public class JsonObjectParsersTest extends ParserTest {

  JsonObjectParser parser = new JsonObjectParser();

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
    var mockEventHandler = mock(IParserEventHandler.class);
    parser.parse(event, mockEventHandler);

    Map<String, Object> expectedEvent = new HashMap<>();
    expectedEvent.put(K1, "v1");
    expectedEvent.put(K2, 2);
    verify(mockEventHandler).handle(expectedEvent);
  }

  @Test(expected = ParseException.class)
  public void parseNullCheck() {
    parser.parse(null, mock(IParserEventHandler.class));
  }

  @Test(expected = ParseException.class)
  public void parseEmptyString() {
    parser.parse(toStream(""), mock(IParserEventHandler.class));
  }

  @Test(expected = ParseException.class)
  public void parseInvalidJson() {
    parser.parse(toStream("{\"f\","), mock(IParserEventHandler.class));
  }

}
