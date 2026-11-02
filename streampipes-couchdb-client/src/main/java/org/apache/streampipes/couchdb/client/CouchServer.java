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

package org.apache.streampipes.couchdb.client;

import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;
import org.apache.streampipes.couchdb.client.exception.DocumentMappingException;
import org.apache.streampipes.couchdb.client.exception.PreconditionFailedException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;
import org.apache.streampipes.couchdb.client.transport.CouchTransport;
import org.apache.streampipes.couchdb.client.transport.JdkHttpTransport;
import org.apache.streampipes.couchdb.client.transport.TransportFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entry point of the client: one instance per CouchDB server and process. Thread-safe; holds the connection pool,
 * so create it once and close it on shutdown.
 *
 * <pre>{@code
 * try (var server = CouchServer.builder().url("http://localhost:5984").credentials("admin", "secret").build()) {
 *   var pipelines = server.database("pipeline", DatabaseOptions.createIfMissing())
 *       .typed(Pipeline.class, GsonCodec.of(gson));
 *   Optional<Pipeline> pipeline = pipelines.find(id);
 * }
 * }</pre>
 */
public final class CouchServer implements AutoCloseable {

  private final CouchServerConfig config;
  private final CouchTransport transport;
  private final Map<String, String> headers;
  private final Set<String> knownDatabases = ConcurrentHashMap.newKeySet();

  private CouchServer(CouchServerConfig config, TransportFactory transportFactory) {
    this.config = config;
    this.transport = transportFactory.create(config);
    this.headers = config.username() == null
        ? Map.of()
        : Map.of("Authorization", basicAuth(config.username(), config.password()));
  }

  public static Builder builder() {
    return new Builder();
  }

  public CouchServerConfig config() {
    return config;
  }

  /**
   * Handle for a database that is expected to exist.
   *
   * @param name database name
   * @return the handle; cheap to create
   */
  public CouchDatabase database(String name) {
    return database(name, DatabaseOptions.existing());
  }

  public CouchDatabase database(String name, DatabaseOptions options) {
    return new CouchDatabase(this, name, options);
  }

  public List<String> allDatabases() {
    return execute(CouchRequest.get("/_all_dbs"),
        response -> CouchJson.MAPPER.readValue(response.body(),
            CouchJson.MAPPER.getTypeFactory().constructCollectionType(List.class, String.class)));
  }

  @Override
  public void close() {
    transport.close();
  }

  @FunctionalInterface
  interface ResponseHandler<T> {
    T handle(CouchResponse response) throws IOException;
  }

  /**
   * Sends a request, maps error statuses to exceptions and closes the response after the handler ran.
   */
  <T> T execute(CouchRequest request, ResponseHandler<T> handler) {
    try (CouchResponse response = transport.send(request, headers)) {
      if (response.status() >= 400) {
        throw CouchJson.toException(request, response);
      }
      return handler.handle(response);
    } catch (JsonProcessingException e) {
      throw new DocumentMappingException(
          "Could not read response of " + request.method() + " " + CouchJson.pathOnly(request.path()), e);
    } catch (IOException e) {
      throw new CouchDbTransportException(
          "Could not read response of " + request.method() + " " + CouchJson.pathOnly(request.path()), e);
    }
  }

  /**
   * Sends a request and returns the open response for streaming; the caller closes it.
   */
  CouchResponse open(CouchRequest request) {
    CouchResponse response = transport.send(request, headers);
    if (response.status() >= 400) {
      try (response) {
        throw CouchJson.toException(request, response);
      }
    }
    return response;
  }

  void ensureDatabase(String name) {
    if (knownDatabases.contains(name)) {
      return;
    }
    try {
      execute(new CouchRequest("PUT", CouchPaths.database(name), CouchRequest.JSON, null, null), response -> null);
    } catch (PreconditionFailedException e) {
      // 412: the database exists already.
    }
    knownDatabases.add(name);
  }

  void forgetDatabase(String name) {
    knownDatabases.remove(name);
  }

  private static String basicAuth(String username, String password) {
    String credentials = username + ":" + (password == null ? "" : password);
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Builder for {@link CouchServer}. Defaults: {@code http://localhost:5984}, no credentials, connect timeout 1 s,
   * request timeout 100 s, {@link JdkHttpTransport}.
   */
  public static final class Builder {

    private String url = "http://localhost:5984";
    private String username;
    private String password;
    private Duration connectTimeout = Duration.ofSeconds(1);
    private Duration requestTimeout = Duration.ofSeconds(100);
    private TransportFactory transportFactory = JdkHttpTransport::new;

    private Builder() {
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder url(String protocol, String host, int port) {
      this.url = URI.create(protocol + "://" + host + ":" + port).toString();
      return this;
    }

    public Builder credentials(String username, String password) {
      this.username = username;
      this.password = password;
      return this;
    }

    public Builder connectTimeout(Duration connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }

    public Builder requestTimeout(Duration requestTimeout) {
      this.requestTimeout = requestTimeout;
      return this;
    }

    public Builder transport(TransportFactory transportFactory) {
      this.transportFactory = transportFactory;
      return this;
    }

    public CouchServer build() {
      return new CouchServer(
          new CouchServerConfig(url, username, password, connectTimeout, requestTimeout),
          transportFactory);
    }
  }
}
