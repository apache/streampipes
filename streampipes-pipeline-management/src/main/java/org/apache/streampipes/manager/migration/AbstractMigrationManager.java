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
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.manager.verification.extractor.TypeExtractor;
import org.apache.streampipes.model.base.VersionedNamedStreamPipesEntity;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
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

import static org.apache.streampipes.manager.migration.MigrationUtils.getRequestUrl;

public abstract class AbstractMigrationManager {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractMigrationManager.class);

  protected static final String MIGRATION_ENDPOINT = "api/v1/migrations";

  /**
   * Performs the actual migration of a pipeline element.
   * This includes the communication with the extensions service which runs the migration.
   *
   * @param pipelineElement pipeline element to be migrated
   * @param migrationConfig config of the migration to be performed
   * @param url             url of the migration endpoint at the extensions service
   *                        where the migration should be performed
   * @param <T>             type of the processing element
   * @return result of the migration
   */
  protected <T extends VersionedNamedStreamPipesEntity> MigrationResult<T> performMigration(
      T pipelineElement,
      ModelMigratorConfig migrationConfig,
      String url
  ) {

    try {

      var migrationRequest = new MigrationRequest<>(pipelineElement, migrationConfig);

      String serializedRequest = JacksonSerializer.getObjectMapper().writeValueAsString(migrationRequest);

      var migrationResponse = ExtensionServiceExecutions.extServicePostRequest(
          url,
          serializedRequest
      ).execute();

      TypeReference<MigrationResult<T>> typeReference = new TypeReference<>() {
      };

      String migrationResponseString = migrationResponse.returnContent().asString();
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
   * @param serviceUrl       Url of the extension service that provides the migrations.
   */
  protected void updateDescriptions(List<ModelMigratorConfig> migrationConfigs, String serviceUrl) {
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
            var requestUrl = getRequestUrl(config.modelType(), config.targetAppId(), serviceUrl);
            performUpdate(requestUrl);
          }
        });
  }

  protected abstract boolean isInstalled(SpServiceTagPrefix modelType, String appId);

  /**
   * Perform the update of the description based on the given requestUrl
   *
   * @param requestUrl URl that references the description to be updated at the extensions service.
   */
  protected void performUpdate(String requestUrl) {

    try {
      var entityPayload = ExtensionServiceExecutions.extServiceGetRequest(requestUrl)
          .execute()
          .returnContent()
          .asString();
      var updateResult = new TypeExtractor(entityPayload).getTypeVerifier().verifyAndUpdate();
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
