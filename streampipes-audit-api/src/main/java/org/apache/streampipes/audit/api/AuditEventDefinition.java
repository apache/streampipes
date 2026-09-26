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

import java.util.Objects;
import java.util.regex.Pattern;

public record AuditEventDefinition<T extends AuditDetails>(String id, Class<T> detailsType, String resourceType) {
  private static final Pattern ID = Pattern.compile("[a-z][a-z0-9-]*(\\.[a-z][a-z0-9-]*)+");

  /** Defines an event without an associated resource, for example a login. */
  public AuditEventDefinition(String id, Class<T> detailsType) {
    this(id, detailsType, null);
  }

  public AuditEventDefinition {
    Objects.requireNonNull(id);
    Objects.requireNonNull(detailsType);
    if (resourceType != null && (resourceType.isBlank() || resourceType.length() > 128)) {
      throw new IllegalArgumentException("Invalid audit resource type");
    }
    if (id.length() > 128 || !ID.matcher(id).matches()) {
      throw new IllegalArgumentException("Invalid audit event type");
    }
  }
}
