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
import org.apache.streampipes.extensions.management.connect.adapter.parser.json.GeoJsonParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.json.JsonArrayKeyParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.json.JsonArrayParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.json.JsonObjectParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.json.JsonParser;
import org.apache.streampipes.model.connect.grounding.ParserDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.ParserDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

public class JsonParsers implements IParser {

  private static final Logger LOG = LoggerFactory.getLogger(JsonParsers.class);

  public static final String ID = "org.apache.streampipes.extensions.management.connect.adapter.parser.json";
  public static final String LABEL = "Json";
  public static final String DESCRIPTION = "Can be used to read json";


  public static final String KEY_JSON_OPTIONS = "json_options";

  public static final String KEY_OBJECT = "object";
  public static final String LABEL_OBJECT = "Single Object";
  public static final String DESCRIPTION_OBJECT = "Each event is a single json object (e.g. {'value': 1})";

  public static final String KEY_ARRAY = "array";
  public static final String LABEL_ARRAY = "Array";
  public static final String DESCRIPTION_ARRAY =
      "Each event consists of only one array of json objects, e.g. [{'value': 1}, {'value': 2}]";

  public static final String KEY_ARRAY_FIELD = "arrayField";
  public static final String LABEL_ARRAY_FIELD = "Array Field";
  public static final String DESCRIPTION_ARRAY_FIELD =
      "Use one property of the json object that is an array, e.g. {'arrayKey': [{'value': 1}, {'value': 2}]}";

  public static final String KEY_GEO_JSON = "geojson";
  public static final String LABEL_GEO_JSON = "GeoJSON";
  public static final String DESCRIPTION_GEO_JSON = "Reads GeoJson";

  private JsonParser selectedParser;


  public JsonParsers() {
  }

  public JsonParsers(JsonParser selectedParser) {
    this.selectedParser = selectedParser;
  }

  @Override
  public IParser fromDescription(List<StaticProperty> config) {
    var extractor = StaticPropertyExtractor.from(config);
    var selectedJsonAlternative = extractor.selectedAlternativeInternalId(KEY_JSON_OPTIONS);

    switch (selectedJsonAlternative) {
      case KEY_OBJECT -> {
        return new JsonParsers(new JsonObjectParser());
      }
      case KEY_ARRAY -> {
        return new JsonParsers(new JsonArrayParser());
      }
      case KEY_ARRAY_FIELD -> {
        var key = extractor.singleValueParameter("key", String.class);
        return new JsonParsers(new JsonArrayKeyParser(key));
      }
      case KEY_GEO_JSON -> {
        return new JsonParsers(new GeoJsonParser());
      }
    }

    LOG.warn("No parser was found. Json object parser is used as a default");
    return new JsonParsers(new JsonObjectParser());
  }

  @Override
  public ParserDescription declareDescription() {
    return ParserDescriptionBuilder.create(ID, LABEL, DESCRIPTION)
        .requiredAlternatives(
            Labels.from(KEY_JSON_OPTIONS, "", ""),
            Alternatives.from(Labels.from(KEY_OBJECT, LABEL_OBJECT, DESCRIPTION_OBJECT), true),
            Alternatives.from(Labels.from(KEY_ARRAY, LABEL_ARRAY, DESCRIPTION_ARRAY)),
            Alternatives.from(Labels.from(KEY_ARRAY_FIELD, LABEL_ARRAY_FIELD, DESCRIPTION_ARRAY_FIELD),
                StaticProperties.group(Labels.from("arrayFieldConfig", "Delimiter", ""),
                    StaticProperties.stringFreeTextProperty(
                        Labels.from("key", "Key", "Key of the array within the Json object")))),
            Alternatives.from(Labels.from(KEY_GEO_JSON, LABEL_GEO_JSON, DESCRIPTION_GEO_JSON)))
        .build();
  }

  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) {
    return selectedParser.getGuessSchema(inputStream);
  }

  @Override
  public void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException {
    selectedParser.parse(inputStream, handler);
  }
}
