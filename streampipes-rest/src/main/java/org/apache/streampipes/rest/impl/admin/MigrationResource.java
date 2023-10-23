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
import org.apache.streampipes.model.extensions.migration.AdapterMigrationRequest;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IDataProcessorStorage;
import org.apache.streampipes.storage.api.IDataSinkStorage;

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

import java.util.List;


@Path("v2/migrations")
@Component
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class MigrationResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);

  private final IAdapterStorage adapterStorage = getNoSqlStorage().getAdapterInstanceStorage();
  private final IDataProcessorStorage dataProcessorStorage = getNoSqlStorage().getDataProcessorStorage();
  private final IDataSinkStorage dataSinkStorage = getNoSqlStorage().getDataSinkStorage();

  private final CRUDStorage<String, SpServiceRegistration> extensionsServiceStorage =
          getNoSqlStorage().getExtensionsServiceStorage();

  @POST
  @Path("{serviceId}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerMigrations(
          @PathParam("serviceId") String serviceId,
          List<ModelMigratorConfig> migrationConfigs) {

    LOG.info("Received {} migrations from extension service.", migrationConfigs.size());
    LOG.info("Checking migrations for existing assets in StreamPipes Core ...");
    for (var migrationConfig: migrationConfigs) {
      LOG.info("Searching for assets of '{}'", migrationConfig.targetAppId());
      LOG.debug("Searching for assets of '{}' with config {}", migrationConfig.targetAppId(), migrationConfig);
      switch (migrationConfig.modelType()) {
        case ADAPTER -> {
          var adapterDescriptions = adapterStorage.getAdaptersByAppId(migrationConfig.targetAppId());
          LOG.info("Found {} instances for appId '{}'", adapterDescriptions.size(), migrationConfig.targetAppId());
          for (var adapterDescription: adapterDescriptions) {

            var adapterVersion = adapterDescription.getVersion();

            if (adapterVersion == migrationConfig.fromVersion()) {
              LOG.info("Migration is required for adapter '{}'. Migrating from version '{}' to '{}' ...",
                      adapterDescription.getElementId(),
                      adapterVersion, migrationConfig.toVersion()
              );

              var extensionsServiceConfig = extensionsServiceStorage.getElementById(serviceId);

              var adapterMigrationRequest = new AdapterMigrationRequest(adapterDescription, migrationConfig);

              var migrationResult = WorkerRestClient.migrateAdapter(
                      adapterMigrationRequest,
                      extensionsServiceConfig.getServiceUrl()
              );

              if (migrationResult.success()) {
                LOG.info("Migration successfully performed by extensions service. Updating adapter description ...");
                LOG.debug(
                        "Migration was performed by extensions service '{}'",
                        extensionsServiceConfig.getServiceUrl());

                adapterStorage.updateAdapter(migrationResult.element());
                LOG.info("Adapter description is updated - Migration successfully completed at core.");
              } else {
                LOG.error(
                        "Migration failed - Failure report: {}",
                        StringUtils.join(migrationResult.messages(), ",")
                );
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
              LOG.info("Migration is not applicable for adapter '{}' because of a version mismatch - "
                      + "adapter version: '{}',  migration starts at: '{}'",
                      adapterDescription.getElementId(),
                      adapterVersion,
                      migrationConfig.fromVersion()
              );

            }
          }
        }
        case DATA_PROCESSOR ->  {}
        case DATA_SINK -> {}
        default -> LOG.error("Can't handle migration for model type {}", migrationConfig.modelType()
        );
      }

    }
    return ok();
  }
}
