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


package org.apache.streampipes.service.core.migrations;

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;
import org.apache.streampipes.couchdb.client.transport.CouchTransport;
import org.apache.streampipes.storage.couchdb.CouchDbCodecs;
import org.apache.streampipes.storage.couchdb.CouchDbStores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * The part of CouchDB's HTTP API that setup tasks and migrations use, held in memory. Records every request and can
 * answer selected ones with an error.
 */
public class InMemoryCouchTransport implements CouchTransport {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Map<String, NavigableMap<String, ObjectNode>> databases = new HashMap<>();
  private final List<String> requests = new ArrayList<>();
  private final Map<String, Integer> failures = new LinkedHashMap<>();
  private final Map<String, Integer> remainingFailures = new HashMap<>();

  public CouchDbStores stores() {
    return new CouchDbStores(CouchServer.builder().transport(config -> this).build(), CouchDbCodecs.defaults());
  }

  public void createDatabase(String name) {
    databases.put(name, new TreeMap<>());
  }

  public boolean hasDatabase(String name) {
    return databases.containsKey(name);
  }

  public void store(String database, String json) {
    try {
      var document = (ObjectNode) MAPPER.readTree(json);
      if (!document.has("_rev")) {
        document.put("_rev", "1-stored");
      }
      databases.get(database).put(document.get("_id").asText(), document);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public ObjectNode document(String database, String id) {
    return databases.get(database).get(id);
  }

  public List<String> documentIds(String database) {
    return List.copyOf(databases.get(database).keySet());
  }

  /**
   * Every request as {@code METHOD path}, with the query string.
   *
   * @return the requests in order
   */
  public List<String> requests() {
    return requests;
  }

  public List<String> requestsStartingWith(String prefix) {
    return requests.stream().filter(request -> request.startsWith(prefix)).toList();
  }

  public void fail(String requestPrefix, int status) {
    failTimes(requestPrefix, status, Integer.MAX_VALUE);
  }

  public void failOnce(String requestPrefix, int status) {
    failTimes(requestPrefix, status, 1);
  }

  private void failTimes(String requestPrefix, int status, int times) {
    failures.put(requestPrefix, status);
    remainingFailures.put(requestPrefix, times);
  }

  @Override
  public CouchResponse send(CouchRequest request, Map<String, String> headers) {
    String line = request.method() + " " + request.path();
    requests.add(line);
    for (var failure : failures.entrySet()) {
      if (line.startsWith(failure.getKey()) && remainingFailures.merge(failure.getKey(), -1, Integer::sum) >= 0) {
        return error(failure.getValue(), "failed", "Configured failure");
      }
    }
    try {
      return route(request);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
  }

  private CouchResponse route(CouchRequest request) throws IOException {
    String[] pathAndQuery = request.path().split("\\?", 2);
    String[] segments = pathAndQuery[0].substring(1).split("/", 2);
    Map<String, String> query = pathAndQuery.length > 1 ? parseQuery(pathAndQuery[1]) : Map.of();
    String database = decode(segments[0]);
    if (segments.length == 1) {
      return databaseRequest(request.method(), database);
    }
    var documents = databases.get(database);
    if (documents == null) {
      return error(404, "not_found", "Database does not exist.");
    }
    if ("_all_docs".equals(segments[1])) {
      return allDocs(documents, query);
    }
    return documentRequest(request, documents, decode(segments[1]), query);
  }

  private CouchResponse databaseRequest(String method, String database) {
    boolean exists = databases.containsKey(database);
    return switch (method) {
      case "HEAD" -> exists ? json(200, Map.of(), "{}") : error(404, "not_found", "Database does not exist.");
      case "PUT" -> {
        if (exists) {
          yield error(412, "file_exists", "The database could not be created, the file already exists.");
        }
        createDatabase(database);
        yield json(201, Map.of(), "{\"ok\":true}");
      }
      case "DELETE" -> {
        if (!exists) {
          yield error(404, "not_found", "Database does not exist.");
        }
        databases.remove(database);
        yield json(200, Map.of(), "{\"ok\":true}");
      }
      default -> error(405, "method_not_allowed", method);
    };
  }

  private CouchResponse allDocs(NavigableMap<String, ObjectNode> documents, Map<String, String> query)
      throws IOException {
    var selected = query.containsKey("start_key")
        ? documents.tailMap(MAPPER.readValue(query.get("start_key"), String.class), true)
        : documents;
    var body = MAPPER.createObjectNode();
    body.put("total_rows", documents.size());
    body.put("offset", 0);
    var rows = body.putArray("rows");
    selected.values().stream().limit(Long.parseLong(query.getOrDefault("limit", "1000000"))).forEach(document -> {
      var row = rows.addObject();
      row.put("id", document.get("_id").asText());
      row.put("key", document.get("_id").asText());
      row.putObject("value").put("rev", document.get("_rev").asText());
      if ("true".equals(query.get("include_docs"))) {
        row.set("doc", document);
      }
    });
    return json(200, Map.of(), MAPPER.writeValueAsString(body));
  }

  private CouchResponse documentRequest(CouchRequest request,
                                        NavigableMap<String, ObjectNode> documents,
                                        String id,
                                        Map<String, String> query) throws IOException {
    var current = documents.get(id);
    return switch (request.method()) {
      case "HEAD" -> current == null
          ? error(404, "not_found", "missing")
          : json(200, Map.of("etag", "\"" + current.get("_rev").asText() + "\""), "");
      case "GET" -> current == null
          ? error(404, "not_found", "missing")
          : json(200, Map.of(), MAPPER.writeValueAsString(current));
      case "PUT" -> write(documents, id, (ObjectNode) MAPPER.readTree(request.body()), query.get("rev"), current);
      default -> error(405, "method_not_allowed", request.method());
    };
  }

  private CouchResponse write(NavigableMap<String, ObjectNode> documents,
                              String id,
                              ObjectNode document,
                              String queryRev,
                              ObjectNode current) throws IOException {
    String rev = queryRev != null ? queryRev : document.path("_rev").asText(null);
    String currentRev = current == null ? null : current.get("_rev").asText();
    if (rev == null ? currentRev != null : !rev.equals(currentRev)) {
      return error(409, "conflict", "Document update conflict.");
    }
    int generation = currentRev == null ? 1 : Integer.parseInt(currentRev.split("-")[0]) + 1;
    String newRev = generation + "-memory";
    document.put("_id", id);
    document.put("_rev", newRev);
    documents.put(id, document);
    var response = MAPPER.createObjectNode().put("ok", true).put("id", id).put("rev", newRev);
    return json(201, Map.of(), MAPPER.writeValueAsString(response));
  }

  private static Map<String, String> parseQuery(String query) {
    var params = new HashMap<String, String>();
    for (String param : query.split("&")) {
      String[] keyAndValue = param.split("=", 2);
      params.put(keyAndValue[0], decode(keyAndValue[1]));
    }
    return params;
  }

  private static String decode(String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }

  private static CouchResponse error(int status, String error, String reason) {
    return json(status, Map.of(), "{\"error\":\"" + error + "\",\"reason\":\"" + reason + "\"}");
  }

  private static CouchResponse json(int status, Map<String, String> headers, String body) {
    return new CouchResponse(status, headers, new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
  }
}
