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

package org.apache.streampipes.couchdb.client.codec;

import org.apache.streampipes.couchdb.client.exception.DocumentMappingException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Codec backed by a Jackson {@link ObjectMapper}. Documents are read directly from the response stream.
 */
public final class JacksonCodec implements DocumentCodec {

  private static final JacksonCodec DEFAULT = new JacksonCodec(JsonMapper.builder()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .build());

  private final ObjectMapper mapper;

  private JacksonCodec(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Codec using the given mapper. The mapper is used as is and must not be reconfigured afterwards.
   *
   * @param mapper mapper with the settings the stored documents need
   * @return the codec
   */
  public static JacksonCodec of(ObjectMapper mapper) {
    return new JacksonCodec(mapper);
  }

  /**
   * Codec with a plain mapper that ignores unknown properties; suited for {@code Map} and {@code JsonNode}
   * documents.
   *
   * @return the shared default codec
   */
  public static JacksonCodec defaults() {
    return DEFAULT;
  }

  public ObjectMapper mapper() {
    return mapper;
  }

  @Override
  public byte[] encode(Object document) {
    try {
      return mapper.writeValueAsBytes(document);
    } catch (JsonProcessingException e) {
      throw new DocumentMappingException("Could not serialize " + document.getClass().getName(), e);
    }
  }

  @Override
  public <T> T decode(JsonParser parser, Type type) throws IOException {
    return mapper.readValue(parser, mapper.getTypeFactory().constructType(type));
  }
}
