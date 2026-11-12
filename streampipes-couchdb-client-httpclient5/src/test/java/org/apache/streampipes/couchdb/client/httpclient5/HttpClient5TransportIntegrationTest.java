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

package org.apache.streampipes.couchdb.client.httpclient5;

import org.apache.streampipes.couchdb.client.CouchDatabase;
import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.DatabaseOptions;
import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.couchdb.client.exception.CouchDbAuthenticationException;
import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the client on {@link HttpClient5Transport} against CouchDB 3.3.1. Enable with
 * {@code -Dstreampipes.couchdb.integration=true}.
 */
@EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
public class HttpClient5TransportIntegrationTest {

  private static GenericContainer<?> couchDb;
  private static CouchServer server;

  @BeforeAll
  public static void startCouchDb() {
    couchDb = new GenericContainer<>("couchdb:3.3.1")
        .withEnv("COUCHDB_USER", "admin")
        .withEnv("COUCHDB_PASSWORD", "admin")
        .withExposedPorts(5984)
        .waitingFor(Wait.forHttp("/_up").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(60)));
    couchDb.start();
    server = builder().credentials("admin", "admin").build();
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
  public void readsAndWritesDocuments() {
    var raw = newDatabase().raw();
    var ref = raw.create(Map.of("_id", "a/b", "n", 1));
    assertEquals(1, raw.get("a/b").get("n"));
    assertEquals(ref.rev(), raw.revision("a/b").orElseThrow());
    assertThrows(DocumentConflictException.class, () -> raw.create(Map.of("_id", "a/b")));
    raw.update(Map.of("_id", "a/b", "_rev", ref.rev(), "n", 2));
    assertTrue(raw.delete("a/b"));
    assertThrows(DocumentNotFoundException.class, () -> raw.get("a/b"));
  }

  @Test
  public void queriesViewsWithPostedKeys() {
    var db = newDatabase();
    db.design().sync(DesignDocument.named("d").withView("by-owner", "function(doc) { emit(doc.owner, null); }"));
    db.raw().create(Map.of("_id", "1", "owner", "u1"));
    db.raw().create(Map.of("_id", "2", "owner", "u2"));
    assertEquals(List.of("1", "2"), db.raw().view("d", "by-owner").keys(List.of("u1", "u2")).ids());
  }

  @Test
  public void streamsAttachments() throws IOException {
    var db = newDatabase();
    byte[] data = "content".getBytes(StandardCharsets.UTF_8);
    db.attachments().put("doc", null, "file.txt", "text/plain", data);
    assertEquals("text/plain", db.attachments().get("doc", "file.txt").orElseThrow().contentType());
    try (InputStream in = db.attachments().open("doc", "file.txt")) {
      assertArrayEquals(data, in.readAllBytes());
    }
  }

  @Test
  public void handlesConcurrentWritesOnPooledConnections() throws Exception {
    var raw = server.database("concurrent-" + UUID.randomUUID(), DatabaseOptions.createIfMissing()).raw();
    try (var executor = Executors.newFixedThreadPool(32)) {
      List<Future<Object>> results = new ArrayList<>();
      for (int i = 0; i < 640; i++) {
        Callable<Object> task = () -> raw.create(Map.of("n", 1));
        results.add(executor.submit(task));
      }
      for (Future<Object> result : results) {
        result.get();
      }
    }
    assertEquals(640, raw.findAll().size());
  }

  @Test
  public void reportsErrors() throws IOException {
    try (var wrong = builder().credentials("admin", "wrong").build()) {
      assertThrows(CouchDbAuthenticationException.class, wrong::allDatabases);
    }
    int port;
    try (var socket = new ServerSocket(0)) {
      port = socket.getLocalPort();
    }
    try (var unreachable = CouchServer.builder().url("http", "localhost", port)
        .transport(HttpClient5Transport::new).build()) {
      assertThrows(CouchDbTransportException.class, unreachable::allDatabases);
    }
  }

  private static CouchServer.Builder builder() {
    return CouchServer.builder()
        .url("http", couchDb.getHost(), couchDb.getMappedPort(5984))
        .transport(HttpClient5Transport::new);
  }

  private static CouchDatabase newDatabase() {
    var db = server.database("test-" + UUID.randomUUID());
    db.create();
    return db;
  }
}
