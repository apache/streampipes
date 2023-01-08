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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.file.FileManager;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;

@Path("/v2/files")
@Component
public class PipelineElementFile extends AbstractAuthGuardedRestResource {

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response storeFile(@FormDataParam("file_upload") InputStream uploadedInputStream,
                            @FormDataParam("file_upload") FormDataContentDisposition fileDetail) {
    try {
      FileMetadata metadata =
          FileManager.storeFile(getAuthenticatedUsername(), fileDetail.getFileName(), uploadedInputStream);
      return ok(metadata);
    } catch (Exception e) {
      return fail();
    }
  }

  @DELETE
  @Path("{fileId}")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response deleteFile(@PathParam("fileId") String fileId) {
    FileManager.deleteFile(fileId);
    return ok();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_READ_FILE_PRIVILEGE)
  public Response getFileInfo(@QueryParam("filetypes") String filetypes) {
    return ok(FileManager.getAllFiles(filetypes));
  }

  @GET
  @Path("/{filename}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getFile(@PathParam("filename") String filename) {
    return ok(FileManager.getFile(filename));
  }

}
