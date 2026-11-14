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

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Converts documents between Java objects and JSON.
 *
 * <p>A codec only handles document bodies. CouchDB's response envelopes ({@code rows}, {@code docs},
 * {@code ok/id/rev}) are always parsed by the client itself, which hands each document to the codec as a parser
 * positioned on the document's {@code START_OBJECT}. The codec decides how documents map to fields, including how
 * {@code _id} and {@code _rev} are named.</p>
 */
public interface DocumentCodec {

  /**
   * Serializes a document to UTF-8 JSON. The result must be a JSON object.
   *
   * @param document document to serialize
   * @return UTF-8 encoded JSON
   */
  byte[] encode(Object document);

  /**
   * Reads one document.
   *
   * @param parser parser positioned on the {@code START_OBJECT} of the document; on return it must be positioned on
   *               the matching {@code END_OBJECT}
   * @param type   target type
   * @param <T>    target type
   * @return the decoded document
   * @throws IOException if the JSON cannot be read or mapped
   */
  <T> T decode(JsonParser parser, Type type) throws IOException;
}
