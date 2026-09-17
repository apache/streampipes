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

package org.apache.streampipes.storage.couchdb.impl.system;

import org.apache.streampipes.storage.api.system.IGroundingMigrationStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GroundingMigrationStorageImpl implements IGroundingMigrationStorage {
  // Templates contain static properties/IDs only, not concrete streams.
  private static final List<String> COLLECTIONS = List.of(
      "adapterdescription", "adapterinstance", "data-stream", "data-processor", "data-sink", "pipeline");

  @Override
  public List<String> collections() throws IOException {
    var response = Utils.getRequest(Utils.getDatabaseRoute("_all_dbs"))
        .execute().returnContent().asString(StandardCharsets.UTF_8);
    var available = new ArrayList<String>();
    for (var name : JsonParser.parseString(response).getAsJsonArray()) {
      if (COLLECTIONS.contains(name.getAsString())) {
        available.add(name.getAsString());
      }
    }
    return available;
  }

  @Override
  public List<String> readPage(String collection, String afterId, int limit) throws IOException {
    String query = route(collection) + "/_all_docs?include_docs=true&limit=" + (afterId == null ? limit : limit + 1);
    if (afterId != null) {
      query += "&startkey=" + URLEncoder.encode(new JsonPrimitive(afterId).toString(), StandardCharsets.UTF_8);
    }
    var response = Utils.getRequest(query).execute().returnContent().asString(StandardCharsets.UTF_8);
    var result = new ArrayList<String>();
    for (var row : JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("rows")) {
      var document = row.getAsJsonObject().getAsJsonObject("doc");
      if (!document.get("_id").getAsString().equals(afterId) && result.size() < limit) {
        result.add(document.toString());
      }
    }
    return result;
  }

  @Override
  public String read(String collection, String id) throws IOException {
    return Utils.getRequest(route(collection) + "/" + Utils.escapePathSegment(id))
        .execute().returnContent().asString(StandardCharsets.UTF_8);
  }

  @Override
  public boolean update(String collection, String id, String document) throws IOException {
    try {
      Utils.putRequest(route(collection) + "/" + Utils.escapePathSegment(id), document).execute().returnContent();
      return true;
    } catch (HttpResponseException e) {
      if (e.getStatusCode() == 409) {
        return false;
      }
      throw e;
    }
  }

  private String route(String collection) {
    if (!COLLECTIONS.contains(collection)) {
      throw new IllegalArgumentException("Unsupported grounding collection");
    }
    return Utils.getDatabaseRoute(collection);
  }
}
