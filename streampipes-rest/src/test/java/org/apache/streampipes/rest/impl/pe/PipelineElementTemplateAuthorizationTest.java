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

package org.apache.streampipes.rest.impl.pe;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineElementTemplateAuthorizationTest {

  @Test
  void deniesUsersWithoutWritePrivileges() throws Exception {
    assertFalse(canDelete());
    assertFalse(canDelete("PRIVILEGE_READ_PIPELINE"));
    assertFalse(canDelete("PRIVILEGE_READ_ADAPTER"));
  }

  @Test
  void allowsPipelineAndAdapterWriters() throws Exception {
    assertTrue(canDelete("PRIVILEGE_WRITE_PIPELINE"));
    assertTrue(canDelete("PRIVILEGE_WRITE_ADAPTER"));
  }

  @Test
  void allowsAdministrators() throws Exception {
    assertTrue(canDelete("ROLE_ADMIN"));
    assertTrue(canDelete("ROLE_SERVICE_ADMIN"));
  }

  private boolean canDelete(String... authorities) throws Exception {
    var authentication = new UsernamePasswordAuthenticationToken("user", "unused",
        Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList());
    var previousContext = SecurityContextHolder.getContext();
    try {
      var context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);
      SecurityContextHolder.setContext(context);
      var resource = new PipelineElementTemplateResource(null);
      var method = PipelineElementTemplateResource.class.getMethod("delete", String.class);
      var annotation = method.getAnnotation(PreAuthorize.class);
      if (annotation == null) {
        return true;
      }
      var handler = new DefaultMethodSecurityExpressionHandler();
      var invocation = new SimpleMethodInvocation(resource, method, "shared-template");
      var evaluationContext = handler.createEvaluationContext(() -> authentication, invocation);
      return ExpressionUtils.evaluateAsBoolean(
          handler.getExpressionParser().parseExpression(annotation.value()), evaluationContext);
    } finally {
      SecurityContextHolder.setContext(previousContext);
    }
  }
}
