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

import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.VersionedNamedStreamPipesEntity;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.message.Notification;
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
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("v2/migrations")
@Component
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class MigrationResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);
  private static final String MIGRATION_ENDPOINT = "api/v1/migrations";

  private final CRUDStorage<String, SpServiceRegistration> extensionsServiceStorage =
          getNoSqlStorage().getExtensionsServiceStorage();

  private final IAdapterStorage adapterStorage = getNoSqlStorage().getAdapterInstanceStorage();

  private final IDataProcessorStorage dataProcessorStorage = getNoSqlStorage().getDataProcessorStorage();

  private final IDataSinkStorage dataSinkStorage = getNoSqlStorage().getDataSinkStorage();
  private final IPipelineStorage pipelineStorage = getNoSqlStorage().getPipelineStorageAPI();

  @POST
  @Path("adapter/{serviceId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(
          summary = "Migrate adapters based on migration configs", tags = {"Core", "Migration"},
          responses = {
              @ApiResponse(
                      responseCode = "" + HttpStatus.SC_OK,
                      description = "All provided migrations are handled. If an error appeared, "
                              + "the corresponding actions are taken.")
          }
  )
  public Response registerAdapterMigrations(
          @Parameter(
                  in = ParameterIn.PATH,
                  description = "the id of the extensions service that requests migrations",
                  required = true
          )
          @PathParam("serviceId") String serviceId,
          @Parameter(
                  description = "list of configs (ModelMigratorConfig) that describe the requested migrations",
                  required = true
          )
          List<ModelMigratorConfig> migrationConfigs) {

    var extensionsServiceConfig = extensionsServiceStorage.getElementById(serviceId);

    LOG.info("Received {} migrations from extension service {}.",
            migrationConfigs.size(),
            extensionsServiceConfig.getServiceUrl());
    LOG.info("Updating adapter descriptions by replacement...");
    updateDescriptions(migrationConfigs, extensionsServiceConfig.getServiceUrl());
    LOG.info("Adapter descriptions are up to date.");

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
                  String.format("%s/%s/adapter",
                          extensionsServiceConfig.getServiceUrl(),
                          MIGRATION_ENDPOINT
                  )
          );

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
  @Operation(
          summary = "Migrate pipeline elements based on migration configs", tags = {"Core", "Migration"},
          responses = {
              @ApiResponse(
                      responseCode = "200" + HttpStatus.SC_OK,
                      description = "All provided migrations are handled. "
                              + "If an error appeared, the corresponding actions are taken."
              )
          }
  )
  public Response registerPipelineElementMigrations(
          @Parameter(
                  in = ParameterIn.PATH,
                  description = "the id of the extensions service that requests migrations",
                  required = true
          )
          @PathParam("serviceId") String serviceId,
          @Parameter(
                  description = "list of config that describe the requested migrations"
          )
          List<ModelMigratorConfig> migrationConfigs) {

    var extensionsServiceConfig = extensionsServiceStorage.getElementById(serviceId);
    LOG.info("Updating pipeline element descriptions by replacement...");
    updateDescriptions(migrationConfigs, extensionsServiceConfig.getServiceUrl());
    LOG.info("Pipeline element descriptions are up to date.");

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
                                  String.format("%s/%s/processor",
                                          extensionsServiceConfig.getServiceUrl(),
                                          MIGRATION_ENDPOINT
                                  ),
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
                                  String.format("%s/%s/sink",
                                          extensionsServiceConfig.getServiceUrl(),
                                          MIGRATION_ENDPOINT
                                  ),
                                  failedMigrations
                          );
                        } else {
                          LOG.info("No migration applicable for data sink '{}'.", sink.getElementId());
                          return sink;
                        }
                      }
              ).toList();
      pipeline.setActions(migratedDataSinks);

      pipelineStorage.updatePipeline(pipeline);

      if (failedMigrations.isEmpty()) {
        LOG.info("Migration for pipeline successfully completed.");
      } else {
        // pass most recent version of pipeline
        handleFailedMigrations(pipelineStorage.getPipeline(pipeline.getPipelineId()), failedMigrations);
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
  protected void handleFailedMigrations(Pipeline pipeline, List<MigrationResult<?>> failedMigrations) {
    LOG.error("Failures in migration detected - The following pipeline elements could to be migrated:\n"
            + StringUtils.join(failedMigrations.stream().map(Record::toString).toList()), "\n");

    pipeline.setPipelineNotifications(failedMigrations.stream().map(
            failedMigration -> "Failed migration of pipeline element: %s".formatted(failedMigration.message())
    ).toList());
    pipeline.setHealthStatus(PipelineHealthStatus.REQUIRES_ATTENTION);

    pipelineStorage.updatePipeline(pipeline);

    // get updated version of pipeline after modification
    pipeline = pipelineStorage.getPipeline(pipeline.getPipelineId());

    stopPipeline(pipeline);
  }


  public void stopPipeline(Pipeline pipeline) {
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
   *
   * @param pipelineElement  pipeline element to be migrated
   * @param modelMigrations  list of model migrations that might be applicable for this pipeline element
   * @param url              url of the extensions service endpoint that handles the migration
   * @param failedMigrations collection of failed migrations which is extended by occurring migration failures
   * @param <T>              type of the pipeline element (e.g., DataProcessorInvocation)
   * @return the migrated (or - in case of a failure - updated) pipeline element
   */
  protected <T extends InvocableStreamPipesEntity> T migratePipelineElement(
          T pipelineElement,
          List<ModelMigratorConfig> modelMigrations,
          String url,
          List<MigrationResult<?>> failedMigrations
  ) {

    // loop until no migrations are available anymore
    // this allows to apply multiple migrations for a pipeline element sequentially
    // For example, first migration from 0 to 1 and the second migration from 1 to 2
    while (getApplicableMigration(pipelineElement, modelMigrations).isPresent() && failedMigrations.isEmpty()) {

      var migrationConfig = getApplicableMigration(pipelineElement, modelMigrations).get();
      LOG.info(
              "Found applicable migration for pipeline element '{}': {}",
              pipelineElement.getElementId(),
              migrationConfig
      );

      var migrationResult = performMigration(
              pipelineElement,
              migrationConfig,
              url
      );

      if (migrationResult.success()) {
        LOG.info("Migration successfully performed by extensions service. Updating pipeline element invocation ...");
        LOG.debug("Migration was performed at extensions service endpoint '{}'", url);
        pipelineElement = migrationResult.element();
      } else {
        LOG.error("Migration failed with the following reason: {}", migrationResult.message());
        failedMigrations.add(migrationResult);
      }
    }
    if (!failedMigrations.isEmpty()) {
      updateFailedPipelineElement(pipelineElement);
      LOG.info("Updated pipeline elements with new description where automatic migration failed.");
    }
    return pipelineElement;
  }

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
        .stream()
        .peek(config -> {
                  var requestUrl = getRequestUrl(config.modelType(), config.targetAppId(), serviceUrl);
                  performUpdate(requestUrl);
                }
        );
  }

  /**
   * Get the URL that provides the description for an entity.
   *
   * @param entityType Type of the entity to be updated.
   * @param appId      AppId of the entity to be updated
   * @param serviceUrl URL of the extensions service to which the entity belongs
   * @return
   */
  protected String getRequestUrl(SpServiceTagPrefix entityType, String appId, String serviceUrl) {

    SpServiceUrlProvider urlProvider;
    switch (entityType) {
      case ADAPTER -> urlProvider = SpServiceUrlProvider.ADAPTER;
      case DATA_PROCESSOR -> urlProvider = SpServiceUrlProvider.DATA_PROCESSOR;
      case DATA_SINK -> urlProvider = SpServiceUrlProvider.DATA_SINK;
      default -> throw new RuntimeException("Unexpected instance type.");
    }
    return urlProvider.getInvocationUrl(serviceUrl, appId);
  }

  /**
   * Perform the update of the description based on the given requestUrl
   *
   * @param requestUrl URl that references the description to be updated at the extensions service.
   */
  protected void performUpdate(String requestUrl) {

    try {
      var entityPayload = parseURIContent(requestUrl);
      var updateResult = Operations.verifyAndUpdateElement(entityPayload);
      if (!updateResult.isSuccess()) {
        LOG.error(
                "Updating the pipeline element description failed: {}",
                StringUtils.join(
                        updateResult.getNotifications().stream().map(Notification::toString).toList(),
                        "\n")
        );
      }
    } catch (IOException | URISyntaxException | SepaParseException e) {
      LOG.error("Updating the pipeline element description failed due to the following exception:\n{}",
              StringUtils.join(e.getStackTrace(), "\n")
      );
    }
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
