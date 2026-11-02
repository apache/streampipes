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

package org.apache.streampipes.export;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Registrations for resource types persisted in generic storage. */
public class GenericStorageResourceRegistry {

  public static final GenericStorageResourceRegistry INSTANCE = new GenericStorageResourceRegistry();

  private final Map<String, ResourceRegistration<?>> registrations = new ConcurrentHashMap<>();

  public void register(String appDocType, ResourceRegistration<?> registration) {
    Objects.requireNonNull(appDocType, "appDocType");
    Objects.requireNonNull(registration, "registration");
    var existing = registrations.putIfAbsent(appDocType, registration);
    if (existing != null && !existing.equals(registration)) {
      throw new IllegalArgumentException("Resource type already registered for " + appDocType);
    }
  }

  public Optional<ResourceRegistration<?>> find(String appDocType) {
    return Optional.ofNullable(registrations.get(appDocType));
  }
}
