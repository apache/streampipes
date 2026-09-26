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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Best-effort recording: implementations must not change a business operation's outcome. */
public interface AuditService extends AutoCloseable {
  @Override
  default void close() {
    // Disabled implementations have no worker or storage resources.
  }

  /** Null or blank actors are recorded as unknown; background operations should supply an explicit system actor. */
  <T extends AuditDetails> void record(AuditEventDefinition<T> definition, AuditOutcome outcome,
                                      String actor, String resourceId, T details);

  default AuditPage query(AuditQuery query) {
    throw new IllegalStateException("Audit reading unavailable");
  }

  default Optional<AuditEntryDetails> find(UUID eventId, String locator) {
    throw new IllegalStateException("Audit reading unavailable");
  }

  default List<String> eventTypes() {
    return List.of();
  }

  default AuditStatus status() {
    return new AuditStatus(false, false, 0, null);
  }

  static AuditService disabled() {
    return new AuditService() {
      @Override
      public <T extends AuditDetails> void record(AuditEventDefinition<T> definition, AuditOutcome outcome,
                                                  String actor, String resourceId, T details) {
        // Explicitly disabled for deployments without audit configuration and legacy constructors.
      }
    };
  }
}
