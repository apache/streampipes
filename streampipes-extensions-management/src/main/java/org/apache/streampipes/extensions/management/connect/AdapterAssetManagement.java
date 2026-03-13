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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.extensions.management.assets.AssetZipGenerator;

import java.io.IOException;
import java.util.Optional;

public class AdapterAssetManagement {

  private final ConnectWorkerDescriptionProvider connectWorkerDescriptionProvider;

  public AdapterAssetManagement() {
    this(new ConnectWorkerDescriptionProvider());
  }

  public AdapterAssetManagement(ConnectWorkerDescriptionProvider connectWorkerDescriptionProvider) {
    this.connectWorkerDescriptionProvider = connectWorkerDescriptionProvider;
  }

  public Optional<byte[]> getAssets(String appId) throws IOException {
    var adapterConfig = connectWorkerDescriptionProvider.getAdapterConfiguration(appId);
    if (adapterConfig.isPresent()) {
      return Optional.of(
          new AssetZipGenerator(
              adapterConfig.get().getAdapterDescription().getIncludedAssets(),
              adapterConfig.get().getAssetResolver()).makeZip()
      );
    }

    return Optional.empty();
  }

  public Optional<byte[]> getIconAsset(String appId) throws IOException {
    var adapterConfig = connectWorkerDescriptionProvider.getAdapterConfiguration(appId);
    if (adapterConfig.isPresent()) {
      return Optional.of(adapterConfig.get().getAssetResolver().getAsset(GlobalStreamPipesConstants.STD_ICON_NAME));
    }

    return Optional.empty();
  }

  public Optional<String> getDocumentationAsset(String appId) throws IOException {
    var adapterConfig = connectWorkerDescriptionProvider.getAdapterConfiguration(appId);
    if (adapterConfig.isPresent()) {
      return Optional.of(new String(
          adapterConfig.get().getAssetResolver().getAsset(GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME))
      );
    }

    return Optional.empty();
  }
}
