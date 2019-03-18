/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.rest.impl;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.streampipes.model.client.assetdashboard.AssetDashboardConfig;
import org.streampipes.rest.api.IAssetDashboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/asset-dashboards")
public class AssetDashboard extends AbstractRestInterface implements IAssetDashboard {

  private static final String APP_ID = "org.streampipes.apps.assetdashboard";

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  @Override
  public Response getAssetDashboard(@PathParam("dashboardId") String dashboardId) {
    return ok(getNoSqlStorage().getAssetDashboardStorage().getAssetDashboard(dashboardId));
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAllDashboards() {
    return ok(getNoSqlStorage().getAssetDashboardStorage().getAllAssetDashboards());
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response storeAssetDashboard(AssetDashboardConfig dashboardConfig) {
    getNoSqlStorage().getAssetDashboardStorage().storeAssetDashboard(dashboardConfig);
    return ok();
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  @Override
  public Response deleteAssetDashboard(@PathParam("dashboardId") String dashboardId) {
    getNoSqlStorage().getAssetDashboardStorage().deleteAssetDashboard(dashboardId);
    return ok();
  }

  @GET
  @Path("/images/{imageName}")
  @Override
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
  @Override
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
}
