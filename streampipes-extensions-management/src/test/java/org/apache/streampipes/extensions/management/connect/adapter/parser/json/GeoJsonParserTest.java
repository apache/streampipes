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
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.Geo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GeoJsonParserTest {

  GeoJsonParser parser = new GeoJsonParser();

  Map<String, Object> event = Map.of(
      "type", "Feature",
      "geometry", Map.of(
          "type", "Point",
          "coordinates", new Double[]{6.946535, 51.437344}
      ),
      "properties", Map.of(
          "temperature", 5.0
      )
  );

  @Test
  public void getGuessSchema() {
    var expected = GuessSchemaBuilder.create()
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "longitude")
            .domainProperty(Geo.LNG)
            .description("")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "latitude")
            .domainProperty(Geo.LAT)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .description("")
            .build())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "temperature")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .description("")
            .build())
        .sample("longitude", 6.946535)
        .sample("latitude", 51.437344)
        .sample("temperature", 5.0)
        .build();

    var result = parser.getGuessSchema(toEvent(event));

    assertEquals(expected, result);
  }

  @Test
  public void parse() {
    var mockEventHandler = mock(IParserEventHandler.class);
    parser.parse(toEvent(event), mockEventHandler);

    Map<String, Object> expectedEvent = new HashMap<>();
    expectedEvent.put("latitude", 51.437344);
    expectedEvent.put("temperature", 5.0);
    expectedEvent.put("longitude", 6.946535);
    verify(mockEventHandler, times(1)).handle(expectedEvent);
  }


  private InputStream toEvent(Map<String, Object> event) {
    try {
      var s = new ObjectMapper().writeValueAsString(event);
      return IOUtils.toInputStream(s, StandardCharsets.UTF_8);
    } catch (JsonProcessingException e) {
      fail("Could not convert event to string: " + event);
      return null;
    }
  }
}
