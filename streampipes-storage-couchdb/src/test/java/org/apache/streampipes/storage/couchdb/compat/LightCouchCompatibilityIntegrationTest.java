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

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.TypedCollection;
import org.apache.streampipes.couchdb.client.codec.GsonCodec;
import org.apache.streampipes.couchdb.client.codec.JacksonCodec;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.couchdb.compat.ModelFixtures.Variant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Writes every stored model with the real LightCouch client and with the new client into CouchDB 3.3.1, then
 * compares what CouchDB stored and reads each document back with both clients. Unlike
 * {@link CodecCompatibilityTest} this covers CouchDB's own re-serialization of documents. Enable with
 * {@code -Dstreampipes.couchdb.integration=true}.
 */
@EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
public class LightCouchCompatibilityIntegrationTest {

  private static final String USER = "admin";
  private static final String PASSWORD = "admin";
  private static final JacksonCodec EXACT = JacksonCodec.of(JsonTrees.MAPPER);

  private static GenericContainer<?> couchDb;
  private static CouchServer server;

  @BeforeAll
  public static void startCouchDb() {
    couchDb = new GenericContainer<>("couchdb:3.3.1")
        .withEnv("COUCHDB_USER", USER)
        .withEnv("COUCHDB_PASSWORD", PASSWORD)
        .withExposedPorts(5984)
        .waitingFor(Wait.forHttp("/_up").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(60)));
    couchDb.start();
    server = CouchServer.builder()
        .url("http", couchDb.getHost(), couchDb.getMappedPort(5984))
        .credentials(USER, PASSWORD)
        .build();
  }

  @AfterAll
  public static void stopCouchDb() {
    if (server != null) {
      server.close();
    }
    if (couchDb != null) {
      couchDb.stop();
    }
  }

  @TestFactory
  public Stream<DynamicTest> bothClientsStoreAndReadTheSameDocuments() {
    return IntStream.range(0, StoredModels.ALL.size()).boxed()
        .map(index -> DynamicTest.dynamicTest(StoredModels.ALL.get(index).name(),
            () -> check(StoredModels.ALL.get(index), "compat-" + index)));
  }

  /**
   * The generic storage sends the caller's JSON as is. Phase 3 goes through the raw collection, which parses it into
   * a map first; CouchDB must end up with the same document either way.
   */
  @Test
  public void genericPayloadStoredLikeVerbatimPut() throws Exception {
    String payload = "{\"appDocType\":\"asset-type\",\"name\":\"Ünï <&> \\u2028 😀\","
        + "\"count\":2,\"ratio\":1.10,\"whole\":3.0,\"big\":12345678901234567890,\"long\":9007199254740993,"
        + "\"tiny\":1.0e-7,\"huge\":1E21,\"none\":null,\"nested\":{\"list\":[1,\"a\",true,{\"k\":[]}]}}";
    var db = server.database("compat-generic");
    db.create();

    putVerbatim("compat-generic", "verbatim", payload);
    Map<String, Object> parsed = JacksonSerializer.getObjectMapper().readValue(payload, new TypeReference<>() {
    });
    parsed.put("_id", "via-map");
    db.raw().create(parsed);

    var stored = db.typed(JsonNode.class, EXACT);
    assertSame("generic payload", JsonTrees.without(stored.get("verbatim"), "_id", "_rev"),
        JsonTrees.without(stored.get("via-map"), "_id", "_rev"));
  }

  private static void check(StoredModel model, String databasePrefix) {
    String legacyDb = databasePrefix + "-legacy";
    String newDb = databasePrefix + "-new";
    Gson legacyGson = LegacyCouchDbPath.gson(model.gsonBuilder().get());
    GsonCodec codec = model.newCodec();
    CouchDbClient legacyOnLegacy = lightCouch(legacyDb, model);
    CouchDbClient legacyOnNew = lightCouch(newDb, model);
    try {
      TypedCollection<Object> newOnLegacy = server.database(legacyDb).typed(rawClass(model.readType()), codec);
      TypedCollection<Object> newOnNew = server.database(newDb).typed(rawClass(model.readType()), codec);
      for (Variant variant : Variant.values()) {
        String id = "fixture-" + variant.name().toLowerCase();
        Object fixture = ModelFixtures.create(model.fixtureType(), variant, id, codec.gson());
        String step = model + " " + variant;

        legacyOnLegacy.save(fixture);
        newOnNew.create(fixture);
        assertSame(step + ", stored document",
            JsonTrees.without(server.database(legacyDb).typed(JsonNode.class, EXACT).get(id), "_rev"),
            JsonTrees.without(server.database(newDb).typed(JsonNode.class, EXACT).get(id), "_rev"));

        JsonNode expected = reencode(legacyGson, legacyOnLegacy.find(model.readType(), id));
        assertSame(step + ", LightCouch reads new client's document", expected,
            reencode(legacyGson, legacyOnNew.find(model.readType(), id)));
        assertSame(step + ", new client reads LightCouch's document", expected,
            reencode(legacyGson, newOnLegacy.get(id)));
        assertSame(step + ", new client reads its own document", expected, reencode(legacyGson, newOnNew.get(id)));
        assertSame(step + ", LightCouch _all_docs", expected,
            byId(legacyGson, legacyOnNew.view("_all_docs").includeDocs(true).query(model.readType()), id));
        assertSame(step + ", new client findAll", expected, byId(legacyGson, newOnLegacy.findAll(), id));
      }
    } finally {
      legacyOnLegacy.shutdown();
      legacyOnNew.shutdown();
    }
  }

  private static CouchDbClient lightCouch(String database, StoredModel model) {
    var client = new CouchDbClient(new CouchDbProperties(database, true, "http", couchDb.getHost(),
        couchDb.getMappedPort(5984), USER, PASSWORD));
    client.setGsonBuilder(model.gsonBuilder().get());
    return client;
  }

  private static void putVerbatim(String database, String id, String payload) throws Exception {
    String auth = Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
    try (var http = HttpClient.newHttpClient()) {
      var response = http.send(HttpRequest.newBuilder(URI.create(server.config().baseUrl() + "/" + database + "/" + id))
          .header("Authorization", "Basic " + auth)
          .header("Content-Type", "application/json")
          .PUT(HttpRequest.BodyPublishers.ofString(payload))
          .build(), HttpResponse.BodyHandlers.ofString());
      assertEquals(201, response.statusCode(), response.body());
    }
  }

  private static JsonNode byId(Gson legacyGson, List<?> documents, String id) {
    return documents.stream()
        .map(document -> reencode(legacyGson, document))
        .filter(tree -> id.equals(tree.path("_id").asText()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("no document " + id + " in " + documents.size() + " results"));
  }

  @SuppressWarnings("unchecked")
  private static Class<Object> rawClass(Class<?> type) {
    return (Class<Object>) type;
  }

  private static JsonNode reencode(Gson legacyGson, Object document) {
    return JsonTrees.parse(LegacyCouchDbPath.encode(legacyGson, document));
  }

  private static void assertSame(String step, JsonNode expected, JsonNode actual) {
    List<String> differences = JsonTrees.differences(expected, actual);
    if (!differences.isEmpty()) {
      fail(step + ": LightCouch and new client differ\n  " + String.join("\n  ", differences));
    }
  }
}
