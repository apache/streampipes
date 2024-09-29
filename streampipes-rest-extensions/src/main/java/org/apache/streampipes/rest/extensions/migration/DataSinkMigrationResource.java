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

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/migrations/sink")
public class DataSinkMigrationResource
        extends
          MigrateExtensionsResource<DataSinkInvocation, IDataSinkParameterExtractor, IDataSinkMigrator> {
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Execute the migration for a specific data sink instance", tags = {"Extensions",
      "Migration"}, responses = {@ApiResponse(responseCode = ""
              + HttpStatus.SC_OK, description = "The migration was executed. It's result is described in the response. "
                      + "The Response needs to be handled accordingly.", content = @Content(examples = @ExampleObject(name = "Successful migration", value = "{\"success\": true,\"messages\": \"SUCCESS\", \"element\": {}}"), mediaType = MediaType.APPLICATION_JSON_VALUE))})
  public ResponseEntity<MigrationResult<DataSinkInvocation>> migrateDataSink(
          @Parameter(description = "Request that encompasses the data sink description (DataSinkInvocation) and "
                  + "the configuration of the migration to be performed", example = "{\"migrationElement\": {}, \"modelMigratorConfig\": {\"targetAppId\": \"app-id\", "
                          + "\"modelType\": \"dsink\", \"fromVersion\": 0, \"toVersion\": 1}}", required = true) @RequestBody MigrationRequest<DataSinkInvocation> sinkMigrationRequest) {
    return ok(handleMigration(sinkMigrationRequest));
  }

  @Override
  protected IDataSinkParameterExtractor getPropertyExtractor(DataSinkInvocation pipelineElementDescription) {
    return DataSinkParameterExtractor.from(pipelineElementDescription);
  }
}
