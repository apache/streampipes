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
  public AuditQuery {
    if (from == null || to == null || !from.isBefore(to) || from.isBefore(Instant.EPOCH)
        || Duration.between(from, to).compareTo(Duration.ofDays(31)) > 0
        || to.isAfter(Instant.parse("2262-01-01T00:00:00Z")) || limit < 1 || limit > 200) {
      throw new IllegalArgumentException("Invalid audit time range or page size");
    }
    eventType = normalize(eventType, 128);
    actor = normalize(actor, 256);
    outcome = normalize(outcome, 16);
    if (eventType != null && !eventType.matches("[a-z][a-z0-9-]*(\\.[a-z][a-z0-9-]*)+")) {
      throw new IllegalArgumentException("Invalid audit event type");
    }
    if (outcome != null) {
      outcome = AuditOutcome.valueOf(outcome.toUpperCase(Locale.ROOT)).name().toLowerCase(Locale.ROOT);
    }
    if (cursor != null && cursor.length() > 256) {
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
