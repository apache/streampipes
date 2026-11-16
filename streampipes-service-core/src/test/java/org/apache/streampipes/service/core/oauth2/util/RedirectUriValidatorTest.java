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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedirectUriValidatorTest {

  private static final String AUTHORIZED = "https://sp.example.org";

  @Test
  void acceptsUriOnTheConfiguredOrigin() {
    assertTrue(RedirectUriValidator.isAuthorized("https://sp.example.org/#/login", AUTHORIZED));
    assertTrue(RedirectUriValidator.isAuthorized("https://SP.example.org:443/#/login", AUTHORIZED));
    assertTrue(RedirectUriValidator.isAuthorized("http://localhost:8082/#/login", "http://localhost:8082"));
  }

  @Test
  void rejectsOtherOrigins() {
    assertFalse(RedirectUriValidator.isAuthorized("https://evil.example/landing", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized("http://sp.example.org/#/login", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized("https://sp.example.org:8443/#/login", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized("https://sp.example.org.evil.example/", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized("https://sp.example.org@evil.example/", AUTHORIZED));
  }

  @Test
  void rejectsUrisWithoutOrigin() {
    assertFalse(RedirectUriValidator.isAuthorized("//evil.example/landing", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized("/#/login", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized("https:evil.example", AUTHORIZED));
  }

  @Test
  void rejectsMalformedValues() {
    assertFalse(RedirectUriValidator.isAuthorized("https://evil.example\\@sp.example.org/", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized("https://sp.example.org/ #/login", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized("", AUTHORIZED));
    assertFalse(RedirectUriValidator.isAuthorized(null, AUTHORIZED));
  }

  @Test
  void rejectsEverythingWithoutConfiguredRedirectUri() {
    assertFalse(RedirectUriValidator.isAuthorized("https://sp.example.org/#/login", null));
  }
}
