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

package org.apache.streampipes.audit.influx;

import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InfluxAuditDatabaseProvisionerTest {
  static void existingDatabase(HttpServer server) {
    server.createContext("/api/v2/orgs", e -> respond(e, 200, "{\"orgs\":[{\"id\":\"org-1\",\"name\":\"sp\"}]}"));
    server.createContext("/api/v2/buckets", e -> respond(e, 200,
        "{\"buckets\":[{\"id\":\"bucket-1\",\"name\":\"audit-only\"}]}"));
    server.createContext("/api/v2/dbrps", e -> respond(e, 200, mapping()));
  }

  private static String mapping() {
    return "{\"content\":[{\"database\":\"audit-only\",\"bucketID\":\"bucket-1\","
        + "\"default\":true,\"retention_policy\":\"autogen\"}]}";
  }

  @Test
  void createsMissingBucketAndMappingAndLeavesExistingDatabaseUntouched() throws Exception {
    createsMissingBucketAndMapping(200);
  }

  @Test
  void createsMissingBucketAfterInflux26NotFoundResponse() throws Exception {
    createsMissingBucketAndMapping(404);
  }

  private void createsMissingBucketAndMapping(int missingStatus) throws Exception {
    var bucketExists = new AtomicBoolean();
    var mappingExists = new AtomicBoolean();
    var mappingUnavailable = new AtomicBoolean(true);
    var bucketCreates = new AtomicInteger();
    var mappingCreates = new AtomicInteger();
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    var mapper = new ObjectMapper();
    server.createContext("/api/v2/orgs", e -> respond(e, 200, "{\"orgs\":[{\"id\":\"org-1\",\"name\":\"sp\"}]}"));
    server.createContext("/api/v2/buckets", e -> {
      assertEquals("Token test-token", e.getRequestHeaders().getFirst("Authorization"));
      if ("POST".equals(e.getRequestMethod())) {
        var body = mapper.readTree(e.getRequestBody());
        assertEquals("audit-only", body.path("name").asText());
        assertEquals("org-1", body.path("orgID").asText());
        assertEquals(0, body.path("retentionRules").size());
        bucketCreates.incrementAndGet();
        bucketExists.set(true);
        respond(e, 201, "{\"id\":\"bucket-1\"}");
      } else {
        if (bucketExists.get()) {
          respond(e, 200, "{\"buckets\":[{\"id\":\"bucket-1\",\"name\":\"audit-only\"}]}");
        } else {
          respond(e, missingStatus, missingStatus == 404
              ? "{\"code\":\"not found\",\"message\":\"bucket not found\"}" : "{\"buckets\":[]}");
        }
      }
    });
    server.createContext("/api/v2/dbrps", e -> {
      if ("POST".equals(e.getRequestMethod())) {
        if (mappingUnavailable.getAndSet(false)) {
          respond(e, 503, "{}");
          return;
        }
        var body = mapper.readTree(e.getRequestBody());
        assertEquals("bucket-1", body.path("bucketID").asText());
        assertEquals("audit-only", body.path("database").asText());
        assertEquals(true, body.path("default").asBoolean());
        mappingCreates.incrementAndGet();
        mappingExists.set(true);
        respond(e, 201, "{\"id\":\"mapping-1\"}");
      } else {
        respond(e, 200, mappingExists.get() ? mapping() : "{\"content\":[]}");
      }
    });
    server.start();
    try (var provisioner = provisioner(server)) {
      assertThrows(IllegalStateException.class, provisioner::ensureDatabase);
      provisioner.ensureDatabase();
      provisioner.ensureDatabase();
      assertEquals(1, bucketCreates.get());
      assertEquals(1, mappingCreates.get());
      mappingExists.set(false);
      provisioner.ensureDatabase();
      assertEquals(1, bucketCreates.get());
      assertEquals(2, mappingCreates.get());
    } finally {
      server.stop(0);
    }
  }

  @Test
  void rejectsBucketLookupFailuresWithoutCreatingAnything() throws Exception {
    for (int status : new int[]{401, 403, 404, 500}) {
      rejectsBucketLookupFailure(status);
    }
  }

  private void rejectsBucketLookupFailure(int status) throws Exception {
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    var creates = new AtomicInteger();
    server.createContext("/api/v2/orgs", e -> respond(e, 200, "{\"orgs\":[{\"id\":\"org-1\",\"name\":\"sp\"}]}"));
    server.createContext("/api/v2/dbrps", e -> {
      if ("POST".equals(e.getRequestMethod())) {
        creates.incrementAndGet();
      }
      respond(e, 200, "{\"content\":[]}");
    });
    server.createContext("/api/v2/buckets", e -> {
      if ("POST".equals(e.getRequestMethod())) {
        creates.incrementAndGet();
      }
      respond(e, status, "{}");
    });
    server.start();
    try (var provisioner = provisioner(server)) {
      assertThrows(IllegalStateException.class, provisioner::ensureDatabase);
      assertEquals(0, creates.get());
    } finally {
      server.stop(0);
    }
  }

  @Test
  void rejectsPermissionFailuresAndConflictingMappingsWithoutCreatingAnything() throws Exception {
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    var denied = new AtomicBoolean(true);
    var creates = new AtomicInteger();
    server.createContext("/api/v2/orgs", e -> respond(e, denied.get() ? 403 : 200,
        "{\"orgs\":[{\"id\":\"org-1\",\"name\":\"sp\"}]}"));
    server.createContext("/api/v2/dbrps", e -> {
      if ("POST".equals(e.getRequestMethod())) {
        creates.incrementAndGet();
      }
      respond(e, 200, mapping().replace("bucket-1", "other-bucket"));
    });
    server.createContext("/api/v2/buckets", e -> {
      if ("POST".equals(e.getRequestMethod())) {
        creates.incrementAndGet();
      }
      respond(e, 200, "{\"buckets\":[]}");
    });
    server.start();
    try (var provisioner = provisioner(server)) {
      assertThrows(IllegalStateException.class, provisioner::ensureDatabase);
      denied.set(false);
      assertThrows(IllegalStateException.class, provisioner::ensureDatabase);
      assertEquals(0, creates.get());
    } finally {
      server.stop(0);
    }
  }

  private InfluxAuditDatabaseProvisioner provisioner(HttpServer server) {
    return new InfluxAuditDatabaseProvisioner(InfluxConnectionSettings.from("http", "127.0.0.1",
        server.getAddress().getPort(), "audit-only", "test-token"), "sp", Duration.ofSeconds(1));
  }

  private static void respond(HttpExchange exchange, int status, String json) throws IOException {
    byte[] body = json.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(status, body.length);
    exchange.getResponseBody().write(body);
    exchange.close();
  }
}
