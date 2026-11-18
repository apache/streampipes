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

import org.apache.streampipes.export.resolver.AbstractResolver;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import java.util.Objects;
import java.util.function.Function;

/** Metadata and a required document resolver factory for an external resource type. */
public record ResourceRegistration<T>(PermissionDefaults permissions,
                                      String category,
                                      Function<IGenericStorage, ? extends AbstractResolver<T>> resolverFactory) {

  public ResourceRegistration {
    Objects.requireNonNull(permissions, "permissions");
    Objects.requireNonNull(resolverFactory, "resolverFactory");
  }

  public AbstractResolver<T> createResolver(IGenericStorage storage) {
    return Objects.requireNonNull(resolverFactory.apply(storage), "resolver");
  }
}
