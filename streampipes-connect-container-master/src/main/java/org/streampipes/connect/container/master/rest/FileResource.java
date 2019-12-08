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

package org.streampipes.connect.container.master.rest;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.container.master.management.FileManagement;
import org.streampipes.connect.rest.AbstractContainerResource;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/file")
public class FileResource extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(FileResource.class);

    FileManagement fileManagement;

    public FileResource() {
        this.fileManagement = new FileManagement();
    }

    public FileResource(FileManagement fileManagement) {
        this.fileManagement = fileManagement;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addFileForAdapter(@PathParam("username") String username,
                                      @FormDataParam("appId") String id,
                                      @FormDataParam("file_upload") InputStream uploadedInputStream,
                                      @FormDataParam("file_upload") FormDataContentDisposition fileDetail) {

        try {
            String filePath = fileManagement.saveFileAtWorker(id, uploadedInputStream, fileDetail.getFileName(), username);
            return ok(filePath);
//            return ok(Notifications.success(filePath));
        } catch (Exception e) {
            logger.error(e.toString());
            return fail();
        }
    }

    @GET
    @Path("/{appId}/{filename}")
    public Response getFileFromWorker(@PathParam("appId") String id, @PathParam("filename") String fileName,
                                            @PathParam("username") String username) {
        try {
            InputStream fileStream = fileManagement.getFileFromWorker(id, fileName, username);
            return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        } catch (AdapterException e) {
            logger.error(e.toString());
            return fail();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFilePathsFromWorker(@PathParam("username") String username) {
        try {
            return ok(fileManagement.getAllFilePathsFromWorker(username));
        } catch (AdapterException e) {
            logger.error(e.toString());
            return fail();
        }
    }

    @DELETE
    @Path("/{filename}")
    public Response deleteFile(String id, @PathParam("filename") String fileName,
                               @PathParam("username") String username) {
        try {
            fileManagement.deleteFileFromWorker(id, fileName, username);
            return ok();
        } catch (AdapterException e) {
            logger.error(e.toString());
            return fail();
        }
    }


}
