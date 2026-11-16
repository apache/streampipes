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

package org.apache.streampipes.rest.impl;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.util.SimpleMethodInvocation;

import java.io.Serializable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineStatusAuthorizationTest {

  @Test
  void deniesOtherUsersPipelineAndUnknownPipeline() throws Exception {
    assertFalse(evaluate(true, "other"));
    assertFalse(evaluate(true, "unknown"));
  }

  @Test
  void allowsOwnedOrSharedPipeline() throws Exception {
    assertTrue(evaluate(true, "owned"));
    assertTrue(evaluate(true, "shared"));
  }

  @Test
  void requiresGeneralReadPrivilegeEvenWithObjectPermission() throws Exception {
    assertFalse(evaluate(false, "owned"));
  }

  public static class AuthorityFixture {
    private final boolean allowed;

    AuthorityFixture(boolean allowed) {
      this.allowed = allowed;
    }

    public boolean hasReadAuthority() {
      return allowed;
    }
  }

  private boolean evaluate(boolean readAuthority, String pipelineId) throws Exception {
    var method = PipelineResource.class.getMethod("getPipelineStatus", String.class);
    var handler = new DefaultMethodSecurityExpressionHandler();
    handler.setPermissionEvaluator(new PermissionEvaluator() {
      @Override
      public boolean hasPermission(org.springframework.security.core.Authentication authentication,
                                   Object target, Object permission) {
        return "READ".equals(permission) && List.of("owned", "shared").contains(target);
      }

      @Override
      public boolean hasPermission(org.springframework.security.core.Authentication authentication,
                                   Serializable targetId, String targetType, Object permission) {
        return false;
      }
    });
    var authentication = new UsernamePasswordAuthenticationToken("reader", "unused", List.of());
    var invocation = new SimpleMethodInvocation(new AuthorityFixture(readAuthority), method, pipelineId);
    var context = handler.createEvaluationContext(() -> authentication, invocation);
    var expression = handler.getExpressionParser().parseExpression(method.getAnnotation(PreAuthorize.class).value());
    return ExpressionUtils.evaluateAsBoolean(expression, context);
  }
}
