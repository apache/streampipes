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

import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.events.StandardAuditEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Publishes group lifecycle events without serializing security assignments or external identifiers. */
public final class SecurityAuditRecorder {
  private final AuditService auditService;

  public SecurityAuditRecorder(AuditService auditService) {
    this.auditService = Objects.requireNonNull(auditService);
  }

  public void groupCreated(String actor, String groupId) {
    auditService.record(StandardAuditEvents.SECURITY_GROUP_CREATE, AuditOutcome.SUCCEEDED, actor,
        groupId, new GroupAuditDetails(List.of()));
  }

  public void groupUpdated(String actor,
                           String groupId,
                           String beforeName,
                           String afterName,
                           Set<String> beforeRoles,
                           Set<String> afterRoles,
                           Set<String> beforeAlternateIds,
                           Set<String> afterAlternateIds) {
    auditService.record(StandardAuditEvents.SECURITY_GROUP_UPDATE, AuditOutcome.SUCCEEDED, actor,
        groupId, new GroupAuditDetails(changedFields(beforeName, afterName, beforeRoles, afterRoles,
            beforeAlternateIds, afterAlternateIds)));
  }

  public void groupDeleted(String actor, String groupId) {
    auditService.record(StandardAuditEvents.SECURITY_GROUP_DELETE, AuditOutcome.SUCCEEDED, actor,
        groupId, new GroupAuditDetails(List.of()));
  }

  private List<String> changedFields(String beforeName,
                                     String afterName,
                                     Set<String> beforeRoles,
                                     Set<String> afterRoles,
                                     Set<String> beforeAlternateIds,
                                     Set<String> afterAlternateIds) {
    if (beforeName == null && beforeRoles == null && beforeAlternateIds == null) {
      return List.of("group");
    }

    var changes = new ArrayList<String>();
    addIfChanged(changes, "groupName", beforeName, afterName);
    addIfChanged(changes, "roles", beforeRoles, afterRoles);
    addIfChanged(changes, "alternateIds", beforeAlternateIds, afterAlternateIds);
    return List.copyOf(changes);
  }

  private void addIfChanged(List<String> changes, String field, Object before, Object after) {
    if (!Objects.equals(before, after)) {
      changes.add(field);
    }
  }
}
