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

package org.apache.streampipes.service.core.oauth2.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorizationRequestCookieCodecTest {

  private static final Duration MAX_AGE = Duration.ofSeconds(180);
  private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

  private final AuthorizationRequestCookieCodec codec = codecAt(NOW);

  @Test
  void restoresAllFieldsOfTheRequest() {
    var request = authorizationRequest();

    var restored = codec.decode(codec.encode(request)).orElseThrow();

    assertEquals(request.getAuthorizationUri(), restored.getAuthorizationUri());
    assertEquals(request.getGrantType(), restored.getGrantType());
    assertEquals(request.getResponseType(), restored.getResponseType());
    assertEquals(request.getClientId(), restored.getClientId());
    assertEquals(request.getRedirectUri(), restored.getRedirectUri());
    assertEquals(request.getScopes(), restored.getScopes());
    assertEquals(request.getState(), restored.getState());
    assertEquals(request.getAdditionalParameters(), restored.getAdditionalParameters());
    assertEquals(request.getAuthorizationRequestUri(), restored.getAuthorizationRequestUri());
    assertEquals(request.getAttributes(), restored.getAttributes());
  }

  @Test
  void rejectsChangedValue() {
    byte[] value = Base64.getUrlDecoder().decode(codec.encode(authorizationRequest()));
    value[value.length - 20] ^= 1;

    assertTrue(codec.decode(Base64.getUrlEncoder().withoutPadding().encodeToString(value)).isEmpty());
  }

  @Test
  void rejectsExpiredValue() {
    var value = codec.encode(authorizationRequest());

    assertTrue(codecAt(NOW.plus(MAX_AGE)).decode(value).isPresent());
    assertTrue(codecAt(NOW.plus(MAX_AGE).plusSeconds(1)).decode(value).isEmpty());
  }

  @Test
  void rejectsMalformedValues() {
    assertTrue(codec.decode("").isEmpty());
    assertTrue(codec.decode("not base64!").isEmpty());
    assertTrue(codec.decode("AAAA").isEmpty());
    assertTrue(codec.decode(Base64.getUrlEncoder().encodeToString("{\"state\":\"x\"}".getBytes())).isEmpty());
  }

  private static AuthorizationRequestCookieCodec codecAt(Instant instant) {
    return new AuthorizationRequestCookieCodec(MAX_AGE, Clock.fixed(instant, ZoneOffset.UTC));
  }

  static OAuth2AuthorizationRequest authorizationRequest() {
    return OAuth2AuthorizationRequest.authorizationCode()
        .authorizationUri("https://idp.example.org/authorize")
        .clientId("streampipes")
        .redirectUri("https://streampipes.example.org/streampipes-backend/login/oauth2/code/idp")
        .scopes(Set.of("openid", "email"))
        .state("state-value")
        .additionalParameters(Map.of(
            "code_challenge", "challenge-value",
            "code_challenge_method", "S256",
            "nonce", "nonce-hash"))
        .attributes(Map.of(
            "registration_id", "idp",
            "code_verifier", "verifier-value",
            "nonce", "nonce-value"))
        .authorizationRequestUri("https://idp.example.org/authorize?response_type=code&client_id=streampipes")
        .build();
  }
}
