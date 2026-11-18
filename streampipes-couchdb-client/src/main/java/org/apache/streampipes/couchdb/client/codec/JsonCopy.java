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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

/**
 * Token-level JSON copying that keeps numbers exactly as written.
 */
public final class JsonCopy {

  private JsonCopy() {
  }

  /**
   * Copies the current value like {@link JsonGenerator#copyCurrentStructure}, but keeps numbers exactly as written
   * ({@code 1.10} stays {@code 1.10}). The parser ends on the value's last token.
   *
   * @param parser    parser positioned on the first token of the value
   * @param generator target
   * @throws IOException if reading or writing fails
   */
  public static void copyExact(JsonParser parser, JsonGenerator generator) throws IOException {
    int depth = 0;
    do {
      JsonToken token = parser.currentToken();
      generator.copyCurrentEventExact(parser);
      if (token.isStructStart()) {
        depth++;
      } else if (token.isStructEnd()) {
        depth--;
      }
      if (depth > 0) {
        parser.nextToken();
      }
    } while (depth > 0);
  }
}
