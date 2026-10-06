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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public class CookieUtils {

  public static Optional<Cookie> getCookie(HttpServletRequest request,
                                           String name) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          return Optional.of(cookie);
        }
      }
    }

    return Optional.empty();
  }

  /**
   * SameSite is Lax and not Strict, because the identity provider redirects back to StreamPipes with a
   * cross-site navigation that must still carry the cookie.
   */
  public static void addCookie(HttpServletRequest request,
                               HttpServletResponse response,
                               String name,
                               String value,
                               int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(isSecureRequest(request));
    cookie.setAttribute("SameSite", "Lax");
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  public static boolean isSecureRequest(HttpServletRequest request) {
    String forwardedProto = request.getHeader("X-Forwarded-Proto");
    return request.isSecure() || "https".equalsIgnoreCase(forwardedProto);
  }

  public static void deleteCookie(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          cookie.setValue("");
          cookie.setPath("/");
          cookie.setMaxAge(0);
          response.addCookie(cookie);
        }
      }
    }
  }
}
