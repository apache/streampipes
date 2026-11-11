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

import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.couchdb.client.design.SyncResult;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentMappingException;
import org.apache.streampipes.couchdb.client.query.MangoQuery;
import org.apache.streampipes.couchdb.client.query.Selector;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypedCollectionTest {

  private static final String OK = "{\"ok\":true,\"id\":\"%s\",\"rev\":\"%s\"}";

  @Test
  public void sendsBasicAuthAndJson() {
    var stub = StubTransport.create().respond(200, "{\"_id\":\"a\"}");
    stub.server().database("db").raw().get("a");
    assertEquals("Basic YWRtaW46c2VjcmV0", stub.headers.get(0).get("Authorization"));
    assertEquals("application/json", stub.request(0).accept());
  }

  @Test
  public void reportsAResponseThatIsNotADocumentAsMappingError() {
    var stub = StubTransport.create().respond(200, "[\"not\", \"a document\"]");

    assertThrows(DocumentMappingException.class, () -> stub.server().database("db").raw().get("a"));
  }

  @Test
  public void createRecognisesItsOwnWriteWhenARepeatedPutConflicts() {
    var stub = StubTransport.create()
        .respond(409, "{\"error\":\"conflict\",\"reason\":\"Document update conflict.\"}")
        .respond(200, "", Map.of("ETag", "\"1-a\""));
    var doc = new LinkedHashMap<String, Object>();
    doc.put("name", "x");

    var ref = stub.server().database("db").raw().create(doc);

    assertEquals("1-a", ref.rev());
    assertEquals("HEAD", stub.request(1).method());
    assertEquals(stub.request(0).path(), stub.request(1).path());
  }

  @Test
  public void createKeepsTheConflictForAGivenId() {
    var stub = StubTransport.create()
        .respond(409, "{\"error\":\"conflict\",\"reason\":\"Document update conflict.\"}");

    assertThrows(DocumentConflictException.class,
        () -> stub.server().database("db").raw().create(new LinkedHashMap<>(Map.of("_id", "given"))));
    assertEquals(1, stub.requests.size());
  }

  @Test
  public void createGeneratesIdAndStripsNullMetadata() {
    var stub = StubTransport.create().respond(201, OK.formatted("gen", "1-a"));
    var doc = new LinkedHashMap<String, Object>();
    doc.put("_id", null);
    doc.put("_rev", null);
    doc.put("name", "x");

    var ref = stub.server().database("db").raw().create(doc);

    assertEquals("PUT", stub.request(0).method());
    assertTrue(stub.request(0).path().matches("/db/[0-9a-f]{32}"));
    assertEquals("{\"name\":\"x\"}", stub.body(0));
    assertEquals(new DocumentRef("gen", "1-a"), ref);
    assertFalse(doc.containsKey("_rev") && doc.get("_rev") != null, "no write-back without opt-in");
  }

  @Test
  public void postLetsCouchDbAssignTheId() {
    var stub = StubTransport.create().respond(201, OK.formatted("server-id", "1-a"));
    var doc = new LinkedHashMap<String, Object>();
    doc.put("_id", null);
    doc.put("name", "x");

    var ref = stub.server().database("db").raw().post(doc);

    assertEquals("POST", stub.request(0).method());
    assertEquals("/db", stub.request(0).path());
    assertEquals("{\"name\":\"x\"}", stub.body(0));
    assertEquals(new DocumentRef("server-id", "1-a"), ref);

    var withId = new LinkedHashMap<String, Object>();
    withId.put("_id", "given");
    withId.put("_rev", null);
    stub.respond(201, OK.formatted("given", "1-b"));
    stub.server().database("db").raw().post(withId);
    assertEquals("{\"_id\":\"given\"}", stub.body(1));
  }

  @Test
  public void createRejectsRevision() {
    var stub = StubTransport.create();
    assertThrows(IllegalArgumentException.class,
        () -> stub.server().database("db").raw().create(Map.of("_id", "a", "_rev", "1-a")));
    assertTrue(stub.requests.isEmpty());
  }

  @Test
  public void createMapsConflict() {
    var stub = StubTransport.create().respond(409, "{\"error\":\"conflict\",\"reason\":\"Document update conflict.\"}");
    assertThrows(DocumentConflictException.class,
        () -> stub.server().database("db").raw().create(Map.of("_id", "a")));
  }

  @Test
  public void metadataWriterIsOptIn() {
    var stub = StubTransport.create().respond(201, OK.formatted("a", "1-a"));
    var doc = new LinkedHashMap<String, Object>(Map.of("_id", "a"));
    stub.server().database("db").raw().withMetadataWriter(MetadataWriter.forMaps()).create(doc);
    assertEquals("1-a", doc.get("_rev"));
  }

  @Test
  public void updateNeedsIdAndRevision() {
    var stub = StubTransport.create().respond(201, OK.formatted("a/b", "2-b"));
    var raw = stub.server().database("db").raw();
    assertThrows(IllegalArgumentException.class, () -> raw.update(Map.of("_id", "a")));
    raw.update(Map.of("_id", "a/b", "_rev", "1-a"));
    assertEquals("/db/a%2Fb", stub.request(0).path());
  }

  @Test
  public void upsertOverwritesCurrentRevision() throws Exception {
    var stub = StubTransport.create()
        .respond(200, "", Map.of("ETag", "\"3-c\""))
        .respond(201, OK.formatted("a", "4-d"));
    stub.server().database("db").raw().upsert(Map.of("_id", "a", "_rev", "1-old", "v", 1));
    assertEquals("HEAD", stub.request(0).method());
    assertEquals("/db/a?rev=3-c", stub.request(1).path());
    assertEquals(Map.of("_id", "a", "v", 1), CouchJson.MAPPER.readValue(stub.body(1), Map.class));
  }

  @Test
  public void deleteByIdReadsRevisionFirst() {
    var stub = StubTransport.create()
        .respond(200, "", Map.of("ETag", "\"5-e\""))
        .respond(200, OK.formatted("a", "6-f"))
        .respond(404, "");
    var raw = stub.server().database("db").raw();
    assertTrue(raw.delete("a"));
    assertEquals("/db/a?rev=5-e", stub.request(1).path());
    assertFalse(raw.delete("missing"));
  }

  @Test
  public void findReturnsEmptyOnMissingDocument() {
    var stub = StubTransport.create().respond(404, "{\"error\":\"not_found\",\"reason\":\"missing\"}");
    assertEquals(Optional.empty(), stub.server().database("db").raw().find("a"));
  }

  @Test
  public void createsDatabaseOncePerServer() {
    var stub = StubTransport.create()
        .respond(404, "")
        .respond(201, "{\"ok\":true}")
        .respond(200, "{\"_id\":\"a\"}")
        .respond(200, "{\"_id\":\"b\"}");
    var server = stub.server();
    server.database("db", DatabaseOptions.createIfMissing()).raw().get("a");
    server.database("db", DatabaseOptions.createIfMissing()).raw().get("b");
    assertEquals(List.of("HEAD /db", "PUT /db", "GET /db/a", "GET /db/b"),
        stub.requests.stream().map(r -> r.method() + " " + r.path()).toList());
  }

  @Test
  public void doesNotCreateAnExistingDatabase() {
    var stub = StubTransport.create()
        .respond(200, "")
        .respond(200, "{\"_id\":\"a\"}");
    stub.server().database("db", DatabaseOptions.createIfMissing()).raw().get("a");
    assertEquals(List.of("HEAD /db", "GET /db/a"),
        stub.requests.stream().map(r -> r.method() + " " + r.path()).toList());
  }

  @Test
  public void acceptsDatabaseCreatedByAnotherClientAfterTheCheck() {
    var stub = StubTransport.create()
        .respond(404, "")
        .respond(412, "{\"error\":\"file_exists\"}")
        .respond(200, "{\"_id\":\"a\"}");
    stub.server().database("db", DatabaseOptions.createIfMissing()).raw().get("a");
    assertEquals(List.of("HEAD /db", "PUT /db", "GET /db/a"),
        stub.requests.stream().map(r -> r.method() + " " + r.path()).toList());
  }

  @Test
  public void recreatesDeletedDatabaseAndRetries() {
    var stub = StubTransport.create()
        .respond(200, "")
        .respond(404, "{\"error\":\"not_found\",\"reason\":\"Database does not exist.\"}")
        .respond(404, "")
        .respond(201, "{\"ok\":true}")
        .respond(201, OK.formatted("a", "1-a"));
    var ref = stub.server().database("db", DatabaseOptions.createIfMissing()).raw().create(Map.of("_id", "a"));
    assertEquals("1-a", ref.rev());
    assertEquals(5, stub.requests.size());
  }

  @Test
  public void viewQueryEncodesKeysAndPostsKeyLists() {
    var stub = StubTransport.create()
        .respond(200, "{\"rows\":[]}")
        .respond(200, "{\"rows\":[{\"id\":\"p\",\"key\":\"a1\",\"value\":\"p\"}]}");
    var raw = stub.server().database("db").raw();

    raw.view("appDocType", "appDocType").startKey(List.of("t")).endKey(List.of("t", Map.of())).docs();
    assertEquals("/db/_design/appDocType/_view/appDocType?start_key=%5B%22t%22%5D"
        + "&end_key=%5B%22t%22%2C%7B%7D%5D&include_docs=true", stub.request(0).path());

    List<String> values = raw.view("_design/adapters", "used-adapters").keys(List.of("a1", "a2")).values(String.class);
    assertEquals("POST", stub.request(1).method());
    assertEquals("/db/_design/adapters/_view/used-adapters", stub.request(1).path());
    assertEquals("{\"keys\":[\"a1\",\"a2\"]}", stub.body(1));
    assertEquals(List.of("p"), values);
  }

  @Test
  public void findAllPagesWithBookmarks() {
    var stub = StubTransport.create()
        .respond(200, "{\"docs\":[{\"_id\":\"a\"},{\"_id\":\"b\"}],\"bookmark\":\"b1\"}")
        .respond(200, "{\"docs\":[{\"_id\":\"c\"}],\"bookmark\":\"b2\"}");
    var query = MangoQuery.where(Selector.eq("appDocType", "t").andEq("owner", "u")).skip(1);

    var docs = stub.server().database("db").raw().findAll(query, 2);

    assertEquals(3, docs.size());
    assertEquals("{\"selector\":{\"appDocType\":{\"$eq\":\"t\"},\"owner\":{\"$eq\":\"u\"}},\"limit\":2,\"skip\":1}",
        stub.body(0));
    assertEquals("{\"selector\":{\"appDocType\":{\"$eq\":\"t\"},\"owner\":{\"$eq\":\"u\"}},\"limit\":2,"
        + "\"bookmark\":\"b1\"}", stub.body(1));
  }

  @Test
  public void findAllStopsAtQueryLimit() {
    var stub = StubTransport.create()
        .respond(200, "{\"docs\":[{\"_id\":\"a\"},{\"_id\":\"b\"}],\"bookmark\":\"b1\"}")
        .respond(200, "{\"docs\":[{\"_id\":\"c\"}],\"bookmark\":\"b2\"}");
    var query = MangoQuery.where(Selector.eq("appDocType", "t")).limit(3);

    var docs = stub.server().database("db").raw().findAll(query, 2);

    assertEquals(3, docs.size());
    assertEquals(2, stub.requests.size(), "no request after the limit is reached");
    assertTrue(stub.body(1).contains("\"limit\":1"), "last page asks only for what is missing");
    assertThrows(IllegalArgumentException.class, () -> stub.server().database("db").raw().findAll(query, 0));
  }

  @Test
  public void queryFromMapKeepsOtherOptions() {
    var body = new LinkedHashMap<String, Object>();
    body.put("selector", Map.of("appDocType", "t"));
    body.put("limit", 10);
    body.put("execution_stats", true);
    body.put("r", 1);

    var query = MangoQuery.fromMap(body);

    assertEquals(body, query.toMap());
    assertEquals(body, query.copy().toMap());
  }

  @Test
  public void rejectsAQueryWithoutASelectorObject() {
    assertThrows(IllegalArgumentException.class, () -> MangoQuery.fromMap(Map.of("limit", 1)));
    assertThrows(IllegalArgumentException.class, () -> MangoQuery.fromMap(Map.of("selector", "not-an-object")));
  }

  @Test
  public void selectorMergesConditionsOnSameField() {
    var selector = Selector.of("ts", "$gte", 1).and("ts", "$lt", 2).toMap();
    assertEquals(Map.of("ts", Map.of("$gte", 1, "$lt", 2)), selector);
  }

  @Test
  public void designSyncReportsWhatItDid() {
    String stored = "{\"_id\":\"_design/users\",\"_rev\":\"1-a\",\"language\":\"javascript\","
        + "\"views\":{\"username\":{\"map\":\"function(doc){}\"}},\"validate_doc_update\":\"function(){}\"}";
    var stub = StubTransport.create()
        .respond(404, "{\"error\":\"not_found\",\"reason\":\"missing\"}")
        .respond(201, OK.formatted("_design/users", "1-a"))
        .respond(200, stored)
        .respond(200, stored)
        .respond(201, OK.formatted("_design/users", "2-b"));
    var design = stub.server().database("users").design();
    var desired = DesignDocument.named("users").withView("username", "function(doc){}");

    assertEquals(SyncResult.CREATED, design.sync(desired));
    assertFalse(stub.body(1).contains("_rev"), "no rev on create");
    assertEquals(SyncResult.UNCHANGED, design.sync(desired));
    assertEquals(SyncResult.UPDATED, design.sync(desired.withView("role", "function(doc){emit(1)}")));
    String update = stub.body(4);
    assertTrue(update.contains("\"_rev\":\"1-a\""));
    assertTrue(update.contains("\"validate_doc_update\""), "keeps fields it does not manage");
    assertTrue(update.contains("\"role\""));
    assertEquals("/users/_design/users", stub.request(4).path());
  }

  @Test
  public void designSyncWritesChangedOtherFields() {
    String stored = "{\"_id\":\"_design/users\",\"_rev\":\"1-a\",\"language\":\"javascript\","
        + "\"views\":{\"username\":{\"map\":\"function(doc){}\"}},\"validate_doc_update\":\"function(){}\"}";
    var stub = StubTransport.create()
        .respond(200, stored)
        .respond(200, stored)
        .respond(201, OK.formatted("_design/users", "2-b"));
    var design = stub.server().database("users").design();
    var desired = DesignDocument.named("users").withView("username", "function(doc){}");

    assertEquals(SyncResult.UNCHANGED, design.sync(desired.withOtherFields(
        Map.of("validate_doc_update", CouchJson.MAPPER.getNodeFactory().textNode("function(){}")))));
    assertEquals(SyncResult.UPDATED, design.sync(desired.withOtherFields(
        Map.of("validate_doc_update", CouchJson.MAPPER.getNodeFactory().textNode("function(n){}")))));
    assertTrue(stub.body(2).contains("function(n){}"));
  }
}
