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

import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.storage.api.IDataProcessorStorage;
import org.apache.streampipes.storage.api.IDataSinkStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.streampipes.manager.migration.MigrationUtils.getApplicableMigration;

public class PipelineElementMigrationManager extends AbstractMigrationManager implements IMigrationHandler {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineElementMigrationManager.class);

  private final IPipelineStorage pipelineStorage;
  private final IDataProcessorStorage dataProcessorStorage;
  private final IDataSinkStorage dataSinkStorage;

  public PipelineElementMigrationManager(IPipelineStorage pipelineStorage,
                                         IDataProcessorStorage dataProcessorStorage,
                                         IDataSinkStorage dataSinkStorage) {
    this.pipelineStorage = pipelineStorage;
    this.dataProcessorStorage = dataProcessorStorage;
    this.dataSinkStorage = dataSinkStorage;
  }

  @Override
  public void handleMigrations(SpServiceRegistration extensionsServiceConfig,
                               List<ModelMigratorConfig> migrationConfigs) {
    if (!migrationConfigs.isEmpty()) {
      LOG.info("Updating pipeline element descriptions by replacement...");
      updateDescriptions(migrationConfigs, extensionsServiceConfig.getServiceUrl());
      LOG.info("Pipeline element descriptions are up to date.");

      LOG.info("Received {} pipeline element migrations from extension service {}.",
          migrationConfigs.size(),
          extensionsServiceConfig.getServiceUrl());
      var availablePipelines = pipelineStorage.findAll();
      if (!availablePipelines.isEmpty()) {
        LOG.info("Found {} available pipelines. Checking pipelines for applicable migrations...",
            availablePipelines.size()
        );
      }

      for (var pipeline : availablePipelines) {
        if (shouldMigratePipeline(pipeline, migrationConfigs)) {
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
              })
              .toList();
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
              })
              .toList();
          pipeline.setActions(migratedDataSinks);

          pipelineStorage.updateElement(pipeline);

          if (failedMigrations.isEmpty()) {
            LOG.info("Migration for pipeline successfully completed.");
          } else {
            // pass most recent version of pipeline
            handleFailedMigrations(pipelineStorage.getElementById(pipeline.getPipelineId()), failedMigrations);
          }
        }
      }
    } else {
      LOG.info("No pipeline element migrations to perform");
    }
  }

  private boolean shouldMigratePipeline(Pipeline pipeline,
                                        List<ModelMigratorConfig> migrationConfigs) {
    return Stream
        .concat(pipeline.getSepas().stream(), pipeline.getActions().stream())
        .anyMatch(element -> getApplicableMigration(element, migrationConfigs).isPresent());
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

    pipelineStorage.updateElement(pipeline);

    // get updated version of pipeline after modification
    pipeline = pipelineStorage.getElementById(pipeline.getPipelineId());

    stopPipeline(pipeline);
  }


  public void stopPipeline(Pipeline pipeline) {
    var pipelineExecutor = new PipelineExecutor(pipeline);
    var pipelineStopResult = pipelineExecutor.stopPipeline(true);

    if (pipelineStopResult.isSuccess()) {
      LOG.info("Pipeline successfully stopped.");
    } else {
      LOG.error("Pipeline stop failed.");
    }
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

  @Override
  protected boolean isInstalled(SpServiceTagPrefix modelType, String appId) {
    if (modelType == SpServiceTagPrefix.DATA_PROCESSOR) {
      return !dataProcessorStorage.getDataProcessorsByAppId(appId).isEmpty();
    } else if (modelType == SpServiceTagPrefix.DATA_SINK) {
      return !dataSinkStorage.getDataSinksByAppId(appId).isEmpty();
    } else {
      throw new RuntimeException(String.format("Wrong service tag provided: %s", modelType.asString()));
    }
  }
}
