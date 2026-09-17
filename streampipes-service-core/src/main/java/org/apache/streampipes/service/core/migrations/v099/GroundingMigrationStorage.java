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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Raw document access scoped to the broker grounding migration. */
class GroundingMigrationStorage {
  private static final String MARKER_ID = "migration:extract-broker-configuration-v1";

  boolean isCompleted() throws IOException {
    var marker = readMarker();
    return marker != null && marker.has("completed") && marker.get("completed").getAsBoolean();
  }

  void markCompleted() throws IOException {
    var marker = readMarker();
    if (marker == null) {
      marker = new JsonObject();
      marker.addProperty("_id", MARKER_ID);
    }
    marker.addProperty("completed", true);
    marker.addProperty("completedAt", Instant.now().toString());
    try {
      Utils.putRequest(markerRoute(), marker.toString()).execute().returnContent();
    } catch (HttpResponseException e) {
      // Another core may have completed the same migration concurrently.
      if (e.getStatusCode() != 409 || !isCompleted()) {
        throw e;
      }
    }
  }

  private JsonObject readMarker() throws IOException {
    try {
      var response = Utils.getRequest(markerRoute()).execute().returnContent().asString(StandardCharsets.UTF_8);
      return JsonParser.parseString(response).getAsJsonObject();
    } catch (HttpResponseException e) {
      if (e.getStatusCode() == 404) {
        return null;
      }
      throw e;
    }
  }

  private String markerRoute() {
    return Utils.getDatabaseRoute("general-configuration") + "/" + Utils.escapePathSegment(MARKER_ID);
  }

  // Templates contain static properties/IDs only, not concrete streams.
  private static final List<String> COLLECTIONS = List.of(
      "adapterdescription", "adapterinstance", "data-stream", "data-processor", "data-sink", "pipeline");

  List<String> collections() throws IOException {
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

  List<String> readPage(String collection, String afterId, int limit) throws IOException {
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

  String read(String collection, String id) throws IOException {
    return Utils.getRequest(route(collection) + "/" + Utils.escapePathSegment(id))
        .execute().returnContent().asString(StandardCharsets.UTF_8);
  }

  /** Returns false on a revision conflict; other failures propagate. */
  boolean update(String collection, String id, String document) throws IOException {
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
