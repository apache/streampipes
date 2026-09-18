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

package org.apache.streampipes.dataformat;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JsonDataFormatDefinition implements SpDataFormatDefinition {

  private final ObjectMapper objectMapper;

  public JsonDataFormatDefinition() {
    this.objectMapper = JacksonSerializer.getObjectMapper(Map.of(
      DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true,
      SerializationFeature.INDENT_OUTPUT, false
    ));
    objectMapper.getFactory().enable(StreamReadFeature.USE_FAST_DOUBLE_PARSER.mappedFeature());
    var module = new SimpleModule();
    // Register an internal envelope so fromMap retains its existing serialization
    // of arbitrary values, including values whose Java type happens to be Event.
    module.addSerializer(EventJsonSerializer.Payload.class, new EventJsonSerializer());
    objectMapper.registerModule(module);
  }

  @Override
  public Map<String, Object> toMap(byte[] event) throws SpRuntimeException {
    try {
      return objectMapper.readValue(event, HashMap.class);
    } catch (IOException e) {
      throw new SpRuntimeException("Could not convert event to map data structure");
    }
  }

  @Override
  public byte[] fromEvent(Event event) throws SpRuntimeException {
    Objects.requireNonNull(event);
    try {
      return objectMapper.writeValueAsBytes(new EventJsonSerializer.Payload(event));
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException("Could not convert event to JSON string", e);
    }
  }

  @Override
  public byte[] fromMap(Map<String, Object> event) throws SpRuntimeException {
    try {
      return objectMapper.writeValueAsBytes(event);
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException("Could not convert map data structure to JSON string");
    }
  }
}
