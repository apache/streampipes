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

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/v1/migrations/adapter")
public class AdapterMigrationResource extends MigrateExtensionsResource<
    AdapterDescription,
    IStaticPropertyExtractor,
        IAdapterMigrator
    > {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  @Operation(
      summary = "Execute the migration for a specific adapter instance", tags = {"Extensions", "Migration"},
      responses = {
          @ApiResponse(
              responseCode = "" + HttpStatus.SC_OK,
              description = "The migration was executed. Its result is described in the response. "
                  + "The Response needs to be handled accordingly.",
              content = @Content(
                  examples = @ExampleObject(
                      name = "Successful migration",
                      value = "{\"success\": true,\"messages\": \"SUCCESS\", \"element\": {}}"
                  ),
                  mediaType = MediaType.APPLICATION_JSON
              )
          )
      }
  )
  public Response migrateAdapter(
      @Parameter(
          description = "request that encompasses the adapter description(AdapterDescription) and "
              + "the configuration of the migration",
          example = "{\"migrationElement\": {}, \"modelMigratorConfig\": {\"targetAppId\": \"app-id\","
              + "\"modelType\": \"adapter\", \"fromVersion\": 0, \"toVersion\": 1}}",
          required = true
      )
      MigrationRequest<AdapterDescription> adapterMigrationRequest) {
    return ok(handleMigration(adapterMigrationRequest));
  }

  @Override
  protected IStaticPropertyExtractor getPropertyExtractor(AdapterDescription pipelineElementDescription) {
    return StaticPropertyExtractor.from(pipelineElementDescription.getConfig());
  }
}
