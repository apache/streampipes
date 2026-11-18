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

import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { AssetBtns } from '../../support/utils/asset/AssetBtns';
import { AssetBuilder } from '../../support/builder/AssetBuilder';

describe('Upload, view and remove an asset image', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const assetName = 'ImageAsset';

        // Select an image for a new asset; it is previewed before saving
        AssetUtils.goToAssets();
        AssetUtils.addNewAsset(AssetBuilder.create(assetName).build());
        AssetUtils.uploadImage();
        AssetUtils.checkImageShown();
        AssetBtns.saveAssetBtn().click();
        AssetBtns.createAssetBtn().should('be.visible');
        AssetUtils.checkStoredImage(assetName, true);

        // The stored image is loaded on the details page and opens full screen
        AssetUtils.showAsset(assetName);
        AssetUtils.checkImageShown();
        AssetUtils.openAndCloseImageViewer();

        // Removing the image clears the reference when the asset is saved
        AssetUtils.goToAssets();
        AssetUtils.editAsset(assetName);
        AssetUtils.checkImageShown();
        AssetUtils.removeImage();
        AssetBtns.saveAssetBtn().click();
        AssetBtns.createAssetBtn().should('be.visible');
        AssetUtils.checkStoredImage(assetName, false);

        AssetUtils.showAsset(assetName);
        AssetBtns.imagePlaceholder().should('be.visible');
        AssetBtns.imageThumbnail().should('not.exist');
    });
});
