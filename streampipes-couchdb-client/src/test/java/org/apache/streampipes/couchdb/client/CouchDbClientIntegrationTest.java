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

package org.apache.streampipes.couchdb.client;

import org.apache.streampipes.couchdb.client.codec.GsonCodec;
import org.apache.streampipes.couchdb.client.codec.JacksonCodec;
import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.couchdb.client.design.SyncResult;
import org.apache.streampipes.couchdb.client.exception.CouchDbAuthenticationException;
import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;
import org.apache.streampipes.couchdb.client.exception.DatabaseNotFoundException;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;
import org.apache.streampipes.couchdb.client.query.MangoIndex;
import org.apache.streampipes.couchdb.client.query.MangoQuery;
import org.apache.streampipes.couchdb.client.query.Selector;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the client against CouchDB 3.3.1, the version shipped in the StreamPipes compose files. Enable with
 * {@code -Dstreampipes.couchdb.integration=true}.
 */
@EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
public class CouchDbClientIntegrationTest {

  private static final String USER = "admin";
  private static final String PASSWORD = "admin";

  private static GenericContainer<?> couchDb;
  private static CouchServer server;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  record JacksonDoc(@JsonProperty("_id") String id, @JsonProperty("_rev") String rev, String name, int count) {
  }

  static class GsonDoc {
    @SerializedName("_id")
    String id;
    @SerializedName("_rev")
    String rev;
    String appDocType;
    String owner;
    double value;
  }

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

  @Test
  public void managesDatabases() {
    var db = server.database(newName());
    assertFalse(db.exists());
    assertTrue(db.create());
    assertFalse(db.create());
    assertTrue(server.allDatabases().contains(db.name()));
    db.delete();
    assertFalse(db.exists());
    assertThrows(DatabaseNotFoundException.class, () -> db.raw().find("a"));
  }

  @Test
  public void createsDatabaseOnFirstUseAndAfterExternalDelete() {
    String name = newName();
    var db = server.database(name, DatabaseOptions.createIfMissing());
    db.raw().create(Map.of("_id", "a"));
    assertTrue(db.exists());

    server.database(name).delete();
    // A second server has its own cache and does not know the database is gone.
    try (var other = CouchServer.builder().url(server.config().baseUrl()).credentials(USER, PASSWORD).build()) {
      var otherDb = other.database(name, DatabaseOptions.createIfMissing());
      otherDb.raw().create(Map.of("_id", "b"));
    }
    db.raw().create(Map.of("_id", "c"));
    assertEquals(2, db.raw().findAll().size());
  }

  @Test
  public void usesExistingDatabaseWithoutTheRightToCreateDatabases() throws Exception {
    String name = newName();
    server.database(name).create();
    openToAnonymousUsers(name);

    try (var anonymous = CouchServer.builder().url(server.config().baseUrl()).build()) {
      var db = anonymous.database(name, DatabaseOptions.createIfMissing());
      db.ensureExists();
      assertEquals("a", db.raw().create(Map.of("_id", "a")).id());

      var missing = anonymous.database(newName(), DatabaseOptions.createIfMissing());
      assertThrows(CouchDbAuthenticationException.class, missing::ensureExists);
    }
  }

  private static void openToAnonymousUsers(String database) throws Exception {
    String auth = Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
    try (var http = HttpClient.newHttpClient()) {
      var uri = URI.create(server.config().baseUrl() + "/" + database + "/_security");
      var response = http.send(HttpRequest.newBuilder(uri)
          .header("Authorization", "Basic " + auth)
          .header("Content-Type", "application/json")
          .PUT(HttpRequest.BodyPublishers.ofString("{\"members\":{\"names\":[],\"roles\":[]}}"))
          .build(), HttpResponse.BodyHandlers.ofString());
      assertEquals(200, response.statusCode(), response.body());
    }
  }

  @Test
  public void writesAndReadsJacksonDocuments() {
    var docs = newDatabase().typed(JacksonDoc.class, JacksonCodec.defaults());

    DocumentRef created = docs.create(new JacksonDoc(null, null, "first", 1));
    assertEquals(32, created.id().length());
    JacksonDoc stored = docs.get(created.id());
    assertEquals("first", stored.name());
    assertEquals(created.rev(), stored.rev());
    assertEquals(created.rev(), docs.revision(created.id()).orElseThrow());

    DocumentRef updated = docs.update(new JacksonDoc(stored.id(), stored.rev(), "second", 2));
    assertNotEquals(created.rev(), updated.rev());
    assertThrows(DocumentConflictException.class,
        () -> docs.update(new JacksonDoc(stored.id(), stored.rev(), "stale", 3)));
    assertThrows(DocumentConflictException.class, () -> docs.create(new JacksonDoc(stored.id(), null, "dup", 4)));

    DocumentRef upserted = docs.upsert(new JacksonDoc(stored.id(), "1-ignored", "upserted", 5));
    assertEquals("upserted", docs.get(upserted.id()).name());
    docs.upsert(new JacksonDoc("fresh", null, "new", 6));
    assertTrue(docs.exists("fresh"));

    assertTrue(docs.delete(stored.id()));
    assertFalse(docs.delete(stored.id()));
    assertTrue(docs.find(stored.id()).isEmpty());
    assertThrows(DocumentNotFoundException.class, () -> docs.get(stored.id()));
  }

  @Test
  public void writesAndReadsGsonDocumentsWithSpecialIds() {
    var docs = newDatabase().typed(GsonDoc.class, GsonCodec.of(new Gson()));
    var doc = new GsonDoc();
    doc.id = "sp:asset/1 +ä?#";
    doc.value = 0.1;

    DocumentRef ref = docs.withMetadataWriter((d, r) -> d.rev = r.rev()).create(doc);
    assertEquals(ref.rev(), doc.rev);
    GsonDoc stored = docs.get(doc.id);
    assertEquals(doc.id, stored.id);
    assertEquals(0.1, stored.value);
    assertEquals(List.of(doc.id), docs.allDocs().ids());
  }

  @Test
  public void leavesDesignDocumentsOutOfAllDocs() {
    var db = newDatabase();
    db.raw().create(Map.of("_id", "a"));
    db.design().sync(DesignDocument.named("pipelines").withView("all", "function(doc) { emit(doc._id, doc); }"));

    assertEquals(List.of("a"), db.raw().allDocs().ids());
    assertEquals(1, db.raw().findAll().size());
    assertEquals(2, db.raw().allDocs().includeDesignDocuments().docs().size());
  }

  @Test
  public void queriesViews() {
    var db = newDatabase();
    var design = DesignDocument.named("appDocType")
        .withView("appDocType", "function(doc) { if (doc.appDocType) { emit([doc.appDocType, doc._id], 1); } }")
        .withView("by-owner", "function(doc) { if (doc.owner) { emit(doc.owner, doc._id); } }");
    assertEquals(SyncResult.CREATED, db.design().sync(design));
    assertEquals(SyncResult.UNCHANGED, db.design().sync(design));

    db.raw().create(Map.of("_id", "a1", "appDocType", "asset", "owner", "u1"));
    db.raw().create(Map.of("_id", "a2", "appDocType", "asset", "owner", "u2"));
    db.raw().create(Map.of("_id", "c1", "appDocType", "chart", "owner", "u1"));

    var assets = db.raw().view("appDocType", "appDocType")
        .startKey(List.of("asset"))
        .endKey(List.of("asset", Map.of()))
        .docs();
    assertEquals(List.of("a1", "a2"), assets.stream().map(d -> d.get("_id")).toList());

    assertEquals(List.of("a1", "c1"), db.raw().view("appDocType", "by-owner").key("u1").values(String.class));
    assertEquals(3, db.raw().view("appDocType", "by-owner").keys(List.of("u1", "u2", "nobody")).docs().size());
    assertEquals(1, db.raw().view("appDocType", "by-owner").limit(1).rows().size());
    assertEquals("a2", db.raw().view("appDocType", "by-owner").descending().first().orElseThrow().get("_id"));

    var extended = design.withView("count", new org.apache.streampipes.couchdb.client.design.ViewDefinition(
        "function(doc) { emit(doc.appDocType, 1); }", "_count"));
    assertEquals(SyncResult.UPDATED, db.design().sync(extended));
    var counts = db.raw().view("appDocType", "count").rows();
    assertEquals(3, counts.get(0).value().asInt());
    assertEquals(3, db.design().get("appDocType").orElseThrow().views().size());
  }

  @Test
  public void runsMangoQueries() {
    var db = newDatabase();
    var docs = db.typed(GsonDoc.class, GsonCodec.of(new Gson()));
    IntStream.range(0, 60).forEach(i -> {
      var doc = new GsonDoc();
      doc.id = "doc-%03d".formatted(i);
      doc.appDocType = "maintenance";
      doc.owner = i % 2 == 0 ? "even" : "odd";
      doc.value = i;
      docs.create(doc);
    });

    var byOwner = MangoQuery.where(Selector.eq("appDocType", "maintenance").andEq("owner", "even"));
    FindResult<GsonDoc> firstPage = docs.find(byOwner);
    assertEquals(25, firstPage.docs().size(), "CouchDB's default limit");
    assertTrue(firstPage.warning() != null && firstPage.warning().contains("index"));
    assertEquals(30, docs.findAll(byOwner).size());
    assertEquals(30, docs.findAll(byOwner, 7).size(), "pages of 7");
    assertEquals(12, docs.findAll(byOwner.copy().limit(12), 5).size(), "limit caps the total");
    assertEquals(List.of("doc-004", "doc-006", "doc-008"),
        docs.findAll(MangoQuery.where(Selector.eq("owner", "even").and("_id", "$gt", ""))
            .sortAscending("_id").skip(2).limit(3), 2).stream().map(d -> d.id).toList(),
        "skip on the first page only");

    assertTrue(db.indexes().ensure(MangoIndex.on("appDocType", "owner").named("by-owner")));
    assertFalse(db.indexes().ensure(MangoIndex.on("appDocType", "owner").named("by-owner")));
    assertEquals(60, docs.findAll().size(), "index design document stays out of findAll");

    var range = MangoQuery.where(Selector.eq("appDocType", "maintenance")
            .and("value", "$gte", 10)
            .and("value", "$lt", 13))
        .fields("_id", "value")
        .limit(100);
    var partial = db.raw().find(range).docs();
    assertEquals(3, partial.size());
    assertEquals("doc-010", partial.get(0).get("_id"));
    assertEquals(10, ((Number) partial.get(0).get("value")).intValue());
    assertEquals(2, partial.get(0).size());
  }

  @Test
  public void storesAttachments() throws IOException {
    var db = newDatabase();
    byte[] image = "fake-jpeg".getBytes(StandardCharsets.UTF_8);

    DocumentRef first = db.attachments().put("img-1", null, "img-1", "image/jpeg", image);
    Attachment stored = db.attachments().get("img-1", "img-1").orElseThrow();
    assertEquals("image/jpeg", stored.contentType());
    assertArrayEquals(image, stored.data());
    try (InputStream stream = db.attachments().open("img-1", "img-1")) {
      assertArrayEquals(image, stream.readAllBytes());
    }

    DocumentRef second = db.attachments().put("img-1", first.rev(), "dir/second.txt", "text/plain",
        "hello".getBytes(StandardCharsets.UTF_8));
    assertThrows(DocumentConflictException.class,
        () -> db.attachments().put("img-1", first.rev(), "third", "text/plain", new byte[] {1}));
    assertEquals("hello", new String(db.attachments().get("img-1", "dir/second.txt").orElseThrow().data(),
        StandardCharsets.UTF_8));

    db.attachments().delete("img-1", second.rev(), "img-1");
    assertTrue(db.attachments().get("img-1", "img-1").isEmpty());
    assertTrue(db.attachments().get("missing", "x").isEmpty());
    assertThrows(DocumentNotFoundException.class, () -> db.attachments().open("missing", "x"));
  }

  @Test
  public void keepsUnmanagedDesignFieldsOnSync() {
    var db = newDatabase();
    var withValidation = new HashMap<String, Object>(Map.of(
        "_id", "_design/users",
        "validate_doc_update", "function(newDoc) { }",
        "views", Map.of("old", Map.of("map", "function(doc) { emit(1); }"))));
    db.raw().create(withValidation);

    assertEquals(SyncResult.UPDATED,
        db.design().sync(DesignDocument.named("users").withView("username", "function(doc) { emit(2); }")));
    var stored = db.design().get("_design/users").orElseThrow();
    assertEquals(List.of("username"), List.copyOf(stored.views().keySet()));
    assertTrue(stored.otherFields().containsKey("validate_doc_update"));
  }

  @Test
  public void reportsAuthenticationFailures() {
    try (var wrong = CouchServer.builder().url(server.config().baseUrl()).credentials(USER, "wrong").build()) {
      assertThrows(CouchDbAuthenticationException.class, wrong::allDatabases);
    }
  }

  @Test
  public void reportsTransportFailures() throws IOException {
    int port;
    try (var socket = new ServerSocket(0)) {
      port = socket.getLocalPort();
    }
    try (var unreachable = CouchServer.builder().url("http", "localhost", port).build()) {
      assertThrows(CouchDbTransportException.class, unreachable::allDatabases);
    }
  }

  private static CouchDatabase newDatabase() {
    var db = server.database(newName());
    db.create();
    return db;
  }

  private static String newName() {
    return "test-" + UUID.randomUUID();
  }
}
