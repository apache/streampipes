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

package org.apache.streampipes.client.api.external;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ExternalRequest {

  private static final Set<String> RESTRICTED_HEADERS = Set.of(
      "connection", "content-length", "host", "transfer-encoding", "upgrade"
  );

  private final ExternalRequestMethod method;
  private final URI uri;
  private final Map<String, String> headers;
  private final Map<String, String> queryParameters;
  private final Object payload;
  private final Integer connectTimeoutMillis;
  private final Integer responseTimeoutMillis;
  private final Long maxResponseBytes;

  private ExternalRequest(Builder builder) {
    this.method = Objects.requireNonNull(builder.method, "method must not be null");
    this.uri = validateUri(builder.uri);
    this.headers = Map.copyOf(builder.headers);
    validateHeaders(headers);
    this.queryParameters = Map.copyOf(builder.queryParameters);
    this.payload = builder.payload;
    validatePayload();
    this.connectTimeoutMillis = builder.connectTimeoutMillis;
    this.responseTimeoutMillis = builder.responseTimeoutMillis;
    this.maxResponseBytes = builder.maxResponseBytes;
    validateOverrides();
  }

  public static Builder builder(ExternalRequestMethod method, URI uri) {
    return new Builder(method, uri);
  }

  public ExternalRequestMethod getMethod() {
    return method;
  }

  public URI getUri() {
    return uri;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public Map<String, String> getQueryParameters() {
    return queryParameters;
  }

  public Object getPayload() {
    return payload;
  }

  public Integer getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }

  public Integer getResponseTimeoutMillis() {
    return responseTimeoutMillis;
  }

  public Long getMaxResponseBytes() {
    return maxResponseBytes;
  }

  private static URI validateUri(URI uri) {
    Objects.requireNonNull(uri, "uri must not be null");
    if (!uri.isAbsolute() || uri.getHost() == null
        || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
        || uri.getUserInfo() != null || uri.getFragment() != null) {
      throw new IllegalArgumentException("uri must be an absolute HTTP(S) URI without user info or a fragment");
    }
    return uri;
  }

  private static void validateHeaders(Map<String, String> headers) {
    headers.forEach((name, value) -> {
      if (name == null || name.isBlank() || value == null) {
        throw new IllegalArgumentException("header names and values must not be null or blank");
      }
      if (RESTRICTED_HEADERS.contains(name.toLowerCase())) {
        throw new IllegalArgumentException("header '" + name + "' is controlled by the HTTP client");
      }
    });
  }

  private void validatePayload() {
    boolean supportsPayload = method == ExternalRequestMethod.POST || method == ExternalRequestMethod.PUT;
    if (supportsPayload && payload == null) {
      throw new IllegalArgumentException(method + " requests require a JSON payload");
    }
    if (!supportsPayload && payload != null) {
      throw new IllegalArgumentException(method + " requests must not contain a payload");
    }
  }

  private void validateOverrides() {
    validatePositive(connectTimeoutMillis, "connectTimeoutMillis");
    validatePositive(responseTimeoutMillis, "responseTimeoutMillis");
    validatePositive(maxResponseBytes, "maxResponseBytes");
  }

  private static void validatePositive(Number value, String name) {
    if (value != null && value.longValue() <= 0) {
      throw new IllegalArgumentException(name + " must be greater than zero");
    }
  }

  public static final class Builder {

    private final ExternalRequestMethod method;
    private final URI uri;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final Map<String, String> queryParameters = new LinkedHashMap<>();
    private Object payload;
    private Integer connectTimeoutMillis;
    private Integer responseTimeoutMillis;
    private Long maxResponseBytes;

    private Builder(ExternalRequestMethod method, URI uri) {
      this.method = method;
      this.uri = uri;
    }

    public Builder header(String name, String value) {
      headers.put(name, value);
      return this;
    }

    public Builder headers(Map<String, String> headers) {
      this.headers.putAll(headers);
      return this;
    }

    public Builder queryParameter(String name, String value) {
      queryParameters.put(name, value);
      return this;
    }

    public Builder queryParameters(Map<String, String> queryParameters) {
      this.queryParameters.putAll(queryParameters);
      return this;
    }

    public Builder payload(Object payload) {
      this.payload = payload;
      return this;
    }

    public Builder connectTimeoutMillis(int connectTimeoutMillis) {
      this.connectTimeoutMillis = connectTimeoutMillis;
      return this;
    }

    public Builder responseTimeoutMillis(int responseTimeoutMillis) {
      this.responseTimeoutMillis = responseTimeoutMillis;
      return this;
    }

    public Builder maxResponseBytes(long maxResponseBytes) {
      this.maxResponseBytes = maxResponseBytes;
      return this;
    }

    public ExternalRequest build() {
      return new ExternalRequest(this);
    }
  }
}
