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
import org.apache.streampipes.model.client.file.FileMetadata;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/users/{username}/files")
public class PipelineElementFile extends AbstractRestInterface {

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response storeFile(@PathParam("username") String username,
                            @FormDataParam("file_upload") InputStream uploadedInputStream,
                            @FormDataParam("file_upload") FormDataContentDisposition fileDetail) {
    try {
      FileMetadata metadata = FileManager.storeFile(username, fileDetail.getFileName(), uploadedInputStream);
      return ok(metadata);
    } catch (Exception e) {
      return fail();
    }
  }

  @DELETE
  @Path("{fileId}")
  public Response deleteFile(@PathParam("fileId") String fileId) {
    FileManager.deleteFile(fileId);
    return ok();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFileInfo(@QueryParam("filetypes") String filetypes) {
    List<FileMetadata> allFiles = getFileMetadataStorage().getAllFileMetadataDescriptions();
    return filetypes != null ? ok(filterFiletypes(allFiles, filetypes)) : ok(allFiles);
  }

  private List<FileMetadata> filterFiletypes(List<FileMetadata> allFiles, String filetypes) {
    return allFiles
            .stream()
            .filter(fileMetadata -> Arrays
                    .stream(filetypes.split(","))
                    .anyMatch(ft -> ft.equals(fileMetadata.getFiletype())))
            .collect(Collectors.toList());
  }

}
