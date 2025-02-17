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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.manager.migration.AbstractMigrationManager;
import org.apache.streampipes.manager.migration.IMigrationHandler;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdapterMigrationManager extends AbstractMigrationManager implements IMigrationHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMigrationManager.class);

  private final IAdapterStorage adapterStorage;
  private final IAdapterStorage adapterDescriptionStorage;

  public AdapterMigrationManager(IAdapterStorage adapterStorage,
                                 IAdapterStorage adapterDescriptionStorage) {
    this.adapterStorage = adapterStorage;
    this.adapterDescriptionStorage = adapterDescriptionStorage;
  }

  @Override
  public void handleMigrations(SpServiceRegistration extensionsServiceConfig,
                               List<ModelMigratorConfig> migrationConfigs) {

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

            adapterStorage.updateElement(migrationResult.element());
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
  }

  @Override
  protected boolean isInstalled(SpServiceTagPrefix modelType, String appId) {
    return !adapterDescriptionStorage.getAdaptersByAppId(appId).isEmpty();
  }
}
