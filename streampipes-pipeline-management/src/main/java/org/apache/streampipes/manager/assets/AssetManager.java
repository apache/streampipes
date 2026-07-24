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
package org.apache.streampipes.manager.assets;

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AssetManager {

  private final ISpCoreConfigurationStorage coreConfigurationStorage;

  public AssetManager(ISpCoreConfigurationStorage coreConfigurationStorage) {
    this.coreConfigurationStorage = coreConfigurationStorage;
  }

  public byte[] getAssetIcon(String appId) throws IOException {
    return Files.readAllBytes(Paths.get(getAssetIconPath(appId)));
  }

  public String getAssetDocumentation(String appId) throws IOException {
    return new String(Files.readAllBytes(Paths.get(getAssetDocumentationPath(appId))));
  }

  public byte[] getAsset(String appId, String assetName) throws IOException {
    return Files.readAllBytes(Paths.get(getAssetPath(appId, assetName)));
  }

  public boolean existsAssetDir(String appId) {
    var directory = new File(getAssetDir(appId));
    return directory.exists() && directory.isDirectory();
  }

  public void storeAsset(SpServiceUrlProvider spServiceUrlProvider,
                                String appId,
                                ExtensionServiceRequestManager requestManager) throws IOException, NoServiceEndpointsAvailableException {
    InputStream assetStream = new AssetFetcher(spServiceUrlProvider, appId, requestManager)
        .fetchPipelineElementAssets();
    new AssetExtractor(assetStream, appId, coreConfigurationStorage).extractAssetContents();
  }

  public void deleteAsset(String appId) throws IOException {
    Path path = Paths.get(getAssetDir(appId));
    if (Files.exists(path)) {
      FileUtils.deleteDirectory(path.toFile());
    }
  }

  private String getAssetPath(String appId, String assetName) {
    return getAssetDir(appId) + File.separator + assetName;
  }

  private String getAssetIconPath(String appId) {
    return getAssetDir(appId) + File.separator + GlobalStreamPipesConstants.STD_ICON_NAME;
  }

  private String getAssetDocumentationPath(String appId) {
    return getAssetDir(appId) + File.separator + GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME;
  }

  private String getAssetDir(String appId) {
    return this.coreConfigurationStorage.get().getAssetDir() + File.separator + appId;
  }

}
