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

import org.apache.streampipes.audit.events.authentication.AuthenticationAuditRecorder;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class OAuth2AuthenticationFailureHandlerWiringTest {

  /**
   * With OAuth enabled the repository exists twice: as component and as bean of WebSecurityConfig.
   * The handler must still be created, otherwise the core does not start.
   */
  @Test
  void handlerIsCreatedWhenRepositoryIsRegisteredTwice() {
    try (var context = new AnnotationConfigApplicationContext()) {
      context.registerBean("httpCookieOAuth2AuthorizationRequestRepository",
          HttpCookieOAuth2AuthorizationRequestRepository.class);
      context.registerBean("cookieOAuth2AuthorizationRequestRepository",
          HttpCookieOAuth2AuthorizationRequestRepository.class);
      context.registerBean(AuthenticationAuditRecorder.class, () -> mock(AuthenticationAuditRecorder.class));
      context.registerBean(OAuth2AuthenticationFailureHandler.class);

      context.refresh();

      assertNotNull(context.getBean(OAuth2AuthenticationFailureHandler.class));
    }
  }
}
