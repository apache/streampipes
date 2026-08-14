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

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExternalRequestTest {

  @Test
  void preservesRequestDataInImmutableCopies() {
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("Authorization", "Bearer token");
    Map<String, String> queryParameters = new LinkedHashMap<>();
    queryParameters.put("page", "1");

    ExternalRequest request = ExternalRequest.builder(ExternalRequestMethod.POST, URI.create("https://api.example.org/v1"))
        .headers(headers)
        .queryParameters(queryParameters)
        .payload(Map.of("name", "value"))
        .connectTimeoutMillis(1_000)
        .responseTimeoutMillis(2_000)
        .maxResponseBytes(3_000)
        .build();
    headers.put("Authorization", "changed");
    queryParameters.put("page", "2");

    assertEquals("Bearer token", request.getHeaders().get("Authorization"));
    assertEquals("1", request.getQueryParameters().get("page"));
    assertThrows(UnsupportedOperationException.class, () -> request.getHeaders().put("X-Test", "value"));
    assertEquals(1_000, request.getConnectTimeoutMillis());
    assertEquals(2_000, request.getResponseTimeoutMillis());
    assertEquals(3_000L, request.getMaxResponseBytes());
  }

  @Test
  void acceptsOnlyAbsoluteHttpUrisWithoutSensitiveParts() {
    assertThrows(IllegalArgumentException.class,
        () -> request(URI.create("/relative"), ExternalRequestMethod.GET, null));
    assertThrows(IllegalArgumentException.class,
        () -> request(URI.create("ftp://api.example.org"), ExternalRequestMethod.GET, null));
    assertThrows(IllegalArgumentException.class,
        () -> request(URI.create("https://user@api.example.org"), ExternalRequestMethod.GET, null));
    assertThrows(IllegalArgumentException.class,
        () -> request(URI.create("https://api.example.org#fragment"), ExternalRequestMethod.GET, null));
  }

  @Test
  void rejectsTransportControlledHeadersCaseInsensitively() {
    for (String header : new String[] {"Connection", "CONTENT-LENGTH", "Host", "Transfer-Encoding", "upgrade"}) {
      assertThrows(IllegalArgumentException.class,
          () -> ExternalRequest.builder(ExternalRequestMethod.GET, URI.create("https://api.example.org"))
              .header(header, "value")
              .build());
    }
  }

  @Test
  void rejectsHeaderInjectionCharacters() {
    assertThrows(IllegalArgumentException.class,
        () -> ExternalRequest.builder(ExternalRequestMethod.GET, URI.create("https://api.example.org"))
            .header("X-Test\r\nHost", "example.org")
            .build());
    assertThrows(IllegalArgumentException.class,
        () -> ExternalRequest.builder(ExternalRequestMethod.GET, URI.create("https://api.example.org"))
            .header("X-Test", "value\r\nHost: example.org")
            .build());
  }

  @Test
  void requiresBodiesOnlyForPostAndPut() {
    assertThrows(IllegalArgumentException.class,
        () -> request(URI.create("https://api.example.org"), ExternalRequestMethod.POST, null));
    assertThrows(IllegalArgumentException.class,
        () -> request(URI.create("https://api.example.org"), ExternalRequestMethod.PUT, null));
    assertThrows(IllegalArgumentException.class,
        () -> request(URI.create("https://api.example.org"), ExternalRequestMethod.GET, Map.of()));
    assertThrows(IllegalArgumentException.class,
        () -> request(URI.create("https://api.example.org"), ExternalRequestMethod.DELETE, Map.of()));
  }

  @Test
  void exposesSecureDefaultLimits() {
    ExternalRequestConfig config = ExternalRequestConfig.defaults();

    assertEquals(10_000, config.getConnectTimeoutMillis());
    assertEquals(30_000, config.getResponseTimeoutMillis());
    assertEquals(10L * 1024 * 1024, config.getMaxResponseBytes());
  }

  @Test
  void rejectsNonPositiveLimits() {
    assertThrows(IllegalArgumentException.class,
        () -> ExternalRequestConfig.builder().connectTimeoutMillis(0).build());
    assertThrows(IllegalArgumentException.class,
        () -> ExternalRequestConfig.builder().responseTimeoutMillis(-1).build());
    assertThrows(IllegalArgumentException.class,
        () -> ExternalRequestConfig.builder().maxResponseBytes(0).build());
    assertThrows(IllegalArgumentException.class,
        () -> ExternalRequest.builder(ExternalRequestMethod.GET, URI.create("https://api.example.org"))
            .maxResponseBytes(-1)
            .build());
  }

  private ExternalRequest request(URI uri, ExternalRequestMethod method, Object payload) {
    ExternalRequest.Builder builder = ExternalRequest.builder(method, uri);
    if (payload != null) {
      builder.payload(payload);
    }
    return builder.build();
  }
}
