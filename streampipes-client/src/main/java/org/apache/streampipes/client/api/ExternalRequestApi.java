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

import org.apache.streampipes.client.api.external.ExternalRequest;
import org.apache.streampipes.client.api.external.ExternalRequestConfig;
import org.apache.streampipes.client.api.external.ExternalRequestException;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ExternalRequestApi implements IExternalRequestApi {

  private static final String JSON_MEDIA_TYPE = "application/json";
  private static final int MAX_ERROR_RESPONSE_BYTES = 8 * 1024;

  private final ExternalRequestConfig config;

  public ExternalRequestApi(ExternalRequestConfig config) {
    this.config = config;
  }

  @Override
  public <T> T execute(ExternalRequest request, Class<T> responseClass) {
    String response = executeRequest(request);
    if (response == null || response.isBlank()) {
      return null;
    }
    try {
      return JacksonSerializer.getObjectMapper().readValue(response, responseClass);
    } catch (JsonProcessingException e) {
      throw new ExternalRequestException("Could not deserialize external HTTP response", e);
    }
  }

  @Override
  public Object executeJson(ExternalRequest request) {
    return execute(request, Object.class);
  }

  @Override
  public <T> List<T> getList(ExternalRequest request, Class<T> responseClass) {
    String response = executeRequest(request);
    if (response == null || response.isBlank()) {
      return null;
    }
    try {
      return JacksonSerializer.getObjectMapper().readValue(
          response,
          JacksonSerializer.getObjectMapper().getTypeFactory().constructCollectionType(List.class, responseClass)
      );
    } catch (JsonProcessingException e) {
      throw new ExternalRequestException("Could not deserialize external HTTP response", e);
    }
  }

  private String executeRequest(ExternalRequest request) {
    validateRequestLimits(request);
    HttpUriRequest httpRequest = makeRequest(request);
    try (CloseableHttpClient client = HttpClients.custom()
        .disableAutomaticRetries()
        .disableRedirectHandling()
        .build();
         CloseableHttpResponse response = client.execute(httpRequest)) {
      int statusCode = response.getStatusLine().getStatusCode();
      String body;
      try {
        body = readEntity(response.getEntity(), effectiveMaxResponseBytes(request));
      } catch (ResponseSizeLimitException e) {
        throw sizeLimitException(request, statusCode, e);
      }
      if (statusCode < 200 || statusCode >= 300) {
        throw new ExternalRequestException(
            "External " + request.getMethod() + " request to " + sanitizedUri(request.getUri())
                + " failed with status " + statusCode + ": " + abbreviate(body),
            statusCode
        );
      }
      return body;
    } catch (IOException e) {
      throw new ExternalRequestException(
          "Could not execute external " + request.getMethod() + " request to " + sanitizedUri(request.getUri()),
          e
      );
    }
  }

  private HttpUriRequest makeRequest(ExternalRequest request) {
    RequestBuilder builder = RequestBuilder.create(request.getMethod().name())
        .setUri(withQueryParameters(request.getUri(), request.getQueryParameters()))
        .setConfig(RequestConfig.custom()
            .setConnectTimeout(effectiveConnectTimeout(request))
            .setSocketTimeout(effectiveResponseTimeout(request))
            .build());

    if (request.getPayload() != null) {
      builder.setEntity(new StringEntity(serialize(request.getPayload()), ContentType.APPLICATION_JSON));
    }

    request.getHeaders().forEach((name, value) -> builder.setHeader(new BasicHeader(name, value)));
    if (!containsHeader(request.getHeaders(), "Accept")) {
      builder.setHeader("Accept", JSON_MEDIA_TYPE);
    }
    if (request.getPayload() != null && !containsHeader(request.getHeaders(), "Content-Type")) {
      builder.setHeader("Content-Type", JSON_MEDIA_TYPE);
    }
    return builder.build();
  }

  private String serialize(Object payload) {
    try {
      return JacksonSerializer.getObjectMapper().writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      throw new ExternalRequestException("Could not serialize external HTTP request payload", e);
    }
  }

  private URI withQueryParameters(URI uri, Map<String, String> queryParameters) {
    try {
      URIBuilder builder = new URIBuilder(uri);
      queryParameters.forEach(builder::addParameter);
      return builder.build();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Could not build external request URI", e);
    }
  }

  private String readEntity(HttpEntity entity, long maxResponseBytes) throws IOException {
    if (entity == null) {
      return null;
    }
    try (InputStream input = entity.getContent(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[8192];
      int bytesRead;
      long totalBytes = 0;
      while ((bytesRead = input.read(buffer)) != -1) {
        totalBytes += bytesRead;
        if (totalBytes > maxResponseBytes) {
          throw new ResponseSizeLimitException(output.toString(StandardCharsets.UTF_8));
        }
        output.write(buffer, 0, bytesRead);
      }
      return output.toString(StandardCharsets.UTF_8);
    }
  }

  private int effectiveConnectTimeout(ExternalRequest request) {
    return request.getConnectTimeoutMillis() == null
        ? config.getConnectTimeoutMillis() : request.getConnectTimeoutMillis();
  }

  private int effectiveResponseTimeout(ExternalRequest request) {
    return request.getResponseTimeoutMillis() == null
        ? config.getResponseTimeoutMillis() : request.getResponseTimeoutMillis();
  }

  private long effectiveMaxResponseBytes(ExternalRequest request) {
    return request.getMaxResponseBytes() == null
        ? config.getMaxResponseBytes() : request.getMaxResponseBytes();
  }

  private void validateRequestLimits(ExternalRequest request) {
    validateLimit(request.getConnectTimeoutMillis(), config.getConnectTimeoutMillis(), "connectTimeoutMillis");
    validateLimit(request.getResponseTimeoutMillis(), config.getResponseTimeoutMillis(), "responseTimeoutMillis");
    validateLimit(request.getMaxResponseBytes(), config.getMaxResponseBytes(), "maxResponseBytes");
  }

  private void validateLimit(Number requestedLimit, Number configuredLimit, String limitName) {
    if (requestedLimit != null && requestedLimit.longValue() > configuredLimit.longValue()) {
      throw new IllegalArgumentException(limitName + " must not exceed the configured external request limit");
    }
  }

  private ExternalRequestException sizeLimitException(ExternalRequest request,
                                                      int statusCode,
                                                      ResponseSizeLimitException exception) {
    return new ExternalRequestException(
        "External " + request.getMethod() + " request to " + sanitizedUri(request.getUri())
            + " exceeded the configured response size limit with status " + statusCode + ": "
            + abbreviate(exception.responsePrefix),
        statusCode
    );
  }

  private boolean containsHeader(Map<String, String> headers, String name) {
    return headers.keySet().stream().anyMatch(header -> header.equalsIgnoreCase(name));
  }

  private String sanitizedUri(URI uri) {
    try {
      return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getRawPath(), null, null).toString();
    } catch (URISyntaxException e) {
      return uri.getScheme() + "://" + uri.getHost();
    }
  }

  private String abbreviate(String body) {
    if (body == null || body.isBlank()) {
      return "no response body";
    }
    return body.length() <= MAX_ERROR_RESPONSE_BYTES ? body : body.substring(0, MAX_ERROR_RESPONSE_BYTES) + "...";
  }

  private static final class ResponseSizeLimitException extends IOException {

    private final String responsePrefix;

    private ResponseSizeLimitException(String responsePrefix) {
      this.responsePrefix = responsePrefix;
    }
  }
}
