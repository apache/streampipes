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

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.migration.DataProcessorMigrator;
import org.apache.streampipes.model.extensions.migration.MigrationRequest;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;

import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("api/v1/migrations/processor")
public class DataProcessorMigrationResource extends MigrateExtensionsResource<
        DataProcessorInvocation,
        IDataProcessorParameterExtractor,
        DataProcessorMigrator
        > {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response migrateDataProcessor(MigrationRequest<DataProcessorInvocation> processorMigrationRequest) {
    return ok(handleMigration(processorMigrationRequest));
  }

  @Override
  protected IDataProcessorParameterExtractor getPropertyExtractor(DataProcessorInvocation pipelineElementDescription) {
    return ProcessingElementParameterExtractor.from(pipelineElementDescription);
  }
}
