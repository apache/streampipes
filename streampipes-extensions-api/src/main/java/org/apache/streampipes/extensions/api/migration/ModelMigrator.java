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

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

public interface ModelMigrator<T extends NamedStreamPipesEntity, ExT extends IParameterExtractor<?>> {

  ModelMigratorConfig config();

  /**
   * Defines the migration to be performed.
   * @param element Entity to be transformed.
   * @param extractor Extractor that allows to handle static properties.
   * @return Result of the migration that describes both outcomes: successful and failed migrations
   * @throws RuntimeException in case any unexpected error occurs
   */
  MigrationResult<T> migrate(T element, ExT extractor) throws RuntimeException;

}
