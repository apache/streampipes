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

import org.apache.streampipes.client.api.config.ClientConnectionUrlResolver;
import org.apache.streampipes.client.credentials.StreamPipesApiKeyCredentials;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataLakeMeasureApiTest {

  private HttpServer server;

  @AfterEach
  void stopServer() {
    if (server != null) {
      server.stop(0);
    }
  }

  @Test
  void returnsEmptyForForbiddenDatasetName() throws Exception {
    startServer(403);

    assertTrue(api().getByDatasetName("missing").isEmpty());
  }

  @Test
  void preservesUnexpectedErrorResponses() throws Exception {
    startServer(500);

    SpHttpErrorStatusCode exception = assertThrows(SpHttpErrorStatusCode.class,
        () -> api().getByDatasetName("missing"));
    assertEquals(500, exception.getHttpStatusCode());
  }

  private void startServer(int statusCode) throws IOException {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/streampipes-backend/api/v4/datalake/measure/byName/missing", exchange -> {
      exchange.sendResponseHeaders(statusCode, -1);
      exchange.close();
    });
    server.start();
  }

  private DataLakeMeasureApi api() {
    return new DataLakeMeasureApi(new StreamPipesClientConfig(new ClientConnectionUrlResolver() {
      @Override
      public StreamPipesApiKeyCredentials getCredentials() {
        return new StreamPipesApiKeyCredentials("service", "secret");
      }

      @Override
      public String getBaseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
      }
    }));
  }
}
