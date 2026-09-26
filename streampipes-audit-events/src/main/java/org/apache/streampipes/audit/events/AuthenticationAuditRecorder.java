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

package org.apache.streampipes.audit.events;

import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;

import java.util.Objects;

/** Resource-free authentication events; identities come from validated authentication, never submitted names. */
public final class AuthenticationAuditRecorder {
  private final AuditService auditService;

  public AuthenticationAuditRecorder(AuditService auditService) {
    this.auditService = Objects.requireNonNull(auditService);
  }

  public void loggedIn(String actor, AuthenticationMethod method) {
    auditService.record(StandardAuditEvents.AUTH_LOGIN, AuditOutcome.SUCCEEDED, actor, null,
        new AuthenticationDetails(method));
  }

  public void loginDenied(AuthenticationMethod method) {
    auditService.record(StandardAuditEvents.AUTH_LOGIN, AuditOutcome.DENIED, null, null,
        new AuthenticationDetails(method));
  }

  public void loggedOut(String actor) {
    auditService.record(StandardAuditEvents.AUTH_LOGOUT, AuditOutcome.SUCCEEDED, actor, null, null);
  }
}
