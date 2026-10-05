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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OAuth2AuthenticationFailureHandlerTest {

  @Test
  void errorCodeBecomesPartOfTheHashRoute() {
    assertEquals(
        "http://localhost/#/login?error=oauth_login_failed",
        OAuth2AuthenticationFailureHandler.appendErrorCode("http://localhost/#/login")
    );
  }

  @Test
  void errorCodeIsAppendedToExistingRouteParameters() {
    assertEquals(
        "http://localhost/#/login?returnUrl=home&error=oauth_login_failed",
        OAuth2AuthenticationFailureHandler.appendErrorCode("http://localhost/#/login?returnUrl=home")
    );
  }

  @Test
  void queryBeforeTheHashRouteDoesNotCount() {
    assertEquals(
        "http://localhost/?a=b#/login?error=oauth_login_failed",
        OAuth2AuthenticationFailureHandler.appendErrorCode("http://localhost/?a=b#/login")
    );
  }

  @Test
  void errorCodeIsAppendedToUrlWithoutHashRoute() {
    assertEquals("/?error=oauth_login_failed", OAuth2AuthenticationFailureHandler.appendErrorCode("/"));
    assertEquals("/?a=b&error=oauth_login_failed", OAuth2AuthenticationFailureHandler.appendErrorCode("/?a=b"));
  }

  @Test
  void logMessageContainsNoControlCharacters() {
    assertEquals(
        "failed__INFO forged entry_",
        OAuth2AuthenticationFailureHandler.sanitizeForLog("failed\r\nINFO forged entry\t")
    );
    assertEquals("", OAuth2AuthenticationFailureHandler.sanitizeForLog(null));
  }
}
