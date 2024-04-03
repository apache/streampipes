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
import org.apache.streampipes.model.client.assetdashboard.AssetDashboardConfig;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.IAssetDashboardStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v2/asset-dashboards")
@Deprecated(forRemoval = true, since = "0.95.0")
public class AssetDashboardResource extends AbstractRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(AssetDashboardResource.class);

  private static final String APP_ID = "org.apache.streampipes.apps.assetdashboard";

  @GetMapping(path = "/{dashboardId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AssetDashboardConfig> getAssetDashboard(@PathVariable("dashboardId") String dashboardId) {
    return ok(getNoSqlStorage().getAssetDashboardStorage()
                               .getAssetDashboard(dashboardId));
  }

  @PutMapping(
      path = "/{dashboardId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateAssetDashboard(
      @PathVariable("dashboardId") String dashboardId,
      @RequestBody AssetDashboardConfig dashboardConfig
  ) {
    AssetDashboardConfig dashboard = getAssetDashboardStorage().getAssetDashboard(dashboardId);
    dashboardConfig.setRev(dashboard.getRev());
    getNoSqlStorage().getAssetDashboardStorage()
                     .updateAssetDashboard(dashboardConfig);
    return ok();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AssetDashboardConfig>> getAllDashboards() {
    return ok(getNoSqlStorage().getAssetDashboardStorage()
                               .getAllAssetDashboards());
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> storeAssetDashboard(@RequestBody AssetDashboardConfig dashboardConfig) {
    getNoSqlStorage().getAssetDashboardStorage()
                     .storeAssetDashboard(dashboardConfig);
    return ok();
  }

  @DeleteMapping(path = "/{dashboardId}")
  public ResponseEntity<Void> deleteAssetDashboard(@PathVariable("dashboardId") String dashboardId) {
    getNoSqlStorage().getAssetDashboardStorage()
                     .deleteAssetDashboard(dashboardId);
    return ok();
  }

  @GetMapping(path = "/images/{imageName}")
  public ResponseEntity<byte[]> getDashboardImage(@PathVariable("imageName") String imageName) {
    try {
      var sanitizedFileName = FileManager.sanitizeFilename(imageName);
      java.nio.file.Path path = Paths.get(getTargetFile(sanitizedFileName));
      File file = new File(path.toString());
      FileNameMap fileNameMap = URLConnection.getFileNameMap();
      String mimeType = fileNameMap.getContentTypeFor(file.getName());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(mimeType));
      headers.setContentDispositionFormData("attachment", "filename"); // You can adjust the filename as needed

      return new ResponseEntity<>(Files.readAllBytes(path), headers, org.springframework.http.HttpStatus.OK);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      throw new SpMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }


  @PostMapping(path = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> storeDashboardImage(@RequestPart("file_upload") MultipartFile fileDetail) {
    try {
      storeFile(fileDetail.getName(), fileDetail.getInputStream());
      return ok();
    } catch (IOException e) {
      LOG.error("Could not extract image input stream from request", e);
      return fail();
    }
  }

  private void storeFile(String fileName, InputStream fileInputStream) {

    var targetDirectory = new File(getTargetDirectory());
    if (!targetDirectory.exists()) {
      targetDirectory.mkdirs();
    }

    var sanitizedFileName = FileManager.sanitizeFilename(fileName);

    var targetFile = new File(getTargetFile(sanitizedFileName));

    try {
      FileUtils.copyInputStreamToFile(fileInputStream, targetFile);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      throw new SpMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e);
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
    return StorageDispatcher.INSTANCE.getNoSqlStore()
                                     .getAssetDashboardStorage();
  }
}
