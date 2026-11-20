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
import org.mockito.ArgumentCaptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CookieUtilsTest {

  @Test
  void cookieIsSecureOnHttps() {
    var request = mock(HttpServletRequest.class);
    when(request.isSecure()).thenReturn(true);

    var cookie = addCookie(request);

    assertTrue(cookie.getSecure());
    assertEquals("Lax", cookie.getAttribute("SameSite"));
    assertTrue(cookie.isHttpOnly());
  }

  @Test
  void cookieIsSecureBehindHttpsProxy() {
    var request = mock(HttpServletRequest.class);
    when(request.getHeader("X-Forwarded-Proto")).thenReturn("https");

    assertTrue(addCookie(request).getSecure());
  }

  @Test
  void cookieOnPlainHttpStillHasSameSite() {
    var cookie = addCookie(mock(HttpServletRequest.class));

    assertFalse(cookie.getSecure());
    assertEquals("Lax", cookie.getAttribute("SameSite"));
  }

  private static Cookie addCookie(HttpServletRequest request) {
    var response = mock(HttpServletResponse.class);
    CookieUtils.addCookie(request, response, "name", "value", 180);
    var captor = ArgumentCaptor.forClass(Cookie.class);
    verify(response).addCookie(captor.capture());
    return captor.getValue();
  }
}
