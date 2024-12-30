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

package org.apache.streampipes.assetmodel.management;

import org.apache.streampipes.model.assets.SpAsset;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class AssetModelHelper {
  private static final Logger LOG = LoggerFactory.getLogger(AssetModelHelper.class);

  private final AssetModelManagement assetModelManagement;

  public AssetModelHelper(AssetModelManagement assetModelManagement) {
    this.assetModelManagement = assetModelManagement;
  }


  public AssetModelHelper() {
    var genericStorage = StorageDispatcher.INSTANCE.getNoSqlStore()
                                                   .getGenericStorage();
    assetModelManagement = new AssetModelManagement(genericStorage);
  }

  /**
   * Removes the asset link with the given resource ID from all asset models.
   *
   * @param resourceId The ID of the resource to be removed from the asset links.
   * @throws IOException If an I/O error occurs while fetching or updating asset models.
   */
  public void removeAssetLinkFromAllAssets(String resourceId) throws IOException {
    var allAssetModels = getAllAssetModelsFromStorage();

    removeAssetLinksFromAssetModels(resourceId, allAssetModels);
  }

  private void removeAssetLinksFromAssetModels(String resourceId, List<SpAssetModel> allAssetModels)
      throws IOException {
    for (SpAssetModel assetModel : allAssetModels) {
      removeAssetLinksFromAssetModelRecursively(assetModel, resourceId);
      updateAssetModel(assetModel);
    }
  }

  private void updateAssetModel(SpAssetModel assetModel) throws IOException {
    try {
      assetModelManagement.update(assetModel.getId(), assetModel);
    } catch (IOException e) {
      LOG.error("Could not fetch all asset models from storage", e);
      throw new IOException("Could not fetch all asset models from storage", e);
    }
  }

  private List<SpAssetModel> getAllAssetModelsFromStorage() throws IOException {
    try {
      return assetModelManagement.findAll();
    } catch (IOException e) {
      LOG.error("Could not fetch all asset models from storage", e);
      throw new IOException("Could not fetch all asset models from storage", e);
    }
  }

  /**
   * This method removes the asset link from the asset model and recursively from all sub-assets.
   */
  private void removeAssetLinksFromAssetModelRecursively(SpAssetModel assetModel, String resourceId) {
    removeAssetLinks(assetModel, resourceId);

    assetModel.getAssets()
              .forEach(asset -> removeAssetLinksFromAsset(asset, resourceId));
  }

  /**
   * Removes the resourceId from the asset links and recursively from all sub-assets.
   */
  private void removeAssetLinksFromAsset(SpAsset asset, String resourceId) {
    removeAssetLinks(asset, resourceId);

    if (asset.getAssets() != null) {
      asset.getAssets()
           .forEach(subAsset -> removeAssetLinks(subAsset, resourceId));
    }
  }

  /**
   * Takes the asset as an input and removes the asset link with the given resource ID.
   */
  private void removeAssetLinks(SpAsset asset, String resourceId) {
    var assetLinks = asset.getAssetLinks();
    if (assetLinks != null) {
      assetLinks.removeIf(link -> resourceId.equals(link.getResourceId()));
    }
  }
}
