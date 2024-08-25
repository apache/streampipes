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

import org.apache.streampipes.connect.management.management.AdapterMigrationManager;
import org.apache.streampipes.manager.health.CoreInitialInstallationProgress;
import org.apache.streampipes.manager.health.CoreServiceStatusManager;
import org.apache.streampipes.manager.health.ServiceRegistrationManager;
import org.apache.streampipes.manager.migration.PipelineElementMigrationManager;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IDataProcessorStorage;
import org.apache.streampipes.storage.api.IDataSinkStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/migrations")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class MigrationResource extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);

  private final CRUDStorage<SpServiceRegistration> extensionsServiceStorage =
      getNoSqlStorage().getExtensionsServiceStorage();
  private final IAdapterStorage adapterDescriptionStorage = getNoSqlStorage().getAdapterDescriptionStorage();
  private final IAdapterStorage adapterStorage = getNoSqlStorage().getAdapterInstanceStorage();

  private final IDataProcessorStorage dataProcessorStorage = getNoSqlStorage().getDataProcessorStorage();

  private final IDataSinkStorage dataSinkStorage = getNoSqlStorage().getDataSinkStorage();
  private final IPipelineStorage pipelineStorage = getNoSqlStorage().getPipelineStorageAPI();

  private final CoreServiceStatusManager coreServiceStatusManager = new CoreServiceStatusManager(
      getNoSqlStorage().getSpCoreConfigurationStorage()
  );

  @PostMapping(path = "{serviceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Migrate adapters and pipeline elements based on migration configs", tags = {"Core", "Migration"},
      responses = {
          @ApiResponse(
              responseCode = "" + HttpStatus.SC_OK,
              description = "All provided migrations are handled. If an error appeared, "
                  + "the corresponding actions are taken.")
      }
  )
  public ResponseEntity<Void> performMigrations(
      @Parameter(
          in = ParameterIn.PATH,
          description = "the id of the extensions service that requests migrations",
          required = true
      )
      @PathVariable("serviceId") String serviceId,
      @Parameter(
          description = "list of configs (ModelMigratorConfig) that describe the requested migrations",
          required = true
      )
      @RequestBody List<ModelMigratorConfig> migrationConfigs) {

    var serviceManager = new ServiceRegistrationManager(extensionsServiceStorage);
    try {
      var extensionsServiceConfig = serviceManager.getService(serviceId);
      if (!CoreInitialInstallationProgress.INSTANCE.isInitiallyInstalling()) {
        if (!migrationConfigs.isEmpty()) {
          var anyServiceMigrating = serviceManager.isAnyServiceMigrating();
          var coreReady = isCoreReady();
          if (anyServiceMigrating || !coreReady) {
            LOG.info(
                "Refusing migration request since precondition is not met (anyServiceMigrating={}, coreReady={}.",
                anyServiceMigrating,
                coreReady
            );
            return ResponseEntity.status(HttpStatus.SC_CONFLICT).build();
          } else {
            serviceManager.applyServiceStatus(serviceId, SpServiceStatus.MIGRATING);
            var adapterMigrations = filterConfigs(migrationConfigs, List.of(SpServiceTagPrefix.ADAPTER));
            var pipelineElementMigrations = filterConfigs(
                migrationConfigs,
                List.of(SpServiceTagPrefix.DATA_PROCESSOR, SpServiceTagPrefix.DATA_SINK)
            );

            new AdapterMigrationManager(adapterStorage, adapterDescriptionStorage)
              .handleMigrations(extensionsServiceConfig, adapterMigrations);
            new PipelineElementMigrationManager(
                pipelineStorage,
                dataProcessorStorage,
                dataSinkStorage)
                .handleMigrations(extensionsServiceConfig, pipelineElementMigrations);
          }
        }
      }
      new ServiceRegistrationManager(extensionsServiceStorage)
          .applyServiceStatus(extensionsServiceConfig.getSvcId(), SpServiceStatus.HEALTHY);
      return ok();
    } catch (IllegalArgumentException e) {
      LOG.warn("Refusing migration request since the service {} is not registered.", serviceId);
      throw new SpMessageException(org.springframework.http.HttpStatus.NOT_FOUND, e);
    }
  }

  private boolean isCoreReady() {
    return coreServiceStatusManager.isCoreReady();
  }

  private List<ModelMigratorConfig> filterConfigs(List<ModelMigratorConfig> migrationConfigs,
                                                  List<SpServiceTagPrefix> modelTypes) {
    return migrationConfigs
        .stream()
        .filter(config -> modelTypes.stream().anyMatch(modelType -> modelType == config.modelType()))
        .toList();
  }
}
