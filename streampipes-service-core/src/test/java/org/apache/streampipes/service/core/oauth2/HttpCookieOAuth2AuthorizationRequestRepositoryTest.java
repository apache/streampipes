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

package org.apache.streampipes.service.core.oauth2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HttpCookieOAuth2AuthorizationRequestRepositoryTest {

  private static final String COOKIE_NAME = "oauth2_auth_request";

  private final HttpCookieOAuth2AuthorizationRequestRepository repository =
      new HttpCookieOAuth2AuthorizationRequestRepository();

  @BeforeEach
  void resetTripwire() {
    Tripwire.triggered = false;
  }

  @Test
  void loadsTheRequestItSaved() {
    var saved = authorizationRequest();
    var cookie = save(saved);

    var loaded = repository.loadAuthorizationRequest(requestWith(cookie));

    assertEquals(saved.getState(), loaded.getState());
    assertEquals(saved.getAttributes(), loaded.getAttributes());
  }

  @Test
  void doesNotDeserializeJavaObjectsFromTheCookie() throws IOException {
    var cookie = new Cookie(COOKIE_NAME, javaSerialized(new Tripwire()));

    assertNull(repository.loadAuthorizationRequest(requestWith(cookie)));
    assertFalse(Tripwire.triggered);
  }

  @Test
  void ignoresJavaSerializedString() {
    // the serialized string "hello"
    var value = Base64.getUrlEncoder().encodeToString(new byte[]{
        (byte) 0xac, (byte) 0xed, 0x00, 0x05, 0x74, 0x00, 0x05, 'h', 'e', 'l', 'l', 'o'});

    assertNull(repository.loadAuthorizationRequest(requestWith(new Cookie(COOKIE_NAME, value))));
  }

  @Test
  void ignoresChangedCookie() {
    var cookie = save(authorizationRequest());
    var forged = new Cookie(COOKIE_NAME, cookie.getValue().substring(0, cookie.getValue().length() - 2) + "AA");

    assertNull(repository.loadAuthorizationRequest(requestWith(forged)));
  }

  @Test
  void removeDeletesTheCookie() {
    var cookie = save(authorizationRequest());
    var response = mock(HttpServletResponse.class);

    var removed = repository.removeAuthorizationRequest(requestWith(cookie), response);

    assertEquals("state-value", removed.getState());
    var captor = ArgumentCaptor.forClass(Cookie.class);
    verify(response, atLeastOnce()).addCookie(captor.capture());
    var deleted = captor.getAllValues().stream().filter(c -> COOKIE_NAME.equals(c.getName())).findFirst().orElseThrow();
    assertEquals(0, deleted.getMaxAge());
  }

  private Cookie save(OAuth2AuthorizationRequest authorizationRequest) {
    var response = mock(HttpServletResponse.class);
    repository.saveAuthorizationRequest(authorizationRequest, mock(HttpServletRequest.class), response);
    var captor = ArgumentCaptor.forClass(Cookie.class);
    verify(response, atLeastOnce()).addCookie(captor.capture());
    return captor.getAllValues().stream().filter(c -> COOKIE_NAME.equals(c.getName())).findFirst().orElseThrow();
  }

  private static HttpServletRequest requestWith(Cookie cookie) {
    var request = mock(HttpServletRequest.class);
    when(request.getCookies()).thenReturn(new Cookie[]{cookie});
    return request;
  }

  private static String javaSerialized(Object object) throws IOException {
    var bytes = new ByteArrayOutputStream();
    try (var out = new ObjectOutputStream(bytes)) {
      out.writeObject(object);
    }
    return Base64.getUrlEncoder().encodeToString(bytes.toByteArray());
  }

  private static OAuth2AuthorizationRequest authorizationRequest() {
    return OAuth2AuthorizationRequest.authorizationCode()
        .authorizationUri("https://idp.example.org/authorize")
        .clientId("streampipes")
        .redirectUri("https://streampipes.example.org/streampipes-backend/login/oauth2/code/idp")
        .scopes(Set.of("openid"))
        .state("state-value")
        .attributes(Map.of("registration_id", "idp", "code_verifier", "verifier-value"))
        .build();
  }

  /**
   * Records whether it was ever deserialized.
   */
  static class Tripwire implements Serializable {

    static volatile boolean triggered;

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      triggered = true;
    }
  }
}
