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

import com.squareup.moshi.Moshi;
import com.sun.net.httpserver.HttpServer;
import okhttp3.Call;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InfluxQueryCursorTest {
  @Test
  void managedCursorCancelsItsHttpCallBeforeAnyCallback() {
    var call = mock(Call.class);
    var released = new AtomicReference<InfluxQueryCursor>();
    var cursor = new InfluxQueryCursor(call, new Moshi.Builder().build().adapter(QueryResult.class), released::set);
    cursor.close();
    cursor.close();
    verify(call).cancel();
    assertEquals(cursor, released.get());
    assertFalse(cursor.hasNext());
  }

  @Test
  void streamsEveryChunkAndNormalizesNanosecondTimestampsToMilliseconds() {
    assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
      var client = mock(InfluxDB.class);
      var cancellation = mock(InfluxDB.Cancellable.class);
      var worker = new AtomicReference<Thread>();
      doAnswer(invocation -> {
        BiConsumer<InfluxDB.Cancellable, QueryResult> next = invocation.getArgument(2);
        Runnable complete = invocation.getArgument(3);
        worker.set(Thread.startVirtualThread(() -> {
          next.accept(cancellation, chunk("a", "1970-01-01T00:00:01.001500Z"));
          next.accept(cancellation, chunk("b", "1970-01-01T00:00:01.001500Z"));
          complete.run();
        }));
        return null;
      }).when(client).query(any(Query.class), eq(1000), any(BiConsumer.class), any(Runnable.class), any(Consumer.class));
      try (var cursor = new InfluxQueryCursor(client, new Query("SELECT *", "db"))) {
        assertTrue(cursor.hasNext());
        var first = cursor.next();
        assertEquals(1001L, first.rows().getFirst().getFirst());
        assertEquals(Map.of("machine", "a"), first.tags());
        assertEquals(Map.of("machine", "b"), cursor.next().tags());
        assertFalse(cursor.hasNext());
      }
      worker.get().join();
      verify(client).close();
    });
  }

  @Test
  void earlyCloseUnblocksTheProducerAndCancelsTheDatabaseQuery() {
    assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
      var client = mock(InfluxDB.class);
      var cancellation = mock(InfluxDB.Cancellable.class);
      var worker = new AtomicReference<Thread>();
      doAnswer(invocation -> {
        BiConsumer<InfluxDB.Cancellable, QueryResult> next = invocation.getArgument(2);
        worker.set(Thread.startVirtualThread(() -> {
          for (int i = 0; i < 100; i++) {
            next.accept(cancellation, chunk("a", "1970-01-01T00:00:01Z"));
          }
        }));
        return null;
      }).when(client).query(any(Query.class), eq(1000), any(BiConsumer.class), any(Runnable.class), any(Consumer.class));
      var cursor = new InfluxQueryCursor(client, new Query("SELECT *", "db"));
      cursor.next();
      cursor.close();
      worker.get().join();
      verify(cancellation, atLeastOnce()).cancel();
      verify(client).close();
    });
  }

  @Test
  void propagatesDatabaseErrorsAndClosesTheClient() {
    var client = mock(InfluxDB.class);
    doAnswer(invocation -> {
      Consumer<Throwable> failure = invocation.getArgument(4);
      failure.accept(new IllegalStateException("Database unavailable"));
      return null;
    }).when(client).query(any(Query.class), eq(1000), any(BiConsumer.class), any(Runnable.class), any(Consumer.class));
    var cursor = new InfluxQueryCursor(client, new Query("SELECT *", "db"));
    assertThrows(QueryExecutionException.class, cursor::hasNext);
    verify(client).close();
  }

  @Test
  void closingBeforeTheFirstCallbackCancelsOwnedHttpCalls() {
    var client = mock(InfluxDB.class);
    var cancel = mock(Runnable.class);
    var connection = new InfluxQueryConnection(client, cancel);
    var backend = new InfluxQueryBackend("db", () -> connection);
    var dataset = new org.apache.streampipes.model.dataset.DatasetMetadata();
    dataset.setElementId("catalog-id");
    dataset.setMeasureName("physical");
    var cursor = backend.open(dataset,
        org.apache.streampipes.dataexplorer.api.query.QuerySpec.builder().select("*").build());
    cursor.close();
    cursor.close();
    verify(cancel).run();
    verify(client).close();
    verify(client).query(org.mockito.ArgumentMatchers.argThat((Query query) ->
        query.getCommand().equals("SELECT * FROM \"physical\";")), eq(1000),
        any(BiConsumer.class), any(Runnable.class), any(Consumer.class));
  }

  @Test
  void realClientParsesChunkedHttpResponsesIncludingEverySeries() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      server.createContext("/query", exchange -> {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);
        try (var body = exchange.getResponseBody()) {
          for (String machine : List.of("a", "b")) {
            String chunk = """
                {"results":[{"statement_id":0,"series":[{"name":"physical","tags":{"machine":"%s"},
                "columns":["time","value"],"values":[["1970-01-01T00:00:01.001500Z",42]]}]}]}
                """.formatted(machine).replace("\n", "") + "\n";
            body.write(chunk.getBytes(StandardCharsets.UTF_8));
            body.flush();
          }
        }
      });
      server.start();
      try {
        var client = InfluxDBFactory.connect("http://127.0.0.1:" + server.getAddress().getPort());
        var tags = new ArrayList<String>();
        try (var cursor = new InfluxQueryCursor(client, new Query("SELECT * FROM physical", "db"))) {
          while (cursor.hasNext()) {
            var batch = cursor.next();
            tags.add(batch.tags().get("machine"));
            assertEquals(1001L, batch.rows().getFirst().getFirst());
          }
        }
        assertEquals(List.of("a", "b"), tags);
      } finally {
        server.stop(0);
      }
    });
  }

  @Test
  void ownedChunkedConnectionsRequestNumericMilliseconds() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      var requested = new AtomicReference<String>();
      var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      server.createContext("/query", exchange -> {
        requested.set(exchange.getRequestURI().getQuery());
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] response = """
            {"results":[{"series":[{"columns":["time","value"],"values":[[1001,42],[null,7]]}]}]}
            """.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        try (var body = exchange.getResponseBody()) {
          body.write(response);
        }
      });
      server.start();
      try {
        var settings = org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings.from(
            "http", "127.0.0.1", server.getAddress().getPort(), "db", "token");
        var connection = InfluxQueryConnection.open(settings);
        try (var cursor = new InfluxQueryCursor(connection.client(), new Query("SELECT * FROM physical", "db"),
            connection::close)) {
          var batch = cursor.next();
          assertEquals(1001L, batch.rows().getFirst().getFirst());
          org.junit.jupiter.api.Assertions.assertNull(batch.rows().get(1).getFirst());
          assertFalse(cursor.hasNext());
        }
        assertTrue(requested.get().contains("epoch=ms"));
        assertTrue(requested.get().contains("chunked=true"));
      } finally {
        server.stop(0);
      }
    });
  }

  private QueryResult chunk(String tag, String time) {
    var series = new QueryResult.Series();
    series.setColumns(List.of("time", "value"));
    series.setTags(Map.of("machine", tag));
    series.setValues(List.of(List.of(time, 42)));
    var statement = new QueryResult.Result();
    statement.setSeries(List.of(series));
    var result = new QueryResult();
    result.setResults(List.of(statement));
    return result;
  }
}
