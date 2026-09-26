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

package org.apache.streampipes.audit.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Logical immutable event; the adapter owns its physical identity and JSON encoding. */
public record AuditEvent<T extends AuditDetails>(UUID eventId, Instant recordedAt,
                                                AuditEventDefinition<T> definition, AuditOutcome outcome,
                                                String actor, String resourceType, String resourceId, T details) {
  public AuditEvent {
    Objects.requireNonNull(eventId);
    Objects.requireNonNull(recordedAt);
    Objects.requireNonNull(definition);
    Objects.requireNonNull(outcome);
    if (actor == null || actor.isBlank() || actor.length() > 256) {
      throw new IllegalArgumentException("Invalid audit actor");
    }
    if (resourceType != null && (resourceType.isBlank() || resourceType.length() > 128)) {
      throw new IllegalArgumentException("Invalid audit resource type");
    }
    if (resourceId != null && (resourceId.isBlank() || resourceId.length() > 1024)) {
      throw new IllegalArgumentException("Invalid audit resource ID");
    }
    if (details != null && !definition.detailsType().isInstance(details)) {
      throw new IllegalArgumentException("Unexpected audit details type");
    }
  }
}
