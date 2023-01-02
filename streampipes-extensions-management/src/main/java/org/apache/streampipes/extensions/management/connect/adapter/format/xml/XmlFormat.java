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
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.adapter.FormatDescriptionBuilder;
import org.apache.streampipes.sdk.helpers.Labels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class XmlFormat implements IFormat {

  public static final String TAG_ID = "tag";
  public static final String ID = "https://streampipes.org/vocabulary/v1/format/xml";

  private String tag;

  Logger logger = LoggerFactory.getLogger(XmlFormat.class);

  public XmlFormat() {
  }

  public XmlFormat(String tag) {
    this.tag = tag;
  }

  @Override
  public IFormat getInstance(FormatDescription formatDescription) {
    ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());
    String tag = extractor.singleValue(TAG_ID);

    return new XmlFormat(tag);
  }

  @Override
  public FormatDescription declareModel() {

    return FormatDescriptionBuilder.create(ID, "XML", "Process XML data")
        .requiredTextParameter(Labels.from(TAG_ID, "Tag",
            "Information in the tag is transformed into an event"))
        .build();

  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public Map<String, Object> parse(byte[] object) throws ParseException {
    EventSchema resultSchema = new EventSchema();

    JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();

    Map<String, Object> result = null;

    try {
      result = jsonDefinition.toMap(object);
    } catch (SpRuntimeException e) {
      throw new ParseException("Could not parse Data : " + e.toString());
    }

    return result;
  }
}
