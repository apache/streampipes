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


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.connect.EmitBinaryEvent;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.format.util.JsonEventProperty;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Parser;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.connect.guess.AdapterGuessInfo;
import org.apache.streampipes.model.connect.guess.GuessTypeInfo;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonObjectParser extends Parser {

  private static final Logger logger = LoggerFactory.getLogger(JsonObjectParser.class);

  private ObjectMapper mapper;
  private JsonDataFormatDefinition jsonDefinition;

  @Override
  public Parser getInstance(FormatDescription formatDescription) {
    return new JsonObjectParser();
  }

  /**
   * Use this constructor when just a specific key of the object should be parsed
   */
  public JsonObjectParser() {
    mapper = new ObjectMapper();
    jsonDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) throws ParseException {

    try {
      Map<String, Object> map = mapper.readValue(data, HashMap.class);
      emitBinaryEvent.emit(jsonDefinition.fromMap(map));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }

  }

  @Override
  public EventSchema getEventSchema(List<byte[]> oneEvent) {
    return getSchemaAndSample(oneEvent).getEventSchema();
  }

  @Override
  public boolean supportsPreview() {
    return true;
  }

  @Override
  public AdapterGuessInfo getSchemaAndSample(List<byte[]> eventSample) throws ParseException {
    EventSchema resultSchema = new EventSchema();

    JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();


    Map<String, Object> exampleEvent = null;

    try {
      exampleEvent = jsonDefinition.toMap(eventSample.get(0));
      var sample = exampleEvent
          .entrySet()
          .stream()
          .collect(Collectors.toMap(Map.Entry::getKey, e ->
              new GuessTypeInfo(e.getValue().getClass().getCanonicalName(), e.getValue())));

      for (Map.Entry<String, Object> entry : exampleEvent.entrySet()) {
        EventProperty p = JsonEventProperty.getEventProperty(entry.getKey(), entry.getValue());

        resultSchema.addEventProperty(p);
      }

      return new AdapterGuessInfo(resultSchema, sample);
    } catch (SpRuntimeException e) {
      throw new ParseException("Could not serialize event, did you choose the correct format?", e);
    }
  }
}
