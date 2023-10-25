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

import org.apache.streampipes.extensions.api.migration.AdapterMigrator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/v1/migrations/adapter")
public class AdapterMigrationResource extends MigrateExtensionsResource<AdapterMigrator> {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMigrationResource.class);

  /**
   * Migrates an adapter instance based on the provided {@link MigrationRequest}.
   * The outcome of the migration is described in {@link MigrationResult}.
   * The result is always part of the response.
   * Independent, of the migration outcome, the returned response always has OK as status code.
   * It is the responsibility of the recipient to interpret the migration result and act accordingly.
   * @param adapterMigrateRequest Request that contains both the adapter to be migrated and the migration config.
   * @return A response with status code ok, that contains a migration result reflecting the outcome of the operation.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response migrateAdapter(MigrationRequest<AdapterDescription> adapterMigrateRequest) {

    var adapterDescription = adapterMigrateRequest.migrationElement();
    var migrationConfig = adapterMigrateRequest.modelMigratorConfig();

    LOG.info("Received migration request for adapter '{}' to migrate from version {} to {}",
            adapterDescription.getElementId(),
            migrationConfig.fromVersion(),
            migrationConfig.toVersion()
    );

    var migratorOptional = getMigrator(migrationConfig);

    if (migratorOptional.isPresent()) {
      LOG.info("Migrator found for request, starting migration...");
      var migrator = migratorOptional.get();

      var extractor = StaticPropertyExtractor.from(adapterDescription.getConfig());

      try {
        var result = migrator.migrate(adapterDescription, extractor);

        if (result.success()) {
          LOG.info("Migration successfully finished.");

          // Since adapter migration was successful, version can be adapted to the target version.
          // this step is explicitly performed here and not left to the migration itself to
          // prevent leaving this step out
          AdapterDescription migratedAdapterDescription = result.element();
          migratedAdapterDescription.setVersion(migrationConfig.toVersion());
          return ok(MigrationResult.success(migratedAdapterDescription));

        } else {
          LOG.error("Migration failed with the following reason: {}", result.message());
          // The failed migration is documented in the MigrationResult
          // The core is expected to handle the response accordingly, so we can safely return a positive status code
          return ok(result);
        }

      } catch (RuntimeException e) {
        LOG.error("An unexpected exception caused the migration to fail - "
                + "sending exception report in migration result");
        return ok(MigrationResult.failure(
                        adapterDescription,
                              String.format(
                                      "Adapter Migration failed due to an unexpected exception: %s",
                                      StringUtils.join(e.getStackTrace(), "\n")
                                      )
                              )
                        );
      }
    }
    LOG.error("Migrator for migration config {} could not be found. Migration is cancelled.", migrationConfig);
    return ok(MigrationResult.failure(
            adapterDescription,
                String.format(
                        "The given migration config '%s' could not be mapped to a registered migrator.",
                        migrationConfig
                )
            )
    );
  }
}
