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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.client.user.DefaultPrivilege;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.util.SimpleMethodInvocation;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Evaluates the {@link PreAuthorize} expressions of the single-element handlers with the
 * Spring Security expression handler, so that the object permission of the addressed
 * element is required in addition to the general pipeline element privilege.
 */
class PipelineElementResourceAuthorizationTest {

  private static final String ELEMENT_ID = "sp:spdatastream:element-a";

  private record Handler(Method method, Object[] args, String privilege, String permission) {
  }

  private static List<Handler> handlers() throws NoSuchMethodException {
    var read = DefaultPrivilege.Constants.PRIVILEGE_READ_PIPELINE_ELEMENT_VALUE;
    var write = DefaultPrivilege.Constants.PRIVILEGE_WRITE_PIPELINE_ELEMENT_VALUE;
    var stream = new SpDataStream();
    stream.setElementId(ELEMENT_ID);

    return List.of(
        new Handler(DataStreamResource.class.getMethod("getElement", String.class),
            new Object[]{ELEMENT_ID}, read, "READ"),
        new Handler(DataStreamResource.class.getMethod("delete", String.class),
            new Object[]{ELEMENT_ID}, write, "WRITE"),
        new Handler(DataStreamResource.class.getMethod("update", String.class, SpDataStream.class),
            new Object[]{ELEMENT_ID, stream}, write, "WRITE"),
        new Handler(DataStreamResource.class.getMethod("performPipelineMigrationPreflight", SpDataStream.class),
            new Object[]{stream}, write, "WRITE"),
        new Handler(DataProcessorResource.class.getMethod("getElement", String.class),
            new Object[]{ELEMENT_ID}, read, "READ"),
        new Handler(DataProcessorResource.class.getMethod("removeOwn", String.class),
            new Object[]{ELEMENT_ID}, write, "WRITE"),
        new Handler(DataSinkResource.class.getMethod("getElement", String.class),
            new Object[]{ELEMENT_ID}, read, "READ"),
        new Handler(DataSinkResource.class.getMethod("removeOwn", String.class),
            new Object[]{ELEMENT_ID}, write, "WRITE")
    );
  }

  @Test
  void deniesUserWithPrivilegeButWithoutObjectPermission() throws Exception {
    for (var handler : handlers()) {
      var evaluator = mock(PermissionEvaluator.class);
      when(evaluator.hasPermission(any(), any(), any())).thenReturn(false);

      assertFalse(evaluate(handler, evaluator, handler.privilege()), handler.method().toString());
      verify(evaluator).hasPermission(any(Authentication.class), eq(ELEMENT_ID), eq(handler.permission()));
    }
  }

  @Test
  void allowsUserWithPrivilegeAndObjectPermission() throws Exception {
    for (var handler : handlers()) {
      var evaluator = mock(PermissionEvaluator.class);
      when(evaluator.hasPermission(any(), eq(ELEMENT_ID), eq(handler.permission()))).thenReturn(true);

      assertTrue(evaluate(handler, evaluator, handler.privilege()), handler.method().toString());
    }
  }

  @Test
  void deniesUserWithObjectPermissionButWithoutPrivilege() throws Exception {
    for (var handler : handlers()) {
      var evaluator = mock(PermissionEvaluator.class);
      when(evaluator.hasPermission(any(), any(), any())).thenReturn(true);

      assertFalse(evaluate(handler, evaluator, "PRIVILEGE_UNRELATED"), handler.method().toString());
    }
  }

  private static boolean evaluate(Handler handler,
                                  PermissionEvaluator evaluator,
                                  String authority) {
    var expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(evaluator);

    Authentication auth = new UsernamePasswordAuthenticationToken(
        "user", "n/a", List.of(new SimpleGrantedAuthority(authority)));
    var invocation = new SimpleMethodInvocation(null, handler.method(), handler.args());
    var context = expressionHandler.createEvaluationContext(() -> auth, invocation);
    var expression = expressionHandler.getExpressionParser()
        .parseExpression(handler.method().getAnnotation(PreAuthorize.class).value());

    return ExpressionUtils.evaluateAsBoolean(expression, context);
  }
}
