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

package org.apache.streampipes.client.api;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.client.api.config.ClientConnectionUrlResolver;
import org.apache.streampipes.client.api.external.ExternalRequestConfig;
import org.apache.streampipes.client.api.external.ExternalRequestException;
import org.apache.streampipes.client.credentials.StreamPipesApiKeyCredentials;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalRequestApiTest {

  private HttpServer server;

  @AfterEach
  void stopServer() {
    if (server != null) {
      server.stop(0);
    }
  }

  @Test
  void sendsJsonGetWithoutStreamPipesCredentialsOrCustomHeaders() throws Exception {
    AtomicReference<String> authorization = new AtomicReference<>();
    AtomicReference<String> customHeader = new AtomicReference<>();
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/resource", exchange -> {
      authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
      customHeader.set(exchange.getRequestHeaders().getFirst("X-On-Behalf-Of"));
      byte[] body = "{\"name\":\"external\"}".getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, body.length);
      exchange.getResponseBody().write(body);
      exchange.close();
    });
    server.start();

    var client = client();
    client.getConfig().addCustomHeader("X-On-Behalf-Of", "user-id");
    Map<?, ?> response = client.externalRequest(ExternalRequestConfig.defaults())
        .sendGet(resourceUri("/resource"), Map.of(), Map.of(), Map.class);

    assertEquals("external", response.get("name"));
    assertNull(authorization.get());
    assertNull(customHeader.get());
  }

  @Test
  void sendsPostPutAndDeleteWithCallerHeadersAndQueryParameters() throws Exception {
    AtomicReference<String> requestMethod = new AtomicReference<>();
    AtomicReference<String> requestBody = new AtomicReference<>();
    AtomicReference<String> authorization = new AtomicReference<>();
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/resource", exchange -> {
      requestMethod.set(exchange.getRequestMethod());
      requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
      authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
      if ("POST".equals(exchange.getRequestMethod())) {
        writeJson(exchange, 200, "{\"created\":true}");
      } else if ("PUT".equals(exchange.getRequestMethod())) {
        writeJson(exchange, 200, "{\"updated\":true}");
      } else {
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
      }
    });
    server.start();

    var api = client().externalRequest();
    api.sendPost(resourceUri("/resource").toString(), Map.of("Authorization", "Bearer external-token"), Map.of("value", 1));
    assertEquals("POST", requestMethod.get());
    assertTrue(requestBody.get().contains("\"value\" : 1"));
    assertEquals("Bearer external-token", authorization.get());

    api.sendPut(resourceUri("/resource"), Map.of(), Map.of("value", 2));
    assertEquals("PUT", requestMethod.get());

    api.sendDelete(resourceUri("/resource"), Map.of());
    assertEquals("DELETE", requestMethod.get());

    Map<String, Object> postResponse = api.sendPostJson(resourceUri("/resource").toString(), Map.of("value", 3));
    assertEquals(true, postResponse.get("created"));

    Map<String, Object> putResponse = api.sendPutJson(resourceUri("/resource"), Map.of(), Map.of("value", 4));
    assertEquals(true, putResponse.get("updated"));
  }

  @Test
  void sendsVoidRequestsWithoutRequiringJsonResponses() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/acknowledged", exchange -> {
      byte[] body = "accepted".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(202, body.length);
      exchange.getResponseBody().write(body);
      exchange.close();
    });
    server.start();

    var api = client().externalRequest();
    api.sendPost(resourceUri("/acknowledged"), Map.of(), Map.of("value", 1));
    api.sendPut(resourceUri("/acknowledged"), Map.of(), Map.of("value", 2));
  }

  @Test
  void preservesExistingUriQueriesWhenAddingQueryParameters() throws Exception {
    AtomicReference<String> query = new AtomicReference<>();
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/query", exchange -> {
      query.set(exchange.getRequestURI().getRawQuery());
      writeJson(exchange, 200, "{}");
    });
    server.start();

    client().externalRequest().sendGetJson(
        resourceUri("/query").toString() + "?existing=value",
        Map.of(),
        Map.of("added", "another value")
    );

    assertEquals("existing=value&added=another+value", query.get());
  }

  @Test
  void deserializesListsAndDoesNotFollowRedirects() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/list", exchange -> writeJson(exchange, 200, "[{\"name\":\"one\"}]"));
    server.createContext("/redirect", exchange -> {
      exchange.getResponseHeaders().set("Location", resourceUri("/list").toString());
      exchange.sendResponseHeaders(302, -1);
      exchange.close();
    });
    server.start();

    List<Map> response = client().externalRequest().getList(resourceUri("/list"), Map.of(), Map.of(), Map.class);
    assertEquals("one", response.get(0).get("name"));

    ExternalRequestException exception = assertThrows(ExternalRequestException.class,
        () -> client().externalRequest().sendGetJson(resourceUri("/redirect").toString()));
    assertEquals(302, exception.getStatusCode());
  }

  @Test
  void rejectsInvalidRequestsAndBoundsResponseBodies() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> client().externalRequest().sendGetJson("/relative"));
    assertThrows(IllegalArgumentException.class,
        () -> client().externalRequest().sendGetJson("https://user@example.org"));
    assertThrows(IllegalArgumentException.class,
        () -> client().externalRequest().sendGetJson("https://example.org#fragment"));
    assertThrows(IllegalArgumentException.class,
        () -> client().externalRequest().sendGetJson(
            "https://example.org", Map.of("hOsT", "other.example"), Map.of()));

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/large", exchange -> writeJson(exchange, 200, "\"" + "x".repeat(128) + "\""));
    server.start();
    var api = client().externalRequest(ExternalRequestConfig.builder().maxResponseBytes(64).build());
    assertThrows(ExternalRequestException.class, () -> api.sendGetJson(resourceUri("/large").toString()));
  }

  @Test
  void sanitizesFailureUrisAndIncludesStatusCode() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/failure", exchange -> writeJson(exchange, 418, "{\"error\":\"unavailable\"}"));
    server.start();

    ExternalRequestException exception = assertThrows(ExternalRequestException.class,
        () -> client().externalRequest().sendGetJson(resourceUri("/failure").toString() + "?apiKey=secret"));

    assertEquals(418, exception.getStatusCode());
    assertTrue(exception.getMessage().contains("unavailable"));
    assertFalse(exception.getMessage().contains("apiKey=secret"));
  }

  private String resourceUri(String path) {
    return "http://localhost:" + server.getAddress().getPort() + path;
  }

  private StreamPipesClient client() {
    return StreamPipesClient.create(new ClientConnectionUrlResolver() {
      @Override
      public StreamPipesApiKeyCredentials getCredentials() {
        return new StreamPipesApiKeyCredentials("service", "secret");
      }

      @Override
      public String getBaseUrl() {
        return "https://streampipes.example";
      }
    });
  }

  private void writeJson(com.sun.net.httpserver.HttpExchange exchange, int statusCode, String response) throws IOException {
    byte[] body = response.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(statusCode, body.length);
    exchange.getResponseBody().write(body);
    exchange.close();
  }
}
