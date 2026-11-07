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

package org.apache.streampipes.storage.couchdb.compat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The serialization steps of LightCouch 0.2.0, reproduced without its HTTP client, which cannot be constructed
 * without a server. Each method names the LightCouch code it mirrors; the integration test checks the same
 * fixtures against the real {@code CouchDbClient}.
 */
final class LegacyCouchDbPath {

  private LegacyCouchDbPath() {
  }

  /**
   * {@code CouchDbClientBase.setGsonBuilder} → {@code initGson}: adds pass-through adapters for {@code JsonObject}.
   */
  static Gson gson(GsonBuilder builder) {
    builder.registerTypeAdapter(JsonObject.class,
        (JsonDeserializer<JsonObject>) (json, type, context) -> json.getAsJsonObject());
    builder.registerTypeAdapter(JsonObject.class,
        (JsonSerializer<JsonObject>) (src, type, context) -> src.getAsJsonObject());
    return builder.create();
  }

  /**
   * {@code CouchDbClientBase.put}: {@code toJsonTree(object).getAsJsonObject()}, sent as {@code json.toString()}.
   */
  static String encode(Gson gson, Object document) {
    return gson.toJsonTree(document).getAsJsonObject().toString();
  }

  /**
   * {@code CouchDbClientBase.get(URI, Class)}, used by {@code find}: Gson reads the response stream.
   */
  static <T> T find(Gson gson, byte[] storedDocument, Class<T> type) {
    return gson.fromJson(new InputStreamReader(new ByteArrayInputStream(storedDocument), StandardCharsets.UTF_8),
        type);
  }

  /**
   * {@code View.query(Class)} with {@code includeDocs(true)}: the whole response is parsed into a tree and each
   * row's {@code doc} is read from it, including design documents and {@code null} for deleted ones.
   */
  static <T> List<T> view(Gson gson, byte[] viewResponse, Class<T> type) {
    var rows = JsonParser.parseReader(
            new InputStreamReader(new ByteArrayInputStream(viewResponse), StandardCharsets.UTF_8))
        .getAsJsonObject()
        .getAsJsonArray("rows");
    var result = new ArrayList<T>();
    for (JsonElement row : rows) {
      result.add(gson.fromJson(row.getAsJsonObject().get("doc"), type));
    }
    return result;
  }
}
