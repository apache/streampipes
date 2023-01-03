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

import org.apache.streampipes.export.ExportManager;
import org.apache.streampipes.model.export.ExportConfiguration;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;

@Path("/v2/export")
@Component
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class DataExportResource extends AbstractAuthGuardedRestResource {

  @Path("/preview")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response getExportPreview(List<String> selectedAssetIds) {
    var exportConfig = ExportManager.getExportPreview(selectedAssetIds);
    return ok(exportConfig);
  }

  @Path("/download")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response download(ExportConfiguration exportConfiguration) throws IOException {
    var applicationPackage = ExportManager.getExportPackage(exportConfiguration);
    return ok(applicationPackage);
  }

}
