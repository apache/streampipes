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

package org.apache.streampipes.extensions.management.connect.adapter.format.xml;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.connect.EmitBinaryEvent;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.format.util.JsonEventProperty;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Parser;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.underscore.lodash.U;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class XmlParser extends Parser {

  private static final Logger logger = LoggerFactory.getLogger(XmlParser.class);
  private static final String ENCODING = "#encoding";

  private String tag;
  private ObjectMapper objectMapper;

  public XmlParser() {
    this.objectMapper = new ObjectMapper();
  }

  public XmlParser(String tag) {
    this();
    this.tag = tag;
  }

  @Override
  public Parser getInstance(FormatDescription formatDescription) {
    ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());
    String tag = extractor.singleValue("tag");

    return new XmlParser(tag);
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) throws ParseException {

    try {
      String dataString = CharStreams.toString(new InputStreamReader(data, Charsets.UTF_8));

      Map<String, Object> map =
          (Map<String, Object>) U.fromXmlWithoutNamespaces(dataString);
      map.remove(ENCODING);
      Map<String, Object> convertedMap = new XmlMapConverter(map).convert();
      searchAndEmitEvents(convertedMap, tag, emitBinaryEvent);

    } catch (IOException e) {
      logger.error(e.toString());
      throw new ParseException(e.getMessage());
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
      logger.error(e.toString());
    }

    for (Map.Entry<String, Object> entry : exampleEvent.entrySet()) {
      EventProperty p = JsonEventProperty.getEventProperty(entry.getKey(), entry.getValue());
      resultSchema.addEventProperty(p);
    }

    return resultSchema;
  }

  private void searchAndEmitEvents(Map<String, Object> map, String key, EmitBinaryEvent emitBinaryEvent) {
    Gson gson = new Gson();

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getKey().equals(key)) {

        if (entry.getValue() instanceof List) {
          List list = (List) entry.getValue();

          list.forEach(listEntry -> emitBinaryEvent.emit(gson.toJson(listEntry).getBytes()));

        } else if (entry.getValue() instanceof Map) {
          byte[] bytes = gson.toJson(entry.getValue()).getBytes();
          emitBinaryEvent.emit(bytes);

        } else {
          logger.error("Events are found, but could not disjunct: " + entry.toString());
        }

      } else if (entry.getValue() instanceof Map) {
        searchAndEmitEvents((Map) entry.getValue(), key, emitBinaryEvent);
      } else if (entry.getValue() instanceof List) {
        List list = (List) entry.getValue();
        list.forEach(listEntry -> searchAndEmitEvents((Map) listEntry, key, emitBinaryEvent));
      }
    }
  }

}
