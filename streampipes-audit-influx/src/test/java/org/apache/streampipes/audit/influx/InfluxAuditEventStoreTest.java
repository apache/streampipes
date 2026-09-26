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

import org.apache.streampipes.audit.api.AuditEvent;
import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.events.AdapterCreatedDetails;
import org.apache.streampipes.audit.events.StandardAuditEvents;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InfluxAuditEventStoreTest {
  @Test
  void preservesWatermarkAndRetriesExactPointWithOnlyEventTypeAsTag() throws Exception {
    var writes = new ArrayList<String>();
    var requests = new AtomicInteger();
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    long watermark = 1_790_337_600_000_000_123L;
    server.createContext("/query", exchange -> {
      assertTrue(exchange.getRequestURI().getQuery().contains("epoch=ns"));
      assertEquals("Token audit-token", exchange.getRequestHeaders().getFirst("Authorization"));
      byte[] body = ("{\"results\":[{\"series\":[{\"columns\":[\"time\",\"last\"],\"values\":[["
          + watermark + ",\"previous\"]]}]}]}").getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      exchange.getResponseBody().write(body);
      exchange.close();
    });
    server.createContext("/write", exchange -> {
      assertEquals("Token audit-token", exchange.getRequestHeaders().getFirst("Authorization"));
      assertEquals("db=audit-only&precision=ns", exchange.getRequestURI().getQuery());
      writes.add(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
      exchange.sendResponseHeaders(requests.getAndIncrement() == 0 ? 503 : 204, -1);
      exchange.close();
    });
    InfluxAuditDatabaseProvisionerTest.existingDatabase(server);
    server.start();
    try (var store = new InfluxAuditEventStore("http://127.0.0.1:" + server.getAddress().getPort(),
        "audit-only", "audit-token", Clock.fixed(Instant.ofEpochSecond(0, watermark), ZoneOffset.UTC))) {
      var time = Instant.ofEpochSecond(0, watermark - TimeUnit.SECONDS.toNanos(30));
      store.append(event(time));
      store.append(event(time));
      assertEquals(3, writes.size());
      assertEquals(writes.get(0), writes.get(1));
      assertTrue(writes.get(0).endsWith(" " + (watermark + 1)));
      assertTrue(writes.get(2).endsWith(" " + (watermark + 2)));
      assertEquals("audit_events,event_type=sp.adapter.create", writes.getFirst().split(" ")[0]);
      assertTrue(writes.getFirst().contains("event_id=\""));
      assertTrue(writes.getFirst().contains("recorded_at"));
      assertFalse(writes.getFirst().contains("reasonCode"));
      assertTrue(writes.getFirst().contains("resource_type=\"adapter\""));
      assertTrue(writes.getFirst().contains("resource_id=\"1\""));
      assertFalse(writes.getFirst().contains("resource="));
      try (var rollbackStore = new InfluxAuditEventStore("http://127.0.0.1:" + server.getAddress().getPort(),
          "audit-only", "audit-token",
          Clock.fixed(Instant.ofEpochSecond(0, watermark).minusSeconds(2), ZoneOffset.UTC))) {
        assertThrows(IllegalStateException.class, () -> rollbackStore.append(event(time)));
        assertEquals(3, writes.size());
      }
      store.append(new AuditEvent<>(UUID.randomUUID(), time, StandardAuditEvents.ADAPTER_CREATE,
          AuditOutcome.SUCCEEDED, "user-1", null, null, null));
      assertEquals(4, writes.size());
      assertFalse(writes.getLast().contains("resource_type"));
      assertFalse(writes.getLast().contains("resource_id"));
    } finally {
      server.stop(0);
    }
  }

  @Test
  void failedWatermarkDoesNotWriteAndNextAttemptRecovers() throws Exception {
    var queries = new AtomicInteger();
    var writes = new AtomicInteger();
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/query", exchange -> {
      var body = "{\"results\":[{}]}".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(queries.getAndIncrement() == 0 ? 503 : 200, body.length);
      exchange.getResponseBody().write(body);
      exchange.close();
    });
    server.createContext("/write", exchange -> {
      writes.incrementAndGet();
      exchange.sendResponseHeaders(204, -1);
      exchange.close();
    });
    InfluxAuditDatabaseProvisionerTest.existingDatabase(server);
    server.start();
    try (var store = new InfluxAuditEventStore("http://127.0.0.1:" + server.getAddress().getPort(),
        "audit-only", "audit-token")) {
      assertThrows(RuntimeException.class, () -> store.append(event(Instant.now())));
      assertEquals(0, writes.get());
      store.append(event(Instant.now()));
      assertEquals(1, writes.get());
    } finally {
      server.stop(0);
    }
  }

  private AuditEvent<AdapterCreatedDetails> event(Instant time) {
    return new AuditEvent<>(UUID.randomUUID(), time, StandardAuditEvents.ADAPTER_CREATE,
        AuditOutcome.SUCCEEDED, "user-1", "adapter", "1", new AdapterCreatedDetails("stream-1", null));
  }
}
