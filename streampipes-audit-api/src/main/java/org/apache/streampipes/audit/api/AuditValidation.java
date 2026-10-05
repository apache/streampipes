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

import java.util.regex.Pattern;

/** Shared validation rules for event envelopes, definitions and query filters. */
final class AuditValidation {
  static final int MAX_EVENT_TYPE_LENGTH = 128;
  static final int MAX_ACTOR_LENGTH = 256;
  static final int MAX_RESOURCE_TYPE_LENGTH = 128;
  static final int MAX_RESOURCE_ID_LENGTH = 1024;

  private static final Pattern EVENT_TYPE_PATTERN = Pattern.compile("[a-z][a-z0-9-]*(\\.[a-z][a-z0-9-]*)+");

  private AuditValidation() {
  }

  static void validateEventType(String eventType) {
    if (eventType.length() > MAX_EVENT_TYPE_LENGTH || !EVENT_TYPE_PATTERN.matcher(eventType).matches()) {
      throw new IllegalArgumentException("Invalid audit event type");
    }
  }

  static void validateResourceType(String resourceType) {
    if (resourceType != null && (resourceType.isBlank() || resourceType.length() > MAX_RESOURCE_TYPE_LENGTH)) {
      throw new IllegalArgumentException("Invalid audit resource type");
    }
  }
}
