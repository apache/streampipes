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

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

public record AuditQuery(Instant from, Instant to, String eventType, String actor, String outcome,
                         int limit, String cursor) {
  private static final Duration MAX_TIME_RANGE = Duration.ofDays(31);
  // Keep timestamps within the nanosecond range supported by the initial storage adapter.
  private static final Instant MAX_TIMESTAMP = Instant.parse("2262-01-01T00:00:00Z");
  private static final int MAX_PAGE_SIZE = 200;
  private static final int MAX_OUTCOME_LENGTH = 16;
  private static final int MAX_CURSOR_LENGTH = 256;

  public AuditQuery {
    if (from == null || to == null || !from.isBefore(to) || from.isBefore(Instant.EPOCH)
        || Duration.between(from, to).compareTo(MAX_TIME_RANGE) > 0
        || to.isAfter(MAX_TIMESTAMP) || limit < 1 || limit > MAX_PAGE_SIZE) {
      throw new IllegalArgumentException("Invalid audit time range or page size");
    }
    eventType = normalize(eventType, AuditValidation.MAX_EVENT_TYPE_LENGTH);
    actor = normalize(actor, AuditValidation.MAX_ACTOR_LENGTH);
    outcome = normalize(outcome, MAX_OUTCOME_LENGTH);
    if (eventType != null) {
      AuditValidation.validateEventType(eventType);
    }
    if (outcome != null) {
      outcome = AuditOutcome.valueOf(outcome.toUpperCase(Locale.ROOT)).name().toLowerCase(Locale.ROOT);
    }
    if (cursor != null && cursor.length() > MAX_CURSOR_LENGTH) {
      throw new IllegalArgumentException("Invalid audit cursor");
    }
  }

  private static String normalize(String value, int maximum) {
    if (value == null || value.isBlank()) {
      return null;
    }
    if (value.length() > maximum || value.chars().anyMatch(Character::isISOControl)) {
      throw new IllegalArgumentException("Invalid audit filter");
    }
    return value;
  }
}
