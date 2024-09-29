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

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.extensions.management.assets.AssetZipGenerator;
import org.apache.streampipes.extensions.management.connect.ConnectWorkerDescriptionProvider;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/worker/adapters")
public class AdapterAssetResource extends AbstractSharedRestInterface {

  private final ConnectWorkerDescriptionProvider connectWorkerDescriptionProvider;

  public AdapterAssetResource() {
    this.connectWorkerDescriptionProvider = new ConnectWorkerDescriptionProvider();
  }

  @GetMapping(path = "/{id}/assets", produces = "application/zip")
  public ResponseEntity<byte[]> getAssets(@PathVariable("id") String id) {
    var adapterConfig = this.connectWorkerDescriptionProvider.getAdapterConfiguration(id);
    if (adapterConfig.isPresent()) {
      try {
        return ok(new AssetZipGenerator(adapterConfig.get().getAdapterDescription().getIncludedAssets(),
                adapterConfig.get().getAssetResolver()).makeZip());
      } catch (IOException e) {
        throw new SpMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e);
      }
    } else {
      throw new SpMessageException(HttpStatus.NOT_FOUND,
              Notifications.error(String.format("Could not find adapter with id %s", id)));
    }

  }

  @GetMapping(path = "/{id}/assets/icon", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> getIconAsset(@PathVariable("id") String elementId) throws IOException {
    var adapterConfig = this.connectWorkerDescriptionProvider.getAdapterConfiguration(elementId);
    if (adapterConfig.isPresent()) {
      return ok(adapterConfig.get().getAssetResolver().getAsset(GlobalStreamPipesConstants.STD_ICON_NAME));
    } else {
      throw new IOException("Could not find adapter");
    }
  }

  @GetMapping(path = "/{id}/assets/documentation", produces = MediaType.TEXT_PLAIN_VALUE)
  public String getDocumentationAsset(@PathVariable("id") String elementId) throws IOException {
    var adapterConfig = this.connectWorkerDescriptionProvider.getAdapterConfiguration(elementId);
    if (adapterConfig.isPresent()) {
      return new String(
              adapterConfig.get().getAssetResolver().getAsset(GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME));
    } else {
      throw new IOException("Could not find documentation");
    }
  }
}
