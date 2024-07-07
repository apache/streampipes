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

import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v2/pe")
public class PipelineElementAsset extends AbstractRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineElementAsset.class);

  @GetMapping(path = "/{appId}/assets/icon", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<?> getIconAsset(@PathVariable("appId") String appId) {
    try {
      return ok(AssetManager.getAssetIcon(appId));
    } catch (IOException e) {
      return fail();
    }
  }

  @GetMapping(path = "/{appId}/assets/documentation", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<?> getDocumentationAsset(@PathVariable("appId") String appId) {
    try {
      //If the provided appId contains "sp:spdatastream",
      //it indicates usage by the asset-overview view where the data stream ID is supplied.
      //In such cases, the appId of the adapter description needs retrieval for successful documentation loading.
      if (appId.contains("sp:spdatastream")) {
        var dataStream = StorageDispatcher.INSTANCE
            .getNoSqlStore()
            .getDataStreamStorage()
            .getDataStreamByAppId(appId);
        var adapterDescription = StorageDispatcher.INSTANCE
            .getNoSqlStore()
            .getAdapterInstanceStorage()
            .getElementById(dataStream.getCorrespondingAdapterId());
        appId = adapterDescription.getAppId();
      }
      return ok(AssetManager.getAssetDocumentation(appId));
    } catch (IOException e) {
      return fail();
    }
  }

  @GetMapping(path = "/{appId}/assets/{assetName}", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<?> getAsset(@PathVariable("appId") String appId, @PathVariable("assetName") String
      assetName) {
    try {
      byte[] asset = AssetManager.getAsset(appId, assetName);
      return ok(asset);
    } catch (IOException e) {
      LOG.error("Could not find asset {}", assetName);
      return fail();
    }
  }
}
