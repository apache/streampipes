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

package org.apache.streampipes.sinks.databases.jvm.couchdb;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.runtime.EventFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CouchDbSinkTest {

  private final List<Request> requests = new CopyOnWriteArrayList<>();
  private final CouchDbSink sink = new CouchDbSink();
  private HttpServer server;
  private int databaseCreateStatus = 201;

  @BeforeEach
  void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext("/", this::answer);
    server.start();
  }

  @AfterEach
  void stopServer() {
    server.stop(0);
  }

  @Test
  void createsTheConfiguredDatabaseWithoutCredentials() {
    sink.onPipelineStarted(parameters(), null);
    sink.onPipelineStopped();

    assertEquals(1, requests.size());
    assertEquals("PUT /events", requests.get(0).line());
    assertNull(requests.get(0).authorization());
  }

  @Test
  void acceptsAnExistingDatabase() {
    databaseCreateStatus = 412;

    sink.onPipelineStarted(parameters(), null);
    sink.onPipelineStopped();

    assertEquals("PUT /events", requests.get(0).line());
  }

  @Test
  void storesEventUnderAGeneratedIdWithoutEmptyFields() {
    var fields = new HashMap<String, Object>();
    fields.put("temperature", 1.5);
    fields.put("count", 3L);
    fields.put("name", "a<b");
    fields.put("empty", null);

    sink.onPipelineStarted(parameters(), null);
    sink.onEvent(EventFactory.fromMap(fields));
    sink.onPipelineStopped();

    var request = requests.get(requests.size() - 1);
    assertTrue(request.line().matches("PUT /events/[0-9a-f]{32}"), request.line());
    assertTrue(request.body().contains("\"temperature\":1.5"), request.body());
    assertTrue(request.body().contains("\"count\":3"), request.body());
    assertTrue(request.body().contains("\"name\":\"a\\u003cb\""), request.body());
    assertFalse(request.body().contains("empty"), request.body());
  }

  private IDataSinkParameters parameters() {
    var extractor = mock(IDataSinkParameterExtractor.class);
    when(extractor.singleValueParameter("db_host", String.class)).thenReturn("127.0.0.1");
    when(extractor.singleValueParameter("db_port", Integer.class)).thenReturn(server.getAddress().getPort());
    when(extractor.singleValueParameter("db_name", String.class)).thenReturn("events");
    var parameters = mock(IDataSinkParameters.class);
    when(parameters.extractor()).thenReturn(extractor);
    return parameters;
  }

  private void answer(HttpExchange exchange) throws IOException {
    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    String line = exchange.getRequestMethod() + " " + exchange.getRequestURI().getRawPath();
    requests.add(new Request(line, exchange.getRequestHeaders().getFirst("Authorization"), body));
    int status = "PUT /events".equals(line) ? databaseCreateStatus : 201;
    byte[] response = (status == 201 ? "{\"ok\":true,\"id\":\"id\",\"rev\":\"1-a\"}" : "{\"error\":\"file_exists\"}")
        .getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(status, response.length);
    try (var output = exchange.getResponseBody()) {
      output.write(response);
    }
  }

  private record Request(String line, String authorization, String body) {
  }
}
