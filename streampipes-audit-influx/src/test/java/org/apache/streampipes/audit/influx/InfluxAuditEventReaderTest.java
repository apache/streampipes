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

import org.apache.streampipes.audit.api.AuditQuery;
import org.apache.streampipes.dataexplorer.influx.InfluxQueryTransport;
import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InfluxAuditEventReaderTest {
  private static final long TIME = 1_790_337_600_000_000_123L;
  private static final String ID = "f1ee7c4a-7829-4a9d-a2e8-92bf4d5f761a";

  @Test
  void pagesAcrossTypesWithExactTimestampAndNoDetailProjection() throws Exception {
    try (var fixture = new Fixture()) {
      fixture.response.set(fixture.rows(false, row(TIME, "sp.adapter.create"),
          row(TIME + 2, "bytefabrik.ai-notebook.create"), row(TIME + 1, "sp.adapter.create")));
      var query = query(null, "user'\\name");
      var page = fixture.reader.query(query);
      assertEquals(2, page.items().size());
      assertEquals("adapter", page.items().getFirst().resourceType());
      assertEquals("1", page.items().getFirst().resourceId());
      assertEquals(Instant.ofEpochSecond(0, TIME + 2).toString(), page.items().getFirst().storedAt());
      assertFalse(fixture.statement.get().contains("details"));
      assertTrue(fixture.statement.get().contains("LIMIT 3"));
      assertTrue(fixture.statement.get().contains("\"actor\" = 'user\\'\\\\name'"));
      fixture.response.set(fixture.rows(false, row(TIME, "sp.adapter.create")));
      var next = fixture.reader.query(query(page.nextCursor(), "user'\\name"));
      assertEquals(1, next.items().size());
      assertTrue(fixture.statement.get().contains("time < " + (TIME + 1)));
      assertThrows(IllegalArgumentException.class, () -> fixture.reader.query(query(page.nextCursor(), "other")));
    }
  }

  @Test
  void loadsDetailsOnlyOnDemandAndChecksIdentityInQuery() throws Exception {
    try (var fixture = new Fixture()) {
      fixture.response.set(fixture.rows(false, row(TIME, "sp.adapter.create")));
      var entry = fixture.reader.query(query(null, null)).items().getFirst();
      fixture.response.set(fixture.rows(true, List.of(TIME, ID, "sp.adapter.create", "succeeded", "actor", "adapter", "1",
          "{\"recorded_at\":\"2026-09-25T11:59:59Z\",\"changes\":[{\"path\":\"name\",\"before\":null,\"after\":\"x\"}]}")));
      var detail = fixture.reader.find(UUID.fromString(ID), entry.locator()).orElseThrow();
      assertEquals("2026-09-25T11:59:59Z", detail.recordedAt());
      assertTrue(detail.details().containsKey("changes"));
      assertFalse(detail.details().containsKey("recorded_at"));
      assertTrue(fixture.statement.get().contains("time = " + TIME));
      assertTrue(fixture.statement.get().contains("\"event_id\" = '" + ID + "'"));
      fixture.response.set("{\"results\":[{}]}");
      assertTrue(fixture.reader.find(UUID.fromString(ID), entry.locator()).isEmpty());
    }
  }

  @Test
  void validatesBoundsAndDoesNotTreatStorageErrorsAsEmptyResults() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> new AuditQuery(Instant.EPOCH, Instant.now(), null, null, null, 50, null));
    try (var fixture = new Fixture()) {
      fixture.response.set("{\"results\":[{\"error\":\"unavailable\"}]}");
      assertThrows(RuntimeException.class, () -> fixture.reader.query(query(null, null)));
      assertThrows(IllegalArgumentException.class, () -> fixture.reader.find(UUID.fromString(ID), "invalid"));
    }
  }

  private static List<Object> row(long time, String type) {
    return Arrays.asList(time, ID, type, "succeeded", "actor", "adapter", "1");
  }

  @Test
  void readsEventsWithoutResourceFields() throws Exception {
    try (var fixture = new Fixture()) {
      fixture.response.set(new ObjectMapper().writeValueAsString(Map.of("results", List.of(Map.of("series", List.of(
          Map.of("name", "audit_events", "columns", List.of("time", "event_id", "event_type", "outcome", "actor"),
              "values", List.of(List.of(TIME, ID, "sp.user.login", "succeeded", "user-1")))))))));
      var entry = fixture.reader.query(query(null, null)).items().getFirst();
      assertNull(entry.resourceType());
      assertNull(entry.resourceId());
      assertEquals("sp.user.login", entry.eventType());
    }
  }

  private AuditQuery query(String cursor, String actor) {
    return new AuditQuery(Instant.ofEpochSecond(0, TIME).minusSeconds(1), Instant.ofEpochSecond(0, TIME).plusSeconds(1),
        null, actor, null, 2, cursor);
  }

  private static final class Fixture implements AutoCloseable {
    private final AtomicReference<String> response = new AtomicReference<>();
    private final AtomicReference<String> statement = new AtomicReference<>();
    private final HttpServer server;
    private final InfluxQueryTransport transport;
    private final InfluxAuditEventReader reader;

    private Fixture() throws Exception {
      server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      server.createContext("/query", exchange -> {
        for (var parameter : exchange.getRequestURI().getRawQuery().split("&")) {
          if (parameter.startsWith("q=")) {
            statement.set(URLDecoder.decode(parameter.substring(2), StandardCharsets.UTF_8));
          }
        }
        byte[] body = response.get().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
      });
      server.start();
      transport = new InfluxQueryTransport(InfluxConnectionSettings.from("http", "127.0.0.1",
          server.getAddress().getPort(), "audit", "token"), Duration.ofSeconds(1));
      reader = new InfluxAuditEventReader(transport);
    }

    @SafeVarargs
    private final String rows(boolean details, List<Object>... rows) throws Exception {
      var columns = details ? List.of("time", "event_id", "event_type", "outcome", "actor", "resource_type", "resource_id", "details")
          : List.of("time", "event_id", "event_type", "outcome", "actor", "resource_type", "resource_id");
      return new ObjectMapper().writeValueAsString(Map.of("results", List.of(Map.of("series",
          List.of(Map.of("name", "audit_events", "columns", columns, "values", Arrays.asList(rows)))))));
    }

    @Override
    public void close() {
      transport.close();
      server.stop(0);
    }
  }
}
