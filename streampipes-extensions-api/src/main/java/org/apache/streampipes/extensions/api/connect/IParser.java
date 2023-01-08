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
package org.apache.streampipes.extensions.api.connect;

import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.connect.guess.AdapterGuessInfo;
import org.apache.streampipes.model.schema.EventSchema;

import java.io.InputStream;
import java.util.List;

public interface IParser {

  IParser getInstance(FormatDescription formatDescription);

  void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) throws ParseException;

  List<byte[]> parseNEvents(InputStream data, int n) throws ParseException;

  /**
   * Pass one event to Parser to get the event schema
   *
   * @param oneEvent
   * @return
   */
  EventSchema getEventSchema(List<byte[]> oneEvent);

  default boolean supportsPreview() {
    return false;
  }

  default AdapterGuessInfo getSchemaAndSample(List<byte[]> eventSample) throws ParseException {
    throw new RuntimeException("Not yet implemented!");
  }
}
