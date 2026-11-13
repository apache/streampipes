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
import org.apache.streampipes.client.credentials.StreamPipesApiKeyCredentials;
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatasetMetadataApiTest {

  private static final String BASE_PATH = "/streampipes-backend/api/v4/datalake/measure";

  private HttpServer server;

  @BeforeEach
  void startServer() throws IOException {
    var metadata = new DatasetMetadata("temperature", "s0::timestamp", null);
    metadata.setElementId("measure-id");
    var json = JacksonSerializer.getObjectMapper().writeValueAsString(metadata);

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(BASE_PATH + "/byName/temperature", exchange -> writeJson(exchange, 200, json));
    server.createContext(BASE_PATH + "/byName/missing", exchange -> writeJson(exchange, 404, "{}"));
    server.createContext(BASE_PATH + "/byName/forbidden", exchange -> writeJson(exchange, 403, "{}"));
    server.createContext(BASE_PATH + "/byName/broken", exchange -> writeJson(exchange, 500, "{}"));
    server.start();
  }

  @AfterEach
  void stopServer() {
    server.stop(0);
  }

  @Test
  void getByDatasetNameReturnsMetadata() {
    Optional<DatasetMetadata> metadata = client().datasetMetadataApi().getByDatasetName("temperature");

    assertTrue(metadata.isPresent());
    assertEquals("measure-id", metadata.get().getElementId());
  }

  @Test
  void getByDatasetNameReturnsEmptyOnNotFound() {
    assertTrue(client().datasetMetadataApi().getByDatasetName("missing").isEmpty());
  }

  @Test
  void getByDatasetNameReturnsEmptyOnForbidden() {
    assertTrue(client().datasetMetadataApi().getByDatasetName("forbidden").isEmpty());
  }

  @Test
  void getByDatasetNamePropagatesUnexpectedErrorResponses() {
    SpHttpErrorStatusCode exception = assertThrows(SpHttpErrorStatusCode.class,
        () -> client().datasetMetadataApi().getByDatasetName("broken"));
    assertEquals(500, exception.getHttpStatusCode());
  }

  private StreamPipesClient client() {
    return StreamPipesClient.create(new ClientConnectionUrlResolver() {
      @Override
      public StreamPipesApiKeyCredentials getCredentials() {
        return new StreamPipesApiKeyCredentials("service", "secret");
      }

      @Override
      public String getBaseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
      }
    });
  }

  private void writeJson(HttpExchange exchange, int statusCode, String response) throws IOException {
    byte[] body = response.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(statusCode, body.length);
    exchange.getResponseBody().write(body);
    exchange.close();
  }
}
