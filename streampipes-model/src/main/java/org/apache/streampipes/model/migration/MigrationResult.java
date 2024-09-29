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
package org.apache.streampipes.model.migration;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;

/**
 * Models the outcome of a migration.
 * 
 * @param success
 *          whether the migration was successfully or not.
 * @param element
 *          the migrated pipeline element or in case of a failure the original one
 * @param message
 *          message that describes the outcome of the migration
 * @param <T>
 *          type of the migration element
 */
public record MigrationResult<T extends NamedStreamPipesEntity>(boolean success, T element, String message) {

  public static <T extends NamedStreamPipesEntity> MigrationResult<T> failure(T element, String message) {
    return new MigrationResult<>(false, element, message);
  }

  public static <T extends NamedStreamPipesEntity> MigrationResult<T> success(T element) {
    return new MigrationResult<>(true, element, "SUCCESS");
  }
}
