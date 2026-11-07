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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Reads the documents of an existing StreamPipes installation with LightCouch and with the new client and compares
 * the results. Read-only: databases are opened without creating them and nothing is written.
 *
 * <p>Run against a local stack with
 * {@code -Dstreampipes.couchdb.compat.url=http://localhost:5984 -Dstreampipes.couchdb.compat.user=...
 * -Dstreampipes.couchdb.compat.password=...}.</p>
 */
@EnabledIfSystemProperty(named = "streampipes.couchdb.compat.url", matches = ".+")
public class LiveCouchDbCompatibilityTest {

  private static final int MAX_FIND_COMPARISONS = 50;

  private static URI url;
  private static String user;
  private static String password;
  private static CouchServer server;

  @BeforeAll
  public static void connect() {
    url = URI.create(System.getProperty("streampipes.couchdb.compat.url"));
    user = System.getProperty("streampipes.couchdb.compat.user", "admin");
    password = System.getProperty("streampipes.couchdb.compat.password", "admin");
    server = CouchServer.builder().url(url.toString()).credentials(user, password).build();
  }

  @AfterAll
  public static void disconnect() {
    if (server != null) {
      server.close();
    }
  }

  @TestFactory
  public Stream<DynamicTest> readsExistingDocumentsLikeLightCouch() {
    return StoredModels.ALL.stream().map(model -> DynamicTest.dynamicTest(model.name(), () -> check(model)));
  }

  private static void check(StoredModel model) {
    Assumptions.assumeTrue(server.database(model.database()).exists(), "no database " + model.database());
    Gson legacyGson = LegacyCouchDbPath.gson(model.gsonBuilder().get());
    GsonCodec codec = model.newCodec();
    TypedCollection<Object> collection = server.database(model.database()).typed(rawClass(model.readType()), codec);
    var legacy = new CouchDbClient(new CouchDbProperties(model.database(), false, url.getScheme(), url.getHost(),
        url.getPort(), user, password));
    legacy.setGsonBuilder(model.gsonBuilder().get());
    try {
      List<?> legacyDocs = legacy.view(model.view() == null ? "_all_docs" : model.view())
          .includeDocs(true)
          .query(model.readType());
      List<Object> newDocs = model.view() == null
          ? collection.findAll()
          : collection.view(model.designDocument(), model.viewName()).docs();

      Map<String, JsonNode> expected = byId(legacyGson, legacyDocs);
      Map<String, JsonNode> actual = byId(legacyGson, newDocs);
      assertEquals(expected.keySet(), actual.keySet(), model + ": documents found");

      var failures = new ArrayList<String>();
      expected.forEach((id, tree) -> collect(failures, model + " " + id + " read", tree, actual.get(id)));
      for (Object document : newDocs) {
        collect(failures, model + " encode", JsonTrees.parse(LegacyCouchDbPath.encode(legacyGson, document)),
            JsonTrees.parse(codec.encode(document)));
      }
      expected.keySet().stream().filter(id -> !id.startsWith("#")).limit(MAX_FIND_COMPARISONS).forEach(id ->
          collect(failures, model + " " + id + " find",
              reencode(legacyGson, legacy.find(model.readType(), id)),
              reencode(legacyGson, collection.get(id))));
      if (!failures.isEmpty()) {
        fail(String.join("\n", failures));
      }
      System.out.printf("%s: %d documents compared%n", model, expected.size());
    } finally {
      legacy.shutdown();
    }
  }

  /**
   * Re-encoded documents by id. LightCouch's {@code _all_docs} also decodes design documents, which the new client
   * leaves out; those and the {@code null} entries for deleted rows are dropped here.
   */
  private static Map<String, JsonNode> byId(Gson legacyGson, List<?> documents) {
    var result = new LinkedHashMap<String, JsonNode>();
    for (int i = 0; i < documents.size(); i++) {
      if (documents.get(i) == null) {
        continue;
      }
      JsonNode tree = reencode(legacyGson, documents.get(i));
      String id = tree.path("_id").asText("");
      if (!id.startsWith("_design/")) {
        result.put(id.isEmpty() ? "#" + i : id, tree);
      }
    }
    return result;
  }

  private static void collect(List<String> failures, String step, JsonNode expected, JsonNode actual) {
    List<String> differences = JsonTrees.differences(expected, actual);
    if (!differences.isEmpty()) {
      failures.add(step + ": LightCouch and new client differ\n  " + String.join("\n  ", differences));
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<Object> rawClass(Class<?> type) {
    return (Class<Object>) type;
  }

  private static JsonNode reencode(Gson legacyGson, Object document) {
    return JsonTrees.parse(LegacyCouchDbPath.encode(legacyGson, document));
  }
}
