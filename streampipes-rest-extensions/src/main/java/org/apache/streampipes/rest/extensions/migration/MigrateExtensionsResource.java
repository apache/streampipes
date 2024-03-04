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

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IModelMigrator;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.base.VersionedNamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.rest.extensions.AbstractExtensionsResource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public abstract class MigrateExtensionsResource<
    T extends VersionedNamedStreamPipesEntity,
    ExT extends IParameterExtractor,
    MmT extends IModelMigrator<T, ExT>> extends AbstractExtensionsResource {

  private static final Logger LOG = LoggerFactory.getLogger(MigrateExtensionsResource.class);

  /**
   * Find and return the corresponding {@link IModelMigrator} instance within the registered migrators.
   * This allows to pass the corresponding model migrator to a {@link ModelMigratorConfig} which is exchanged
   * between Core and Extensions service.
   *
   * @param modelMigratorConfig config that describes the model migrator to be returned
   * @return Optional model migrator which is empty in case no appropriate migrator is found among the registered.
   */
  public Optional<MmT> getMigrator(ModelMigratorConfig modelMigratorConfig) {
    return DeclarersSingleton.getInstance()
                             .getServiceDefinition()
                             .getMigrators()
                             .stream()
                             .filter(modelMigrator -> modelMigrator.config()
                                                                   .equals(modelMigratorConfig))
                             .map(modelMigrator -> (MmT) modelMigrator)
                             .findFirst();
  }

  /**
   * Migrates a pipeline element instance based on the provided {@link MigrationRequest}.
   * The outcome of the migration is described in {@link MigrationResult}.
   * The result is always part of the response.
   * Independent, of the migration outcome, the returned response always has OK as status code.
   * It is the responsibility of the recipient to interpret the migration result and act accordingly.
   *
   * @param migrationRequest Request that contains both the pipeline element to be migrated and the migration config.
   * @return A response with status code ok, that contains a migration result reflecting the outcome of the operation.
   */
  protected MigrationResult<T> handleMigration(MigrationRequest<T> migrationRequest) {

    var pipelineElementDescription = migrationRequest.migrationElement();
    var migrationConfig = migrationRequest.modelMigratorConfig();

    LOG.info(
        "Received migration request for pipeline element '{}' to migrate from version {} to {}",
        pipelineElementDescription.getElementId(),
        migrationConfig.fromVersion(),
        migrationConfig.toVersion()
    );

    var migratorOptional = getMigrator(migrationConfig);

    if (migratorOptional.isPresent()) {
      LOG.info("Migrator found for request, starting migration...");
      return executeMigration(migratorOptional.get(), pipelineElementDescription);
    }
    LOG.error("Migrator for migration config {} could not be found. Migration is cancelled.", migrationConfig);
    return MigrationResult.failure(
        pipelineElementDescription,
        String.format(
            "The given migration config '%s' could not be mapped to a registered migrator.",
            migrationConfig
        )
    );
  }

  /**
   * Executes the migration for the given pipeline element based on the given migrator.
   *
   * @param migrator                   migrator that executes the migration
   * @param pipelineElementDescription pipeline element to be migrated
   * @return the migration result containing either the migrated element or the original one in case of a failure
   */
  protected MigrationResult<T> executeMigration(
      MmT migrator,
      T pipelineElementDescription
  ) {

    var extractor = getPropertyExtractor(pipelineElementDescription);

    try {
      var result = migrator.migrate(pipelineElementDescription, extractor);

      if (result.success()) {
        LOG.info("Migration successfully finished.");

        var migratedElement = result.element();

        migratedElement = updateLabels(migratedElement);

        // Since adapter migration was successful, version can be adapted to the target version.
        // this step is explicitly performed here and not left to the migration itself to
        // prevent leaving this step out
        migratedElement.setVersion(
            migrator.config()
                    .toVersion());

        return MigrationResult.success(migratedElement);

      } else {
        LOG.error("Migration failed with the following reason: {}", result.message());
        // The failed migration is documented in the MigrationResult
        // The core is expected to handle the response accordingly, so we can safely return a positive status code
        return result;
      }
    } catch (RuntimeException e) {
      LOG.error("An unexpected exception caused the migration to fail - "
                    + "sending exception report in migration result");
      return MigrationResult.failure(
          pipelineElementDescription,
          String.format(
              "Migration failed due to an unexpected exception: %s",
              StringUtils.join(e.getStackTrace(), "\n")
          )
      );
    }
  }

  private static <T extends VersionedNamedStreamPipesEntity> T updateLabels(T migratedElement) {
    if (migratedElement.isIncludesLocales()) {
      try {
        boolean replaceTitles = !(migratedElement instanceof AdapterDescription);
        migratedElement = new LabelGenerator<>(migratedElement, replaceTitles).generateLabels();
      } catch (IOException e) {
        LOG.error("Failed to generate labels for migrated element: %s".formatted(migratedElement.getAppId()), e);
      }
    }
    return migratedElement;
  }

  protected abstract ExT getPropertyExtractor(T pipelineElementDescription);
}
