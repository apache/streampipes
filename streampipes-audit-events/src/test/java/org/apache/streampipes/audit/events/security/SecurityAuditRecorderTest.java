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

package org.apache.streampipes.audit.events.security;

import org.apache.streampipes.audit.api.AuditDetails;
import org.apache.streampipes.audit.api.AuditEventDefinition;
import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityAuditRecorderTest {

  @Test
  void recordsChangedGroupFieldsWithoutSecurityAssignments() {
    var auditService = new CapturingAuditService();
    var recorder = new SecurityAuditRecorder(auditService);
    recorder.groupUpdated("admin-1", "group-1", "Old operators", "New operators",
        Set.of("ROLE_USER"), Set.of("ROLE_ADMIN"), Set.of("old-id"), Set.of("new-id"));

    var call = auditService.calls.getFirst();
    var details = (GroupAuditDetails) call.details();
    assertEquals(StandardAuditEvents.SECURITY_GROUP_UPDATE, call.definition());
    assertEquals(AuditOutcome.SUCCEEDED, call.outcome());
    assertEquals(List.of("groupName", "roles", "alternateIds"), details.changedFields());
    assertFalse(details.toString().contains("ROLE_ADMIN"));
    assertFalse(details.toString().contains("new-id"));
  }

  @Test
  void registersGroupLifecycleEventTypes() {
    var eventTypes = new StandardAuditEvents().eventTypes();

    assertTrue(eventTypes.containsAll(List.of(
        StandardAuditEvents.SECURITY_GROUP_CREATE,
        StandardAuditEvents.SECURITY_GROUP_UPDATE,
        StandardAuditEvents.SECURITY_GROUP_DELETE)));
  }

  private record AuditCall(AuditEventDefinition<?> definition, AuditOutcome outcome, AuditDetails details) {
  }

  private static final class CapturingAuditService implements AuditService {
    private final List<AuditCall> calls = new ArrayList<>();

    @Override
    public <T extends AuditDetails> void record(AuditEventDefinition<T> definition,
                                                AuditOutcome outcome,
                                                String actor,
                                                String resourceId,
                                                T details) {
      calls.add(new AuditCall(definition, outcome, details));
    }
  }
}
