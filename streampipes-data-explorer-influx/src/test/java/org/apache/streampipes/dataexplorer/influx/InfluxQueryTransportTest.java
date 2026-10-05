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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;
import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;
import org.apache.streampipes.dataexplorer.query.QueryResultCollector;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.influxdb.dto.Query;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InfluxQueryTransportTest {
  private static final String CHUNK = """
      {"results":[{"series":[{"columns":["time","value"],"values":[[1001,42],[1002,null]]}]}]}
      """;
  private static final Query QUERY = new Query("SELECT * FROM \"physical\"", "db");

  @Test
  void reusesConnectionAcrossStreamsAndMetadataAndPreservesResultOptions() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var ports = new ArrayList<Integer>();
      var requests = new ArrayList<String>();
      var server = server();
      server.createContext("/query", exchange -> {
        ports.add(exchange.getRemoteAddress().getPort());
        requests.add(exchange.getRequestURI().getQuery());
        assertEquals("Token token", exchange.getRequestHeaders().getFirst("Authorization"));
        respond(exchange, 200, CHUNK);
      });
      server.start();
      try (var transport = transport(server)) {
        for (int i = 0; i < 3; i++) {
          try (var cursor = transport.open(QUERY)) {
            var result = QueryResultCollector.collect(cursor, QueryExecutionOptions.defaults().withTimestampFormat(
                    i == 0 ? QueryTimestampFormat.EPOCH_MILLIS : QueryTimestampFormat.RFC3339));
            assertEquals(2, result.getTotal());
            var series = result.getAllDataSeries().getFirst();
            assertNull(series.getTags());
            assertEquals(i == 0 ? 1001L : "1970-01-01T00:00:01.001Z", series.getRows().getFirst().getFirst());
            assertNull(series.getRows().get(1).get(1));
            series.getRows().getFirst().set(1, "mutable");
          }
        }
        assertEquals(1, transport.execute(QUERY).getResults().size());
        assertEquals(1, ports.stream().distinct().count(), "All four requests should reuse one TCP connection");
        assertEquals(4, requests.size());
        assertTrue(requests.stream().allMatch(request -> request.contains("epoch=ms")));
        assertTrue(requests.getFirst().contains("chunked=true"));
        assertFalse(requests.getLast().contains("chunked=true"));
      } finally {
        server.stop(0);
      }
    });
  }

  @Test
  void cancellingBeforeHeadersDoesNotCancelConcurrentQueries() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var started = new CountDownLatch(1);
      var unblock = new CountDownLatch(1);
      var server = server();
      try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        server.setExecutor(executor);
        server.createContext("/query", exchange -> {
          if (exchange.getRequestURI().getQuery().contains("slow")) {
            started.countDown();
            try {
              unblock.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }
          respond(exchange, 200, CHUNK);
        });
        server.start();
        try (var transport = transport(server)) {
          var slow = transport.open(new Query("slow", "db"));
          assertTrue(started.await(3, TimeUnit.SECONDS));
          try (var other = transport.open(QUERY)) {
            slow.close();
            slow.close();
            assertFalse(slow.hasNext());
            assertEquals(1001L, other.next().rows().getFirst().getFirst());
            assertFalse(other.hasNext());
          }
          try (var next = transport.open(QUERY)) {
            assertEquals(1001L, next.next().rows().getFirst().getFirst());
            assertFalse(next.hasNext());
          }
        } finally {
          unblock.countDown();
          server.stop(0);
        }
      }
    });
  }

  @Test
  void shutdownUnblocksConsumersAndRejectsNewQueries() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var started = new CountDownLatch(2);
      var unblock = new CountDownLatch(1);
      var server = server();
      try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        server.setExecutor(executor);
        server.createContext("/query", exchange -> {
          started.countDown();
          try {
            unblock.await(5, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          respond(exchange, 200, CHUNK);
        });
        server.start();
        try (var transport = transport(server)) {
          var cursor = transport.open(QUERY);
          var metadata = executor.submit(() -> transport.execute(QUERY));
          assertTrue(started.await(3, TimeUnit.SECONDS));
          var waiting = executor.submit(cursor::hasNext);
          transport.close();
          transport.close();
          assertFalse(waiting.get(3, TimeUnit.SECONDS));
          var failure = assertThrows(ExecutionException.class, () -> metadata.get(3, TimeUnit.SECONDS));
          assertTrue(failure.getCause() instanceof QueryExecutionException);
          assertThrows(IllegalStateException.class, () -> transport.open(QUERY));
          assertThrows(IllegalStateException.class, () -> transport.execute(QUERY));
        } finally {
          unblock.countDown();
          server.stop(0);
        }
      }
    });
  }

  @Test
  void shutdownAlsoClosesCursorsWhoseBatchQueueIsFull() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var server = server();
      server.createContext("/query", exchange -> respond(exchange, 200, CHUNK.repeat(100)));
      server.start();
      try (var transport = transport(server)) {
        var cursor = transport.open(QUERY);
        var first = cursor.next();
        assertEquals(1001L, first.rows().getFirst().getFirst());
        transport.close();
        assertFalse(cursor.hasNext());
        assertEquals(1001L, first.rows().getFirst().getFirst());
        assertThrows(UnsupportedOperationException.class, () -> first.rows().getFirst().set(0, 0L));
      } finally {
        server.stop(0);
      }
    });
  }

  @Test
  void propagatesHttpJsonAndStatementErrorsAndCanQueryAgain() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var response = new AtomicReference<>(CHUNK);
      var status = new AtomicInteger(200);
      var server = server();
      server.createContext("/query", exchange -> respond(exchange, status.get(), response.get()));
      server.start();
      try (var transport = transport(server)) {
        status.set(503);
        assertQueryFails(transport);
        status.set(200);
        for (var invalid : List.of("{broken", "{\"results\":[{\"error\":\"invalid query\"}]}",
            "{\"error\":\"invalid database\"}", CHUNK + "{\"results\":[")) {
          response.set(invalid);
          assertQueryFails(transport);
        }
        response.set(CHUNK.repeat(2) + "  ");
        try (var cursor = transport.open(QUERY)) {
          var first = cursor.next();
          var second = cursor.next();
          assertEquals(first.rows(), second.rows());
          assertFalse(cursor.hasNext());
        }
      } finally {
        server.stop(0);
      }
    });
  }

  @Test
  void supportsBasicAuthenticationPostAndEncodedBoundParameters() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var request = new AtomicReference<String>();
      var auth = new AtomicReference<String>();
      var method = new AtomicReference<String>();
      var server = server();
      server.createContext("/query", exchange -> {
        request.set(URLDecoder.decode(exchange.getRequestURI().getRawQuery(), StandardCharsets.UTF_8));
        auth.set(exchange.getRequestHeaders().getFirst("Authorization"));
        method.set(exchange.getRequestMethod());
        respond(exchange, 200, CHUNK);
      });
      server.start();
      var settings = InfluxConnectionSettings.from("http", "127.0.0.1", server.getAddress().getPort(),
          "db", "user", "password");
      try (var transport = new InfluxQueryTransport(settings)) {
        transport.execute(new Query("SELECT * WHERE value=$value", "db", true).bindParameter("value", "a+b & ü"));
        assertEquals("POST", method.get());
        assertEquals("Basic dXNlcjpwYXNzd29yZA==", auth.get());
        assertTrue(request.get().contains("params={\"value\":\"a+b & ü\"}"));
        assertTrue(request.get().contains("q=SELECT * WHERE value=$value"));
      } finally {
        server.stop(0);
      }
    });
  }

  @Test
  void requestsEveryTimestampFormatAndKeepsNanosecondIntegersExact() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var requested = new AtomicReference<String>();
      var server = server();
      server.createContext("/query", exchange -> {
        String query = exchange.getRequestURI().getQuery();
        requested.set(query);
        String time = query.contains("epoch=ns") ? "1788912000123456789"
            : query.contains("epoch=u") ? "1788912000123456"
            : query.contains("epoch=ms") ? "1788912000123"
            : query.contains("epoch=s") ? "1788912000"
            : query.contains("epoch=m") ? "29815200"
            : query.contains("epoch=h") ? "496920" : "\"2026-09-09T00:00:00.123456789Z\"";
        // Values precede columns deliberately: decoder correctness must not depend on JSON member order.
        respond(exchange, 200, "{\"results\":[{\"series\":[{\"values\":[[" + time
            + ",42]],\"columns\":[\"time\",\"value\"]}]}]}\n");
      });
      server.start();
      try (var transport = transport(server)) {
        for (var format : QueryTimestampFormat.values()) {
          var options = QueryExecutionOptions.defaults().withTimestampFormat(format);
          try (var cursor = transport.open(QUERY, format)) {
            assertEquals(InfluxTimestampEncoding.supported(format), cursor.timestampFormat());
            var result = QueryResultCollector.collect(cursor, options);
            var row = result.getAllDataSeries().getFirst().getRows().getFirst();
            assertEquals(format.convert("2026-09-09T00:00:00.123456789Z", QueryTimestampFormat.RFC3339), row.getFirst());
            assertEquals(42.0, row.get(1));
            assertEquals(format.toEpochMillis(row.getFirst()), result.getLastTimestamp());
          }
          var epoch = InfluxTimestampEncoding.epoch(InfluxTimestampEncoding.supported(format));
          assertEquals(epoch != null, requested.get().contains("epoch="));
          if (epoch != null) {
            assertTrue(requested.get().contains("epoch=" + epoch));
          }
        }
      } finally {
        server.stop(0);
      }
    });
  }

  private void assertQueryFails(InfluxQueryTransport transport) {
    try (var cursor = transport.open(QUERY)) {
      assertThrows(QueryExecutionException.class, () -> {
        while (cursor.hasNext()) {
          cursor.next();
        }
      });
    }
  }

  private HttpServer server() throws IOException {
    return HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
  }

  private InfluxQueryTransport transport(HttpServer server) {
    return new InfluxQueryTransport(InfluxConnectionSettings.from(
        "http", "127.0.0.1", server.getAddress().getPort(), "db", "token"));
  }

  private void respond(HttpExchange exchange, int status, String data) throws IOException {
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(status, bytes.length);
    try (var body = exchange.getResponseBody()) {
      body.write(bytes);
    }
  }
}
