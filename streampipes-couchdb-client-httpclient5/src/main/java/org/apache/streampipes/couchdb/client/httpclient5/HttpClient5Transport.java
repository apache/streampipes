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

package org.apache.streampipes.couchdb.client.httpclient5;

import org.apache.streampipes.couchdb.client.CouchServerConfig;
import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;
import org.apache.streampipes.couchdb.client.transport.CouchTransport;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Transport on Apache HttpClient 5 (classic API).
 *
 * <p>Used by the core service, where it outperformed {@code JdkHttpTransport} on small requests, allocated less and
 * handled stale pooled connections without errors (see {@code docs/couchdb-client-concept.md}, section 6.2). The
 * pool holds up to {@value #MAX_CONNECTIONS} connections; connections idle for more than two seconds are validated
 * before reuse.</p>
 */
public class HttpClient5Transport implements CouchTransport {

  private static final int MAX_CONNECTIONS = 256;

  private final String baseUrl;
  private final CloseableHttpClient client;

  public HttpClient5Transport(CouchServerConfig config) {
    this(config.baseUrl(), createClient(config));
  }

  HttpClient5Transport(String baseUrl, CloseableHttpClient client) {
    this.baseUrl = baseUrl;
    this.client = client;
  }

  private static CloseableHttpClient createClient(CouchServerConfig config) {
    var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
        .setMaxConnTotal(MAX_CONNECTIONS)
        .setMaxConnPerRoute(MAX_CONNECTIONS)
        .setDefaultConnectionConfig(ConnectionConfig.custom()
            .setConnectTimeout(Timeout.of(config.connectTimeout()))
            .setSocketTimeout(Timeout.of(config.requestTimeout()))
            .setValidateAfterInactivity(TimeValue.ofSeconds(2))
            .build())
        .build();
    return HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setDefaultRequestConfig(RequestConfig.custom().setResponseTimeout(Timeout.of(config.requestTimeout())).build())
        .disableRedirectHandling()
        .disableAutomaticRetries()
        .disableContentCompression()
        .build();
  }

  @Override
  public CouchResponse send(CouchRequest request, Map<String, String> headers) {
    var builder = ClassicRequestBuilder.create(request.method())
        .setUri(baseUrl + request.path())
        .addHeader("Accept", request.accept());
    headers.forEach(builder::addHeader);
    if (request.body() != null) {
      builder.setEntity(new ByteArrayEntity(request.body(), ContentType.parse(request.contentType())));
    }
    ClassicHttpResponse response = null;
    try {
      response = client.executeOpen(null, builder.build(), null);
      return toCouchResponse(response);
    } catch (IOException e) {
      closeQuietly(response);
      throw new CouchDbTransportException(request.method() + " " + pathOnly(request.path()) + " failed", e);
    }
  }

  private static CouchResponse toCouchResponse(ClassicHttpResponse response) throws IOException {
    var responseHeaders = new HashMap<String, String>();
    for (Header header : response.getHeaders()) {
      responseHeaders.putIfAbsent(header.getName().toLowerCase(Locale.ROOT), header.getValue());
    }
    InputStream content = response.getEntity() != null
        ? response.getEntity().getContent()
        : new ByteArrayInputStream(new byte[0]);
    return new CouchResponse(response.getCode(), responseHeaders, new FilterInputStream(content) {
      @Override
      public void close() throws IOException {
        // Closing the response returns a fully read connection to the pool.
        response.close();
      }
    });
  }

  /**
   * Releases the connection of a response that could not be handed to the caller.
   */
  private static void closeQuietly(ClassicHttpResponse response) {
    if (response == null) {
      return;
    }
    try {
      response.close();
    } catch (IOException e) {
      // The request already failed; that error is the one reported.
    }
  }

  @Override
  public void close() {
    try {
      client.close();
    } catch (IOException e) {
      // Shutting down.
    }
  }

  private static String pathOnly(String path) {
    int query = path.indexOf('?');
    return query < 0 ? path : path.substring(0, query);
  }
}
