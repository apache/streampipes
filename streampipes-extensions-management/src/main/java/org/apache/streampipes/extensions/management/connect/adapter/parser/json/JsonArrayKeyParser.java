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
import org.apache.streampipes.model.connect.guess.GuessSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class JsonArrayKeyParser extends JsonParser {
  private String key;

  public JsonArrayKeyParser(String key) {
    this.key = key;
  }

  private static final Logger LOG = LoggerFactory.getLogger(JsonArrayKeyParser.class);

  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) throws ParseException {
    var event = getEvents(inputStream).get(0);
    return parserUtils.getGuessSchema(event);
  }

  @Override
  public void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException {
    try {
      var events = getEvents(inputStream);
      events.forEach(event -> {
        handler.handle(event);
      });
    } catch (ParseException e) {
      LOG.error("Could not parse json event", e);
    }

  }

  private List<Map<String, Object>> getEvents(InputStream inputStream) {
    var event = toMap(inputStream, Map.class);

    if (event.containsKey(key)) {
      var list = event.get(key);
      if (list instanceof List) {
        if (((List<?>) list).size() > 0) {
          if (((List<?>) list).get(0) instanceof Map<?, ?>) {
            return (List<Map<String, Object>>) list;
          } else {
            throw new ParseException("The content of the array must be a json object. It was: %s".formatted(list));
          }
        } else {
          throw new ParseException("The selected array is empty."
              .formatted(list));
        }
      } else {
        throw new ParseException("Please select the key of an array field. The object: %s was selected instead"
            .formatted(list));
      }
    } else {
      throw new ParseException("The selected key '%s' could not be found in the json object".formatted(key));
    }
  }
}
