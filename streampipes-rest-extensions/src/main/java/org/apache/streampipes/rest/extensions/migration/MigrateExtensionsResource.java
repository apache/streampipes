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

package org.apache.streampipes.rest.extensions.migration;

import org.apache.streampipes.extensions.api.migration.ModelMigrator;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.rest.extensions.AbstractExtensionsResource;

import java.util.Optional;

public class MigrateExtensionsResource<T extends ModelMigrator> extends AbstractExtensionsResource {

  /**
   * Find and return the corresponding {@link ModelMigrator} instance within the registered migrators.
   * This allows to pass the corresponding model migrator to a {@link ModelMigratorConfig} which is exchanged
   * between Core and Extensions service.
   * @param modelMigratorConfig config that describes the model migrator to be returned
   * @return Optional model migrator which is empty in case no appropriate migrator is found among the registered.
   */
  public Optional<T> getMigrator(ModelMigratorConfig modelMigratorConfig) {
    return DeclarersSingleton.getInstance().getServiceDefinition().getMigrators()
            .stream()
            .filter(modelMigrator -> modelMigrator.config().equals(modelMigratorConfig))
            .map(modelMigrator -> (T) modelMigrator)
            .findFirst();
  }
}
