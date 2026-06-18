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

package org.apache.streampipes.manager.migration;

import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.manager.verification.extractor.TypeExtractor;
import org.apache.streampipes.model.base.VersionedNamedStreamPipesEntity;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.streampipes.manager.migration.MigrationUtils.getRequestTarget;

public abstract class AbstractMigrationManager {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractMigrationManager.class);
  protected final ExtensionServiceRequestManager requestManager;
  protected final SpResourceManager resourceManager;

  protected AbstractMigrationManager(ExtensionServiceRequestManager requestManager,
                                     SpResourceManager resourceManager) {
    this.requestManager = requestManager;
    this.resourceManager = resourceManager;
  }

  /**
   * Performs the actual migration of a pipeline element.
   * This includes the communication with the extensions service which runs the migration.
   *
   * @param pipelineElement pipeline element to be migrated
   * @param migrationConfig config of the migration to be performed
   * @param service         url of the migration endpoint at the extensions service
   *                        where the migration should be performed
   * @param <T>             type of the processing element
   * @return result of the migration
   */
  protected <T extends VersionedNamedStreamPipesEntity> MigrationResult<T> performMigration(
      T pipelineElement,
      ModelMigratorConfig migrationConfig,
      SpServiceRegistration service,
      String type
  ) {
    return performMigration(pipelineElement, migrationConfig,
        ExtensionServiceRequestTargets.migration(service, type)
    );
  }

  protected <T extends VersionedNamedStreamPipesEntity> MigrationResult<T> performMigration(
      T pipelineElement,
      ModelMigratorConfig migrationConfig,
      ExtensionServiceRequestTarget requestTarget
  ) {

    try {

      var migrationRequest = new MigrationRequest<>(pipelineElement, migrationConfig);

      String serializedRequest = JacksonSerializer.getObjectMapper().writeValueAsString(migrationRequest);

      var migrationResponse = requestManager.request(
          ExtensionServiceRequests.migration(requestTarget, serializedRequest)
      );

      TypeReference<MigrationResult<T>> typeReference = new TypeReference<>() {
      };

      String migrationResponseString = migrationResponse.responseBody();
      return JacksonSerializer
          .getObjectMapper()
          .readValue(migrationResponseString, typeReference);
    } catch (JsonProcessingException e) {
      LOG.error(
          "Migration of pipeline element failed before sending to the extensions service, "
              + "pipeline element is not migrated. Serialization of migration request failed: {}",
          StringUtils.join(e.getStackTrace(), "\n")
      );
    } catch (IOException e) {
      LOG.error("Migration of pipeline element failed at the extensions service, pipeline element is not migrated: {}.",
          StringUtils.join(e.getStackTrace(), "\n")
      );
    }
    return MigrationResult.failure(pipelineElement, "Internal error during migration at StreamPipes Core");
  }

  /**
   * Update all descriptions of entities in the Core that are affected by migrations.
   *
   * @param migrationConfigs List of migrations to take in account
   * @param service       The extension service that provides the migrations.
   */
  protected void updateDescriptions(List<ModelMigratorConfig> migrationConfigs, SpServiceRegistration service) {
    migrationConfigs
        .stream()
        .collect(
            // We only need to update the description once per appId,
            // because this is directly done with the newest version of the description and
            // there is iterative migration required.
            // To avoid unnecessary, multiple updates,
            // we filter the migration configs such that every appId is unique.
            // This ensures that every description is only updated once.
            Collectors.toMap(
                ModelMigratorConfig::targetAppId,
                Function.identity(),
                (existing, replacement) -> existing
            )
        )
        .values()
        .forEach(config -> {
          if (isInstalled(config.modelType(), config.targetAppId())) {
            var requestTarget = getRequestTarget(config.modelType(), config.targetAppId(), service);
            performUpdate(requestTarget);
          }
        });
  }

  protected abstract boolean isInstalled(SpServiceTagPrefix modelType, String appId);

  protected void performUpdate(ExtensionServiceRequestTarget requestTarget) {

    try {
      var entityPayload = requestManager
          .request(ExtensionServiceRequests.descriptionUpdate(requestTarget, resourceManager))
          .responseBody();
      var updateResult = new TypeExtractor(entityPayload, requestManager, resourceManager.managePermissions())
          .getTypeVerifier().verifyAndUpdate();
      if (!updateResult.isSuccess()) {
        LOG.error(
            "Updating the pipeline element description failed: {}",
            StringUtils.join(
                updateResult.getNotifications().stream().map(Notification::toString).toList(),
                "\n")
        );
      }
    } catch (IOException | SepaParseException e) {
      LOG.error("Updating the pipeline element description failed due to the following exception:\n{}",
          StringUtils.join(e.getStackTrace(), "\n")
      );
    }
  }
}
