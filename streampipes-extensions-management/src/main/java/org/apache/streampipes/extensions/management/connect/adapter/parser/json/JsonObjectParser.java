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

import java.io.InputStream;
import java.util.Map;

public class JsonObjectParser extends JsonParser {
  InputStream inputStream;

  public void init(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public Map<String, Object> next() {
    return toMap(inputStream, Map.class);
  }

  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) {
    var event = toMap(inputStream, Map.class);
    return parserUtils.getGuessSchema(event);
  }

  @Override
  public void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException {
    Map<String, Object> event = toMap(inputStream, Map.class);
    handler.handle(event);
  }

}
