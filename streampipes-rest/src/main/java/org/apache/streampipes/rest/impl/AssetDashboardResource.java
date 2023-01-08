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

import org.apache.streampipes.model.client.assetdashboard.AssetDashboardConfig;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.storage.api.IAssetDashboardStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/v2/asset-dashboards")
public class AssetDashboardResource extends AbstractRestResource {

  private static final String APP_ID = "org.apache.streampipes.apps.assetdashboard";

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  public Response getAssetDashboard(@PathParam("dashboardId") String dashboardId) {
    return ok(getNoSqlStorage().getAssetDashboardStorage().getAssetDashboard(dashboardId));
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  public Response updateAssetDashboard(@PathParam("dashboardId") String dashboardId,
                                       AssetDashboardConfig dashboardConfig) {
    AssetDashboardConfig dashboard = getAssetDashboardStorage().getAssetDashboard(dashboardId);
    dashboardConfig.setRev(dashboard.getRev());
    getNoSqlStorage().getAssetDashboardStorage().updateAssetDashboard(dashboardConfig);
    return ok();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllDashboards() {
    return ok(getNoSqlStorage().getAssetDashboardStorage().getAllAssetDashboards());
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response storeAssetDashboard(AssetDashboardConfig dashboardConfig) {
    getNoSqlStorage().getAssetDashboardStorage().storeAssetDashboard(dashboardConfig);
    return ok();
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  public Response deleteAssetDashboard(@PathParam("dashboardId") String dashboardId) {
    getNoSqlStorage().getAssetDashboardStorage().deleteAssetDashboard(dashboardId);
    return ok();
  }

  @GET
  @Path("/images/{imageName}")
  public Response getDashboardImage(@PathParam("imageName") String imageName) {
    try {
      java.nio.file.Path path = Paths.get(getTargetFile(imageName));
      File file = new File(path.toString());
      FileNameMap fileNameMap = URLConnection.getFileNameMap();
      String mimeType = fileNameMap.getContentTypeFor(file.getName());
      return Response.ok(Files.readAllBytes(path)).type(mimeType).build();
    } catch (IOException e) {
      e.printStackTrace();
      return fail();
    }
  }


  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/images")
  public Response storeDashboardImage(@FormDataParam("file_upload") InputStream uploadedInputStream,
                                      @FormDataParam("file_upload") FormDataContentDisposition fileDetail) {
    File targetDirectory = new File(getTargetDirectory());
    if (!targetDirectory.exists()) {
      targetDirectory.mkdirs();
    }

    File targetFile = new File(getTargetFile(fileDetail.getFileName()));

    try {
      FileUtils.copyInputStreamToFile(uploadedInputStream, targetFile);
      return ok();
    } catch (IOException e) {
      e.printStackTrace();
      return fail();
    }
  }

  private String getTargetDirectory() {
    return System.getProperty("user.home") + File.separator + ".streampipes"
        + File.separator + "assets" + File.separator + APP_ID;
  }

  private String getTargetFile(String filename) {
    return getTargetDirectory() + File.separator + filename;
  }

  private IAssetDashboardStorage getAssetDashboardStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getAssetDashboardStorage();
  }
}
