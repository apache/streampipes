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

import org.apache.streampipes.model.assets.AssetLink;
import org.apache.streampipes.model.assets.SpAsset;
import org.apache.streampipes.model.assets.SpAssetModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssetModelHelperTest {

  private static final String RESOURCE_ID_TO_BE_REMOVED = "resourceId";

  private AssetModelManagement assetModelManagement;
  private AssetModelHelper assetModelHelper;

  @BeforeEach
  void setUp() {
    assetModelManagement = Mockito.mock(AssetModelManagement.class);
    assetModelHelper = new AssetModelHelper(assetModelManagement);
  }

  @Test
  void removeAssetLinkFromAllAssets_FromSpAssetModel() throws IOException {
    // Provide a sample asset model that contains the asset link to be removed and mock the asset model management
    // findAll
    var sampleAssetModel = getSampleAssetModel();
    addAssetLink(sampleAssetModel, RESOURCE_ID_TO_BE_REMOVED);
    when(assetModelManagement.findAll()).thenReturn(List.of(sampleAssetModel));

    assetModelHelper.removeAssetLinkFromAllAssets(RESOURCE_ID_TO_BE_REMOVED);

    // Verify that asset model was updated and does not contain the asset link anymore
    verify(assetModelManagement, times(1)).update(sampleAssetModel.getId(), sampleAssetModel);
    assertTrue(sampleAssetModel.getAssetLinks().isEmpty());
  }

  @Test
  void removeAssetLinkFromAllAssets_FromSpAsset() throws IOException {
    // Provide a sample asset model with one asset that contains the asset link to be removed and mock the asset model
    // management findAll
    var sampleAssetModel = getSampleAssetModel();
    var asset = new SpAsset();
    addAssetLink(asset, RESOURCE_ID_TO_BE_REMOVED);
    sampleAssetModel.setAssets(List.of(asset));
    when(assetModelManagement.findAll()).thenReturn(List.of(sampleAssetModel));

    assetModelHelper.removeAssetLinkFromAllAssets(RESOURCE_ID_TO_BE_REMOVED);

    // Verify that asset was updated and does not contain the asset link anymore
    verify(assetModelManagement, times(1)).update(sampleAssetModel.getId(), sampleAssetModel);
    assertTrue(sampleAssetModel.getAssets().get(0).getAssetLinks().isEmpty());
  }

  @Test
  void removeAssetLinkFromAllAssets_IOExceptionOnReadingAssetModels() throws IOException {
    when(assetModelManagement.findAll()).thenThrow(new IOException());

    assertThrows(IOException.class, () -> assetModelHelper.removeAssetLinkFromAllAssets(RESOURCE_ID_TO_BE_REMOVED));
  }

  @Test
  void removeAssetLinkFromAllAssets_IOExceptionWhenUpdatingModel() throws IOException {
    var sampleAssetModel = getSampleAssetModel();
    addAssetLink(sampleAssetModel, RESOURCE_ID_TO_BE_REMOVED);
    when(assetModelManagement.findAll()).thenReturn(List.of(sampleAssetModel));

    when(assetModelManagement.update(sampleAssetModel.getId(), sampleAssetModel)).thenThrow(new IOException());

    assertThrows(IOException.class, () -> assetModelHelper.removeAssetLinkFromAllAssets(RESOURCE_ID_TO_BE_REMOVED));
  }


  private SpAssetModel getSampleAssetModel() {
    var sampleAssetModel = new SpAssetModel();
    sampleAssetModel.setId("1");

    return sampleAssetModel;
  }

  private void addAssetLink(SpAsset asset, String assetLinkResourceId) {
    var assetLink = new AssetLink();
    assetLink.setResourceId(assetLinkResourceId);
    asset.setAssetLinks(new ArrayList<>(List.of(assetLink)));
  }
}