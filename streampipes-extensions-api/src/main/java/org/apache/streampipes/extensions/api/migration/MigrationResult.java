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

package org.apache.streampipes.extensions.api.migration;

import org.apache.streampipes.model.message.Notification;

import java.util.List;

public record MigrationResult<T> (boolean success, T element, List<Notification> messages){

  public static <T> MigrationResult<T> failure(T element, List<Notification> messages) {
    return new MigrationResult<>(false, element, messages);
  }

  public static <T> MigrationResult<T> failure(T element, Notification message) {
    return failure(element, List.of(message));
  }

  public static <T> MigrationResult<T> success(T element) {
    return new MigrationResult<>(true, element, List.of());
  }
}
