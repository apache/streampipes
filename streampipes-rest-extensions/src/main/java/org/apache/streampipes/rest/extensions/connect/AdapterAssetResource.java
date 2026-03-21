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

package org.apache.streampipes.rest.extensions.connect;

import org.apache.streampipes.commons.media.ImageMimeTypeDetector;
import org.apache.streampipes.extensions.management.connect.AdapterAssetManagement;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/worker/adapters")
public class AdapterAssetResource extends AbstractSharedRestInterface {

  private final AdapterAssetManagement adapterAssetManagement;

  public AdapterAssetResource() {
    this.adapterAssetManagement = new AdapterAssetManagement();
  }

  public AdapterAssetResource(AdapterAssetManagement adapterAssetManagement) {
    this.adapterAssetManagement = adapterAssetManagement;
  }


  @GetMapping(path = "/{appId}/assets", produces = "application/zip")
  public ResponseEntity<byte[]> getAssets(@PathVariable("appId") String appId) {
    try {
      var assetOpt = adapterAssetManagement.getAssets(appId);
      if (assetOpt.isPresent()) {
        return ok(assetOpt.get());
      } else {
        throw new SpMessageException(
            HttpStatus.NOT_FOUND,
            Notifications.error(String.format("Could not find adapter with id %s", appId)));
      }
    } catch (IOException e) {
      throw new SpMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @GetMapping(path = "/{appId}/assets/icon")
  public ResponseEntity<byte[]> getIconAsset(@PathVariable("appId") String appId) throws IOException {
    var iconOpt = adapterAssetManagement.getIconAsset(appId);
    if (iconOpt.isPresent()) {
      byte[] icon = iconOpt.get();
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(ImageMimeTypeDetector.detect(icon)))
          .body(icon);
    } else {
      throw new IOException("Could not find adapter");
    }
  }

  @GetMapping(path = "/{appId}/assets/documentation", produces = MediaType.TEXT_PLAIN_VALUE)
  public String getDocumentationAsset(@PathVariable("appId") String appId) throws IOException {
    var documentationOpt = adapterAssetManagement.getDocumentationAsset(appId);
    if (documentationOpt.isPresent()) {
      return documentationOpt.get();
    } else {
      throw new IOException("Could not find documentation");
    }
  }
}
