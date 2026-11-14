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

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Sends the requests LightCouch sent, with the bodies from {@link LegacyCouchDbPath}, independently of the new client.
 */
final class LegacyCouchDbHttp implements AutoCloseable {

  private final HttpClient http = HttpClient.newHttpClient();
  private final String baseUrl;
  private final String authorization;
  private final Gson gson;

  LegacyCouchDbHttp(String baseUrl, String user, String password, Gson gson) {
    this.baseUrl = baseUrl;
    this.authorization = "Basic "
        + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    this.gson = gson;
  }

  /**
   * {@code CouchDbClient.save} for a document with an id.
   */
  void save(String database, String id, Object document) throws IOException, InterruptedException {
    put(database, id, LegacyCouchDbPath.encode(gson, document));
  }

  /**
   * {@code CouchDbClient.find(Class, String)}.
   */
  <T> T find(String database, String id, Class<T> type) throws IOException, InterruptedException {
    return LegacyCouchDbPath.find(gson, get(database + "/" + encode(id)), type);
  }

  /**
   * {@code CouchDbClient.view("_all_docs").includeDocs(true).query(Class)}.
   */
  <T> List<T> allDocs(String database, Class<T> type) throws IOException, InterruptedException {
    return LegacyCouchDbPath.view(gson, get(database + "/_all_docs?include_docs=true"), type);
  }

  /**
   * {@code CouchDbClient.view("design/view").includeDocs(true).query(Class)}.
   */
  <T> List<T> view(String database, String design, String view, Class<T> type)
      throws IOException, InterruptedException {
    return LegacyCouchDbPath.view(gson,
        get(database + "/_design/" + encode(design) + "/_view/" + encode(view) + "?include_docs=true"),
        type);
  }

  /**
   * Stores the body unchanged.
   */
  void put(String database, String id, String body) throws IOException, InterruptedException {
    var response = http.send(request(database + "/" + encode(id))
        .header("Content-Type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(body))
        .build(), HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 201) {
      throw new IOException("PUT " + database + "/" + id + ": " + response.statusCode() + " " + response.body());
    }
  }

  @Override
  public void close() {
    http.close();
  }

  private byte[] get(String path) throws IOException, InterruptedException {
    var response = http.send(request(path).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
    if (response.statusCode() != 200) {
      throw new IOException("GET " + path + ": " + response.statusCode());
    }
    return response.body();
  }

  private HttpRequest.Builder request(String path) {
    return HttpRequest.newBuilder(URI.create(baseUrl + "/" + path)).header("Authorization", authorization);
  }

  private static String encode(String segment) {
    return URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
