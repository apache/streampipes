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

package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.VersionedStreamPipesEntity;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IDataProcessorStorage;
import org.apache.streampipes.storage.api.IDataSinkStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Path("v2/migrations")
@Component
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class MigrationResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);

  private static final String ADAPTER_MIGRATION_ENDPOINT = "api/v1/migrations/adapter";
  private static final String PIPELINE_ELEMENT_MIGRATION_ENDPOINT = "api/v1/migrations/pipeline-elements";

  private final CRUDStorage<String, SpServiceRegistration> extensionsServiceStorage =
          getNoSqlStorage().getExtensionsServiceStorage();

  private final IAdapterStorage adapterStorage = getNoSqlStorage().getAdapterInstanceStorage();

  private final IDataProcessorStorage dataProcessorStorage = getNoSqlStorage().getDataProcessorStorage();

  private final IDataSinkStorage dataSinkStorage = getNoSqlStorage().getDataSinkStorage();
  private final IPipelineStorage pipelineStorage = getNoSqlStorage().getPipelineStorageAPI();


  // TODO: override all existing descriptions(adapter, pipeline elements)
  // Use functionality of update button in UI
  // increase version as well
  @POST
  @Path("adapter/{serviceId}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerAdapterMigrations(
          @PathParam("serviceId") String serviceId,
          List<ModelMigratorConfig> migrationConfigs) {

    var extensionsServiceConfig = extensionsServiceStorage.getElementById(serviceId);

    LOG.info("Received {} migrations from extension service {}.",
            migrationConfigs.size(),
            extensionsServiceConfig.getServiceUrl());
    LOG.info("Checking migrations for existing adapters in StreamPipes Core ...");
    for (var migrationConfig : migrationConfigs) {
      LOG.info("Searching for assets of '{}'", migrationConfig.targetAppId());
      LOG.debug("Searching for assets of '{}' with config {}", migrationConfig.targetAppId(), migrationConfig);
      var adapterDescriptions = adapterStorage.getAdaptersByAppId(migrationConfig.targetAppId());
      LOG.info("Found {} instances for appId '{}'", adapterDescriptions.size(), migrationConfig.targetAppId());
      for (var adapterDescription : adapterDescriptions) {

        var adapterVersion = adapterDescription.getVersion();

        if (adapterVersion == migrationConfig.fromVersion()) {
          LOG.info("Migration is required for adapter '{}'. Migrating from version '{}' to '{}' ...",
                  adapterDescription.getElementId(),
                  adapterVersion, migrationConfig.toVersion()
          );

          var migrationResult = performMigration(
                  adapterDescription,
                  migrationConfig,
                  extensionsServiceConfig.getServiceUrl(),
                  ADAPTER_MIGRATION_ENDPOINT);

          if (migrationResult.success()) {
            LOG.info("Migration successfully performed by extensions service. Updating adapter description ...");
            LOG.debug(
                    "Migration was performed by extensions service '{}'",
                    extensionsServiceConfig.getServiceUrl());

            adapterStorage.updateAdapter(migrationResult.element());
            LOG.info("Adapter description is updated - Migration successfully completed at Core.");
          } else {
            LOG.error("Migration failed with the following reason: {}", migrationResult.message());
            LOG.error(
                    "Migration for adapter '{}' failed - Stopping adapter ...",
                    migrationResult.element().getElementId()
            );
            try {
              WorkerRestClient.stopStreamAdapter(extensionsServiceConfig.getServiceUrl(), adapterDescription);
            } catch (AdapterException e) {
              LOG.error("Stopping adapter failed: {}", StringUtils.join(e.getStackTrace(), "\n"));
            }
            LOG.info("Adapter successfully stopped.");
          }
        } else {
          LOG.info(
                  "Migration is not applicable for adapter '{}' because of a version mismatch - "
                  + "adapter version: '{}',  migration starts at: '{}'",
                  adapterDescription.getElementId(),
                  adapterVersion,
                  migrationConfig.fromVersion()
          );
        }
      }
    }
    return ok();
  }

  @POST
  @Path("pipeline-element/{serviceId}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerPipelineElementMigrations(
          @PathParam("serviceId") String serviceId,
          List<ModelMigratorConfig> migrationConfigs) {

    var extensionsServiceConfig = extensionsServiceStorage.getElementById(serviceId);
    LOG.info("Received {} pipeline element migrations from extension service {}.",
            migrationConfigs.size(),
            extensionsServiceConfig.getServiceUrl());
    var availablePipelines = pipelineStorage.getAllPipelines();
    if (!availablePipelines.isEmpty()) {
      LOG.info("Found {} available pipelines. Checking pipelines for applicable migrations...",
              availablePipelines.size()
      );
    }

    for (var pipeline : availablePipelines) {
      List<MigrationResult<?>> failedMigrations = new ArrayList<>();

      var migratedDataProcessors = pipeline.getSepas()
              .stream()
              .map(processor -> {
                        if (getApplicableMigration(processor, migrationConfigs).isPresent()) {
                          return migratePipelineElement(
                                  processor,
                                  migrationConfigs,
                                  extensionsServiceConfig.getServiceUrl(),
                                  failedMigrations
                          );
                        } else {
                          LOG.info("No migration applicable for data processor '{}'.", processor.getElementId());
                          return processor;
                        }
                      }
              ).toList();
      pipeline.setSepas(migratedDataProcessors);

      var migratedDataSinks = pipeline.getActions()
              .stream()
              .map(sink -> {
                        if (getApplicableMigration(sink, migrationConfigs).isPresent()) {
                          return migratePipelineElement(
                                  sink,
                                  migrationConfigs,
                                  extensionsServiceConfig.getServiceUrl(),
                                  failedMigrations
                          );
                        } else {
                          LOG.info("No migration applicable for data sink '{}'.", sink.getElementId());
                          return sink;
                        }
                      }
              ).toList();
      pipeline.setActions(migratedDataSinks);

      if (failedMigrations.isEmpty()) {
        LOG.info("Migration for pipeline successfully completed.");
      } else {
        handleFailedMigrations(pipeline, failedMigrations);
      }
    }

    return ok();
  }

  /**
   * Takes care about the failed migrations of pipeline elements.
   * This includes the following steps:
   * <ul>
   *   <li> logging of failed pipeline elements
   *   <li> setting migration results as pipeline notifications
   *   <li> updating pipeline health status
   *   <li> stopping the pipeline
   * </ul>
   *
   * @param pipeline         the pipeline affected by failed migrations
   * @param failedMigrations the list of failed migrations
   */
  protected static void handleFailedMigrations(Pipeline pipeline, List<MigrationResult<?>> failedMigrations) {
    LOG.error("Failures in migration detected - The following pipeline elements could to be migrated:\n"
            + StringUtils.join(failedMigrations.stream().map(Record::toString)), "\n");

    pipeline.setPipelineNotifications(failedMigrations.stream().map(MigrationResult::message).toList());
    pipeline.setHealthStatus(PipelineHealthStatus.REQUIRES_ATTENTION);

    var pipelineExecutor = new PipelineExecutor(pipeline, true);
    var pipelineStopResult = pipelineExecutor.stopPipeline();

    if (pipelineStopResult.isSuccess()) {
      LOG.info("Pipeline successfully stopped.");
    } else {
      LOG.error("Pipeline stop failed.");
    }
  }

  /**
   * Filter the application migration for a pipeline definition.
   * By definition, there is only one migration config that fulfills the requirements.
   * Otherwise, it should have been detected as duplicate by the extensions service.
   *
   * @param pipelineElement  pipeline element that should be migrated
   * @param migrationConfigs available migration configs to pick the applicable from
   * @return config that is applicable for the given pipeline element
   */
  protected static Optional<ModelMigratorConfig> getApplicableMigration(
          InvocableStreamPipesEntity pipelineElement,
          List<ModelMigratorConfig> migrationConfigs
  ) {
    return migrationConfigs
            .stream()
            .filter(
                    config -> config.modelType().equals(pipelineElement.getServiceTagPrefix())
                            && config.targetAppId().equals(pipelineElement.getAppId())
                            && config.fromVersion() == pipelineElement.getVersion()
            )
            .findFirst();
  }

  /**
   * Handle the migration of a pipeline element with respect to the given model migration configs.
   * All applicable migrations found in the provided configs are executed for the given pipeline element.
   * In case a migration fails, the related pipeline element receives the latest definition of its static properties,
   * so that the pipeline element can be adapted by the user to resolve the failed migration.
   * @param pipelineElement pipeline element to be migrated
   * @param modelMigrations list of model migrations that might be applicable for this pipeline element
   * @param serviceUrl url of the extensions service that handles the migration
   * @param failedMigrations collection of failed migrations which is extended by occurring migration failures
   * @param <T> type of the pipeline element (e.g., DataProcessorInvocation)
   * @return the migrated (or - in case of a failure - updated) pipeline element
   */
  protected <T extends InvocableStreamPipesEntity> T migratePipelineElement(
          T pipelineElement,
          List<ModelMigratorConfig> modelMigrations,
          String serviceUrl,
          List<MigrationResult<?>> failedMigrations
  ) {

    // loop until no migrations are available anymore
    // this allows to apply multiple migrations for a pipeline element sequentially
    // For example, first migration from 0 to 1 and the second migration from 1 to 2
    while (getApplicableMigration(pipelineElement, modelMigrations).isPresent()) {

      var migrationConfig = getApplicableMigration(pipelineElement, modelMigrations).get();
      LOG.info(
              "Found applicable migration for pipeline element '{}': {}",
              pipelineElement.getElementId(),
              migrationConfig
      );

      var migrationResult = performMigration(
              pipelineElement,
              migrationConfig,
              serviceUrl,
              PIPELINE_ELEMENT_MIGRATION_ENDPOINT
      );

      if (migrationResult.success()) {
        LOG.info("Migration successfully performed by extensions service. Updating pipeline element invocation ...");
        LOG.debug("Migration was performed by extensions service '{}'", serviceUrl);
        return migrationResult.element();
      } else {
        LOG.error("Migration failed with the following reason: {}", migrationResult.message());
        failedMigrations.add(migrationResult);
      }
    }
    updateFailedPipelineElement(pipelineElement);
    return pipelineElement;
  }

  /**
   * Performs the actual migration of a pipeline element.
   * This includes the communication with the extensions service which runs the migration.
   * @param pipelineElement pipeline element to be migrated
   * @param migrationConfig config of the migration to be performed
   * @param serviceUrl url of the extensions service where the migration should be performed
   * @param migrationEndpoint endpoint at which the migration should be performed
   * @param <T> type of the processing element
   * @return result of the migration
   */
  protected <T extends VersionedStreamPipesEntity> MigrationResult<T> performMigration(
          T pipelineElement,
          ModelMigratorConfig migrationConfig,
          String serviceUrl,
          String migrationEndpoint
  ) {

    try {

      var migrationRequest = new MigrationRequest<>(pipelineElement, migrationConfig);

      String serializedRequest = JacksonSerializer.getObjectMapper().writeValueAsString(migrationRequest);

      var migrationResponse = ExtensionServiceExecutions.extServicePostRequest(
              "%s/%s".formatted(serviceUrl, migrationEndpoint),
              serializedRequest
      ).execute();

      TypeReference<MigrationResult<T>> typeReference = new TypeReference<>() {
      };

      return JacksonSerializer
              .getObjectMapper()
              .readValue(migrationResponse.returnContent().asString(), typeReference);
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
   * Update the static properties of the failed pipeline element with its description.
   * This allows to adapt the failed pipeline element in the UI to overcome the failed migration.
   *
   * @param pipelineElement pipeline element with failed migration
   */
  protected void updateFailedPipelineElement(InvocableStreamPipesEntity pipelineElement) {
    List<StaticProperty> updatedStaticProperties = new ArrayList<>();
    if (pipelineElement instanceof DataProcessorInvocation) {
      updatedStaticProperties = dataProcessorStorage
              .getFirstDataProcessorByAppId(pipelineElement.getAppId())
              .getStaticProperties();
    } else if (pipelineElement instanceof DataSinkInvocation) {
      updatedStaticProperties = dataSinkStorage
              .getFirstDataSinkByAppId(pipelineElement.getAppId())
              .getStaticProperties();
    }
    pipelineElement.setStaticProperties(updatedStaticProperties);
  }
}
