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
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.IParserEventHandler;
import org.apache.streampipes.model.connect.grounding.ParserDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.ParserDescriptionBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class ImageParser implements IParser {

  public static final String ID = "org.apache.streampipes.extensions.management.connect.adapter.parser.image";
  public static final String LABEL = "Image";
  public static final String DESCRIPTION = "Processes images and transforms them into events";

  @Override
  public ParserDescription declareDescription() {
    return ParserDescriptionBuilder.create(ID, LABEL, DESCRIPTION)
        .build();
  }

  @Override
  public IParser fromDescription(List<StaticProperty> configuration) {
    return new ImageParser();
  }

  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) throws ParseException {
    // validate that image can be parsed
    var image = parseImage(inputStream);

    return GuessSchemaBuilder.create()
        .property(EpProperties.imageProperty("image"))
        .sample("image", image)
        .build();
  }

  @Override
  public void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException {
    var image = parseImage(inputStream);
    var event = new HashMap<String, Object>();
    event.put("image", image);
    handler.handle(event);
  }

  private String parseImage(InputStream inputStream) throws ParseException {
    byte[] bytes;
    try {
      bytes = IOUtils.toByteArray(inputStream);

      return Base64.getEncoder().encodeToString(bytes);
    } catch (IOException e) {
      throw new ParseException("Image could not be parsed", e);
    }
  }


}
