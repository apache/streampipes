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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * Codec backed by {@link Gson}, for documents whose stored format was written by Gson.
 *
 * <p>Each document is copied token by token out of the response stream into a string, then read by Gson. Numbers
 * are copied with their exact textual value so Gson sees the same input it would read from CouchDB directly.</p>
 */
public final class GsonCodec implements DocumentCodec {

  private static final JsonFactory FACTORY = new JsonFactory();

  private final Gson gson;

  private GsonCodec(Gson gson) {
    this.gson = gson;
  }

  /**
   * Codec using the given Gson instance, which is thread-safe and should be built once.
   *
   * @param gson configured Gson
   * @return the codec
   */
  public static GsonCodec of(Gson gson) {
    return new GsonCodec(gson);
  }

  public Gson gson() {
    return gson;
  }

  @Override
  public byte[] encode(Object document) {
    return gson.toJson(document).getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public <T> T decode(JsonParser parser, Type type) throws IOException {
    var json = new StringWriter(256);
    try (JsonGenerator generator = FACTORY.createGenerator(json)) {
      JsonCopy.copyExact(parser, generator);
    }
    try {
      return gson.fromJson(json.toString(), type);
    } catch (JsonParseException e) {
      throw new DocumentMappingException("Gson could not read document as " + type.getTypeName(), e);
    }
  }
}
