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

package org.apache.streampipes.extensions.management.connect.adapter.parser.xml;

import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.IParserEventHandler;
import org.apache.streampipes.extensions.management.connect.adapter.parser.ParserUtils;
import org.apache.streampipes.model.connect.grounding.ParserDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.ParserDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParser implements IParser {

  public static final String ID = "org.apache.streampipes.extensions.management.connect.adapter.parser.xml";
  public static final String LABEL = "XML";

  public static final String DESCRIPTION = "Can be used to read XML data";

  public static final String TAG = "tag";

  private String tag;
  private final XmlMapper xmlMapper;

  private final ParserUtils parserUtils;

  public XmlParser() {
    xmlMapper = new XmlMapper();
    parserUtils = new ParserUtils();
  }

  public XmlParser(String tag) {
    this.tag = tag;
    xmlMapper = new XmlMapper();
    parserUtils = new ParserUtils();
  }

  @Override
  public ParserDescription declareDescription() {
    return ParserDescriptionBuilder.create(ID, LABEL, DESCRIPTION)
        .requiredTextParameter(Labels.from(TAG, "Tag",
            "Information in the tag is transformed into an event"))
        .build();
  }

  @Override
  public IParser fromDescription(List<StaticProperty> configuration) {
    StaticPropertyExtractor extractor = StaticPropertyExtractor.from(configuration);

    var configuredTag = extractor.singleValueParameter(TAG, String.class);

    return new XmlParser(configuredTag);
  }

  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) throws ParseException {
    var event = getEvents(inputStream).get(0);
    var converter = new XmlMapConverter(event);
    return parserUtils.getGuessSchema(converter.convert());
  }

  @Override
  public void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException {
    var events = getEvents(inputStream);

    events.forEach(event -> {
      var converter = new XmlMapConverter(event);
      handler.handle(converter.convert());
    });
  }


  private List<Map<String, Object>> getEvents(InputStream inputStream) {

    try {
      Map<String, Object> xml = xmlMapper.readValue(inputStream, HashMap.class);

      for (String key : xml.keySet()) {
        if (key.equals(tag)) {
          if (xml.get(tag) instanceof List) {
            return (List<Map<String, Object>>) xml.get(tag);
          } else if (xml.get(tag) instanceof Map) {
            return List.of((Map<String, Object>) xml.get(tag));
          } else {
            throw new ParseException("Could not parse %s with tag %s".formatted(xml, tag));
          }
        }
      }

    } catch (IOException e) {
      throw new ParseException("Could not read XML input", e);
    }

    return List.of();
  }

}
