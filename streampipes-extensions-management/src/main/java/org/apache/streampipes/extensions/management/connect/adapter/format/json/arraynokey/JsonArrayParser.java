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

package org.apache.streampipes.extensions.management.connect.adapter.format.json.arraynokey;


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.connect.EmitBinaryEvent;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.format.util.JsonEventProperty;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Parser;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonArrayParser extends Parser {

  Logger logger = LoggerFactory.getLogger(JsonArrayParser.class);


  @Override
  public Parser getInstance(FormatDescription formatDescription) {
    return new JsonArrayParser();
  }

  public JsonArrayParser() {
  }

  @Override
  public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) throws ParseException {
    JsonParserFactory factory = Json.createParserFactory(null);
    String s = data.toString();
    JsonParser jsonParser = factory.createParser(data);

    // Find first event in array
    boolean foundBeginning = false;
    while (jsonParser.hasNext() && !foundBeginning) {
      JsonParser.Event event = jsonParser.next();
      if (event.equals(JsonParser.Event.START_ARRAY)) {
        foundBeginning = true;
      }
    }
    if (!foundBeginning) {
      throw new ParseException("No JSON Array found");
    }

    // Parse all events
    JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();
    boolean isEvent = true;
    boolean result = true;
    int objectCount = 0;
    while (jsonParser.hasNext() && isEvent && result) {
      Map<String, Object> objectMap = parseObject(jsonParser, true, 1);
      if (objectMap != null) {
        byte[] tmp = new byte[0];
        try {
          tmp = jsonDefinition.fromMap(objectMap);
        } catch (SpRuntimeException e) {
          throw new ParseException(e.toString());
        }
//                    handleEvent(new EventObjectEndEvent(parseObject(tmp)));
        // TODO decide what happens id emit returns false
        result = emitBinaryEvent.emit(tmp);
      } else {
        isEvent = false;
      }

    }
  }

  @Override
  public EventSchema getEventSchema(List<byte[]> oneEvent) {
    EventSchema resultSchema = new EventSchema();

    JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();

    Map<String, Object> exampleEvent = null;

    try {
      exampleEvent = jsonDefinition.toMap(oneEvent.get(0));
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }

    for (Map.Entry<String, Object> entry : exampleEvent.entrySet()) {
//            System.out.println(entry.getKey() + "/" + entry.getValue());
      EventProperty p = JsonEventProperty.getEventProperty(entry.getKey(), entry.getValue());

      resultSchema.addEventProperty(p);

    }

    return resultSchema;
  }


  public Map<String, Object> parseObject(JsonParser jsonParser, boolean root, int start) {
    // this variable is needed to skip the first object start
    String mapKey = "";
    Map<String, Object> result = new HashMap<>();
    List<Object> arr = null;

    while (jsonParser.hasNext()) {
      JsonParser.Event event = jsonParser.next();
      switch (event) {
        case KEY_NAME:
          mapKey = jsonParser.getString();
          logger.debug("key: " + mapKey);
          break;
        case START_OBJECT:
          if (start == 0) {
            Map<String, Object> ob = parseObject(jsonParser, false, 0);
            if (arr == null) {
              result.put(mapKey, ob);
            } else {
              arr.add(ob);
            }
          } else {
            start--;
          }
          logger.debug("start object");
          break;
        case END_OBJECT:

          logger.debug("end object");
          return result;
        case START_ARRAY:
          arr = new ArrayList<>();
          logger.debug("start array");
          break;
        case END_ARRAY:
          // Check if just the end of array is entered
          if (result.keySet().size() == 0 && mapKey.equals("")) {
            return null;
          }
          result.put(mapKey, arr);
          arr = null;
          logger.debug("end array");
          break;
        case VALUE_TRUE:
          if (arr == null) {
            result.put(mapKey, true);
          } else {
            arr.add(true);
          }
          logger.debug("value: true");
          break;
        case VALUE_FALSE:
          if (arr == null) {
            result.put(mapKey, false);
          } else {
            arr.add(false);
          }
          logger.debug("value: false");
          break;
        case VALUE_STRING:
          if (arr == null) {
            result.put(mapKey, jsonParser.getString());
          } else {
            arr.add(jsonParser.getString());
          }
          logger.debug("value string: " + jsonParser.getString());
          break;
        case VALUE_NUMBER:
          if (arr == null) {
            result.put(mapKey, jsonParser.getBigDecimal());
          } else {
            arr.add(jsonParser.getBigDecimal());
          }
          logger.debug("value number: " + jsonParser.getBigDecimal());
          break;
        case VALUE_NULL:
          logger.debug("value null");
          break;
        default:
          logger.error("Error: " + event + " event is not handled in the JSON parser");
          break;
      }
    }

    return result;
  }
}
