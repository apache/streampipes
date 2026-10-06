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

import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class InfluxWriteTransportTest {
  @Test
  void usesExplicitDatabaseBasicAuthAndNanosecondsWithoutAutomaticRetry() throws Exception {
    var requests = new AtomicInteger();
    var authorization = new AtomicReference<String>();
    var query = new AtomicReference<String>();
    var body = new AtomicReference<String>();
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/write", exchange -> {
      requests.incrementAndGet();
      authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
      query.set(exchange.getRequestURI().getRawQuery());
      body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
      exchange.sendResponseHeaders(503, -1);
      exchange.close();
    });
    server.start();
    var settings = InfluxConnectionSettings.from("http", "127.0.0.1", server.getAddress().getPort(),
        "audit only", "user", "pass");
    var transport = new InfluxWriteTransport(settings, Duration.ofSeconds(1));
    try {
      String line = "audit_events,event_type=sp.adapter.create actor=\"user\" 1790337600000000123";
      assertEquals(503, transport.write(line));
      assertEquals(1, requests.get());
      assertEquals("Basic dXNlcjpwYXNz", authorization.get());
      assertEquals("db=audit%20only&precision=ns", query.get());
      assertEquals(line, body.get());
      transport.close();
      assertThrows(IllegalStateException.class, () -> transport.write(line));
    } finally {
      transport.close();
      server.stop(0);
    }
  }

  @Test
  void boundsRequestTimeWhenServerDoesNotRespond() throws Exception {
    var release = new CountDownLatch(1);
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/write", exchange -> {
      try {
        release.await(3, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        exchange.close();
      }
    });
    server.start();
    var settings = InfluxConnectionSettings.from("http", "127.0.0.1", server.getAddress().getPort(),
        "audit", "token");
    try (var transport = new InfluxWriteTransport(settings, Duration.ofMillis(100))) {
      assertTimeoutPreemptively(Duration.ofSeconds(2),
          () -> assertThrows(IOException.class, () -> transport.write("events value=1 1")));
    } finally {
      release.countDown();
      server.stop(0);
    }
  }
}
