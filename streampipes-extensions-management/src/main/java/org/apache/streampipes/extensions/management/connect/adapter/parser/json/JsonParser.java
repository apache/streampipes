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
import org.apache.streampipes.extensions.management.connect.adapter.parser.ParserUtils;
import org.apache.streampipes.model.connect.guess.GuessSchema;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public abstract class JsonParser {

  protected final ObjectMapper mapper;
  protected final ParserUtils parserUtils;

  public JsonParser() {
    this.mapper = new ObjectMapper();
    this.parserUtils = new ParserUtils();
  }

  public abstract GuessSchema getGuessSchema(InputStream inputStream);

  public abstract void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException;


  protected <T> T toMap(InputStream inputStream, Class<T> clazz) throws ParseException {
    if (inputStream == null) {
      throw new ParseException("Input stream was null in JsonParser");
    }

    try {
      return mapper.readValue(inputStream, clazz);
    } catch (IOException e) {
      throw new ParseException("Event " + inputStream, e);
    }
  }

}
