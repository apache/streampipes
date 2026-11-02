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

package org.apache.streampipes.couchdb.client.transport;

import org.apache.streampipes.couchdb.client.CouchServerConfig;
import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Transport on {@link HttpClient} from the JDK.
 *
 * <p>Uses HTTP/1.1 explicitly: CouchDB does not speak HTTP/2, and the client's default would attempt an h2c
 * upgrade on every new cleartext connection. Pool size and keep-alive are JVM-wide settings of the JDK client
 * ({@code jdk.httpclient.connectionPoolSize}, {@code jdk.httpclient.keepalive.timeout}).</p>
 */
public class JdkHttpTransport implements CouchTransport {

  private final String baseUrl;
  private final Duration requestTimeout;
  private final ExecutorService executor;
  private final HttpClient client;

  public JdkHttpTransport(CouchServerConfig config) {
    this.baseUrl = config.baseUrl();
    this.requestTimeout = config.requestTimeout();
    this.executor = Executors.newVirtualThreadPerTaskExecutor();
    this.client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(config.connectTimeout())
        .followRedirects(HttpClient.Redirect.NEVER)
        .executor(executor)
        .build();
  }

  @Override
  public CouchResponse send(CouchRequest request, Map<String, String> headers) {
    var builder = HttpRequest.newBuilder(URI.create(baseUrl + request.path()))
        .timeout(requestTimeout)
        .header("Accept", request.accept());
    headers.forEach(builder::header);
    if (request.body() != null) {
      builder.header("Content-Type", request.contentType());
      builder.method(request.method(), HttpRequest.BodyPublishers.ofByteArray(request.body()));
    } else {
      builder.method(request.method(), HttpRequest.BodyPublishers.noBody());
    }

    try {
      HttpResponse<InputStream> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
      return new CouchResponse(response.statusCode(), toHeaderMap(response), response.body());
    } catch (IOException e) {
      throw new CouchDbTransportException(request.method() + " " + pathOnly(request) + " failed", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new CouchDbTransportException(request.method() + " " + pathOnly(request) + " interrupted", e);
    }
  }

  @Override
  public void close() {
    client.close();
    executor.close();
  }

  private Map<String, String> toHeaderMap(HttpResponse<?> response) {
    var result = new HashMap<String, String>();
    response.headers().map().forEach((name, values) -> {
      if (!values.isEmpty()) {
        result.put(name.toLowerCase(Locale.ROOT), values.get(0));
      }
    });
    return result;
  }

  static String pathOnly(CouchRequest request) {
    int query = request.path().indexOf('?');
    return query < 0 ? request.path() : request.path().substring(0, query);
  }
}
