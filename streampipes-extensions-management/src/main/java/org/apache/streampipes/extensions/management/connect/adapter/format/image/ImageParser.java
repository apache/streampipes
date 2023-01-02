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

package org.apache.streampipes.extensions.management.connect.adapter.format.image;

import org.apache.streampipes.extensions.api.connect.EmitBinaryEvent;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Parser;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.schema.EventSchema;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.apache.streampipes.sdk.helpers.EpProperties.imageProperty;


public class ImageParser extends Parser {

  @Override
  public Parser getInstance(FormatDescription formatDescription) {
    return new ImageParser();
  }

  @Override
  public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) throws ParseException {

    try {
      byte[] result = IOUtils.toByteArray(data);
      emitBinaryEvent.emit(result);
    } catch (IOException e) {
      throw new ParseException(e.getMessage());
    }
  }

  @Override
  public EventSchema getEventSchema(List<byte[]> oneEvent) {
    EventSchema resultSchema = new EventSchema();
    resultSchema.addEventProperty(imageProperty("image"));
    return resultSchema;
  }
}
